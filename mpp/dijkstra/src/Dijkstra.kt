package dijkstra

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import java.util.PriorityQueue
import java.util.concurrent.Phaser
import kotlin.Comparator
import kotlin.concurrent.thread
import kotlin.random.Random

private val NODE_DISTANCE_COMPARATOR = Comparator<Node> { o1, o2 -> Integer.compare(o1!!.distance, o2!!.distance) }

// Returns `Integer.MAX_VALUE` if a path has not been found.
fun shortestPathParallel(start: Node) {
    val workers = Runtime.getRuntime().availableProcessors()
    // The distance to the start node is `0`
    start.distance = 0
    val q = PriorityMultiQueue(workers, NODE_DISTANCE_COMPARATOR)
    q.add(start)
    val onFinish = Phaser(workers + 1)
    repeat(workers) {
        thread {
            while (true) {
                val cur: Node = q.poll() ?: if (q.workIsDone()) break else continue
                for (e in cur.outgoingEdges) {
                    while (true) {
                        val curDistance = e.to.distance
                        val newDistance = cur.distance + e.weight
                        if (curDistance <= newDistance) {
                            break
                        }
                        if (e.to.casDistance(curDistance, newDistance)) {
                            q.add(e.to)
                            break
                        }
                    }
                }
                q.decrement()
            }
            onFinish.arrive()
        }
    }
    onFinish.arriveAndAwaitAdvance()
}

class PriorityMultiQueue(var workers: Int, val comparator: Comparator<Node>) {
    private val active = atomic(0)
    private val data = Array(workers) { PriorityQueue(comparator) to ReentrantLock(true) }
    private val random = Random(0)

    init {
        if (workers < 2) {
            workers = 2
        }
    }

    fun add(node: Node) {
        active.incrementAndGet()
        while (true) {
            val i = random.nextInt(workers)
            val (queue, lock) = data[i]
            if (lock.tryLock()) {
                queue.add(node)
                lock.unlock()
                return
            }
        }
    }

    fun poll(): Node? {
        while (true) {
            val i1 = (0 until workers).random()
            var i2 = (0 until workers).random()
            while (i2 == i1) {
                i2 = (0 until workers).random()
            }
            val (queue1, lock1) = data[i1]
            val (queue2, lock2) = data[i2]
            if (!lock1.tryLock()) continue
            if (!lock2.tryLock()) {
                lock1.unlock()
                continue
            }

            val node1 = queue1.peek()
            val node2 = queue2.peek()

            when {
                node1 == null && node2 == null -> {
                    lock1.unlock()
                    lock2.unlock()
                    return null
                }

                node1 == null -> {
                    lock1.unlock()
                    val result = queue2.poll()
                    lock2.unlock()
                    return result
                }

                node2 == null -> {
                    lock2.unlock()
                    val result = queue1.poll()
                    lock1.unlock()
                    return result
                }

                else -> {
                    val result =
                        if (comparator.compare(
                                node1,
                                node2
                            ) < 0
                        ) queue1.poll() else queue2.poll()
                    lock1.unlock()
                    lock2.unlock()
                    return result
                }
            }
        }
    }

    fun decrement() {
        active.decrementAndGet()
    }

    fun workIsDone(): Boolean = active.value == 0
}