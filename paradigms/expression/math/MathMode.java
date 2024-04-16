package expression.math;

public interface MathMode<N extends Number>
{
    N add(N first, N second);
    N subtract(N first, N second);
    N multiply(N first, N second);
    N divide(N first, N second);
    N negate(N value);
    
    N valueOf(Number value);
    N valueOf(String value);
}   