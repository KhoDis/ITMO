package info.kgeorgiy.ja.khodzhayarov.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T>, RandomAccess {
    private final List<T> elements;
    private final Comparator<? super T> comparator;

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(Collection<? extends T> elements) {
        this(elements, null);
    }

    public ArraySet(Collection<? extends T> elements, Comparator<? super T> comparator) {
        SortedSet<T> sortedSet = new TreeSet<>(comparator);
        sortedSet.addAll(elements);
        this.elements = new ArrayList<>(sortedSet);
        this.comparator = comparator;
    }

    public T get(int index) {
        Objects.checkIndex(index, elements.size());
        return elements.get(index);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return elements.get(0);
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return elements.get(elements.size() - 1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return Collections.binarySearch(elements, Objects.requireNonNull((T) o), comparator) >= 0;
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @SuppressWarnings("unchecked")
    private int compare(T fromElement, T toElement) {
        return comparator == null
                ? ((Comparable<? super T>) fromElement).compareTo(toElement)
                : comparator.compare(fromElement, toElement);
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    public SortedSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        Objects.requireNonNull(fromElement);
        Objects.requireNonNull(toElement);

        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
        }
        return subList(fromElement, fromInclusive, toElement, toInclusive);
    }

    private SortedSet<T> subList(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        return new ArraySet<>(elements.subList(
                        upperIndex(fromElement, fromInclusive),
                        lowerIndex(toElement, toInclusive) + 1
        ), comparator);
    }

    private int lowerIndex(T toElement, boolean toInclusive) {
        return getIndex(toElement, 1, toInclusive ? 0 : -1);
    }

    private int upperIndex(T fromElement, boolean fromInclusive) {
        return getIndex(fromElement, 0, fromInclusive ? 0 : 1);
    }

    private int getIndex(T element, int notFoundOffset, int foundOffset) {
        int index = Collections.binarySearch(elements, Objects.requireNonNull(element), comparator);

        return index < 0 ? -(index + notFoundOffset + 1) : index + foundOffset;
    }

    @Override
    public SortedSet<T> tailSet(T element) {
        return tailSet(element, true);
    }

    public SortedSet<T> tailSet(T fromElement, boolean toInclusive) {
        return isEmpty() ? this : borderSubset(fromElement, last(), toInclusive);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return headSet(toElement, false);
    }

    public SortedSet<T> headSet(T toElement, boolean toInclusive) {
        return isEmpty() ? this : borderSubset(first(), toElement, toInclusive);
    }

    private SortedSet<T> borderSubset(T fromElement, T toElement, boolean toInclusive) {
        if (compare(fromElement, toElement) > 0) {
            return empty();
        }
        return subSet(fromElement, true, toElement, toInclusive);
    }

    private ArraySet<T> empty() {
        return new ArraySet<>(Collections.emptyList(), comparator);
    }
}
