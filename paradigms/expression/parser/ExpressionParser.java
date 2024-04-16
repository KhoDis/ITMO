package expression.parser;

import expression.GenericExpression;
import expression.operations.*;
import expression.operations.binary.*;
import expression.operations.unary.*;

public class ExpressionParser {

    public GenericExpression parse(final String source) throws ParseException {
        return parse(new StringSource(source));
    }

    public GenericExpression parse(CharSource source) throws ParseException {
        return new ExprParser(source).parseExpression();
    }

    private static class ExprParser extends BaseParser {

        private ExprParser(final CharSource source) {
            super(source);
            nextChar();
        }

        private GenericExpression parseExpression() throws ParseException {
            final GenericExpression result = parseElement();
            if (eof()) {
                return result;
            }
            throw error("End of Expression expected: " + result);
        }

        private GenericExpression parseElement() throws ParseException {
            skipWhitespace();
            final GenericExpression result = parseExpr();
            skipWhitespace();
            return result;
        }

        private GenericExpression parseExpr() throws ParseException {
            GenericExpression expression = parseTerm();
            while (true) {
                skipWhitespace();
                if (test('+')) {
                    expression = new Add(expression, parseTerm());
                } else if (test('-')) {
                    expression = new Subtract(expression, parseTerm());
                } else {
                    return expression;
                }
            }
        }

        public GenericExpression parseTerm() throws ParseException {
            GenericExpression expression = parsePrim();
            while (true) {
                skipWhitespace();
                if (test('*')) {
                    expression = new Multiply(expression, parsePrim());
                } else if (test('/')) {
                    expression = new Divide(expression, parsePrim());
                } else {
                    return expression;
                }
            }
        }

        private boolean isNegative() {
            skipWhitespace();
            boolean neg = false;
            while (test('-')) {
                skipWhitespace();
                neg = !neg;
            }
            return neg;
        }

        private GenericExpression parsePrim() throws ParseException {
            GenericExpression expression;
            boolean negative = isNegative();
            skipWhitespace();
            if (test(')')) {
                throw error("Empty brackets");
            } else if (between('0', '9')) {
                expression = parseNumber();
            } else if (between('a', 'z') || between('A', 'Z')) {
                expression = parseVariable();
            } else if (test('(')) {
                expression = parseElement();    
                if (!test(')')) {
                    throw error("Closing bracket not found: " + expression);
                }
            } else {
                throw error("Primary expected");
            }
            return negative ? new Negate(expression) : expression;
        }

        private Const parseNumber() {
            return new Const(copyInteger(new StringBuilder()).toString());
        }

        private StringBuilder copyInteger(StringBuilder sb) throws ParseException {
            if (test('-')) {
                sb.append('-');
            }
            if (test('0')) {
                sb.append('0');
                return copyFractional(sb);
            } 
            if (between('1', '9')) {
                copyDigits(sb);
                return copyFractional(sb);
            }
            throw error("Invalid Number");
        }

        private StringBuilder copyFractional(StringBuilder sb) {
            if (test('.')) {
                sb.append('.');
                copyDigits(sb);
            }
            return sb;
        }

        private StringBuilder copyDigits(final StringBuilder sb) {
            while (between('0', '9')) {
                sb.append(ch);
                nextChar();
            }
            return sb;
        }

        private Variable parseVariable() {
            final String var = copyLetters(new StringBuilder()).toString();

            if (var.equals("x") || var.equals("y") || var.equals("z")) {
                return new Variable(var);
            }
            throw error("Invalid Variable: " + var);
        }

        private StringBuilder copyLetters(StringBuilder sb) {
            while (between('a', 'z') || between('A', 'Z')) {
                sb.append(Character.toLowerCase(ch));
                nextChar();
            }
            return sb;
        }

        private void skipWhitespace() {
            while (test(' ') || test('\r') || test('\n') || test('\t')) {
                // skip
            }
        }
    }
}