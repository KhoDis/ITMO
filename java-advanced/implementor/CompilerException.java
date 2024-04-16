package info.kgeorgiy.ja.khodzhayarov.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

/**
 * Signals that a compiler exception of some sort has occurred. This
 * class is the general class of exceptions produced by {@link Compiler}.
 *
 * @author  Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 * @see     Compiler
 */
public class CompilerException extends ImplerException {
    /**
     * Constructs a {@link CompilerException} with no detail message.
     */
    public CompilerException() {
    }

    /**
     * Constructs a {@link CompilerException} with the specified detail message.
     *
     * @param message the detail message
     */
    public CompilerException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@link CompilerException} with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public CompilerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a {@link CompilerException} with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public CompilerException(final Throwable cause) {
        super(cause);
    }
}
