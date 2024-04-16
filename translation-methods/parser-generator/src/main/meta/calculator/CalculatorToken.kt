package calculator
class CalculatorToken(val type: CalculatorTokenType, val value: String) : CalculatorNode(type.name + ": " + value)