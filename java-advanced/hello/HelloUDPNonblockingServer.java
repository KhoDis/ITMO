package info.kgeorgiy.ja.khodzhayarov.hello;
//
//import java.io.IOException;
//import java.net.*;
//import java.nio.ByteBuffer;
//import java.nio.channels.DatagramChannel;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.charset.StandardCharsets;
//import java.util.Iterator;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import info.kgeorgiy.ja.khodzhayarov.hello.utils.ArgsParser;
//import info.kgeorgiy.ja.khodzhayarov.hello.utils.HelloUDPUtils;
//import info.kgeorgiy.java.advanced.hello.HelloServer;
//
//public class HelloUDPNonblockingServer implements HelloServer {
//    private static final String RESPONSE_FORMAT = "Hello, %s";
//    private Selector selector;
//    private ExecutorService workerThreads;
//    private DatagramSocket socket;
//    private ExecutorService jok;
//
//
//    @Override
//    public void start(int port, int threadCount) {
//        try {
//            selector = Selector.open();
//            socket = new DatagramSocket(port);
//        } catch (IOException e) {
//            log("Unable to open selector: " + e.getMessage());
//            return;
//        }
//        jok = Executors.newSingleThreadExecutor();
//
//        jok.submit(() -> {
//            try (DatagramChannel channel = DatagramChannel.open()) {
//                System.out.println("jok");
//                channel.configureBlocking(false);
//                System.out.println("jok");
//                channel.register(selector, SelectionKey.OP_READ);
//                System.out.println("jok");
//                InetSocketAddress cok = new InetSocketAddress(port);
//                System.out.println("jok" + cok);
//                channel.bind(cok);
//                System.out.println("jok");
//
//                SocketAddress jok = null;
//
//                while (!Thread.currentThread().isInterrupted()) { // todo
//                    selector.select();
//
//                    for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
//                        SelectionKey key = it.next();
//                        try {
//                            if (!key.isValid()) {
//                                continue;
//                            }
//                            if (key.isReadable()) {
//                                jok = read(key);
//                                selector.wakeup();
//                            } else if (key.isWritable()) {
//                                write(key, jok);
//                                selector.wakeup();
//                            }
//                        } finally {
//                            it.remove();
//                        }
//                    }
//                }
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }
//
//    // todo beautify
//    @SuppressWarnings("resource")
//    private void write(SelectionKey key, SocketAddress address) throws IOException {
//        DatagramChannel channel = (DatagramChannel) key.channel();
//        ByteBuffer buffer = (ByteBuffer) key.attachment();
//        channel.send(ByteBuffer.wrap(HelloUDPUtils.toBytes(generateResponse(HelloUDPUtils.toString(buffer.array())))), address);
//        key.interestOps(SelectionKey.OP_READ);
//    }
//
//    // todo beautify
//    @SuppressWarnings("resource")
//    private SocketAddress read(SelectionKey key) throws IOException {
//        DatagramChannel ch = (DatagramChannel) key.channel();
//        final int BUFFER_SIZE = 1 << 6; // todo
//        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
//        SocketAddress address = ch.receive(buffer);
//        key.attach(buffer);
//        key.interestOps(SelectionKey.OP_WRITE);
//        return address;
//    }
//
//    private String generateResponse(String message) {
//        return String.format(RESPONSE_FORMAT, message); // todo HelloUDPUtils.extractData(requestPacket)
//    }
//
//    private void log(String message) {
//        System.err.println(message);
//    }
//
//    @Override
//    public void close() {
//        socket.close();
//        HelloUDPUtils.shutdownAndAwaitTermination(workerThreads);
//        HelloUDPUtils.shutdownAndAwaitTermination(jok);
//    }
//}

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import info.kgeorgiy.ja.khodzhayarov.hello.utils.ArgsParser;
import info.kgeorgiy.ja.khodzhayarov.hello.utils.HelloUDPUtils;
import info.kgeorgiy.java.advanced.hello.HelloServer;

public class HelloUDPNonblockingServer implements HelloServer {
    private static final int BUFFER_SIZE = 1 << 6;
    private static final int SELECTOR_TIMEOUT = 500;
    protected static int SECONDS_BEFORE_TERMINATION = 5;
    protected ExecutorService requestListener;
    private Selector selector;
    private InetSocketAddress address;

    private Runnable getRunnable() { // todo: name
        return () -> {
            try (final DatagramChannel channel = DatagramChannel.open()) {
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                channel.bind(address);
                SocketAddress senderAddress = null;

                while (!Thread.currentThread().isInterrupted()) {
                    selector.select(SELECTOR_TIMEOUT);

                    for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                        final SelectionKey key = it.next();

                        if (!key.isValid()) {
                            continue;
                        }
                        final DatagramChannel curChannel = (DatagramChannel) key.channel();
                        if (key.isReadable()) {
                            senderAddress = read(curChannel, key);
                        }
                        if (key.isWritable()) {
                            write(curChannel, key, senderAddress);
                        }

                        selector.wakeup();
                        it.remove();
                    }
                }
            } catch (final IOException e) {
                System.err.println("Error receiving package: " + e.getMessage());
            }
        };
    }

    private SocketAddress read(final DatagramChannel channel, final SelectionKey key) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        final SocketAddress address = channel.receive(buffer);
        key.attach(buffer);
        key.interestOps(SelectionKey.OP_WRITE);
        return address;
    }

    private void write(final DatagramChannel channel, final SelectionKey key, final SocketAddress to) throws IOException {
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer = ByteBuffer.wrap(formResponse(HelloUtils.bufferToText(buffer)));
        channel.send(buffer, to);
        key.interestOps(SelectionKey.OP_READ);
    }

    @Override
    public void close() {
        try {
            selector.close();
        } catch (IOException ignored) {
        }
        HelloUDPUtils.shutdownAndAwaitTermination(requestListener);
    }

    @Override
    public void start(int port, int threads) {
        try {
            address = new InetSocketAddress(port);
            selector = Selector.open();
        } catch (final IllegalArgumentException e) {
            System.err.println("Invalid port: " + e.getMessage());
            return;
        } catch (final IOException e) {
            System.err.println("Cannot start server");
            return;
        }
        requestListener = Executors.newSingleThreadExecutor();
        requestListener.submit(getRunnable());
    }

    protected byte[] formResponse(final String response) {
        return ("Hello, " + response).getBytes(StandardCharsets.UTF_8);
    }

    public static class HelloUtils {
        static DatagramPacket createPacket(DatagramSocket socket) throws SocketException {
            final int bufferSize = socket.getReceiveBufferSize();
            return new DatagramPacket(new byte[bufferSize], bufferSize);
        }

        static DatagramPacket createPacket(final DatagramSocket socket, final SocketAddress destAddress) throws SocketException {
            final int bufferSize = socket.getReceiveBufferSize();
            return new DatagramPacket(new byte[bufferSize], bufferSize, destAddress);
        }

        static String getBody(final DatagramPacket packet) {
            return new String(
                    packet.getData(),
                    packet.getOffset(),
                    packet.getLength(),
                    StandardCharsets.UTF_8
            );
        }

        static String bufferToText(final ByteBuffer buffer) {
            return new String(buffer.array(), StandardCharsets.UTF_8).trim();
        }

        static ByteBuffer textToBuffer(final String text) {
            return ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));
        }

        static void shutdownAndAwait(final ExecutorService pool, final int timeoutSeconds) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                    if (!pool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                        System.err.println("Pool did not terminate");
                    }
                }
            } catch (final InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(final String[] args) {
        try (HelloUDPNonblockingServer server = new HelloUDPNonblockingServer()) {
            final ArgsParser parser = new ArgsParser(args, "port", "threadCount");
            server.start(
                    parser.getInt(),
                    parser.getInt()
            );
        } catch (ArgsParser.ArgsParserException e) {
            System.err.println("Unable to parse arguments: " + e.getMessage());
        }
    }
}