package calculator
import java.util.regex.Matcher
import java.util.regex.Pattern

class CalculatorLexer(input: CharSequence) {
    class LexerException(message: String) : Exception(message)
    val tokens = mutableListOf<Triple<CalculatorTokenType, Matcher, Boolean>>()
    val length = input.length
    var pos = 0

    init {
        tokens.add(Triple(CalculatorTokenType.NUMBER, Pattern.compile("\\d+").matcher(input), false))
        tokens.add(Triple(CalculatorTokenType.PLUS, Pattern.compile("\\+").matcher(input), false))
        tokens.add(Triple(CalculatorTokenType.MINUS, Pattern.compile("-").matcher(input), false))
        tokens.add(Triple(CalculatorTokenType.MUL, Pattern.compile("\\*").matcher(input), false))
        tokens.add(Triple(CalculatorTokenType.LOG, Pattern.compile("\\\\\\\\").matcher(input), false))
        tokens.add(Triple(CalculatorTokenType.DIV, Pattern.compile("/").matcher(input), false))
        tokens.add(Triple(CalculatorTokenType.LBR, Pattern.compile("\\(").matcher(input), false))
        tokens.add(Triple(CalculatorTokenType.RBR, Pattern.compile("\\)").matcher(input), false))
        tokens.add(Triple(CalculatorTokenType.WS, Pattern.compile("[ \\t\\n\\r]+").matcher(input), true))
        tokens.add(Triple(CalculatorTokenType.END, Pattern.compile("$").matcher(input), false))
    }

    fun nextToken(): CalculatorToken {
        for ((token, matcher, skip) in tokens) {
            if (matcher.region(pos, length).lookingAt()) {
                pos = matcher.end()
                return if (!skip) CalculatorToken(token, matcher.group()) else nextToken()
            }
        }
        throw LexerException("Unexpected token at " + pos)
    }
}