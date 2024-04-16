package info.kgeorgiy.ja.khodzhayarov.hello;

import java.net.InetSocketAddress;

import info.kgeorgiy.ja.khodzhayarov.hello.utils.ArgsParser;
import info.kgeorgiy.java.advanced.hello.HelloClient;

public class HelloUDPNonblockingClient implements HelloClient {
    @Override
    public void run(String host, int port, String prefix, int threadCount, int requestCount) {
        final InetSocketAddress address = new InetSocketAddress(host, port);
    }

    private void run(String prefix) {
    }

    public static void main(final String[] args) {
        try {
            final ArgsParser parser = new ArgsParser(args,
                    "host", "port", "prefix", "threadCount", "requestCount"
            );
            new HelloUDPNonblockingClient().run(
                    parser.getString(),
                    parser.getInt(),
                    parser.getString(),
                    parser.getInt(),
                    parser.getInt()
            );
        } catch (ArgsParser.ArgsParserException e) {
            System.err.println("Unable to parse arguments: " + e.getMessage());
        }
    }
}