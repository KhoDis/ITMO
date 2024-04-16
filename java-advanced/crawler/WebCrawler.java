package info.kgeorgiy.ja.khodzhayarov.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.Result;

/**
 * Thread-safe WebCrawler that crawls websites recursively.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 */
public class WebCrawler implements Crawler {
    private static final int TERMINATION_TIMEOUT = 60;

    private final Downloader downloader;

    private final ExecutorService extractorsThreadPool;

    private final ExecutorService downloadersThreadPool;

    /**
     * The only constructor of web crawler with a given {@link Downloader}.
     *
     * @param downloader downloads files from the web
     * @param downloaders amount of download threads
     * @param extractors amount of extract threads
     * @param perHost maximum amount of host crawling (ignored)
     */
    public WebCrawler(
            final Downloader downloader,
            final int downloaders,
            final int extractors,
            @SuppressWarnings("unused") final int perHost
    ) {
        this.downloader = downloader;
        this.extractorsThreadPool = Executors.newFixedThreadPool(extractors);
        this.downloadersThreadPool = Executors.newFixedThreadPool(downloaders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result download(final String url, final int depth) {
        return new Crawl().go(url, depth);
    }

    /**
     * {@inheritDoc}
     *
     * @see ExecutorService
     */
    @Override
    public void close() {
        shutdownAndAwaitTermination(downloadersThreadPool);
        shutdownAndAwaitTermination(extractorsThreadPool);
    }

    private void shutdownAndAwaitTermination(final ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
                    throw new IllegalThreadStateException("Unable to close the pool: " + pool);
                }
            }
        } catch (final InterruptedException unused) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private final class Crawl {
        private final Set<String> downloaded;

        private final Map<String, IOException> errors;

        private final Queue<String> queue;

        private final Phaser phaser;

        private final Set<String> visited;

        private Crawl() {
            this.downloaded = ConcurrentHashMap.newKeySet();
            this.errors = new ConcurrentHashMap<>();
            this.queue = new ConcurrentLinkedQueue<>();
            this.phaser = new Phaser(1);
            this.visited = ConcurrentHashMap.newKeySet();
        }

        private Result go(final String url, final int depth) {
            queue.add(url);
            IntStream.range(0, depth).forEach(layer -> {
                final List<String> urls = new ArrayList<>(queue);
                queue.clear();
                urls.stream().filter(visited::add).forEach(page -> sumbit(
                        phaser,
                        downloadersThreadPool,
                        getDownloader(page, depth - layer, phaser)
                ));
                phaser.arriveAndAwaitAdvance();
            });
            return new Result(new ArrayList<>(downloaded), errors);
        }

        private static void sumbit(
                final Phaser phaser,
                final ExecutorService threadPool,
                final Runnable page
        ) {
            phaser.register();
            threadPool.submit(page);
        }

        private Runnable getDownloader(
                final String url,
                final int remainingDepth,
                final Phaser phaser
        ) {
            return () -> {
                try {
                    final Document page = downloader.download(url);
                    downloaded.add(url);
                    if (remainingDepth > 1) {
                        sumbit(phaser, extractorsThreadPool, getExtractor(page, url));
                    }
                } catch (final IOException e) {
                    errors.put(url, e);
                }
                phaser.arriveAndDeregister();
            };
        }

        private Runnable getExtractor(final Document page, final String url) {
            return () -> {
                Optional.ofNullable(getLinks(page, url)).ifPresent(queue::addAll);
                phaser.arriveAndDeregister();
            };
        }

        private List<String> getLinks(final Document page, final String url) {
            try {
                return page.extractLinks();
            } catch (final IOException e) {
                errors.put(url, e);
                return null;
            }
        }
    }
}
