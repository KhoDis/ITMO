package mpp.linkedlistset

import kotlinx.atomicfu.*

class Case<E : Comparable<E>>(var curr: Node<E>, var next: Node<E>) {
    override fun toString() = "Window(curr=$curr, next=$next)"
}

class LinkedListSet<E : Comparable<E>> {
    private val first = Node<E>(/* prev = null, */ element = null, next = null)
    private val last = Node<E>(/* prev = first, */ element = null, next = null)
    init {
        first.setNext(last)
    }

    private val head = atomic(first)

    fun compareE(a: E, b: E): Int {
//        if (a == null && b != null) return -1 // a < b
//        if (a != null && b == null) return 1 // a > b
//        if (a == null && b == null) return 0 // a == b
        return a.compareTo(b) // a != null && b != null
    }

    private fun compareE(a: Node<E>, b: E): Int {
        if (a === first) return -1 // a < b
        if (a === last) return 1 // a > b
        return compareE(a.element, b)
    }

    fun findCase(element: E): Case<E> {
        while (true) {
            val head = head.value
            val case = Case(curr = head, next = head.next!!)
            var succ = true
            while (compareE(case.next, element) < 0) {
                val nn = case.next.next!!

                if (!nn.removed) {
                    case.curr = case.next
                    case.next = nn
                    continue
                }
                if (case.curr.casNext(case.next, nn)) {
                    case.next = nn
                    continue
                }
                succ = false
                break
            }
            if (succ) return case
        }
    }

    /**
     * Adds the specified element to this set
     * if it is not already present.
     *
     * Returns `true` if this set did not
     * already contain the specified element.
     */
    fun add(element: E): Boolean {
        while (true) {
            val case = findCase(element)
            val nn = case.next.next
            if (nn != null) {
                if (!nn.removed && compareE(case.next, element) == 0) {
                    return false
                }
                if (case.curr.casNext(case.next, Node(/* prev = case.curr, */ element = element, next = case.next))) {
                    return true
                }
                continue
            }
            val node = Node<E>(/* prev = case.curr, */ element = element, next = case.next)
            if (case.curr.casNext(case.next, node)) {
                case.next.setNext(node)
                return true
            }
        }
    }

    /**
     * Removes the specified element from this set
     * if it is present.
     *
     * Returns `true` if this set contained
     * the specified element.
     */
    fun remove(element: E): Boolean {
        while (true) {
            val case = findCase(element)
            if (compareE(case.next, element) == 0) {
                val nn = case.next.next ?: return false
                if (nn.removed) {
                    return false
                }
                if (case.next.casRemoved(false, true)) {
                    case.curr.casNext(case.next, nn)
                    return true
                }
                continue
            }
            return false
        }
    }

    /**
     * Returns `true` if this set contains
     * the specified element.
     */
    fun contains(element: E): Boolean {
        val case = findCase(element)
        val nn = case.next.next
        return nn != null && !nn.removed && compareE(case.next, element) == 0
    }
}

class Node<E : Comparable<E>>(/* prev: Node<E>?, */ element: E?, next: Node<E>?) {
    private val _element = element // `null` for the first and the last nodes
    val element get() = _element!!

//    private val _prev = atomic(prev)
//    val prev get() = _prev.value
//    fun setPrev(value: Node<E>?) {
//        _prev.value = value
//    }
//    fun casPrev(expected: Node<E>?, update: Node<E>?) =
//        _prev.compareAndSet(expected, update)

    private val _next = atomic(next)
    val next get() = _next.value
    fun setNext(value: Node<E>?) {
        _next.value = value
    }
    fun casNext(expected: Node<E>?, update: Node<E>?) =
        _next.compareAndSet(expected, update)

    private val _removed = atomic(false)
    val removed get() = _removed.value
    fun setRemoved(value: Boolean) {
        _removed.value = value
    }
    fun casRemoved(expected: Boolean, update: Boolean) =
        _removed.compareAndSet(expected, update)
}