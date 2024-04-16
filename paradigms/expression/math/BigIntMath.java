package expression.math;

import java.math.BigInteger;

public class BigIntMath implements MathMode<BigInteger> {

    @Override
    public BigInteger add(BigInteger first, BigInteger second) {
        return first.add(second);
    }

    @Override
    public BigInteger subtract(BigInteger first, BigInteger second) {
        return first.subtract(second);
    }

    @Override
    public BigInteger multiply(BigInteger first, BigInteger second) {
        return first.multiply(second);
    }

    @Override
    public BigInteger divide(BigInteger first, BigInteger second) {
        if (second.signum() == 0) {
            throw new DivisionByZeroException(first);
        }
        return first.divide(second);
    }

    @Override
    public BigInteger negate(BigInteger value) {
        return value.negate();
    }

    @Override
    public BigInteger valueOf(String value) {
        return new BigInteger(value);
    }

    @Override
    public BigInteger valueOf(Number value) {
        return new BigInteger(String.valueOf(value.longValue()));
    }

}