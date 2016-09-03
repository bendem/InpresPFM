package be.hepl.benbear.accountingserversalaries;

import be.hepl.benbear.accounting_db.Salary;
import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.samop.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SalariesServer {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private final Config config;
    private final Database accounting;
    private final ServerSocket socket;
    private final ProtocolHandler proto;
    private volatile boolean closed = false;

    public SalariesServer(Config config, Database accounting) {
        Log.d("setting up");

        this.config = config;
        this.accounting = accounting;


        proto = new ProtocolHandler()
            .registerPacket(All.ID, All.class)
            .registerPacket(AllResponse.ID, AllResponse.class)
            .registerPacket(List.ID, List.class)
            .registerPacket(ListResponse.ID, ListResponse.class)
            .registerPacket(Login.ID, Login.class)
            .registerPacket(LoginResponse.ID, LoginResponse.class)
            .registerPacket(One.ID, One.class)
            .registerPacket(OneResponse.ID, OneResponse.class);

        try {
            Log.d("creating context");
            SSLServerSocketFactory socketFactory = createSslContext();
            Log.d("creating socket");
            socket = socketFactory.createServerSocket(config.getIntThrowing("accounting_salaries.port"));
            Log.d("created socket");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SSLServerSocketFactory createSslContext() throws KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException, UnrecoverableKeyException {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream("./server.jks"), "bleargh".toCharArray());

        // Create TrustManagerFactory for PKIX-compliant trust managers
        TrustManagerFactory factory = TrustManagerFactory.getInstance("PKIX");

        // Pass parameters to factory to be passed to CertPath implementation
        factory.init(keystore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, "bleargh".toCharArray());

        // Use factory
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(keyManagerFactory.getKeyManagers(), factory.getTrustManagers(), null);

        Log.d("setting default");
        return ctx.getServerSocketFactory();
    }

    private Socket accept() throws IOException {
        Log.i("Accepting");
        return socket.accept();
    }

    public void start() {
        while (!closed) {
            Socket s;
            try {
                s = accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Log.i("Connection %s", s.getInetAddress());

            THREAD_POOL.execute(() -> {
                try {
                    handle(s);
                } catch (Exception e) {
                    Log.e("error while handling data, closing socket", e);
                    try {
                        s.close();
                    } catch (IOException e1) {
                        Log.e("error closing the socket", e1);
                    }
                }
            });
        }
    }

    private void handle(Socket socket) throws Exception {
        boolean loggedIn = false;
        while (!closed && !socket.isClosed()) {
            Packet packet = proto.read(socket.getInputStream());
            switch (packet.getId()) {
            case Login.ID:
                loggedIn = handleLogin((Login) packet, socket);
                break;
            case List.ID:
                if (!loggedIn) {
                    socket.close();
                    return;
                }
                handleList((List) packet, socket);
                break;
            case All.ID:
                if (!loggedIn) {
                    socket.close();
                    return;
                }
                handleAll((All) packet, socket);
                break;
            case One.ID:
                if (!loggedIn) {
                    socket.close();
                    return;
                }
                handleOne((One) packet, socket);
                break;
            }
        }
    }

    private void handleOne(One packet, Socket socket) throws IOException {
        // What should we do?
        proto.write(socket.getOutputStream(), new OneResponse());
    }

    private void handleAll(All packet, Socket socket) throws IOException {
        // What should we do?
        proto.write(socket.getOutputStream(), new AllResponse());
    }

    private void handleList(List packet, Socket socket) throws IOException, ExecutionException, InterruptedException {
        int month = packet.getMonth();
        LocalDate start = LocalDate.now().withMonth(month).withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);
        String[] salaries = accounting.table(Salary.class)
            .find(DBPredicate
                .of("due_date", Date.valueOf(start), ">")
                .and("due_date", Date.valueOf(end), "<"))
            .get()
            .filter(Salary::isPaid)
            .map(s -> "staff id: " + String.valueOf(s.getStaffId()) + " | amount: " + s.getAmount())
            .toArray(String[]::new);

        proto.write(socket.getOutputStream(), new ListResponse(salaries));
    }

    private boolean handleLogin(Login read, Socket socket) throws IOException {
        Log.i("%s:%s", read.getUsername(), read.getPassword());
        Optional<Staff> login;
        try {
            login = accounting.table(Staff.class)
                .findOne(DBPredicate.of("login", read.getUsername())
                    .and("duty", "accountant"))
                .get();
        } catch (InterruptedException | ExecutionException e) {
            socket.close();
            throw new RuntimeException(e);
        }


        if (!login.isPresent()) {
            proto.write(socket.getOutputStream(), new LoginResponse(false, "invalid login"));
            return false;
        }

        if (!login.get().getPassword().equals(read.getPassword())) {
            Log.d("%s", login.get().getPassword());

            proto.write(socket.getOutputStream(), new LoginResponse(false, "invalid password"));
            return false;
        }

        proto.write(socket.getOutputStream(), new LoginResponse(true, null));
        return true;
    }

    public void stop() {
        closed = true;
        try {
            socket.close();
            THREAD_POOL.shutdown();
            THREAD_POOL.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
