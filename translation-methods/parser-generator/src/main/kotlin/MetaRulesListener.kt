class MetaRulesListener : MetaBaseListener() {
    val typealiases = hashMapOf<String, Pair<String, String>>()
    val rules = mutableListOf<Rule>()

    override fun exitTypealias(ctx: MetaParser.TypealiasContext?) {
        val name = ctx!!.name.text
        val type = ctx.type.text
        val convert = ctx.OUTATTR().text
        typealiases[name] = Pair(type, convert.substring(1, convert.length - 1))
    }

    override fun exitTerminalRule(ctx: MetaParser.TerminalRuleContext) {
        val skip = ctx.skipe() != null
        rules.add(Rule.Terminal(ctx.name.text, ctx.literal.text, skip))
    }

    private fun format(input: String): String {
        // #T#text#
        val substring = input.substring(1, input.length - 1)
        if (substring.indexOf('#') != -1) {
            println("Formatting $substring")
            val open = substring.indexOf('#')
            val mid = substring.indexOf('#', open + 1)
            val close = substring.indexOf('#', mid + 1)
            val type = substring.substring(open + 1, mid)
            val convert = substring.substring(mid + 1, close)
            println("Type: $type, convert: $convert")
            return substring.replaceRange(
                (open..close),
                typealiases[type]!!.second.replace("$", convert)
            )
        } else {
            return substring
        }
    }

    override fun exitNonTerminalRule(ctx: MetaParser.NonTerminalRuleContext) {
        val name = ctx.name.text
        val input =
            ctx.input()?.args()?.arg()
                ?.map { Arg(it.name.text, if (typealiases.containsKey(it.type.text)) typealiases[it.type.text]!!.first else it.type.text) }
                ?: emptyList()
        val output =
            ctx.output()?.args()?.arg()
                ?.map { Arg(it.name.text, if (typealiases.containsKey(it.type.text)) typealiases[it.type.text]!!.first else it.type.text) }
                ?: emptyList()
        val alternatives = ctx.body.alternative().map { alternative ->
            Alternative(alternative.production().map { production ->
                val inCode = if (production.INATTR() != null) {
                    format(production.INATTR().text)
                } else {
                    "// no args"
                }
                val outCode = if (production.OUTATTR() != null) {
                    format(production.OUTATTR().text)
                } else {
                    "// no return"
                }
                val alias = production.alias()?.text
                when {
                    production.terminalName() != null -> Production.TerminalName(
                        production.terminalName().text,
                        inCode,
                        outCode,
                        alias
                    )

                    production.nonTerminalName() != null -> Production.NonTerminalName(
                        production.nonTerminalName().text,
                        inCode,
                        outCode,
                        alias
                    )

                    production.eps() != null -> Production.Eps(inCode, outCode)
                    else -> throw IllegalStateException()
                }
            })
        }
        rules.add(Rule.NonTerminal(name, Body(alternatives), input, output))
    }
}