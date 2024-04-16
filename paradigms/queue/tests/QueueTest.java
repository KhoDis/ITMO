package queue.tests;

import java.util.Arrays;

import queue.ArrayQueue;
import queue.LinkedQueue;
import queue.Queue;

public class QueueTest {
    public static void fill(Queue queue) {
        for (int i = 0; i < 10; i++) {
            queue.enqueue(i);
        }
    }

    public static void dump(Queue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.size() + " " + queue.element() + " " + queue.dequeue());
        }
    }

    public static void main(String[] args) {
        Queue linkedQueue = new LinkedQueue();
        Queue arrayQueue = new ArrayQueue();
        
        fill(linkedQueue);
        fill(arrayQueue);
        
        System.out.println("Linked:");
        System.out.println(Arrays.toString(linkedQueue.toArray()));
        dump(linkedQueue);

        System.out.println("Array:");
        System.out.println(Arrays.toString(arrayQueue.toArray()));
        dump(arrayQueue);
    }
}
