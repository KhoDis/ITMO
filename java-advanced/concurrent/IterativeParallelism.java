package info.kgeorgiy.ja.khodzhayarov.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

/**
 * Processes lists in several threads.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 */
public class IterativeParallelism implements ScalarIP {
    private final ParallelMapper parallelMapper;

    /**
     * Creates instance without {@link ParallelMapper} processing.
     */
    public IterativeParallelism() {
        this.parallelMapper = null;
    }

    /**
     * Creates instance with {@link ParallelMapper#map(Function, List)} processing.
     *
     * @param parallelMapper mapper instance to call {@link ParallelMapper#map(Function, List)}
     *
     * @see java.util.concurrent
     */
    public IterativeParallelism(final ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    /**
     * Returns maximum value.
     *
     * @param threadCount   number or concurrent threads
     * @param values        values to get maximum of
     * @param comparator    value comparator
     * @param <T>           value type
     *
     * @return maximum of given values
     *
     * @throws InterruptedException     if executing thread was interrupted
     * @throws NoSuchElementException   if no values are given
     */
    @Override
    public <T> T maximum(
            final int threadCount,
            final List<? extends T> values,
            final Comparator<? super T> comparator
    ) throws InterruptedException {
        final Function<List<? extends T>, ? extends T> max = l ->
                l.stream().max(comparator).orElse(null);
        return getResult(threadCount, values, max, max);
    }

    /**
     * Returns minimum value.
     *
     * @param threadCount   number or concurrent threads
     * @param values        values to get minimum of
     * @param comparator    value comparator
     * @param <T>           value type
     *
     * @return minimum of given values
     *
     * @throws InterruptedException     if executing thread was interrupted
     * @throws NoSuchElementException   if no values are given
     */
    @Override
    public <T> T minimum(
            final int threadCount,
            final List<? extends T> values,
            final Comparator<? super T> comparator
    ) throws InterruptedException {
        return maximum(threadCount, values, comparator.reversed());
    }

    /**
     * Returns whether any of values satisfies predicate.
     *
     * @param threadCount   number or concurrent threads
     * @param values        values to test
     * @param predicate     test predicate
     * @param <T>           value type
     *
     * @return whether any value satisfies predicate or {@code false}, if no values are given
     *
     * @throws InterruptedException if executing thread was interrupted
     */
    @Override
    public <T> boolean any(
            final int threadCount,
            final List<? extends T> values,
            final Predicate<? super T> predicate
    ) throws InterruptedException {
        return !all(threadCount, values, predicate.negate());
    }

    /**
     * Returns whether all values satisfy predicate.
     *
     * @param threadCount   number or concurrent threads
     * @param values        values to test
     * @param predicate     test predicate
     * @param <T>           value type
     *
     * @return whether all values satisfy predicate or {@code true}, if no values are given
     *
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean all(
            final int threadCount,
            final List<? extends T> values,
            final Predicate<? super T> predicate
    ) throws InterruptedException {
        return getResult(
                threadCount,
                values,
                l -> l.stream().allMatch(predicate),
                l -> l.stream().allMatch(Boolean::booleanValue)
        );
    }

    private <T, R> R getResult(
            final int possibleThreadCount,
            final List<? extends T> values,
            final Function<List<? extends T>, ? extends R> onChunk,
            final Function<List<? extends R>, ? extends R> onResult
    ) throws InterruptedException {
        if (values.isEmpty()) {
            throw new NoSuchElementException("Value list to get the value on is empty.");
        }

        final List<? extends List<? extends T>> chunks = distribute(possibleThreadCount, values);

        if (parallelMapper != null) {
            return onResult.apply(parallelMapper.map(onChunk, chunks));
        }

        final List<R> chunkResults = new ArrayList<>(Collections.nCopies(chunks.size(), null));
        final List<Thread> threads = IntStream.range(0, chunks.size()).mapToObj(i -> {
            final Thread thread = new Thread(() ->
                    chunkResults.set(i, onChunk.apply(chunks.get(i)))
            );
            thread.start();
            return thread;
        }).toList();

        for (Thread thread : threads) {
            thread.join();
        }

        return onResult.apply(chunkResults);
    }

    private <U> List<List<U>> distribute(final int availableThreadCount, final List<U> values) {
        final int threadCount = Math.max(1, Math.min(availableThreadCount, values.size()));

        final int div = values.size() / threadCount;
        final int mod = values.size() % threadCount;

        return IntStream.range(0, threadCount).mapToObj(i -> values.subList(
                i * div + Math.min(i, mod),
                i * div + Math.min(i, mod) + div + (mod > i ? 1 : 0))
        ).toList();
    }
}
