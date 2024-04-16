package info.kgeorgiy.ja.khodzhayarov.implementor;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Compiler that generates {@code .class} files.
 * Uses {@link JavaCompiler} and {@link ToolProvider} to compile with the specified root.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Compiler {
    private final Class<?> token;
    private final Path path;

    /**
     * Creates an instance of {@code Compiler}.
     *
     * @param token class that needs to be compiled
     * @param path  directory where {@code .java} file is being located
     */
    public Compiler(final Class<?> token, final Path path) {
        this.token = token;
        this.path = path;
    }

    /**
     * Compiles classes with the specified root.
     *
     * @throws CompilerException    if {@link ToolProvider#getSystemJavaCompiler()} is null
     *                              or exit code of the compiler is not {@code 0}
     */
    public void compile() throws CompilerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new CompilerException(
                    "Could not find java compiler, include tools.jar to classpath"
            );
        }
        final String file = getFile().toString();
        final String classpath = path + File.pathSeparator + getClassPath();
        final String[] args = {file, "-cp", classpath};
        final int exitCode = compiler.run(null, null, null, args);
        if (exitCode != 0) {
            throw new CompilerException("Compiler exit code: " + exitCode);
        }
    }

    /**
     * Resolves file name by root directory and the class given.
     *
     * @return resolved path to the file
     */
    private Path getFile() {
        return path.resolve(getImplName().replace(".", File.separator) + ".java").toAbsolutePath();
    }

    /**
     * Gets implementation name of the token. Example: {@code java.lang.MapImpl}
     *
     * @return implementation name
     */
    private String getImplName() {
        return token.getPackageName() + "." + token.getSimpleName() + "Impl";
    }

    /**
     * Locates path of the source where file will be stored.
     *
     * @return path of the source.
     *
     * @throws CompilerException if class path could not parse
     */
    private String getClassPath() throws CompilerException {
        try {
            return Path.of(
                    token.getProtectionDomain().getCodeSource().getLocation().toURI()
            ).toString();
        } catch (final URISyntaxException e) {
            throw new CompilerException(e);
        }
    }
}
