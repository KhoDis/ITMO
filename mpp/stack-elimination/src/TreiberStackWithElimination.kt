package mpp.stackWithElimination

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.atomicArrayOfNulls
import java.util.concurrent.ThreadLocalRandom

private const val MAXIMUM_TRIES = 4

class TreiberStackWithElimination<E> {
    class Slot<E>(var state: State, var value: E?) {
        enum class State {
            WAITING,
            BUSY,
        }
    }

    private val top = atomic<Node<E>?>(null)
    private val eliminationArray = atomicArrayOfNulls<Slot<E>>(ELIMINATION_ARRAY_SIZE)

    private fun normalPush(x: E) {
        while (true) {
            val oldTop = top.value
            val newTop = Node(x, oldTop)
            if (top.compareAndSet(oldTop, newTop)) return
        }
    }

    private fun normalPop(): E? {
        while (true) {
            val oldTop = top.value ?: return null
            val newTop = oldTop.next
            if (top.compareAndSet(oldTop, newTop)) return oldTop.x
        }
    }

    /**
     * Adds the specified element [x] to the stack.
     */
    fun push(x: E) {
        val random = ThreadLocalRandom.current()
        var i = random.nextInt(0, ELIMINATION_ARRAY_SIZE)

        for (j in 0 until MAXIMUM_TRIES) {
            if (!eliminationArray[i].compareAndSet(null, Slot(Slot.State.WAITING, x))) {
                i = (i + 1) % ELIMINATION_ARRAY_SIZE
                continue
            }

            wait(random)

            val slot = eliminationArray[i].value!!
            if (slot.state == Slot.State.WAITING && eliminationArray[i].compareAndSet(slot, null)) {
                normalPush(slot.value!!)
            } else {
                eliminationArray[i].value = null
            }
            return
        }
        normalPush(x)
    }

    private fun wait(random: ThreadLocalRandom) {
        repeat(100) {
            random.nextInt(0, 100)
        }
    }

    /**
     * Retrieves the first element from the stack
     * and returns it; returns `null` if the stack
     * is empty.
     */
    fun pop(): E? {
        var i = ThreadLocalRandom.current().nextInt(0, ELIMINATION_ARRAY_SIZE)
        for (j in 0 until MAXIMUM_TRIES) {
            val slot = eliminationArray[i].value
            if (
                slot != null &&
                slot.state == Slot.State.WAITING &&
                eliminationArray[i].compareAndSet(slot, Slot(Slot.State.BUSY, null))
            ) {
                return slot.value
            }
            i = (i + 1) % ELIMINATION_ARRAY_SIZE
        }
        return normalPop()
    }
}

private class Node<E>(val x: E, val next: Node<E>?)

private const val ELIMINATION_ARRAY_SIZE = 2 // DO NOT CHANGE IT