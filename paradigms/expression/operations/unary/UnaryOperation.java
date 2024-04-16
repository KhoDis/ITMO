package expression.operations.unary;

import expression.GenericExpression;
import expression.math.MathMode;

public abstract class UnaryOperation implements GenericExpression {

    private final GenericExpression operand;
    private final String operator;

    protected UnaryOperation(GenericExpression operand, String operator) {
        this.operand = operand;
        this.operator = operator;
    }

    @Override
    public <N extends Number> N evaluate(MathMode<N> math, N x, N y, N z) {
        return apply(math, operand.evaluate(math, x, y, z));
    }

    protected abstract <N extends Number> N apply(MathMode<N> math, N value);

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (obj != null && obj.getClass() == this.getClass()) {
            UnaryOperation that = (UnaryOperation) obj;
            return that.operand.equals(this.operand);
        }
        return false;
    }

    @Override
    public String toString() {
        return operator + "(" + operand + ")";
    }

    @Override
    public int hashCode() {
        return operand.hashCode() + 18;
    }
    
}
