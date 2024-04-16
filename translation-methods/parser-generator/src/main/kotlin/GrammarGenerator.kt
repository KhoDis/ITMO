//grammar Meta;
//
//meta: grammarName typealiases? rules;
//
//typealiases: typealias+;
//
//typealias: USING name=UPPER '=' type=UPPER OUTATTR SEMI;
//
//grammarName: GRAMMAR name=identifier SEMI;
//
//identifier: UPPER;
//
//rules: rulee*;
//
//rulee: (terminalRule | nonTerminalRule) SEMI;
//
//terminalRule: name=terminalName OF literal=terminalLiteral (ARROW skipe)?;
//terminalLiteral: REGEX;
//terminalName: UPPER;
//
//nonTerminalRule: name=nonTerminalName input? output? OF body=nonTerminalBody;
//nonTerminalBody: alternative (OR alternative)*;
//alternative: production+;
//production: (alias IS)? (terminalName | nonTerminalName | eps) INATTR? OUTATTR?;
//nonTerminalName: LOWER;
//
//eps: EPS;
//
//input: ('<' args '>');
//output: (ARROW '[' args ']');
//args: arg (',' arg)*;
//arg: name=LOWER ':' type=UPPER;
//alias: LOWER;
//skipe: SKIPE;
//
//EPS: 'EPS';
//USING: 'using';
//INATTR: '(' ~')'+ ')';
//OUTATTR: '{' ~'}'+ '}';
//GRAMMAR: 'grammar';
//SKIPE: 'skip';
//ARROW: '->';
//SEMI: ';';
//REGEX: '"' ~'"'* '"';
//UPPER: [A-Z][a-zA-Z_]*;
//LOWER: [a-z][a-zA-Z_]*;
//OF: ':';
//OR: '|';
//IS: '=';
//WS: [ \t\r\n]+ -> skip;

typealias RuleName = String
typealias Literal = String
typealias GrammarName = String

class TypeAlias(val name: String, val type: String, val convert: String)

sealed class Rule(val name: RuleName) {
    class Terminal(name: RuleName, val literal: Literal, val skip: Boolean) : Rule(name)
    class NonTerminal(name: RuleName, val body: Body, val args: List<Arg>, val returns: List<Arg>) :
        Rule(name)

}

data class Arg(val name: String, val type: String)

class Body(val alternatives: List<Alternative>) {
    override fun toString(): String {
        return alternatives.joinToString(" | ")
    }
}

class Alternative(val productions: List<Production>) {
    override fun toString(): String {
        return productions.joinToString(" ")
    }
}

sealed class Production(
    val name: RuleName,
    val inCode: String,
    val outCode: String,
    val alias: String?
) {
    open class TerminalName(name: RuleName, inCode: String, outCode: String, alias: String?) :
        Production(name, inCode, outCode, alias)

    class NonTerminalName(name: RuleName, inCode: String, outCode: String, alias: String?) :
        Production(name, inCode, outCode, alias)

    class Eps(inCode: String, outCode: String) : Production("EPS", inCode, outCode, null)
}

