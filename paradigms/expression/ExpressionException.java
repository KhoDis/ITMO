package expression;

@SuppressWarnings("serial")
public class ExpressionException extends RuntimeException {
    final String reason;
	public ExpressionException(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return reason;
    }
}