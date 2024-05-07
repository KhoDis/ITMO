// Generated from java-escape by ANTLR 4.11.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TeXParser}.
 */
public interface TeXListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TeXParser#document}.
	 * @param ctx the parse tree
	 */
	void enterDocument(TeXParser.DocumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#document}.
	 * @param ctx the parse tree
	 */
	void exitDocument(TeXParser.DocumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#begin}.
	 * @param ctx the parse tree
	 */
	void enterBegin(TeXParser.BeginContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#begin}.
	 * @param ctx the parse tree
	 */
	void exitBegin(TeXParser.BeginContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#end}.
	 * @param ctx the parse tree
	 */
	void enterEnd(TeXParser.EndContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#end}.
	 * @param ctx the parse tree
	 */
	void exitEnd(TeXParser.EndContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(TeXParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(TeXParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(TeXParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(TeXParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(TeXParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(TeXParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#option}.
	 * @param ctx the parse tree
	 */
	void enterOption(TeXParser.OptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#option}.
	 * @param ctx the parse tree
	 */
	void exitOption(TeXParser.OptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(TeXParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(TeXParser.BodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#math}.
	 * @param ctx the parse tree
	 */
	void enterMath(TeXParser.MathContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#math}.
	 * @param ctx the parse tree
	 */
	void exitMath(TeXParser.MathContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathOperator}.
	 * @param ctx the parse tree
	 */
	void enterMathOperator(TeXParser.MathOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathOperator}.
	 * @param ctx the parse tree
	 */
	void exitMathOperator(TeXParser.MathOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathUnaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterMathUnaryOperator(TeXParser.MathUnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathUnaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitMathUnaryOperator(TeXParser.MathUnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathBracket}.
	 * @param ctx the parse tree
	 */
	void enterMathBracket(TeXParser.MathBracketContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathBracket}.
	 * @param ctx the parse tree
	 */
	void exitMathBracket(TeXParser.MathBracketContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathBlock}.
	 * @param ctx the parse tree
	 */
	void enterMathBlock(TeXParser.MathBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathBlock}.
	 * @param ctx the parse tree
	 */
	void exitMathBlock(TeXParser.MathBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathSubscript}.
	 * @param ctx the parse tree
	 */
	void enterMathSubscript(TeXParser.MathSubscriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathSubscript}.
	 * @param ctx the parse tree
	 */
	void exitMathSubscript(TeXParser.MathSubscriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathSuperscript}.
	 * @param ctx the parse tree
	 */
	void enterMathSuperscript(TeXParser.MathSuperscriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathSuperscript}.
	 * @param ctx the parse tree
	 */
	void exitMathSuperscript(TeXParser.MathSuperscriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathUnit}.
	 * @param ctx the parse tree
	 */
	void enterMathUnit(TeXParser.MathUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathUnit}.
	 * @param ctx the parse tree
	 */
	void exitMathUnit(TeXParser.MathUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathObject}.
	 * @param ctx the parse tree
	 */
	void enterMathObject(TeXParser.MathObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathObject}.
	 * @param ctx the parse tree
	 */
	void exitMathObject(TeXParser.MathObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathExpression}.
	 * @param ctx the parse tree
	 */
	void enterMathExpression(TeXParser.MathExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathExpression}.
	 * @param ctx the parse tree
	 */
	void exitMathExpression(TeXParser.MathExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathFraction}.
	 * @param ctx the parse tree
	 */
	void enterMathFraction(TeXParser.MathFractionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathFraction}.
	 * @param ctx the parse tree
	 */
	void exitMathFraction(TeXParser.MathFractionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TeXParser#mathSquareRoot}.
	 * @param ctx the parse tree
	 */
	void enterMathSquareRoot(TeXParser.MathSquareRootContext ctx);
	/**
	 * Exit a parse tree produced by {@link TeXParser#mathSquareRoot}.
	 * @param ctx the parse tree
	 */
	void exitMathSquareRoot(TeXParser.MathSquareRootContext ctx);
}