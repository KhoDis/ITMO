package mpp.faaqueue

import kotlinx.atomicfu.*

private val turnstile = Any()

class FAAQueue<E> {
    private val head: AtomicRef<Segment> // Head pointer, similarly to the Michael-Scott queue (but the first node is _not_ sentinel)
    private val tail: AtomicRef<Segment> // Tail pointer, similarly to the Michael-Scott queue

    init {
        val firstNode = Segment()
        head = atomic(firstNode)
        tail = atomic(firstNode)
    }

    /**
     * Adds the specified element [x] to the queue.
     */
    fun enqueue(element: E) {
        while (true) {
            val curTail: Segment = tail.value
            val i: Long = curTail.enqIdx.getAndIncrement() // FAA
            if (i < SEGMENT_SIZE) {
                if (curTail.cas(i.toInt(), null, element)) {
                    return
                }
            }
            val newTail = Segment()
            newTail.put(0, element)
            newTail.enqIdx.getAndIncrement() // FAA
            if (curTail.next.compareAndSet(null, newTail)) {
                tail.compareAndSet(curTail, newTail)
                return
            }
            tail.compareAndSet(curTail, curTail.next.value!!)
        }
    }

    /**
     * Retrieves the first element from the queue and returns it;
     * returns `null` if the queue is empty.
     */
    fun dequeue(): E? {
        while (true) {
            val curHead = head.value
            val i = curHead.deqIdx.getAndIncrement() // FAA
            if (i < SEGMENT_SIZE && !curHead.cas(i.toInt(), null, turnstile)) {
                return curHead.get(i.toInt()) as E
            }
            val nextHead = curHead.next.value ?: return null
            head.compareAndSet(curHead, nextHead)
        }
    }

    val isEmpty: Boolean get() = head.value === tail.value && head.value.deqIdx.value == head.value.enqIdx.value
}

private class Segment {
    val next: AtomicRef<Segment?> = atomic(null)
    val elements = atomicArrayOfNulls<Any>(SEGMENT_SIZE)
    val deqIdx = atomic(0L)
    val enqIdx = atomic(0L)

    fun get(i: Int) = elements[i].value
    fun cas(i: Int, expect: Any?, update: Any?) = elements[i].compareAndSet(expect, update)
    fun put(i: Int, value: Any?) {
        elements[i].value = value
    }
}

const val SEGMENT_SIZE = 2 // DO NOT CHANGE, IMPORTANT FOR TESTS

