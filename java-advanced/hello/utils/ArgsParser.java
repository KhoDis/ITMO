package info.kgeorgiy.ja.khodzhayarov.hello.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Main args parser.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 */
public class ArgsParser {
    private final String[] args;
    private final String[] names;
    private int step;

    /**
     * Constructs parser depends on names.
     *
     * @param args args to parse
     * @param names expected argument names
     *
     * @throws ArgsParserException if any of the arguments are null
     */
    public ArgsParser(final String[] args, final String... names) throws ArgsParserException {
        if (args == null) {
            throw new ArgsParserException("Args can't be null");
        }
        if (args.length != names.length || Arrays.asList(args).contains(null)) {
            throw new ArgsParserException(String.join(" ",
                    "Incorrect usage.",
                    "Expected usage:",
                    Arrays.stream(names)
                            .map(s -> "[" + s + "]")
                            .collect(Collectors.joining(" "))
            ));
        }

        this.args = args.clone();
        this.names = names;
        this.step = 0;
    }

    /**
     * Parses string to int and moves further.
     *
     * @return int argument
     *
     * @throws ArgsParserException if int parsing failed
     */
    public int getInt() throws ArgsParserException {
        try {
            return Integer.parseInt(args[step]);
        } catch (final NumberFormatException e) {
            throw new ArgsParserException(names[step] + " must be a number: " + e.getMessage(), e);
        } finally {
            step++;
        }
    }

    /**
     * Returns string from args and moves further.
     *
     * @return string argument
     */
    public String getString() {
        return args[step++];
    }

    @SuppressWarnings("unused")
    public static class ArgsParserException extends Exception {
        public ArgsParserException() {
        }

        public ArgsParserException(String message) {
            super(message);
        }

        public ArgsParserException(String message, Throwable cause) {
            super(message, cause);
        }

        public ArgsParserException(Throwable cause) {
            super(cause);
        }
    }
}
