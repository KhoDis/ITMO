// Generated from java-escape by ANTLR 4.11.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TeXParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TeXVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TeXParser#document}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDocument(TeXParser.DocumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#begin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBegin(TeXParser.BeginContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#end}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd(TeXParser.EndContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(TeXParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(TeXParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(TeXParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#option}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption(TeXParser.OptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(TeXParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#math}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMath(TeXParser.MathContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperator(TeXParser.MathOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathUnaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathUnaryOperator(TeXParser.MathUnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathBracket}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathBracket(TeXParser.MathBracketContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathBlock(TeXParser.MathBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathSubscript}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathSubscript(TeXParser.MathSubscriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathSuperscript}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathSuperscript(TeXParser.MathSuperscriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathUnit(TeXParser.MathUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathObject}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathObject(TeXParser.MathObjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathExpression(TeXParser.MathExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathFraction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathFraction(TeXParser.MathFractionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TeXParser#mathSquareRoot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathSquareRoot(TeXParser.MathSquareRootContext ctx);
}