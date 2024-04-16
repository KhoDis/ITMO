package expression.operations.binary;

import expression.GenericExpression;
import expression.math.MathMode;

public class Subtract extends BinaryOperation {

    public Subtract(GenericExpression operand1, GenericExpression operand2) {
        super(operand1, operand2, "-");
    }

    @Override
    protected <N extends Number> N apply(MathMode<N> math, N first, N second) {
        return math.subtract(first, second);
    }
    
}
