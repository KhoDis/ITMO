package expression.operations;

import expression.ExpressionException;
import expression.GenericExpression;
import expression.math.MathMode;

public class Variable implements GenericExpression {

    private final String var;

    public Variable(String var) {
        this.var = var;
    }

    @Override
    public <N extends Number> N evaluate(MathMode<N> math, N x, N y, N z) throws ExpressionException {
        return switch (var) {
            case "x" -> x;
            case "y" -> y;
            case "z" -> z;
            default -> throw new ExpressionException("Wrong variable names");
        };
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Variable) {
            Variable that = (Variable) obj;
            return that.var.equals(this.var);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return var.hashCode();
    }
    
}
