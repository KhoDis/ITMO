package generator

import Alternative
import Arg
import Grammar
import Production
import Rule

class ParserCodeGenerator(val grammar: Grammar, packageName: String) :
    AbstractCodeGenerator(grammar, packageName) {
    private val parser = "${grammar.name}Parser"
    private val lexer = "${grammar.name}Lexer"
    private val token = "${grammar.name}Token"
    private val tokenType = "${grammar.name}TokenType"
    private val node = "${grammar.name}Node"

    fun toLowerCaseFirst(s: String): String {
        return s[0].lowercaseChar() + s.substring(1)
    }

    fun toUpperCaseFirst(s: String): String {
        return s[0].uppercaseChar() + s.substring(1)
    }

    fun generateArgs(args: List<Arg>): String {
        return args.joinToString(",\n") { "${it.name}: ${it.type}" }
    }

    fun generateArgsVal(args: List<Arg>): String {
        return args.joinToString(",\n") { "val ${it.name}: ${it.type}" }
    }

    fun generateContext(nonTerminal: Rule.NonTerminal): String {
        return "${toUpperCaseFirst(nonTerminal.name)}Context"
    }

    fun generateFun(nonTerminal: Rule.NonTerminal): String {
        return toLowerCaseFirst(nonTerminal.name)
    }

    override fun generateClass(): String {
        return """
            |import kotlin.properties.Delegates
            |
            |class $parser(private val lexer: $lexer) {
            |    class ParserException(message: String) : Exception(message)
            |    class TerminalNode(name: String, val text: String) : $node(name + ": " + text)
            |    var token = lexer.nextToken()
            |
            |    fun nextToken() {
            |        token = lexer.nextToken()
            |    }
            |
            |${generateFirstMethod().prependIndent(tab(1))}
            |
            |${generateNonTerminals().prependIndent(tab(1))}
            |}
        """.trimMargin()
    }

    fun generateFirstMethod(): String {
        return """
            |fun parse(
            |${generateArgs(startRule.args).prependIndent(tab(1))}
            |): ${generateContext(startRule)} {
            |    val res = ${generateFun(startRule)}()
            |
            |    if (token.type == $tokenType.END) {
            |        return res
            |    } else {
            |        throw ParserException("Expected END, got " + token.type)
            |    }
            |}
        """.trimMargin()
    }

    private fun generateContextClass(nonTerminal: Rule.NonTerminal): String {
        return """
            |class ${generateContext(nonTerminal)}(name: String,
            |${generateArgsVal(nonTerminal.args).prependIndent(tab(1))}
            |) : $node(name) {
            |${
            nonTerminal.body.alternatives.withIndex().joinToString("\n") { (ai, alt) ->
                alt.productions.withIndex().joinToString("\n") { (pi, prod) ->
                    val name = "${prod.name}_${ai}_${pi}"
                    when (prod) {
                        is Production.TerminalName -> """
                            |var $name: TerminalNode? = null
                            |${if (prod.alias != null) "var ${prod.alias}: TerminalNode? = null" else ""}
                        """.trimMargin()

                        is Production.NonTerminalName -> """
                            |var $name: ${toUpperCaseFirst(prod.name)}Context? = null
                            |${
                            if (prod.alias != null) "var ${prod.alias}: ${
                                toUpperCaseFirst(
                                    prod.name
                                )
                            }Context? = null" else ""
                        }
                        """.trimMargin()

                        is Production.Eps -> "// eps"
                    }
                }
            }
        }
            |   ${
            nonTerminal.returns.joinToString("\n") { ret ->
                "var ${ret.name}: ${ret.type} by Delegates.notNull<${ret.type}>()"
            }
        }
                |    }
        """.trimMargin()
    }


    private fun generateTerminalNamePart(
        production: Production.TerminalName,
        name: String
    ): String {
        val alias = production.alias
        return """
            |// Terminal $name
            |if (token.type != ${grammar.name}TokenType.${production.name}) {
            |    throw ParserException("Expected ${production.name}, got " + token.type)
            |}
            |val $name = TerminalNode("$name", token.value)
            |${if (alias != null) "val $alias = $name" else "// no alias"}
            |_localctx.$name = $name
            |${if (alias != null) "_localctx.$alias = $name" else "// no alias"}
            |_localctx.children.add($name)
            |nextToken()
            |
        """.trimMargin()
    }

    private fun generateNonTerminalNamePart(
        production: Production.NonTerminalName,
        name: String
    ): String {
        val alias = production.alias
        return """
            |// Non-terminal ${production.name}
            |val $name = ${toLowerCaseFirst(production.name)}(
            |${production.inCode.replace("$", "_localctx.")}
            |)
            |${if (alias != null) "val $alias = $name" else "// no alias"}
            |_localctx.$name = $name
            |${if (alias != null) "_localctx.$alias = $name" else "// no alias"}
            |_localctx.children.add($name)
            |
        """.trimMargin()
    }

    private fun generateEpsPart(): String {
        return """
            |// Eps
            |// do nothing
            |
        """.trimMargin()
    }

    private fun generateFunContent(nonTerminal: Rule.NonTerminal): String {
        return """
            |fun ${generateFun(nonTerminal)}(
            |    ${generateArgs(nonTerminal.args).prependIndent(tab(1))}
            |    ): ${generateContext(nonTerminal)} {
            |    val _localctx = ${generateContext(nonTerminal)}("${nonTerminal.name}",
            |    ${nonTerminal.args.joinToString(",\n") { it.name }.prependIndent(tab(1))}
            |    )
            |    when (token.type) {
            |   ${
            nonTerminal.body.alternatives.withIndex().joinToString("\n") { (ai, alternative) ->
                val first1 = grammar.makeFirstFirst(nonTerminal.name, alternative.productions)
                generateWhenBranch(first1, alternative, ai)
            }
        }
            |       else -> throw ParserException("Got " + token.type)
            |   }
            |}
            |
        """.trimMargin()
    }

    private fun generateWhenBranch(
        first1: Set<String>,
        alternative: Alternative,
        ai: Int
    ) = """
    |${generateWhenTokens(first1)} -> {
    |${
        alternative.productions.withIndex().joinToString("\n") { (pi, prod) ->
            """
            |${generateProd(prod, ai, pi)}
            |${prod.outCode.replace("$", "_localctx.")}
            """.trimMargin().prependIndent(tab(1))
        }
    }
    |   return _localctx
    |}
    |
    """.trimMargin()

    private fun generateWhenTokens(first1: Set<String>) = first1.joinToString(", ") {
        "${grammar.name}TokenType.${it}"
    }

    private fun generateProd(production: Production, ai: Int, pi: Int) = when (production) {
        is Production.TerminalName -> generateTerminalNamePart(
            production,
            "${production.name}_${ai}_${pi}"
        )

        is Production.NonTerminalName -> generateNonTerminalNamePart(
            production,
            "${production.name}_${ai}_${pi}"
        )

        is Production.Eps -> generateEpsPart()
    }

    private fun generateNonTerminals(): String {
        return nonTerminalRules.joinToString("\n\n") { nonTerminal ->
            """
                |${generateContextClass(nonTerminal)}
                |    
                |${generateFunContent(nonTerminal)}
            """.trimMargin()
        }
    }
}