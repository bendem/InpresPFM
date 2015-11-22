package be.hepl.benbear.commons.net;

import be.hepl.benbear.commons.generics.Tuple;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public abstract class Server<In, Out> {

    protected static final int MAX_TRIES = 3;

    private final Thread acceptThread;
    private final Thread selectThread;
    private final ExecutorService threadPool;
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final Function<InputStream, In> inputStreamMapping;
    private final Function<OutputStream, Out> outputStreamMapping;
    private final AtomicBoolean closed;
    protected final Map<SocketChannel, Tuple<In, Out>> connections;
    protected final Set<SocketChannel> connectionsToSelect;

    public Server(InetAddress address, int port, ThreadFactory threadFactory, ExecutorService threadPool, Function<InputStream, In> inputStreamMapping, Function<OutputStream, Out> outputStreamMapping) {
        this.threadPool = threadPool;
        this.acceptThread = threadFactory.newThread(this::accept);
        this.selectThread = threadFactory.newThread(this::select);
        try {
            this.serverChannel = ServerSocketChannel.open();
            this.serverChannel.bind(new InetSocketAddress(address, port));
            this.selector = Selector.open();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        this.inputStreamMapping = inputStreamMapping;
        this.outputStreamMapping = outputStreamMapping;
        this.closed = new AtomicBoolean(false);
        this.connections = new ConcurrentHashMap<>();
        this.connectionsToSelect = new CopyOnWriteArraySet<>();
    }

    public void start() {
        if(acceptThread.isAlive() || closed.get()) {
            throw new RuntimeException("Thread already started");
        }
        acceptThread.start();
        selectThread.start();
    }

    public void close() {
        if(this.closed.getAndSet(true)) {
            return;
        }

        this.connections.keySet().forEach(
            UncheckedLambda.consumer(SocketChannel::close, Throwable::printStackTrace)
        );
        try {
            serverChannel.close();
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
        Log.i("Starting accepting on %s", UncheckedLambda.supplier(serverChannel::getLocalAddress).get());

        while(!closed.get()) {
            SocketChannel socketChannel = acceptConnection();
            Log.i("New connection from " + socketChannel.socket().getRemoteSocketAddress().toString());
            registerConnection(socketChannel);
        }
    }

    private void select() {
        while(!closed.get()) {
            connectionsToSelect.stream()
                .filter(channel -> !channel.isRegistered())
                .forEach(channel -> {
                    try {
                        Log.d("Adding channel %s to selection", channel.socket().getRemoteSocketAddress());
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ, channel);
                    } catch(IOException e) {
                        connections.remove(channel);
                        try {
                            channel.close();
                        } catch(IOException e1) {
                            e1.printStackTrace();
                        }
                        onClose(channel, e);
                    }
                });

            int selected = 0;
            Log.d("Selecting");
            try {
                selected = selector.select();
            } catch(IOException e) {
                e.printStackTrace();
                close();
            }
            Log.d("Selected");
            if(selected == 0) {
                continue;
            }
            Log.d("Actually selected");

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if(!key.isReadable()) {
                    Log.w("%s is selected but not readable, what the hell? %s %s %s %s %s %s",
                        key, key.channel(), key.isAcceptable(), key.isConnectable(), key.isReadable(),
                        key.isValid(), key.isWritable());
                    continue;
                }

                Log.d("handling read");
                connectionsToSelect.remove(key.channel());
                it.remove();

                SocketChannel channel = (SocketChannel) key.attachment();
                Tuple<In, Out> streams = connections.get(channel);
                key.cancel();
                threadPool.submit(() -> {
                    try {
                        channel.configureBlocking(true);
                        read(streams.first, streams.second);

                        // Re-add to select once the packet is handled
                        connectionsToSelect.add(channel);
                        selector.wakeup();
                    } catch(IOException e) {
                        try {
                            channel.close();
                        } catch(IOException e1) {
                            onClose(channel, e1);
                            return;
                        }
                        onClose(channel, e);
                    }
                });
            }

        }
    }

    private void registerConnection(SocketChannel s) {
        Out os;
        In is;
        try {
            os = outputStreamMapping.apply(s.socket().getOutputStream());
            is = inputStreamMapping.apply(s.socket().getInputStream());
        } catch(IOException e) {
            try {
                s.close();
            } catch(IOException e1) {
                e1.printStackTrace();
            }
            return;
        }

        if(os == null || is == null) {
            connections.remove(s);
            try {
                s.close();
            } catch(IOException e) {
                onClose(s, e);
                return;
            }
            onClose(s, null);
            Log.w("Failed to construct in or out for %s", s.socket().getRemoteSocketAddress());
            return;
        }
        connections.put(s, new Tuple<>(is, os));
        connectionsToSelect.add(s);
        selector.wakeup();
    }

    private SocketChannel acceptConnection() {
        int tries = 0;
        for(;;) {
            try {
                return serverChannel.accept();
            } catch(IOException e) {
                if(tries++ > MAX_TRIES) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected abstract void read(In is, Out os) throws IOException;
    protected abstract void onClose(SocketChannel channel, Exception e);

}
