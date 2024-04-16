/**
 * В теле класса решения разрешено использовать только переменные делегированные в класс RegularInt.
 * Нельзя volatile, нельзя другие типы, нельзя блокировки, нельзя лазить в глобальные переменные.
 *
 * @author Khodzhayarov Adis
 */
class Solution : MonotonicClock {
    private var c1 by RegularInt(0)
    private var c2 by RegularInt(0)
    private var c3 by RegularInt(0)

    private var r1 by RegularInt(0)
    private var r2 by RegularInt(0)
    private var r3 by RegularInt(0)

    override fun write(time: Time) {
        r1 = time.d1
        r2 = time.d2
        r3 = time.d3

        c3 = time.d3
        c2 = time.d2
        c1 = time.d1
    }

    override fun read(): Time {
        val a1 = c1
        val a2 = c2

        val b3 = r3
        val b2 = r2
        val b1 = r1

        return when {
            a1 == b1 && a2 == b2 -> Time(b1, b2, b3)
            a1 == b1 -> Time(b1, b2, 0)
            else -> Time(b1, 0, 0)
        }
    }
}