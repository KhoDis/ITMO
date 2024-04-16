class Grammar(val name: GrammarName, val rules: List<Rule>) {
    val terminalRules: List<Rule.Terminal> = rules.filterIsInstance<Rule.Terminal>()
    val nonTerminalRules: List<Rule.NonTerminal> = rules.filterIsInstance<Rule.NonTerminal>()
    val startRule: Rule.NonTerminal = nonTerminalRules.first()
    val terminalRuleByName = terminalRules.associateBy { it.name }
    val nonTerminalRuleByName = nonTerminalRules.associateBy { it.name }

    val first: HashMap<RuleName, HashSet<RuleName>> = HashMap()
    val follow: HashMap<RuleName, HashSet<RuleName>> = HashMap()

    private fun findTerminal(rule: List<String>): HashSet<String> {
        if (rule.isEmpty()) {
            return hashSetOf("EPS")
        }
        if (rule[0][0].isUpperCase()) {
            return hashSetOf(rule[0])
        }
        val nonTerminal = rule[0]
        val firstT = HashSet(first[nonTerminal]!!)
        val hadEps = firstT.remove("EPS")
        if (hadEps) {
            firstT.addAll(findTerminal(rule.subList(1, rule.size)))
        }
        return firstT
    }

    fun buildFirst() {
        for (nonTerminalName in nonTerminalRuleByName.keys) {
            first[nonTerminalName] = HashSet()
        }
        var changed = true
        while (changed) {
            changed = false
            for (nonTerminal in nonTerminalRules) {
                val nonTerminalName = nonTerminal.name
                for (alternative in nonTerminal.body.alternatives) {
                    val firstSet = first[nonTerminalName]!!
                    if (firstSet.addAll(findTerminal(alternative.productions.map { it.name }))) {
                        changed = true
                    }
                }
            }
        }
    }

    fun buildFollow() {
        for (nonTerminalName in nonTerminalRuleByName.keys) {
            follow[nonTerminalName] = HashSet()
        }
        follow[startRule.name]!!.add("END")

        var changed = true
        while (changed) {
            changed = false
            for (nonTerminal in nonTerminalRules) {
                val nonTerminalName = nonTerminal.name
                for (alternative in nonTerminal.body.alternatives) {
                    val productions = alternative.productions
                    for (i in productions.indices) {
                        val production = productions[i]
                        if (production is Production.NonTerminalName) {
                            val followT = findTerminal(
                                productions.subList(i + 1, productions.size).map { it.name })
                            val hadEps = followT.remove("EPS")
                            if (hadEps) {
                                followT.addAll(follow[nonTerminalName]!!)
                            }
                            val followSet = follow[production.name]!!
                            if (followSet.addAll(followT)) {
                                changed = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun makeFirstFirst(nonTerminalName: String, nodes: List<Production>): Set<String> {
        val firstT: MutableSet<String> = findTerminal(nodes.map { it.name })
        if (firstT.remove("EPS")) {
            firstT.addAll(follow[nonTerminalName]!!)
        }
        return firstT
    }

    override fun toString(): String {
        return "Grammar(name='$name', rules=\n${
            rules.joinToString("\n") {
                it.toString().prependIndent("    ")
            }
        })"
    }

    fun checkLL(
        nonTerminal: List<Rule.NonTerminal>
    ): Boolean {
        nonTerminal.forEach {
            for (i in it.body.alternatives.indices) {
                for (j in it.body.alternatives.indices) {
                    if (i == j) continue
                    val firstRule = findTerminal(it.body.alternatives[i].productions.map { it.name })
                    val secondRule = findTerminal(it.body.alternatives[j].productions.map { it.name })
                    for (f in firstRule) {
                        if (!secondRule.contains(f)) continue
                        println("firstRule: $firstRule")
                        println("secondRule: $secondRule")
                        return false
                    }
                }
            }
        }
        return true
    }

    init {
        buildFirst()
        buildFollow()

        if (!checkLL(nonTerminalRules)) {
            throw IllegalArgumentException("Grammar is not LL1")
        }
    }
}