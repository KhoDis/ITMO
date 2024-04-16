package expression.math;

import expression.ExpressionException;

@SuppressWarnings("serial")
public class DivisionByZeroException extends ExpressionException {
    public DivisionByZeroException(Number first) {
        super("Division by zero: " + first.longValue() + " / 0");
    }
}
