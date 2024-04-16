package info.kgeorgiy.ja.khodzhayarov.implementor;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

/**
 * The implementor's main entry point.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 **/
public class Main {
    /**
     * Main method for {@link OldImplementor} and {@link Implementor}.
     * Possible usage: {@code [-jar] <class-name> [output-path]}
     * With {@code -jar} key, the program will execute
     * {@link Implementor#implementJar(Class, Path)} method from {@link JarImpler}.
     * Without {@code -jar} key, the program will execute
     * {@link OldImplementor#implement(Class, Path)} method from
     * {@link info.kgeorgiy.java.advanced.implementor.Impler}.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        if (!isArgListValid(args)) {
            return;
        }

        final JarImpler implementor = new Implementor();

        try {
            final ImplementorArgs implementorArgs = ImplementorArgs.parse(args);
            final int argCountWithJarKey = 3;
            final ImplerConsumer<Class<?>, Path> impler = args.length == argCountWithJarKey
                    ? implementor::implementJar
                    : implementor::implement;
            impler.implement(
                    Class.forName(implementorArgs.getToken()),
                    Path.of(implementorArgs.getPath())
            );
        } catch (final ClassNotFoundException e) {
            System.err.println("Invalid class name: " + e.getMessage());
        } catch (final InvalidPathException e) {
            System.err.println("Invalid path: " + e.getMessage());
        } catch (final ImplerException e) {
            System.err.println("Could not create file implementation: " + e.getMessage());
        }
    }

    /**
     * Checks input args from main method.
     *
     * @param args command line arguments
     *
     * @return  {@code true}
     *          if the number of arguments is less than 1, more than 3 or each argument non-null
     *          {@code false} otherwise
     */
    private static boolean isArgListValid(final String[] args) {
        final int minArgCount = 1;
        final int maxArgCount = 3;

        if (args == null || args.length < minArgCount || args.length > maxArgCount) {
            System.err.println("Unexpected amount of arguments. Possible usage:");
            System.err.println("[-jar] <class-name> [output-path]");
            return false;
        }
        for (String arg : args) {
            if (arg == null) {
                System.err.println("Unexpected null in the arg list");
                return false;
            }
        }

        if (args.length == maxArgCount && !args[0].equals("-jar")) {
            System.err.println(
                    "First argument should be '-jar'. Actual argument: <" + args[0] + ">"
            );
            return false;
        }

        return true;
    }


    /**
     * Represents an operation that accepts two input arguments and returns no result.
     * This is a specialization of {@link java.util.function.BiConsumer} with ImplerException.
     * This is a functional interface whose functional method is {@link #implement(Object, Object)}.
     *
     * @param <T> the type of the first argument to the operation
     * @param <U> the type of the second argument to the operation
     *
     * @see java.util.function.BiConsumer
     *
     * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
     */
    @FunctionalInterface
    public interface ImplerConsumer<T, U> {
        /**
         * Function of the functional interface.
         *
         * @param t first argument
         * @param u second argument
         *
         * @throws ImplerException exception for making ImplerConsumer exist
         */
        void implement(T t, U u) throws ImplerException;
    }

    /**
     * A utility class containing a token and a path.
     *
     * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
     */
    private static class ImplementorArgs {
        String token;
        String path;

        /**
         * Creates an {@link ImplementorArgs instance} with token and path.
         *
         * @param token class name
         * @param path  path to the class
         */
        private ImplementorArgs(final String token, final String path) {
            this.token = token;
            this.path = path;
        }

        /**
         * Gets {@link ImplementorArgs#token token} field.
         *
         * @return {@link ImplementorArgs#token token} field
         */
        public String getToken() {
            return token;
        }

        /**
         * Gets {@link ImplementorArgs#path path} field.
         *
         * @return {@link ImplementorArgs#path path} field
         */
        public String getPath() {
            return path;
        }

        /**
         * Factory method that creates {@link ImplementorArgs instances}.
         * Depends on string args from {@link #main(String[])}.
         *
         * @param args argument list to parse
         *
         * @return {@link ImplementorArgs} instances containing proper token and path strings
         *
         * @throws ImplerException if arguments are not properly checked on validness
         */
        private static ImplementorArgs parse(final String[] args) throws ImplerException {
            final int onlyToken = 1;
            final int tokenAndPath = 2;
            final int tokenAndPathWithJarKey = 3;
            return switch (args.length) {
                case onlyToken -> new ImplementorArgs(args[0], Keywords.HOME_DIRECTORY.toString());
                case tokenAndPath -> new ImplementorArgs(args[0], args[1]);
                case tokenAndPathWithJarKey -> new ImplementorArgs(args[1], args[2]);
                default -> throw new ImplerException("Invalid arguments.");
            };
        }
    }
}
