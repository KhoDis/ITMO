package generator

import Grammar

// abstract class that has Grammar as a field
abstract class AbstractCodeGenerator(
    private val grammar: Grammar,
    private val packageName: String
) : CodeGenerator {
    val terminalRules = grammar.terminalRules
    val nonTerminalRules = grammar.nonTerminalRules
    val startRule = grammar.startRule
    val terminalRuleByName = grammar.terminalRuleByName
    val nonTerminalRuleByName = grammar.nonTerminalRuleByName
    val first = grammar.first
    val follow = grammar.follow
    val grammarName = grammar.name
    val tab = "    "

    override fun generate(): String {
        // generate package
        return """
        |package $packageName
        |
        """.trimMargin() + generateClass()
    }

    fun tab(n: Int): String {
        return tab.repeat(n)
    }

    abstract fun generateClass(): String
}