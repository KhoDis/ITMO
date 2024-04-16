package expression.math;

public class FloatMath implements MathMode<Float> {

    @Override
    public Float add(Float first, Float second) {
        return first + second;
    }

    @Override
    public Float subtract(Float first, Float second) {
        return first - second;
    }

    @Override
    public Float multiply(Float first, Float second) {
        return first * second;
    }

    @Override
    public Float divide(Float first, Float second) {
        return first / second;
    }

    @Override
    public Float negate(Float value) {
        return -value;
    }

    @Override
    public Float valueOf(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public Float valueOf(Number value) {
        return value.floatValue();
    }
    
}
