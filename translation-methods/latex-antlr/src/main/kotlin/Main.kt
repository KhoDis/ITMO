import org.antlr.v4.runtime.*
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    println("resources directory: ${System.getProperty("user.dir")}")
    val resources = Paths.get("src", "main", "resources")
    val input = Paths.get(resources.toString(), "test.tex")
    println("input file: $input")
    val lexer = TeXLexer(CharStreams.fromPath(input))
    val tokens = CommonTokenStream(lexer)
    val parser = TeXParser(tokens)
    val tree = parser.document()
    val visitor = HTMLVisitor()
    val html = visitor.visit(tree)
    println(html)
    val output = Paths.get(resources.toString(), "test.html")
    println("output file: $output")
    Files.write(output, html.toByteArray())
}
