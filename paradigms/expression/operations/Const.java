package expression.operations;

import expression.GenericExpression;
import expression.math.MathMode;

public class Const implements GenericExpression {
    
    private final String value;

    public Const(final String value) {
        this.value = value;
    }

    @Override
    public <N extends Number> N evaluate(MathMode<N> math, N x, N y, N z) {
        return math.valueOf(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Const) {
            Const that = (Const) obj;
            return that.value.equals(this.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
