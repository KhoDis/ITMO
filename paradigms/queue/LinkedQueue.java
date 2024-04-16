package queue;

public class LinkedQueue extends AbstractQueue {
    private Node tail = null;
    private Node head = null;

    @Override
    public void enqueueImpl(Object element) {
        Node tail = new Node(element, null);
        if (isEmpty()) {
            head = tail;
        } else {
            this.tail.prev = tail;
        }
        this.tail = tail;
    }

    @Override
    protected void dequeueImpl() {
        head = head.prev;
    }

    @Override
    protected Object elementImpl() {
        return head.value;
    }

    @Override
    public void clearImpl() {
        head = null;
        tail = null;
    }

    private static class Node {
        private Object value;
        private Node prev;

        public Node(Object value, Node prev) {
            assert value != null;

            this.value = value;
            this.prev = prev;
        }
    }
}
