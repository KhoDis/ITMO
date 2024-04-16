package info.kgeorgiy.ja.khodzhayarov.implementor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Generates {@link String} properly formatted implementation for interface.
 * Class with one usable method {@link Generator#generate()}.
 * Uses {@link Keywords keywords} for implementation.
 *
 * @author Adis Khodzhayarov (khodzhayarov.a@gmail.com
 */
public final class Generator {
    private final Class<?> token;

    /**
     * Constructor with token parameter.
     *
     * @param token type token that accepts only an interface
     */
    public Generator(Class<?> token) {
        this.token = token;
    }

    /**
     * Main method for generating properly formatted with preset parameters.
     *
     * @return properly formatted {@link String} representation of the implementation
     */
    public String generate() {
        final StringBuilder code = new StringBuilder();

        if (token.getPackage() != null) {
            code.append(getPackageDeclaration(token));
            code.append(Keywords.NEW_LINE);
        }

        code.append(getClassDeclaration(token))
                .append(Keywords.SPACE)
                .append(Keywords.OPEN_CURLY_BRACKET);

        code.append(Keywords.NEW_LINE);

        code.append(getMethods(token)).append(Keywords.NEW_LINE);

        code.append(Keywords.CLOSE_CURLY_BRACKET);

        return code.toString();
    }

    /**
     * Generates the first line of the code.
     * Specifies the package where implementation is located.
     *
     * @param token type token that being implemented
     *
     * @return package declaration. Example: {@code package java.lang;}
     */
    private String getPackageDeclaration(final Class<?> token) {
        return String.join(
                "",
                Keywords.PACKAGE,
                Keywords.SPACE,
                token.getPackage().getName(),
                Keywords.SEMICOLON,
                Keywords.NEW_LINE);
    }

    /**
     * Generates class declaration of the code.
     * Specifies the class name with {@link Keywords#CLASS_NAME_SUFFIX suffix} and its parent.
     *
     * @param token type token that being implemented
     *
     * @return class declaration. Example: {@code public MapImpl implements java.util.Map}
     */
    private String getClassDeclaration(final Class<?> token) {
        return String.join(Keywords.SPACE,
                Keywords.PUBLIC,
                Keywords.CLASS,
                token.getSimpleName() + Keywords.CLASS_NAME_SUFFIX,
                Keywords.IMPLEMENTS,
                token.getCanonicalName());
    }

    /**
     * Generates overridden methods of the code.
     * Specifies the methods implementations for non-private parent's methods.
     *
     * @param token type token that being implemented
     *
     * @return method implementations.
     */
    private String getMethods(final Class<?> token) {
        final List<String> methods = new ArrayList<>();
        for (final Method method : token.getMethods()) {
            if (!Modifier.isPrivate(method.getModifiers())) {
                methods.add(getMethod(method));
            }
        }
        return methods.stream().collect(Collectors.joining(Keywords.NEW_LINE + Keywords.NEW_LINE));
    }

    /**
     * Generates overridden method of the code.
     * Specifies the method implementation with correct return type.
     *
     * @param method method that being implemented
     *
     * @return method implementation.
     */
    private String getMethod(final Method method) {
        final StringBuilder code = new StringBuilder();

        code.append(String.join("",
                Keywords.TAB,
                Keywords.TAB,
                Keywords.PUBLIC,
                Keywords.SPACE,
                method.getReturnType().getCanonicalName(),
                Keywords.SPACE,
                method.getName(),
                getParameters(method.getParameters()),
                Keywords.SPACE,
                Keywords.OPEN_CURLY_BRACKET,
                Keywords.NEW_LINE
        ));

        if (!method.getReturnType().equals(void.class)) {
            code.append(String.join("",
                    Keywords.TAB, Keywords.TAB,
                    Keywords.RETURN, Keywords.SPACE,
                    getDefaultValue(method.getReturnType()), Keywords.SEMICOLON, Keywords.NEW_LINE
            ));
        }

        code.append(Keywords.TAB).append(Keywords.CLOSE_CURLY_BRACKET);

        return code.toString();
    }

    /**
     * Generates default type of the return type.
     * Specifies the default value of type token.
     *
     * @param returnType type token for generating default value
     *
     * @return default value. Example: {@code false}
     */
    private String getDefaultValue(final Class<?> returnType) {
        if (returnType.isPrimitive()) {
            return returnType.equals(boolean.class) ? Keywords.BOOLEAN_DEFAULT : Keywords.NUMBER_DEFAULT;
        } else {
            return Keywords.CLASS_DEFAULT;
        }
    }

    /**
     * Generates parameters of the method.
     * It specifies types and names of the method.
     *
     * @param parameters parameters for generating
     *
     * @return implemented parameters. Example: {@code (String var1, Map var2)}
     */
    private String getParameters(final Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(parameter ->
                        parameter.getType().getCanonicalName() + " " + parameter.getName()
                ).collect(Collectors.joining(
                        Keywords.DELIMITER, Keywords.OPEN_BRACKET, Keywords.CLOSE_BRACKET
                ));
    }

    public Class<?> token() {
        return token;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Generator) obj;
        return Objects.equals(this.token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "Generator[" +
                "token=" + token + ']';
    }

}
