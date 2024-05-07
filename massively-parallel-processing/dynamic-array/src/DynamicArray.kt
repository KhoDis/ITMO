package mpp.dynamicarray

import kotlinx.atomicfu.*

interface DynamicArray<E> {
    /**
     * Returns the element located in the cell [index],
     * or throws [IllegalArgumentException] if [index]
     * exceeds the [size] of this array.
     */
    fun get(index: Int): E

    /**
     * Puts the specified [element] into the cell [index],
     * or throws [IllegalArgumentException] if [index]
     * exceeds the [size] of this array.
     */
    fun put(index: Int, element: E)

    /**
     * Adds the specified [element] to this array
     * increasing its [size].
     */
    fun pushBack(element: E)

    /**
     * Returns the current size of this array,
     * it increases with [pushBack] invocations.
     */
    val size: Int
}

class DynamicArrayImpl<E> : DynamicArray<E> {
    private val core = atomic(Core<E>(INITIAL_CAPACITY))

    override fun get(index: Int): E = core.value.get(index)

    override fun put(index: Int, element: E) = core.value.put(index, element)

    override fun pushBack(element: E) {
        while (true) {
            val core = core.value
            if (!core.tryPushBack(element)) {
                this.core.compareAndSet(core, core.grow())
                continue
            }
            return
        }
    }

    override val size: Int
        get() {
            return core.value.size
        }
}

private class Core<E>(
    capacity: Int,
) {
    private val array = atomicArrayOfNulls<Slot<E>>(capacity)
    private val _size = atomic(0)
    private val next = atomic<Core<E>?>(null)

    private sealed class Slot<E>
    private class Valued<E>(val value: E) : Slot<E>()
    private class Moving<E>(val value: E) : Slot<E>()
    private class Closed<E> : Slot<E>()

    fun get(index: Int): E {
        require(index in 0 until size) { "Index $index is out of bounds [0, ${size - 1}]" }

        val slot = array[index].value!!
        if (slot is Closed<E>) {
            return next.value!!.get(index)
        }

        if (slot is Valued<E>) {
            return slot.value
        }
        if (slot is Moving<E>) {
            return slot.value
        }
        throw IllegalStateException("Unexpected value: $slot")
    }

    fun put(index: Int, element: E) {
        require(index in 0 until size) { "Index $index is out of bounds [0, ${size - 1}]" }

        while (true) {
            val slot = array[index].value!!
            if (slot is Closed<E>) {
                next.value!!.put(index, element)
                break
            }

            if (slot is Valued<E>) {
                if (array[index].compareAndSet(slot, Valued(element))) {
                    break
                }
            }
            if (slot is Moving<E>) {
                next.value!!.array[index].compareAndSet(null, Valued(slot.value))
                array[index].compareAndSet(slot, Closed())
            }
        }
    }

    fun tryPushBack(value: E): Boolean {
        while (true) {
            val size = _size.value
            return if (size != array.size) {
                if (!array[size].compareAndSet(null, Valued(value))) {
                    _size.compareAndSet(size, size + 1)
                    continue
                }
                _size.compareAndSet(size, size + 1)
                true
            } else false
        }
    }

    val size: Int
        get() {
            return _size.value
        }

    fun grow(): Core<E> {
        if (next.value == null) {
            next.compareAndSet(null, Core<E>(array.size * 2).also { it._size.value = size })
        }

        repeat(size) {
            while (true) {
                val slot = array[it].value!!

                if (slot is Closed) {
                    break
                }

                if (slot is Valued<E>) {
                    array[it].compareAndSet(slot, Moving(slot.value))
                } else if (slot is Moving<E>) {
                    next.value!!.array[it].compareAndSet(null, Valued(slot.value))
                    array[it].compareAndSet(slot, Closed())
                }
            }
        }
        return next.value!!
    }
}

private const val INITIAL_CAPACITY = 1 // DO NOT CHANGE ME