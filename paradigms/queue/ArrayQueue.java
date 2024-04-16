package queue;

public class ArrayQueue extends AbstractQueue {
    private final int INITIAL_CAPACITY = 3;

    private int offset;
    Object[] elements = new Object[INITIAL_CAPACITY];

    @Override
    protected void enqueueImpl(Object element) {
        ensureCapacity(size() + 1);
        elements[shift(size())] = element;
    }

    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] copy = new Object[2 * capacity];
            if (offset + size() > elements.length) {
                System.arraycopy(elements, offset, copy, 0, elements.length - offset);
                System.arraycopy(elements, 0, copy, elements.length - offset, size() - (elements.length - offset));
            } else {
                System.arraycopy(elements, offset, copy, 0, size());
            }
            elements = copy;
            offset = 0;
        }
    }

    @Override
    protected void dequeueImpl() {
        elements[offset] = null;
        offset = shift(1);
    }

    @Override
    protected Object elementImpl() {
        return elements[offset];
    }

    @Override
    protected void clearImpl() {
        offset = 0;
        elements = new Object[INITIAL_CAPACITY];
    }

    protected int shift(int steps) {
        return (offset + steps) % elements.length;
    }
}