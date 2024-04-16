package queue;

import java.util.Arrays;

/*
 * Model:
 *     {a[0], a[1], ..., a[n - 1]}
 *     {e[(0 + o) % c], e[(1 + o) % c], ..., e[(n-1 + o) % c]}
 *     n -- queue size 
 * 
 * e - element
 * n - size
 * a - queue
 * 
 * Inv: a != null && forall i: a[i] == elements[(i + offset) % capacity] (cyclic shift)
 * Immutable: n == n' && forall i = 1..n: a[i] == a'[i]
 */
public class ArrayQueueOld {
    private int size = 0;
    private int offset = 0;
    Object[] elements = new Object[5];

    // Pred: e != null
    // Post: n = n' + 1 && a[n'] == e && forall i = 1..n': a[i] == a'[i]
    public void enqueue(Object element) {
        assert element != null;

        ensureCapacity(size + 1);
        // a[n'] = e
        elements[(offset + size) % elements.length] = element;
        size++;
    }

    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] copy = new Object[2 * capacity];
            for (int i = 0; i < size; i++) {
                copy[i] = elements[(offset + i) % elements.length];
            }
            elements = copy;
            offset = 0;
        }
    }

    // Pred: n > 0
    // Post: n == n' - 1 && forall i = 1..n: a[i] == a'[i + 1] && r == a'[0]
    public Object dequeue() {
        assert size > 0;

        Object value = element();
        // a[0] == null
        elements[offset] = null;

        // forall i = 1..n: a[i] == a'[i + 1]
        offset = (offset + 1) % elements.length;
        size--;

        return value;
    }

    // Pred: n > 0
    // Post: Immutable && r == a[0]
    public Object element() {
        assert size > 0;

        // a[0]
        return elements[offset];
    }

    // Pred: true
    // Post: Immutable && r == n
    public int size() {
        return size;
    }

    // Pred: true
    // Post: Immutable && r == (n == 0)
    public boolean isEmpty() {
        return size == 0;
    }

    // Pred: true
    // Post: n = 0 && a - empty
    public void clear() {
        size = 0;
        offset = 0;
        elements = new Object[5];
    }

    // Pred: n > 0
    // Post: Immutable && forall i: b[i] == a[i] && b.n == a.n
    public ArrayQueueOld makeCopy() {
        final ArrayQueueOld copy = new ArrayQueueOld();
        copy.size = size;
        copy.elements = Arrays.copyOf(elements, size);
        copy.offset = offset;
        return copy;
    }

    // Pred: true
    // Post: forall i: b[i] == a[i] && r == b && Immutable
    public Object[] toArray() {
        Object[] array = new Object[size];
        for (int i = 0; i < size; i++) {
            array[i] = element();
            enqueue(dequeue());
        }
        return array;
    }
}