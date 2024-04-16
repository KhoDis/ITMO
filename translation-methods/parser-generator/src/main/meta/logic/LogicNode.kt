package logic
open class LogicNode(val name: String) {
    val children = mutableListOf<LogicNode>()
}