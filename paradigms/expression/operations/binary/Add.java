package expression.operations.binary;

import expression.GenericExpression;
import expression.math.MathMode;

public class Add extends BinaryOperation {

    public Add(GenericExpression operand1, GenericExpression operand2) {
        super(operand1, operand2, "+");
    }

    @Override
    protected <N extends Number> N apply(MathMode<N> math, N first, N second) {
        return math.add(first, second);
    }
    
}
