package expression.operations.binary;

import expression.GenericExpression;
import expression.math.MathMode;

public abstract class BinaryOperation implements GenericExpression {
    
    private final GenericExpression operand1, operand2;
    private final String operator;

    protected BinaryOperation(GenericExpression operand1, GenericExpression operand2, String operator) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operator = operator;
    }

    @Override
    public <N extends Number> N evaluate(MathMode<N> math, N x, N y, N z) {
        return apply(math, operand1.evaluate(math, x, y, z), operand2.evaluate(math, x, y, z));
    }

    protected abstract <N extends Number> N apply(MathMode<N> math, N first, N second);

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (obj != null && obj.getClass() == this.getClass()) {
            BinaryOperation that = (BinaryOperation) obj;
            return that.operand1.equals(this.operand2) && that.operand2.equals(this.operand2);
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + operand1 + " " + operator + " " + operand2 + ")";
    }

    @Override
    public int hashCode() {
        return (operand1.hashCode() + 3) * (operand2.hashCode() - 100) + (operator.hashCode() + 48);
    }

}
