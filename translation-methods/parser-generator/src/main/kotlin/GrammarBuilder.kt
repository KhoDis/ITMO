import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

class GrammarBuilder {
    fun build(path: String): Grammar {
        val input = CharStreams.fromFileName(path)
        val lexer = MetaLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = MetaParser(tokens)
        val tree = parser.meta()
        val listener = MetaRulesListener()
        ParseTreeWalker.DEFAULT.walk(listener, tree)

        val name = tree.grammarName().name.text
        val rules = listener.rules

        return Grammar(name, rules)
    }
}