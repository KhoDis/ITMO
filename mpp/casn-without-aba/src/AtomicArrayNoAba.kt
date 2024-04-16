import kotlinx.atomicfu.*

class AtomicArrayNoAba<E>(size: Int, initialValue: E) {
    val a = atomicArrayOfNulls<Any>(size)

    init {
        for (i in 0 until size) a[i].value = initialValue
    }

    fun get(index: Int): E = a[index].loop {
        if (it !is AtomicArrayNoAba<*>.CAS2Descriptor) return it as E
        it.complete()
    }

    fun cas(index: Int, expected: E, update: E) =
        a[index].compareAndSet(expected, update)

    fun cas2(index1: Int, expected1: E, update1: E,
             index2: Int, expected2: E, update2: E): Boolean {
        return when {
            index1 == index2 -> cas(index1, expected1, (expected1 as Int + 2) as E)
            index1 < index2 -> cas2InOrder(index1, expected1, update1, index2, expected2, update2)
            else -> cas2InOrder(index2, expected2, update2, index1, expected1, update1)
        }
    }

    enum class Outcome {
        UNDECIDED, SUCCESS, FAILURE
    }

    private fun cas2InOrder(index1: Int, expected1: E, update1: E,
                            index2: Int, expected2: E, update2: E): Boolean {
        val descriptor = CAS2Descriptor(index1, expected1, update1, index2, expected2, update2)

        while (true) {
            when (val first = a[index1].value) {
                is AtomicArrayNoAba<*>.CAS2Descriptor -> first.complete()

                else -> return if (a[index1].compareAndSet(descriptor.expected1, descriptor)) {
                    descriptor.complete()
                    descriptor.outcome.value == Outcome.SUCCESS
                } else false
            }
        }
    }

    inner class CAS2Descriptor(val index1: Int, val expected1: E, val update1: E,
                               val index2: Int, val expected2: E, val update2: E) {
        val outcome = atomic(Outcome.UNDECIDED)

        fun complete() {
            while (outcome.value == Outcome.UNDECIDED) {
                when (val second = a[index2].value) {
                    this -> outcome.compareAndSet(Outcome.UNDECIDED, Outcome.SUCCESS)
                    is AtomicArrayNoAba<*>.CAS2Descriptor -> second.complete()
                    expected2 -> a[index2].compareAndSet(expected2, this)
                    else -> outcome.compareAndSet(Outcome.UNDECIDED, Outcome.FAILURE)
                }
            }
            when (outcome.value) {
                Outcome.FAILURE -> {
                    a[index1].compareAndSet(this, expected1)
                    a[index2].compareAndSet(this, expected2)
                    return
                }
                Outcome.SUCCESS -> {
                    a[index1].compareAndSet(this, update1)
                    a[index2].compareAndSet(this, update2)
                    return
                }
                else -> throw IllegalStateException()
            }
        }
    }
}