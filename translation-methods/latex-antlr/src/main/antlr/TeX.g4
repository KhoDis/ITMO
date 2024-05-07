grammar TeX;

document: begin body end EOF;

BEGIN: '\\begin';
END: '\\end';

begin: BEGIN option;
end: END option;

LBRACKET: '{';
RBRACKET: '}';

MATH_MODE: '$';

DIGIT: '0'..'9';
LATIN: ('a'..'z'|'A'..'Z');

identifier: LATIN (LATIN|DIGIT)*;
number: DIGIT+;
variable: identifier;

option: LBRACKET identifier RBRACKET;

body: (math)+;

math: MATH_MODE mathExpression MATH_MODE;

mathOperator: '+'|'-'|'*'|'/'|'=';
mathUnaryOperator: '+'|'-';

mathBracket: '(' mathExpression ')';
mathBlock: LBRACKET mathExpression RBRACKET;

mathSubscript: '_' mathUnit;
mathSuperscript: '^' mathUnit;

FRAC: '\\frac';
SQRT: '\\sqrt';

mathUnit:
    mathBlock
    | mathBracket
    | mathFraction
    | mathSquareRoot
    | number
    | variable
    ;
mathObject:
    mathUnaryOperator? mathUnit (mathSubscript? mathSuperscript? | mathSuperscript? mathSubscript?)
    ;
mathExpression: mathObject (mathOperator mathObject)*;

mathFraction: FRAC mathBlock mathBlock;
mathSquareRoot: SQRT mathBlock;

WS: (' '|'\t'|'\r'|'\n') -> skip;
