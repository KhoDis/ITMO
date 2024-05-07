import java.util.concurrent.atomic.AtomicReference

class Solution(private val env: Environment) : Lock<Solution.Node> {
    private val tail = AtomicReference<Node>()

    override fun lock(): Node {
        val my = Node() // сделали узел
        val prev = tail.getAndSet(my)
        if (prev != null) {
            prev.next.set(my)
            while (my.waiting.get()) {
                env.park()
            }
        }
        return my // вернули узел
    }

    override fun unlock(node: Node) {
        if (node.next.get() == null) {
            if (tail.compareAndSet(node, null)) {
                return
            }
            while (node.next.get() == null) {
            }
        }
        node.next.get().waiting.set(false)
        env.unpark(node.next.get().thread)
    }

    class Node {
        val thread: Thread = Thread.currentThread() // запоминаем поток, которые создал узел
        val next = AtomicReference<Node>()
        val waiting = AtomicReference(true)
    }
}