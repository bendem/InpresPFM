package be.hepl.benbear.accountingserverclientsalaries;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.samop.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Main {

    public static void main(String... args) throws IOException {
        if (args.length < 2) {
            Log.e("provide user and password");
            return;
        }

        Config config = new Config(Paths.get("..", "global.conf"));

        Main main = new Main(config, args[0], args[1]);
        main.run();
        main.socket.close();
    }

    private final ProtocolHandler proto;
    private final Socket socket;
    private final String user;
    private final String password;

    public Main(Config config, String user, String password) {
        this.user = user;
        this.password = password;
        Log.d("setting up");

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
            Log.i("creating context");
            SSLSocketFactory socketFactory = createSslContext();
            Log.i("creating socket %s:%s", config.getStringThrowing("accounting_salaries.host"), config.getIntThrowing("accounting_salaries.port"));
            socket = socketFactory.createSocket(
                config.getStringThrowing("accounting_salaries.host"), config.getIntThrowing("accounting_salaries.port")
            );
            Log.d("created socket");
        } catch (KeyStoreException | InvalidAlgorithmParameterException | KeyManagementException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private SSLSocketFactory createSslContext() throws KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException, UnrecoverableKeyException {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream("../accounting_server_salaries/server.jks"), "bleargh".toCharArray());

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
        return ctx.getSocketFactory();
    }

    private void run() throws IOException {
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();

        Log.i("======== logging in");
        proto.write(os, new Login(user, password));
        LoginResponse loginResponse = proto.read(is);
        if (!loginResponse.isOk()) {
            Log.e(loginResponse.getReason());
            return;
        }
        Log.i("+++ logged in");

        Log.i("======== listing salaries");
        proto.write(os, new List(9));
        ListResponse listResponse = proto.read(is);
        Log.i("+++ %s", Arrays.toString(listResponse.getData()));
    }

    private void one() throws IOException {
        String line = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
    }

    private void all() throws IOException {
    }

}
