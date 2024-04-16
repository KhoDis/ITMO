package info.kgeorgiy.ja.khodzhayarov.crawler;

import java.io.IOException;
import java.util.Arrays;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;

public final class Main {
    private static final int DEFAULT_DEPTH = 1;

    private static final int DEFAULT_DOWNLOADERS = 1;

    private static final int DEFAULT_EXTRACTORS = 1;

    private static final int DEFAULT_PER_HOST = 4;

    private static final int DEPTH_SPECIFIED = 1;

    private static final int DOWNLOADS_SPECIFIED = 2;

    private static final int EXTRACTORS_SPECIFIED = 3;

    private static final int PER_HOST_SPECIFIED = 4;

    private static final int MIN_ARG_COUNT = 1;

    private static final int MAX_ARG_COUNT = 5;

    private Main() { }

    private static int getOptionalInt(
            final String[] elements,
            final int index,
            final int defaultValue
    ) {
        return elements.length > index ? Integer.parseInt(elements[index]) : defaultValue;
    }

    public static void main(final String[] args) {
        if (args == null || args.length < MIN_ARG_COUNT || args.length > MAX_ARG_COUNT) {
            System.out.println("Invalid usage.");
            System.out.println("Proper usage:");
            System.out.println("\tWebCrawler url [depth [downloads [extractors [perHost]]]]");
            return;
        }

        if (Arrays.asList(args).contains(null)) {
            System.out.println("Null argument cannot be used.");
            return;
        }

        final String url = args[0];
        final int depth = getOptionalInt(args, DEPTH_SPECIFIED, DEFAULT_DEPTH);
        final int downloaders = getOptionalInt(args, DOWNLOADS_SPECIFIED, DEFAULT_DOWNLOADERS);
        final int extractors = getOptionalInt(args, EXTRACTORS_SPECIFIED, DEFAULT_EXTRACTORS);
        final int perHost = getOptionalInt(args, PER_HOST_SPECIFIED, DEFAULT_PER_HOST);
        final CachingDownloader downloader;
        try {
            downloader = new CachingDownloader();
        } catch (final IOException e) {
            System.err.println("Unable to create output folder: " + e.getMessage());
            return;
        }
        new WebCrawler(downloader, downloaders, extractors, perHost).download(url, depth);
    }
}
