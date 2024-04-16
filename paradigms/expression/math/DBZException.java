package expression.math;

import expression.ExpressionException;

@SuppressWarnings("serial")
public class DBZException extends ExpressionException {
    public DBZException(Number first) {
        super("Division by zero: " + first.longValue() + " / 0");
    }
}
