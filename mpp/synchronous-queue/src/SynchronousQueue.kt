import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * An element is transferred from sender to receiver only when [send] and [receive]
 * invocations meet in time (rendezvous), so [send] suspends until another coroutine
 * invokes [receive] and [receive] suspends until another coroutine invokes [send].
 */
class SynchronousQueue<E> {
    private val sentinel = Action.Dummy<E>()
    private val head = atomic<Action<E>>(sentinel)
    private val tail = atomic<Action<E>>(sentinel)

    sealed class Action<E>(val next: AtomicRef<Action<E>?> = atomic(null)) {
        class Send<E>(val cont: Continuation<Unit>, val element: E) : Action<E>()
        class Receive<E>(val cont: Continuation<E>) : Action<E>()
        class Dummy<E> : Action<E>()
    }

    /**
     * Sends the specified [element] to this channel, suspending if there is no waiting
     * [receive] invocation on this channel.
     */
    suspend fun send(element: E) {
        while (true) {
            val head = head.value
            val tail = tail.value

            if (head === tail || tail is Action.Send<E>) {
                suspend<Unit>(tail) { Action.Send(it, element) } ?: continue
                return
            }

            if (head.next.value !is Action.Receive<E>) {
                continue
            }

            val next = head.next.value as Action.Receive<E>
            if (this.head.compareAndSet(head, next)) {
                next.cont.resume(element)
                return
            }
        }
    }

    /**
     * Retrieves and removes an element from this channel if there is a waiting [send] invocation on it,
     * suspends the caller if this channel is empty.
     */
    suspend fun receive(): E {
        while (true) {
            val head = head.value
            val tail = tail.value

            if (head === tail || tail is Action.Receive<E>) {
                return suspend<E>(tail) { Action.Receive(it) } ?: continue
            }

            if (head.next.value !is Action.Send<E>) {
                continue
            }

            val next = head.next.value as Action.Send<E>
            if (this.head.compareAndSet(head, next)) {
                next.cont.resume(Unit)
                return next.element
            }
        }
    }

    suspend fun <T> suspend(
        tail: Action<E>,
        `continue`: (Continuation<T>) -> Action<E>
    ): T? {
        return suspendCoroutine sc@{ cont ->
            val next = tail.next.value
            if (next != null) {
                this.tail.compareAndSet(tail, next)
                cont.resume(null)
                return@sc
            }

            val update = `continue`(cont)
            if (tail.next.compareAndSet(null, update)) {
                this.tail.compareAndSet(tail, update)
            } else {
                cont.resume(null)
            }
            return@sc
        }
    }
}