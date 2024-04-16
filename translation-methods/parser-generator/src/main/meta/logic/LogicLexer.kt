package logic
import java.util.regex.Matcher
import java.util.regex.Pattern

class LogicLexer(input: CharSequence) {
    class LexerException(message: String) : Exception(message)
    val tokens = mutableListOf<Triple<LogicTokenType, Matcher, Boolean>>()
    val length = input.length
    var pos = 0

    init {
        tokens.add(Triple(LogicTokenType.XOR, Pattern.compile("xor").matcher(input), false))
        tokens.add(Triple(LogicTokenType.OR, Pattern.compile("or").matcher(input), false))
        tokens.add(Triple(LogicTokenType.AND, Pattern.compile("and").matcher(input), false))
        tokens.add(Triple(LogicTokenType.NOT, Pattern.compile("not").matcher(input), false))
        tokens.add(Triple(LogicTokenType.LBR, Pattern.compile("\\(").matcher(input), false))
        tokens.add(Triple(LogicTokenType.RBR, Pattern.compile("\\)").matcher(input), false))
        tokens.add(Triple(LogicTokenType.VAR, Pattern.compile("[a-z]+").matcher(input), false))
        tokens.add(Triple(LogicTokenType.CONST, Pattern.compile("[0-9]+").matcher(input), false))
        tokens.add(Triple(LogicTokenType.WS, Pattern.compile("[ \\t\\r\\n]+").matcher(input), true))
        tokens.add(Triple(LogicTokenType.END, Pattern.compile("$").matcher(input), false))
    }

    fun nextToken(): LogicToken {
        for ((token, matcher, skip) in tokens) {
            if (matcher.region(pos, length).lookingAt()) {
                pos = matcher.end()
                return if (!skip) LogicToken(token, matcher.group()) else nextToken()
            }
        }
        throw LexerException("Unexpected token at " + pos)
    }
}