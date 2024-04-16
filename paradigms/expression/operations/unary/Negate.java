package expression.operations.unary;

import expression.GenericExpression;
import expression.math.MathMode;

public class Negate extends UnaryOperation {
    
    public Negate(final GenericExpression operand) {
        super(operand, "-");
    }

    @Override
    protected <N extends Number> N apply(MathMode<N> math, N value) {
        return math.negate(value);
    }

}
