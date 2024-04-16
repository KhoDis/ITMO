package queue;

/*
 * Model:
 *     a[0], a[1], ..., a[n - 1]
 *     n -- queue size 
 * 
 * e - element
 * n - size
 * a - queue
 * 
 * Inv: a != null && forall i = 0..n-1: a[i] != null
 * Immutable: n == n' && forall i = 0..n-1: a[i] == a'[i]
 */
public interface Queue {
    // Pred: e != null
    // Post: n = n' + 1 && a[n'] == e && forall i = 0..n'-1: a[i] == a'[i]
    void enqueue(Object element);

    // Pred: n > 0
    // Post: n == n' - 1 && forall i = 0..n-1: a[i] == a'[i + 1] && r == a'[0]
    Object dequeue();

    // Pred: n > 0
    // Post: Immutable && r == a[0]
    Object element();

    // Pred: true
    // Post: Immutable && r == n
    int size();

    // Pred: true
    // Post: Immutable && r == (n == 0)
    boolean isEmpty();

    // Pred: true
    // Post: n = 0 && a - empty
    void clear();

    // Pred: true
    // Post: forall i: b[i] == a[i] && r == b && Immutable
    Object[] toArray();
}
