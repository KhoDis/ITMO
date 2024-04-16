package logic
class LogicToken(val type: LogicTokenType, val value: String) : LogicNode(type.name + ": " + value)