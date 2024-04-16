package expression.math;

import expression.ExpressionException;

public class CheckedIntMath extends IntMath {
    
    @Override
    public Integer add(final Integer first, final Integer second) throws CheckedIntMathException {
        final int result = first + second;
        if (((first ^ result) & (second ^ result)) < 0) {
            throw overflow();
        }
        return result;
    }

    @Override
    public Integer subtract(final Integer first, final Integer second) throws CheckedIntMathException {
        final int result = first - second;
        if (((first ^ second) & (first ^ result)) < 0) {
            throw overflow();
        }
        return result;
    }

    @Override
    public Integer multiply(final Integer first, final Integer second) throws CheckedIntMathException {
        int result = first * second;
        if (second != 0 && result / second != first || first == Integer.MIN_VALUE && second == -1) {
            throw overflow();
        }
        return result;
    }

    @Override
    public Integer divide(final Integer first, final Integer second) throws CheckedIntMathException {
        if (second == 0) {
            throw divisionByZero(first);
        }
        if (first == Integer.MIN_VALUE && second == -1) {
            throw overflow();
        }
        return first / second;
    }

    @Override
    public Integer negate(Integer value) throws CheckedIntMathException {
        if (value == Integer.MIN_VALUE) {
            throw overflow();
        }
        return -value;
    }

    protected CheckedIntMathException overflow() {
        return new CheckedIntMathException("Overflow");
    }

    protected CheckedIntMathException divisionByZero(Integer first) {
        return new CheckedIntMathException("Division by zero: " + first + " / 0");
    }

    @Override
    public Integer valueOf(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new CheckedIntMathException("Number is too big");
        }
    }

    @Override
    public Integer valueOf(Number value) {
        return value.intValue();
    }

    @SuppressWarnings("serial")
    public class CheckedIntMathException extends ExpressionException {
        public CheckedIntMathException(String message) {
            super(message);
        }
    }

}