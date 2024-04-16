package queue.tests;

import queue.LinkedQueue;

public class LinkedQueueTest {
    public static void fill(LinkedQueue queue) {
        for (int i = 0; i < 10; i++) {
            queue.enqueue(i);
        }
    }

    public static void dump(LinkedQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.size() + " " + queue.element() + " " + queue.dequeue());
        }
    }

    public static void main(String[] args) {
        LinkedQueue queue = new LinkedQueue();
        fill(queue);
        dump(queue);
    }
}
