package generator

import Grammar

class NodeCodeGenerator(grammar: Grammar, packageName: String) :
    AbstractCodeGenerator(grammar, packageName) {
    private val className = "${grammarName}Node"

    override fun generateClass(): String {
        return """
            |open class $className(val name: String) {
            |    val children = mutableListOf<$className>()
            |}
        """.trimMargin()
    }
}