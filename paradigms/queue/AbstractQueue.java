package queue;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    @Override
    public void enqueue(Object element) {
        assert element != null;

        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);

    @Override
    public Object dequeue() {
        assert size > 0;

        Object result = element();
        dequeueImpl();
        size--;
        return result;
    }

    protected abstract void dequeueImpl();

    @Override
    public Object element() {
        assert size > 0;

        return elementImpl();
    }

    protected abstract Object elementImpl();

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        size = 0;
        clearImpl();
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        for (int i = 0; i < size; i++) {
            array[i] = element();
            enqueue(dequeue());
        }
        return array;
    }

    protected abstract void clearImpl();
}
