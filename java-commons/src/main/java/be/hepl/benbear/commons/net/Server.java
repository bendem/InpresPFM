package be.hepl.benbear.commons.net;

import be.hepl.benbear.commons.generics.Tuple3;
import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import javax.net.ServerSocketFactory;

public abstract class Server<In, Out> {

    protected static final int MAX_TRIES = 3;

    private final Thread acceptThread;
    private final Thread selectThread;
    private final ExecutorService threadPool;
    private final ServerSocket socket;
    private final Selector selector;
    private final Function<InputStream, In> inputStreamMapping;
    private final Function<OutputStream, Out> outputStreamMapping;
    private final AtomicBoolean closed;
    protected final List<Socket> connections;

    public Server(int port, ThreadFactory threadFactory, ExecutorService threadPool, Function<InputStream, In> inputStreamMapping, Function<OutputStream, Out> outputStreamMapping) {
        this.threadPool = threadPool;
        this.acceptThread = threadFactory.newThread(this::accept);
        this.selectThread = threadFactory.newThread(this::select);
        try {
            this.socket = ServerSocketFactory.getDefault().createServerSocket(port);
            this.selector = Selector.open();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        this.inputStreamMapping = inputStreamMapping;
        this.outputStreamMapping = outputStreamMapping;
        this.closed = new AtomicBoolean(false);
        this.connections = new CopyOnWriteArrayList<>();
    }

    public void start() {
        if(acceptThread.isAlive() || closed.get()) {
            throw new RuntimeException("Thread already started");
        }
        acceptThread.start();
    }

    public void close() {
        if(this.closed.getAndSet(true)) {
            return;
        }

        this.connections.forEach(
            UncheckedLambda.consumer(Socket::close, Throwable::printStackTrace)
        );
        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
            acceptThread.interrupt();
        }
        try {
            selector.close();
        } catch(IOException e) {
            e.printStackTrace();
            selectThread.interrupt();
        }
        try {
            acceptThread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        try {
            selectThread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void accept() {
        while(!closed.get()) {
            Socket socket = acceptConnection();
            System.out.println("New connection from " + socket.getRemoteSocketAddress().toString());
            registerConnection(socket);
        }
    }

    private void select() {
        while(!closed.get()) {
            connections.forEach(s -> {
                try {
                    s.getChannel().register(
                        selector,
                        SelectionKey.OP_READ,
                        new Tuple3<>(
                            s,
                            inputStreamMapping.apply(s.getInputStream()),
                            outputStreamMapping.apply(s.getOutputStream())
                        )
                    );
                } catch(IOException e) {
                    connections.remove(s);
                    onClose(s, e);
                }
            });
            int selected = 0;
            try {
                selected = selector.select();
            } catch(IOException e) {
                e.printStackTrace();
                close();
            }
            if(selected == 0) {
                continue;
            }

            selector.selectedKeys().stream()
                .filter(SelectionKey::isReadable)
                .forEach(selectionKey -> {
                    Tuple3<Socket, In, Out> streams = (Tuple3<Socket, In, Out>) selectionKey.attachment();
                    threadPool.submit(() -> {
                        try {
                            read(streams.t2, streams.t3);
                        } catch(IOException e) {
                            try {
                                streams.t1.close();
                            } catch(IOException e1) {
                                onClose(streams.t1, e1);
                            }
                            onClose(streams.t1, e);
                        }
                    });
                });

        }
    }

    private void registerConnection(Socket socket) {
        connections.add(socket);
        selector.wakeup();
    }

    private Socket acceptConnection() {
        int tries = 0;
        for(;;) {
            try {
                return socket.accept();
            } catch(IOException e) {
                if(tries++ > MAX_TRIES) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected abstract void read(In is, Out os) throws IOException;
    protected abstract void onClose(Socket socket, Exception e);

}
