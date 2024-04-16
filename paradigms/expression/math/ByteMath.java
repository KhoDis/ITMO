package expression.math;

public class ByteMath implements MathMode<Byte> {

    @Override
    public Byte add(Byte first, Byte second) {
        return (byte) (first + second);
    }

    @Override
    public Byte subtract(Byte first, Byte second) {
        return (byte) (first - second);
    }

    @Override
    public Byte multiply(Byte first, Byte second) {
        return (byte) (first * second);
    }

    @Override
    public Byte divide(Byte first, Byte second) {
        if (second == 0) {
            throw new DivisionByZeroException(first);
        }
        return (byte) (first / second);
    }

    @Override
    public Byte negate(Byte value) {
        return (byte) -value;
    }

    @Override
    public Byte valueOf(String value) {
        return Byte.parseByte(value);
    }

    @Override
    public Byte valueOf(Number value) {
        return value.byteValue();
    }

}
