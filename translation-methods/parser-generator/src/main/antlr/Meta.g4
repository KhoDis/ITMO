grammar Meta;

meta: grammarName typealiases? rules;

typealiases: typealias+;

typealias: USING name=UPPER '=' type=(LOWER | UPPER) OUTATTR SEMI;

grammarName: GRAMMAR name=identifier SEMI;

identifier: UPPER;

rules: rulee*;

rulee: (terminalRule | nonTerminalRule) SEMI;

terminalRule: name=terminalName OF literal=terminalLiteral (ARROW skipe)?;
terminalLiteral: REGEX;
terminalName: UPPER;

nonTerminalRule: name=nonTerminalName input? output? OF body=nonTerminalBody;
nonTerminalBody: alternative (OR alternative)*;
alternative: production+;
production: (alias IS)? (terminalName | nonTerminalName | eps) INATTR? OUTATTR?;
nonTerminalName: LOWER;

eps: EPS;

input: ('<' args '>');
output: (ARROW '[' args ']');
args: arg (',' arg)*;
arg: name=LOWER ':' type=UPPER;
alias: LOWER;
skipe: SKIPE;

EPS: 'EPS';
USING: 'using';
INATTR: '(' ~')'+ ')';
OUTATTR: '{' ~'}'+ '}';
GRAMMAR: 'grammar';
SKIPE: 'skip';
ARROW: '->';
SEMI: ';';
REGEX: '"' ~'"'* '"';
UPPER: [A-Z][a-zA-Z_.]*;
LOWER: [a-z][a-zA-Z_.]*;
OF: ':';
OR: '|';
IS: '=';
WS: [ \t\r\n]+ -> skip;