import java.io.File
import generator.*
import calculator.*
import logic.*

class ASTPrinter {
    fun print(node: LogicNode, indent: String = "") {
        println("$indent${node.name}")
        node.children.forEach {
            print(it, indent + "  ")
        }
    }

}

enum class Mode {
    GENERATE,
    CALCULATOR,
    LOGIC,
}

fun main(args: Array<String>) {
    when (Mode.valueOf(args[0].uppercase())) {
        Mode.GENERATE -> {
            generateAll()
        }

        Mode.CALCULATOR -> {
            while (true) {
                print("Enter expression: ")
                val input = readLine() ?: break
                try {
                    val calculatorLexer = CalculatorLexer(input)
                    try {
                        val calculatorParser = CalculatorParser(calculatorLexer)
                        val result = calculatorParser.parse()
                        println("Result: ${result.res}")
                    } catch (e: CalculatorParser.ParserException) {
                        println(e.message)
                        continue
                    }
                } catch (e: CalculatorLexer.LexerException) {
                    println(e.message)
                    continue
                }
            }
        }

        Mode.LOGIC -> {
            while (true) {
                print("Enter expression: ")
                val input = readLine() ?: break
                try {
                    val logicLexer = LogicLexer(input)
                    try {
                        val logicParser = LogicParser(logicLexer)
                        val result = logicParser.parse()
                        ASTPrinter().print(result)
                    } catch (e: LogicParser.ParserException) {
                        println(e.message)
                        continue
                    }
                } catch (e: LogicLexer.LexerException) {
                    println(e.message)
                    continue
                }
            }
        }

        else -> {
            println("Unknown mode")
        }
    }
}

private fun generateAll() {
    generate("Logic")
    generate("Calculator")
}

private fun generate(name: String) {
    val packageName = name.lowercase()
    val builder = GrammarBuilder()
    val grammar = builder.build("src/main/resources/$name.meta")
    println(grammar)
    println("First: ")
    grammar.first.forEach { (t, u) ->
        println("$t: $u")
    }
    println()
    println("Follow: ")
    grammar.follow.forEach { (t, u) ->
        println("$t: $u")
    }

    val dir = "src/main/meta/$packageName/"
    File(dir).mkdirs()
    File("$dir${name}TokenType.kt").writeText(
        TokenTypeCodeGenerator(
            grammar,
            packageName
        ).generate()
    )
    File("$dir${name}Token.kt").writeText(
        TokenCodeGenerator(
            grammar,
            packageName
        ).generate()
    )
    File("$dir${name}Node.kt").writeText(
        NodeCodeGenerator(
            grammar,
            packageName
        ).generate()
    )
    File("$dir${name}Lexer.kt").writeText(
        LexerCodeGenerator(
            grammar,
            packageName
        ).generate()
    )
    File("$dir${name}Parser.kt").writeText(
        ParserCodeGenerator(
            grammar,
            packageName
        ).generate()
    )
}
