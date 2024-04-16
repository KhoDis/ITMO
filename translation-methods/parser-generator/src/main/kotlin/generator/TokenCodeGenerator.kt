package generator

import Grammar

class TokenCodeGenerator(val grammar: Grammar, packageName: String) :
    AbstractCodeGenerator(grammar, packageName) {
    private val token = "${grammar.name}Token"
    private val tokenType = "${grammar.name}TokenType"
    private val node = "${grammar.name}Node"

    override fun generateClass(): String {
        return """
            |class $token(val type: $tokenType, val value: String) : $node(type.name + ": " + value)
        """.trimMargin()
    }
}