package info.kgeorgiy.ja.khodzhayarov.hello.utils;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Common code for {@link HelloUDPClient} and {@link HelloUDPServer}.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 */
public final class HelloUDPUtils {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int TERMINATION_TIMEOUT = 1;

    private HelloUDPUtils() { }

    public static void shutdownAndAwaitTermination(final ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(HelloUDPUtils.TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(HelloUDPUtils.TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
                    throw new IllegalThreadStateException("Unable to close the pool: " + pool);
                }
            }
        } catch (final InterruptedException unused) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static String extractData(final DatagramPacket requestPacket) {
        return new String(
                requestPacket.getData(),
                requestPacket.getOffset(),
                requestPacket.getLength(),
                HelloUDPUtils.CHARSET
        );
    }

    public static DatagramPacket newDatagramPacket(final String data, final SocketAddress address) {
        return newDatagramPacket(toBytes(data), address);
    }

    public static DatagramPacket newDatagramPacket(final byte[] bytes, final SocketAddress address) {
        return new DatagramPacket(bytes, bytes.length, address);
    }

    public static DatagramPacket newDatagramPacket(final int size) {
        return new DatagramPacket(new byte[size], size);
    }

    public static byte[] toBytes(final String request) {
        return request.getBytes(CHARSET);
    }

    public static String toString(final byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}