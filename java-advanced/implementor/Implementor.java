package info.kgeorgiy.ja.khodzhayarov.implementor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

/**
 * Generates a {@code .jar} file using {@link OldImplementor#implement(Class, Path)}.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 */
public class Implementor extends OldImplementor implements JarImpler {
    /**
     * Anonymous visitor that deletes every file on its way using {@link SimpleFileVisitor}.
     */
    private static final SimpleFileVisitor<Path> DELETE_VISITOR = new SimpleFileVisitor<>() {
        /**
         * Visits file, removes it and continues.
         *
         * @param file  file to remove
         * @param attrs ignored
         *
         * @return {@link FileVisitResult#CONTINUE CONTINUE} visiting state
         *
         * @throws IOException if file could not be deleted
         */
        @Override
        public FileVisitResult visitFile(
                final Path file, final BasicFileAttributes attrs
        ) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * Visits directory, removes it and continues.
         *
         * @param dir directory to remove
         * @param exc ignored
         *
         * @return {@link FileVisitResult#CONTINUE CONTINUE} visiting state
         *
         * @throws IOException if directory could not be deleted
         */
        @Override
        public FileVisitResult postVisitDirectory(
                final Path dir, final IOException exc
        ) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    /**
     * Generates a .jar file with the implementation of the corresponding interface
     * using {@link Class token} type token to the path specified by {@link Path jarFile}.
     *
     * @param token             type token to create implementation for
     * @param jarFile           path to jarFile to create
     * @throws ImplerException  if something happened during implementing interfaces.
     *                          The error message will be in {@link Throwable#getMessage()}
     *
     * @see <a href="https://docs.oracle.com/javase/tutorial/deployment/jar/">Jar File</a>
     */
    @Override
    public void implementJar(final Class<?> token, final Path jarFile) throws ImplerException {
        validateToken(token);

        final Path tempDirectory = createTempDirectory();
        implement(token, tempDirectory);
        new Compiler(token, tempDirectory).compile();
        copyCompiledFilesToJar(tempDirectory, jarFile, token);
        clean(tempDirectory);
    }

    /**
     * Cleans every file on its way from the root directory.
     *
     * @param root directory to remove
     *
     * @throws ImplerException if walker could not remove file or directories on its way
     */
    public static void clean(final Path root) throws ImplerException {
        if (Files.exists(root)) {
            try {
                Files.walkFileTree(root, DELETE_VISITOR);
            } catch (final IOException e) {
                throw new ImplerException("Could not remove temporary files: " + e.getMessage());
            }
        }
    }

    /**
     * Copies compiled file specified by token from temporary directory to jarFile.
     * Uses {@link JarOutputStream}.
     *
     * @param tempDirectory directory where class files are located
     * @param jarFile .jar file, where class file will be
     * @param token type token to write into .jar file
     *
     * @throws ImplerException if {@link JarOutputStream} could not write to .jar file
     */
    private void copyCompiledFilesToJar(
            final Path tempDirectory,
            final Path jarFile,
            final Class<?> token
    ) throws ImplerException {
        final String compiledPath = getOutputPath(
                Paths.get(""), token, Keywords.COMPILED_FORMAT
        ).toString();

        try (JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(jarFile))) {
            outputStream.putNextEntry(new ZipEntry(
                    compiledPath.replace(Keywords.FILE_SEPARATOR, '/')
            ));
            Files.copy(tempDirectory.resolve(compiledPath), outputStream);
        } catch (final IOException e) {
            throw new ImplerException("Could not put entry to the output file: " + e.getMessage());
        }
    }

    /**
     * Creates temporary directory to store compiled classes.
     *
     * @return temporary directory path
     *
     * @throws ImplerException if file could not be created
     */
    private Path createTempDirectory() throws ImplerException {
        try {
            return Files.createTempDirectory(Keywords.HOME_DIRECTORY, "Temp");
        } catch (final IOException e) {
            throw new ImplerException("Could not create temp directory: ", e);
        }
    }
}
