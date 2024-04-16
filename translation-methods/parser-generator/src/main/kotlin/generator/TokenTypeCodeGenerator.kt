package generator

import Grammar

class TokenTypeCodeGenerator(grammar: Grammar, packageName: String) :
    AbstractCodeGenerator(grammar, packageName) {
    private val className = "${grammarName}TokenType"
    private val terminals = terminalRules.map { it.name.uppercase() }

    override fun generateClass(): String {
        return """
            |enum class $className {
            |    ${terminals.joinToString(",\n$tab")},
            |    END,
            |    EPS
            |}
        """.trimMargin()
    }
}