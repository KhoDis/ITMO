package info.kgeorgiy.ja.khodzhayarov.implementor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

/**
 * Generates a class java code with the Impl suffix that implements the specified interface.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 */
public class OldImplementor implements Impler {
    /**
     * Generates a class java code with the Impl suffix that implements the interface
     * using {@link Class token} type token to the path specified by {@link Path root directory}.
     *
     * @param token             type token to create implementation for
     * @param root              root directory
     * @throws ImplerException  if something happened during implementing interfaces.
     *                          The error message will be in {@link Throwable#getMessage()}
     */
    @Override
    public void implement(final Class<?> token, final Path root) throws ImplerException {
        validateToken(token);

        try (BufferedWriter writer = Files.newBufferedWriter(
                getOutputPath(root, token, Keywords.SOURCE_FORMAT),
                StandardCharsets.UTF_8
        )) {
            writer.write(toUnicode(new Generator(token).generate()));
        } catch (final IOException e) {
            throw new ImplerException("Can't write to output file: " + e.getMessage());
        }
    }

    /**
     * Rewrites unicode characters in \\uXXXX format.
     * Escapes \\uXXXX characters to prevent code injecting.
     *
     * @param string string to encode
     *
     * @return unicode converted string
     */
    private String toUnicode(final String string) {
        final int asciiSize = 128;
        return string.chars()
                .mapToObj(c -> c < asciiSize ? Character.toString(c) : String.format("\\u%04x", c))
                .collect(Collectors.joining());
    }

    /**
     * Checks if token is interface and not this interface is not private.
     *
     * @param token type token for validness checking
     *
     * @throws ImplerException if the given token is not interface or the interface is private
     */
    protected void validateToken(final Class<?> token) throws ImplerException {
        if (!token.isInterface()) {
            throw new ImplerException("Interface expected as token.");
        }

        if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Not private interface expected as token.");
        }
    }

    /**
     * Generates an output path for the given root, class name and file format.
     * Creates parent directories if they do not exist yet.
     *
     * @param root          a directory where output file is going to be located
     * @param token         type token that is going to be located
     * @param fileFormat    format of the file that is going to be located
     *
     * @return full output {@link Path path to the file}
     *
     * @throws ImplerException if directories could not be created
     */
    protected Path getOutputPath(
            final Path root,
            final Class<?> token,
            final String fileFormat
    ) throws ImplerException {
        final String directory = token.getPackageName().replace('.', Keywords.FILE_SEPARATOR);

        final String className = token.getSimpleName() + Keywords.CLASS_NAME_SUFFIX;
        final String file = className + Keywords.FILE_FORMAT_SEPARATOR + fileFormat;

        final Path outputPath = root.resolve(directory).resolve(file);
        createParentDirectories(outputPath);
        return outputPath;
    }

    /**
     * Creates parent directories if they do not exist yet.
     *
     * @param outputPath the path whose parents are to be created
     *
     * @throws ImplerException if directories could not be created
     */
    private void createParentDirectories(final Path outputPath) throws ImplerException {
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (final IOException e) {
            throw new ImplerException("Could not create root directories: ", e);
        }
    }
}
