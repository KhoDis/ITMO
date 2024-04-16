//package info.kgeorgiy.ja.khodzhayarov.hello;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.SocketException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.stream.IntStream;
//
//import info.kgeorgiy.ja.khodzhayarov.hello.utils.HelloUDPUtils;
//import info.kgeorgiy.java.advanced.hello.HelloServer;
//
///**
// * Accepts tasks sent by the {@link HelloUDPClient} class and responds to them.
// *
// * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
// */
//public class HelloUDPServer implements HelloServer {
//    private static final String RESPONSE_FORMAT = "Hello, %s";
//    private DatagramSocket socket;
//    private ExecutorService handlers;
//    private int dataSize;
//
//    // :NOTE: close после нескольких повторных вызовов start закроет только последние ресурсы.
//    // Утечка
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void start(final int port, final int threadCount) {
//        if (socket != null) {
//            return;
//        }
//        try {
//            socket = new DatagramSocket(port);
//            dataSize = socket.getReceiveBufferSize();
//        } catch (final SocketException e) {
//            System.err.println("An error in the underlying protocol: " + e.getMessage());
//            return;
//        }
//
//        handlers = Executors.newFixedThreadPool(threadCount);
//
//        IntStream.range(0, threadCount)
//                .<Runnable>mapToObj(i -> this::handle)
//                .forEach(handlers::submit);
//    }
//
//    private void handle() {
//        try {
//            while (!socket.isClosed()) {
//                final DatagramPacket requestPacket = receivePacket();
//                // :NOTE: Несмотря на то,
//                // что текущий способ получения ответа по запросу очень прост,
//                // сервер должен быть рассчитан на ситуацию,
//                // когда этот процесс может требовать много ресурсов и времени.
//                socket.send(HelloUDPUtils.newDatagramPacket(
//                        generateResponse(requestPacket),
//                        requestPacket.getSocketAddress()
//                ));
//            }
//        } catch (final IOException e) {
//            if (!socket.isClosed()) {
//                System.err.println("IOException occurred: " + e.getMessage());
//            }
//        }
//    }
//
//    private String generateResponse(final DatagramPacket requestPacket) {
//        return String.format(RESPONSE_FORMAT, HelloUDPUtils.extractData(requestPacket));
//    }
//
//    private DatagramPacket receivePacket() throws IOException {
//        final DatagramPacket requestPacket = HelloUDPUtils.newDatagramPacket(dataSize);
//        socket.receive(requestPacket);
//        return requestPacket;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void close() {
//        socket.close();
//        HelloUDPUtils.shutdownAndAwaitTermination(handlers);
//    }
//
////    /**
////     * Main driver method of {@link HelloUDPServer}.
////     * Command line arguments:
////     *      name or ip-address of the computer on which the server is running;
////     *      port number to send requests to;
////     *      request prefix (string);
////     *      the number of parallel request streams;
////     *      the number of requests in each thread.
////     *
////     * @param args the command line arguments
////     */
////    public static void main(final String[] args) {
////        final HelloUDPUtils.ArgsParser parser = new HelloUDPUtils.ArgsParser(args,
////                "port", "threadCount"
////        );
////        if (!parser.isFailure()) {
////            try (HelloServer server = new HelloUDPServer()) {
////                server.start(parser.getInt(), parser.getInt());
////            }
////        }
////    }
//}
