package expression.math;

public class DoubleMath implements MathMode<Double> {

    @Override
    public Double add(Double first, Double second) {
        return first + second;
    }

    @Override
    public Double subtract(Double first, Double second) {
        return first - second;
    }

    @Override
    public Double multiply(Double first, Double second) {
        return first * second;
    }

    @Override
    public Double divide(Double first, Double second) {
        return first / second;
    }

    @Override
    public Double negate(Double value) {
        return -value;
    }

    @Override
    public Double valueOf(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public Double valueOf(Number value) {
        return value.doubleValue();
    }
    
}
