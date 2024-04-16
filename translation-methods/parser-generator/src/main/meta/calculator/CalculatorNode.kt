package calculator
open class CalculatorNode(val name: String) {
    val children = mutableListOf<CalculatorNode>()
}