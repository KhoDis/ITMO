//package info.kgeorgiy.ja.khodzhayarov.hello;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.stream.IntStream;
//
//import info.kgeorgiy.ja.khodzhayarov.hello.utils.ArgsParser;
//import info.kgeorgiy.ja.khodzhayarov.hello.utils.HelloUDPUtils;
//import info.kgeorgiy.java.advanced.hello.HelloClient;
//
///**
// * Sends requests to the server, receives the results and prints them to the console.
// *
// * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
// */
//public class HelloUDPClient implements HelloClient {
//    private static final String REQUEST_FORMAT = "%s%d_%d";
//    private static final int SOCKET_TIMEOUT = 100;
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void run(
//            final String host,
//            final int port,
//            final String prefix,
//            final int threadCount,
//            final int requestCount
//    ) {
//        final InetAddress inetAddress;
//        try {
//            inetAddress = InetAddress.getByName(host);
//        } catch (final UnknownHostException e) {
//            System.err.println("No IP address for the host could be found: " + e.getMessage());
//            return;
//        }
//
//        final SocketAddress address = new InetSocketAddress(inetAddress, port);
//        final ExecutorService requesters = Executors.newFixedThreadPool(threadCount);
//
//        IntStream.range(0, threadCount)
//                .<Runnable>mapToObj(threadIndex -> () ->
//                        request(threadIndex, address, prefix, requestCount)
//                ).forEach(requesters::submit);
//
//        HelloUDPUtils.shutdownAndAwaitTermination(requesters);
//    }
//
//    private void request(
//            final int threadIndex,
//            final SocketAddress address,
//            final String prefix,
//            final int requestCount
//    ) {
//        try (DatagramSocket socket = new DatagramSocket()) {
//            socket.setSoTimeout(SOCKET_TIMEOUT);
//            for (int requestIndex = 0; requestIndex < requestCount; requestIndex++) {
//                final String request = String.format(REQUEST_FORMAT,
//                        prefix,
//                        threadIndex,
//                        requestIndex
//                );
//
//                System.out.println(waitForResponse(socket, request,
//                        HelloUDPUtils.newDatagramPacket(request, address),
//                        HelloUDPUtils.newDatagramPacket(socket.getReceiveBufferSize())
//                ));
//            }
//        } catch (final SocketException e) {
//            System.err.println(String.join(" ",
//                    "The socket could not be opened, or the socket could not be bound:",
//                    e.getMessage())
//            );
//        }
//    }
//
//    private String waitForResponse(
//            final DatagramSocket socket,
//            final String request,
//            final DatagramPacket requestPacket,
//            final DatagramPacket responsePacket
//    ) {
//        String response;
//        do {
//            trySend(socket, requestPacket);
//            tryReceive(socket, responsePacket);
//
//            response = HelloUDPUtils.extractData(responsePacket);
//        } while (!response.contains(request));
//        return response;
//    }
//
//    private static void tryReceive(final DatagramSocket socket, final DatagramPacket packet) {
//        try {
//            socket.receive(packet);
//        } catch (final IOException ignored) {
//        }
//    }
//
//    private static void trySend(final DatagramSocket socket, final DatagramPacket packet) {
//        try {
//            socket.send(packet);
//        } catch (final IOException ignored) {
//        }
//    }
//
////    /**
////     * Main driver method of {@link HelloUDPClient}.
////     * Command line arguments:
////     *      port number on which requests will be received;
////     *      the number of worker threads that will process requests.
////     *
////     * @param args the command line arguments
////     */
////    public static void main(final String[] args) {
////        final ArgsParser parser = new ArgsParser(args,
////                "host", "port", "prefix", "threadCount", "requestCount"
////        );
////        if (!parser.isFailure()) {
////            new HelloUDPClient().run(
////                    parser.getString(),
////                    parser.getInt(),
////                    parser.getString(),
////                    parser.getInt(),
////                    parser.getInt()
////            );
////        }
////    }
//}
