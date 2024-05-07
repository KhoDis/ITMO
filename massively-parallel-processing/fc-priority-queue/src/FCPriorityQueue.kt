import kotlinx.atomicfu.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class FCPriorityQueue<E : Comparable<E>> {
    class Task<E : Comparable<E>>(private val function: () -> E?) {
        var finished = false
        var result: E? = null

        fun execute() {
            result = function.invoke()
            finished = true
        }
    }

    private val length = 8
    private val fcArray = atomicArrayOfNulls<Task<E>>(length)
    private val q = PriorityQueue<E>()
    private val locked = atomic(false)
    private fun tryLock() = locked.compareAndSet(false, true)

    private fun unlock() {
        locked.value = false
    }

    /**
     * Retrieves the element with the highest priority
     * and returns it as the result of this function;
     * returns `null` if the queue is empty.
     */
    fun poll(): E? {
        return operate(Task(q::poll))
    }

    /**
     * Returns the element with the highest priority
     * or `null` if the queue is empty.
     */
    fun peek(): E? {
        return operate(Task(q::peek))
    }

    /**
     * Adds the specified element to the queue.
     */
    fun add(element: E) {
        operate(Task { q.add(element); null })
    }

    private fun operate(task: Task<E>): E? {
        val i = findFree(task)

        while (true) {
            if (!tryLock()) {
                if (!task.finished) {
                    continue
                }
                return task.result
            }
            if (!task.finished) {
                fcArray[i].getAndSet(null)?.execute()
            }
            repeat(length) {
                fcArray[it].getAndSet(null)?.execute()
            }
            unlock()
            return task.result
        }
    }

    private fun findFree(task: Task<E>): Int {
        var i: Int
        do {
            i = ThreadLocalRandom.current().nextInt(length)
        } while (!fcArray[i].compareAndSet(null, task))
        return i
    }
}