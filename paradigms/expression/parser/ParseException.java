package expression.parser;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("serial")
public class ParseException extends RuntimeException {
    public ParseException(final String message) {
        super(message);
    }
}
