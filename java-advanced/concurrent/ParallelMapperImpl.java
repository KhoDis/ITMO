package info.kgeorgiy.ja.khodzhayarov.concurrent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.IntStream;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

/**
 * {@link ParallelMapper} implementation using tasks and workers with synchronizes.
 *
 * @author Khozhayarov Adis (khodzhayarov.a@gmail.com)
 *
 * @see java.util.concurrent
 */
public final class ParallelMapperImpl implements ParallelMapper {
    private final Queue<Runnable> tasks = new ArrayDeque<>();
    private final List<Thread> workers = new ArrayList<>();

    /**
     * Creates an instance with given amount of threads.
     * If possibleThreadCount is less than {@code 1}, amount of threads will be {@code 1}.
     *
     * @param possibleThreadCount possible amount of threads
     */
    public ParallelMapperImpl(final int possibleThreadCount) {
        final int threadCount = Math.max(1, possibleThreadCount);
        for (int i = 0; i < threadCount; i++) {
            // :NOTE: один Runnable на все потоки
            final Thread worker = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        run();
                    }
                } catch (final InterruptedException interruptedException) {
                    // Ignored
                }
            });
            workers.add(worker);
            worker.start();
        }
    }

    private void run() throws InterruptedException {
        final Runnable task;
        synchronized (tasks) {
            while (tasks.isEmpty()) {
                tasks.wait();
            }
            task = tasks.poll();
        }
        task.run();
    }

    /**
     * {@inheritDoc}
     *
     * @param f function to map
     * @param args args to map
     *
     * @param <T> type of the element
     * @param <R> type of the return value
     *
     * @return list of results calculated upon each chunk
     */
    @Override
    public <T, R> List<R> map(
            final Function<? super T, ? extends R> f,
            final List<? extends T> args
    ) throws InterruptedException {
        final SynchronizedListWrapper<R> resultWrapper = new SynchronizedListWrapper<>(args.size());
        final List<RuntimeException> exceptions = new ArrayList<>();
        IntStream.range(0, args.size()).<Runnable>mapToObj(index -> () -> {
            try {
                resultWrapper.set(index, f.apply(args.get(index)));
            } catch (final RuntimeException e) {
                synchronized (exceptions) {
                    exceptions.add(e);
                }
            }
        }).forEach(this::addTask);
        if (exceptions.isEmpty()) {
            return resultWrapper.getList();
        }
        final RuntimeException exception =
                new RuntimeException("Mapping failed due to runtime exceptions");
        exceptions.forEach(exception::addSuppressed);
        throw exception;
    }

    private void addTask(final Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        workers.forEach(Thread::interrupt);
        workers.forEach(worker -> {
            try {
                worker.join();
            } catch (final InterruptedException interruptedException) {
                // Ignored
            }
        });
    }

    private static class SynchronizedListWrapper<R> {
        private final List<R> list;
        private int filled;

        SynchronizedListWrapper(final int size) {
            this.list = new ArrayList<>(Collections.nCopies(size, null));
        }

        synchronized void set(final int index, final R element) {
            list.set(index, element);
            filled++;
            if (filled == list.size()) {
                notify();
            }
        }

        synchronized List<R> getList() throws InterruptedException {
            while (filled < list.size()) {
                wait();
            }
            return list;
        }

    }
}
