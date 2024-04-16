package expression;

import expression.math.IntMath;
import expression.math.MathMode;

public interface GenericExpression extends TripleExpression {
    <N extends Number> N evaluate(MathMode<N> math, N x, N y, N z);

    @Override
    default int evaluate(int x, int y, int z) {
        return evaluate(new IntMath(), x, y, z);
    }
}
