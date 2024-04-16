package expression.math;

public class IntMath implements MathMode<Integer> {
    
    @Override
    public Integer add(Integer first, Integer second) {
        return first + second;
    }

    @Override
    public Integer subtract(Integer first, Integer second) {
        return first - second;
    }

    @Override
    public Integer multiply(Integer first, Integer second) {
        return first * second;
    }

    @Override
    public Integer divide(Integer first, Integer second) {
        if (second == 0) {
            throw new DivisionByZeroException(first);
        }
        return first / second;
    }

    @Override
    public Integer negate(Integer value) {
        return -value;
    }

    @Override
    public Integer valueOf(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Integer valueOf(Number value) {
        return value.intValue();
    }

}
