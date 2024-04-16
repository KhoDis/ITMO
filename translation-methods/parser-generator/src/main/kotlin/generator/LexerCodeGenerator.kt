package generator

import Grammar

class LexerCodeGenerator(val grammar: Grammar, packageName: String) :
    AbstractCodeGenerator(grammar, packageName) {
    private val lexer = "${grammar.name}Lexer"
    private val token = "${grammar.name}Token"
    private val tokenType = "${grammar.name}TokenType"
    private val node = "${grammar.name}Node"

    override fun generateClass(): String {
        return """
            |import java.util.regex.Matcher
            |import java.util.regex.Pattern
            |
            |class $lexer(input: CharSequence) {
            |    class LexerException(message: String) : Exception(message)
            |    val tokens = mutableListOf<Triple<$tokenType, Matcher, Boolean>>()
            |    val length = input.length
            |    var pos = 0
            |
            |    init {
            |${generateInit().prependIndent(tab(2))}
            |    }
            |
            |    fun nextToken(): $token {
            |        for ((token, matcher, skip) in tokens) {
            |            if (matcher.region(pos, length).lookingAt()) {
            |                pos = matcher.end()
            |                return if (!skip) $token(token, matcher.group()) else nextToken()
            |            }
            |        }
            |        throw LexerException("Unexpected token at " + pos)
            |    }
            |}
        """.trimMargin()
    }

    private fun generateInit(): String {
        return """
            |${
            terminalRules.joinToString("\n") {
                "tokens.add(Triple($tokenType.${it.name}, Pattern.compile(${it.literal}).matcher(input), ${it.skip}))"
            }
        }
            |tokens.add(Triple($tokenType.END, Pattern.compile("${'$'}").matcher(input), false))
        """.trimMargin()
    }
}