package queue;

/*
 * Model:
 *     {a[0], a[1], ..., a[n-1]}
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
public class ArrayQueueADT {
    private int size = 0;
    private int offset = 0;
    private Object[] elements = new Object[5];

    // Pred: e != null
    // Post: n = n' + 1 && a[n'] == e && forall i = 1..n': a[i] == a'[i]
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;

        ensureCapacity(queue, queue.size + 1);

        // a[n'] = e
        queue.elements[(queue.offset + queue.size) % queue.elements.length] = element;
        queue.size++;
    }

    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (capacity > queue.elements.length) {
            Object[] copy = new Object[2 * capacity];
            for (int i = 0; i < queue.size; i++) {
                copy[i] = queue.elements[(queue.offset + i) % queue.elements.length];
            }
            queue.elements = copy;
            queue.offset = 0;
        }
    }

    // Pred: n > 0
    // Post: n == n' - 1 && forall i = 1..n: a[i] == a'[i + 1] && r == a'[0]
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0;

        Object value = element(queue);

        // a[0] = null
        queue.elements[queue.offset] = null;

        // forall i = 1..n: a[i] == a'[i + 1]
        queue.offset = (queue.offset + 1) % queue.elements.length;
        queue.size--;

        return value;
    }

    // Pred: n > 0
    // Post: Immutable && r == a[0]
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0;

        // a[0]
        return queue.elements[queue.offset];
    }

    // Pred: true
    // Post: Immutable && r == n
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    // Pred: true
    // Post: Immutable && r == (n == 0)
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    // Pred: true
    // Post: n = 0 && a - empty
    public static void clear(ArrayQueueADT queue) {
        queue.elements = new Object[5];
        queue.size = 0;
        queue.offset = 0;
    }

    // Pred: true
    // Post: forall i: b[i] == a[i] && r == b && Immutable
    public static Object[] toArray(ArrayQueueADT queue) {
        Object[] array = new Object[queue.size];
        for (int i = 0; i < queue.size; i++) {
            array[i] = element(queue);
            enqueue(queue, dequeue(queue));
        }
        return array;
    }
}
