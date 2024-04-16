package info.kgeorgiy.ja.khodzhayarov.implementor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Keywords and utils for generating implementations.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com)
 */
public class Keywords {
    public static final String CLASS_NAME_SUFFIX = "Impl";

    public static final String FILE_FORMAT_SEPARATOR = ".";
    public static final String SOURCE_FORMAT = "java";
    public static final String COMPILED_FORMAT = "class";

    public static final String NEW_LINE = System.lineSeparator();
    public static final String OPEN_CURLY_BRACKET = "{";
    public static final String CLOSE_CURLY_BRACKET = "}";
    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";
    public static final String SPACE = " ";
    public static final String SEMICOLON = ";";
    public static final String TAB = "    ";
    public static final String DELIMITER = ", ";

    public static final String PACKAGE = "package";
    public static final String PUBLIC = "public";
    public static final String CLASS = "class";
    public static final String IMPLEMENTS = "implements";
    public static final String RETURN = "return";

    public static final String CLASS_DEFAULT = "null";
    public static final String BOOLEAN_DEFAULT = "false";
    public static final String NUMBER_DEFAULT = "0";
    public static final char FILE_SEPARATOR = File.separatorChar;

    public static final Path HOME_DIRECTORY = Paths.get(".").toAbsolutePath().normalize();
}
