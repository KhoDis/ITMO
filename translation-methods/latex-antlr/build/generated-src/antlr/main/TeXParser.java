// Generated from java-escape by ANTLR 4.11.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class TeXParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		BEGIN=10, END=11, LBRACKET=12, RBRACKET=13, MATH_MODE=14, DIGIT=15, LATIN=16, 
		FRAC=17, SQRT=18, WS=19;
	public static final int
		RULE_document = 0, RULE_begin = 1, RULE_end = 2, RULE_identifier = 3, 
		RULE_number = 4, RULE_variable = 5, RULE_option = 6, RULE_body = 7, RULE_math = 8, 
		RULE_mathOperator = 9, RULE_mathUnaryOperator = 10, RULE_mathBracket = 11, 
		RULE_mathBlock = 12, RULE_mathSubscript = 13, RULE_mathSuperscript = 14, 
		RULE_mathUnit = 15, RULE_mathObject = 16, RULE_mathExpression = 17, RULE_mathFraction = 18, 
		RULE_mathSquareRoot = 19;
	private static String[] makeRuleNames() {
		return new String[] {
			"document", "begin", "end", "identifier", "number", "variable", "option", 
			"body", "math", "mathOperator", "mathUnaryOperator", "mathBracket", "mathBlock", 
			"mathSubscript", "mathSuperscript", "mathUnit", "mathObject", "mathExpression", 
			"mathFraction", "mathSquareRoot"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'+'", "'-'", "'*'", "'/'", "'='", "'('", "')'", "'_'", "'^'", 
			"'\\begin'", "'\\end'", "'{'", "'}'", "'$'", null, null, "'\\frac'", 
			"'\\sqrt'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "BEGIN", 
			"END", "LBRACKET", "RBRACKET", "MATH_MODE", "DIGIT", "LATIN", "FRAC", 
			"SQRT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TeXParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DocumentContext extends ParserRuleContext {
		public BeginContext begin() {
			return getRuleContext(BeginContext.class,0);
		}
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public EndContext end() {
			return getRuleContext(EndContext.class,0);
		}
		public TerminalNode EOF() { return getToken(TeXParser.EOF, 0); }
		public DocumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_document; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterDocument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitDocument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitDocument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DocumentContext document() throws RecognitionException {
		DocumentContext _localctx = new DocumentContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_document);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			begin();
			setState(41);
			body();
			setState(42);
			end();
			setState(43);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BeginContext extends ParserRuleContext {
		public TerminalNode BEGIN() { return getToken(TeXParser.BEGIN, 0); }
		public OptionContext option() {
			return getRuleContext(OptionContext.class,0);
		}
		public BeginContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_begin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterBegin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitBegin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitBegin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BeginContext begin() throws RecognitionException {
		BeginContext _localctx = new BeginContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_begin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(BEGIN);
			setState(46);
			option();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EndContext extends ParserRuleContext {
		public TerminalNode END() { return getToken(TeXParser.END, 0); }
		public OptionContext option() {
			return getRuleContext(OptionContext.class,0);
		}
		public EndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_end; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitEnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitEnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndContext end() throws RecognitionException {
		EndContext _localctx = new EndContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(END);
			setState(49);
			option();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierContext extends ParserRuleContext {
		public List<TerminalNode> LATIN() { return getTokens(TeXParser.LATIN); }
		public TerminalNode LATIN(int i) {
			return getToken(TeXParser.LATIN, i);
		}
		public List<TerminalNode> DIGIT() { return getTokens(TeXParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(TeXParser.DIGIT, i);
		}
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(LATIN);
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DIGIT || _la==LATIN) {
				{
				{
				setState(52);
				_la = _input.LA(1);
				if ( !(_la==DIGIT || _la==LATIN) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NumberContext extends ParserRuleContext {
		public List<TerminalNode> DIGIT() { return getTokens(TeXParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(TeXParser.DIGIT, i);
		}
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(58);
				match(DIGIT);
				}
				}
				setState(61); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DIGIT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OptionContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(TeXParser.LBRACKET, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode RBRACKET() { return getToken(TeXParser.RBRACKET, 0); }
		public OptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_option; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterOption(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitOption(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitOption(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptionContext option() throws RecognitionException {
		OptionContext _localctx = new OptionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_option);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(LBRACKET);
			setState(66);
			identifier();
			setState(67);
			match(RBRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BodyContext extends ParserRuleContext {
		public List<MathContext> math() {
			return getRuleContexts(MathContext.class);
		}
		public MathContext math(int i) {
			return getRuleContext(MathContext.class,i);
		}
		public BodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BodyContext body() throws RecognitionException {
		BodyContext _localctx = new BodyContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(69);
				math();
				}
				}
				setState(72); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==MATH_MODE );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathContext extends ParserRuleContext {
		public List<TerminalNode> MATH_MODE() { return getTokens(TeXParser.MATH_MODE); }
		public TerminalNode MATH_MODE(int i) {
			return getToken(TeXParser.MATH_MODE, i);
		}
		public MathExpressionContext mathExpression() {
			return getRuleContext(MathExpressionContext.class,0);
		}
		public MathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_math; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMath(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathContext math() throws RecognitionException {
		MathContext _localctx = new MathContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_math);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(MATH_MODE);
			setState(75);
			mathExpression();
			setState(76);
			match(MATH_MODE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathOperatorContext extends ParserRuleContext {
		public MathOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathOperatorContext mathOperator() throws RecognitionException {
		MathOperatorContext _localctx = new MathOperatorContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_mathOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			_la = _input.LA(1);
			if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 62L) != 0) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathUnaryOperatorContext extends ParserRuleContext {
		public MathUnaryOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathUnaryOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathUnaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathUnaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathUnaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathUnaryOperatorContext mathUnaryOperator() throws RecognitionException {
		MathUnaryOperatorContext _localctx = new MathUnaryOperatorContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_mathUnaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			_la = _input.LA(1);
			if ( !(_la==T__0 || _la==T__1) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathBracketContext extends ParserRuleContext {
		public MathExpressionContext mathExpression() {
			return getRuleContext(MathExpressionContext.class,0);
		}
		public MathBracketContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathBracket; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathBracket(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathBracket(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathBracket(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathBracketContext mathBracket() throws RecognitionException {
		MathBracketContext _localctx = new MathBracketContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_mathBracket);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(T__5);
			setState(83);
			mathExpression();
			setState(84);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathBlockContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(TeXParser.LBRACKET, 0); }
		public MathExpressionContext mathExpression() {
			return getRuleContext(MathExpressionContext.class,0);
		}
		public TerminalNode RBRACKET() { return getToken(TeXParser.RBRACKET, 0); }
		public MathBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathBlockContext mathBlock() throws RecognitionException {
		MathBlockContext _localctx = new MathBlockContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_mathBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			match(LBRACKET);
			setState(87);
			mathExpression();
			setState(88);
			match(RBRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathSubscriptContext extends ParserRuleContext {
		public MathUnitContext mathUnit() {
			return getRuleContext(MathUnitContext.class,0);
		}
		public MathSubscriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathSubscript; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathSubscript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathSubscript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathSubscript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathSubscriptContext mathSubscript() throws RecognitionException {
		MathSubscriptContext _localctx = new MathSubscriptContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_mathSubscript);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(T__7);
			setState(91);
			mathUnit();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathSuperscriptContext extends ParserRuleContext {
		public MathUnitContext mathUnit() {
			return getRuleContext(MathUnitContext.class,0);
		}
		public MathSuperscriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathSuperscript; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathSuperscript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathSuperscript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathSuperscript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathSuperscriptContext mathSuperscript() throws RecognitionException {
		MathSuperscriptContext _localctx = new MathSuperscriptContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_mathSuperscript);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(T__8);
			setState(94);
			mathUnit();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathUnitContext extends ParserRuleContext {
		public MathBlockContext mathBlock() {
			return getRuleContext(MathBlockContext.class,0);
		}
		public MathBracketContext mathBracket() {
			return getRuleContext(MathBracketContext.class,0);
		}
		public MathFractionContext mathFraction() {
			return getRuleContext(MathFractionContext.class,0);
		}
		public MathSquareRootContext mathSquareRoot() {
			return getRuleContext(MathSquareRootContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public MathUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathUnit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathUnit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathUnitContext mathUnit() throws RecognitionException {
		MathUnitContext _localctx = new MathUnitContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_mathUnit);
		try {
			setState(102);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBRACKET:
				enterOuterAlt(_localctx, 1);
				{
				setState(96);
				mathBlock();
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(97);
				mathBracket();
				}
				break;
			case FRAC:
				enterOuterAlt(_localctx, 3);
				{
				setState(98);
				mathFraction();
				}
				break;
			case SQRT:
				enterOuterAlt(_localctx, 4);
				{
				setState(99);
				mathSquareRoot();
				}
				break;
			case DIGIT:
				enterOuterAlt(_localctx, 5);
				{
				setState(100);
				number();
				}
				break;
			case LATIN:
				enterOuterAlt(_localctx, 6);
				{
				setState(101);
				variable();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathObjectContext extends ParserRuleContext {
		public MathUnitContext mathUnit() {
			return getRuleContext(MathUnitContext.class,0);
		}
		public MathUnaryOperatorContext mathUnaryOperator() {
			return getRuleContext(MathUnaryOperatorContext.class,0);
		}
		public MathSubscriptContext mathSubscript() {
			return getRuleContext(MathSubscriptContext.class,0);
		}
		public MathSuperscriptContext mathSuperscript() {
			return getRuleContext(MathSuperscriptContext.class,0);
		}
		public MathObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathObject; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathObjectContext mathObject() throws RecognitionException {
		MathObjectContext _localctx = new MathObjectContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_mathObject);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0 || _la==T__1) {
				{
				setState(104);
				mathUnaryOperator();
				}
			}

			setState(107);
			mathUnit();
			setState(120);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__7) {
					{
					setState(108);
					mathSubscript();
					}
				}

				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__8) {
					{
					setState(111);
					mathSuperscript();
					}
				}

				}
				break;
			case 2:
				{
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__8) {
					{
					setState(114);
					mathSuperscript();
					}
				}

				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__7) {
					{
					setState(117);
					mathSubscript();
					}
				}

				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathExpressionContext extends ParserRuleContext {
		public List<MathObjectContext> mathObject() {
			return getRuleContexts(MathObjectContext.class);
		}
		public MathObjectContext mathObject(int i) {
			return getRuleContext(MathObjectContext.class,i);
		}
		public List<MathOperatorContext> mathOperator() {
			return getRuleContexts(MathOperatorContext.class);
		}
		public MathOperatorContext mathOperator(int i) {
			return getRuleContext(MathOperatorContext.class,i);
		}
		public MathExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathExpressionContext mathExpression() throws RecognitionException {
		MathExpressionContext _localctx = new MathExpressionContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_mathExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			mathObject();
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((_la) & ~0x3f) == 0 && ((1L << _la) & 62L) != 0) {
				{
				{
				setState(123);
				mathOperator();
				setState(124);
				mathObject();
				}
				}
				setState(130);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathFractionContext extends ParserRuleContext {
		public TerminalNode FRAC() { return getToken(TeXParser.FRAC, 0); }
		public List<MathBlockContext> mathBlock() {
			return getRuleContexts(MathBlockContext.class);
		}
		public MathBlockContext mathBlock(int i) {
			return getRuleContext(MathBlockContext.class,i);
		}
		public MathFractionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathFraction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathFraction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathFraction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathFraction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathFractionContext mathFraction() throws RecognitionException {
		MathFractionContext _localctx = new MathFractionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_mathFraction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			match(FRAC);
			setState(132);
			mathBlock();
			setState(133);
			mathBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathSquareRootContext extends ParserRuleContext {
		public TerminalNode SQRT() { return getToken(TeXParser.SQRT, 0); }
		public MathBlockContext mathBlock() {
			return getRuleContext(MathBlockContext.class,0);
		}
		public MathSquareRootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathSquareRoot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).enterMathSquareRoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TeXListener ) ((TeXListener)listener).exitMathSquareRoot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TeXVisitor ) return ((TeXVisitor<? extends T>)visitor).visitMathSquareRoot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathSquareRootContext mathSquareRoot() throws RecognitionException {
		MathSquareRootContext _localctx = new MathSquareRootContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_mathSquareRoot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			match(SQRT);
			setState(136);
			mathBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0013\u008b\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0005\u00036\b\u0003\n\u0003"+
		"\f\u00039\t\u0003\u0001\u0004\u0004\u0004<\b\u0004\u000b\u0004\f\u0004"+
		"=\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0004\u0007G\b\u0007\u000b\u0007\f\u0007H\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001"+
		"\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000fg\b\u000f"+
		"\u0001\u0010\u0003\u0010j\b\u0010\u0001\u0010\u0001\u0010\u0003\u0010"+
		"n\b\u0010\u0001\u0010\u0003\u0010q\b\u0010\u0001\u0010\u0003\u0010t\b"+
		"\u0010\u0001\u0010\u0003\u0010w\b\u0010\u0003\u0010y\b\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u007f\b\u0011\n\u0011"+
		"\f\u0011\u0082\t\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0000\u0000\u0014\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"+
		"\u001e \"$&\u0000\u0003\u0001\u0000\u000f\u0010\u0001\u0000\u0001\u0005"+
		"\u0001\u0000\u0001\u0002\u0085\u0000(\u0001\u0000\u0000\u0000\u0002-\u0001"+
		"\u0000\u0000\u0000\u00040\u0001\u0000\u0000\u0000\u00063\u0001\u0000\u0000"+
		"\u0000\b;\u0001\u0000\u0000\u0000\n?\u0001\u0000\u0000\u0000\fA\u0001"+
		"\u0000\u0000\u0000\u000eF\u0001\u0000\u0000\u0000\u0010J\u0001\u0000\u0000"+
		"\u0000\u0012N\u0001\u0000\u0000\u0000\u0014P\u0001\u0000\u0000\u0000\u0016"+
		"R\u0001\u0000\u0000\u0000\u0018V\u0001\u0000\u0000\u0000\u001aZ\u0001"+
		"\u0000\u0000\u0000\u001c]\u0001\u0000\u0000\u0000\u001ef\u0001\u0000\u0000"+
		"\u0000 i\u0001\u0000\u0000\u0000\"z\u0001\u0000\u0000\u0000$\u0083\u0001"+
		"\u0000\u0000\u0000&\u0087\u0001\u0000\u0000\u0000()\u0003\u0002\u0001"+
		"\u0000)*\u0003\u000e\u0007\u0000*+\u0003\u0004\u0002\u0000+,\u0005\u0000"+
		"\u0000\u0001,\u0001\u0001\u0000\u0000\u0000-.\u0005\n\u0000\u0000./\u0003"+
		"\f\u0006\u0000/\u0003\u0001\u0000\u0000\u000001\u0005\u000b\u0000\u0000"+
		"12\u0003\f\u0006\u00002\u0005\u0001\u0000\u0000\u000037\u0005\u0010\u0000"+
		"\u000046\u0007\u0000\u0000\u000054\u0001\u0000\u0000\u000069\u0001\u0000"+
		"\u0000\u000075\u0001\u0000\u0000\u000078\u0001\u0000\u0000\u00008\u0007"+
		"\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u0000:<\u0005\u000f\u0000"+
		"\u0000;:\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000=;\u0001\u0000"+
		"\u0000\u0000=>\u0001\u0000\u0000\u0000>\t\u0001\u0000\u0000\u0000?@\u0003"+
		"\u0006\u0003\u0000@\u000b\u0001\u0000\u0000\u0000AB\u0005\f\u0000\u0000"+
		"BC\u0003\u0006\u0003\u0000CD\u0005\r\u0000\u0000D\r\u0001\u0000\u0000"+
		"\u0000EG\u0003\u0010\b\u0000FE\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000"+
		"\u0000HF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000I\u000f\u0001"+
		"\u0000\u0000\u0000JK\u0005\u000e\u0000\u0000KL\u0003\"\u0011\u0000LM\u0005"+
		"\u000e\u0000\u0000M\u0011\u0001\u0000\u0000\u0000NO\u0007\u0001\u0000"+
		"\u0000O\u0013\u0001\u0000\u0000\u0000PQ\u0007\u0002\u0000\u0000Q\u0015"+
		"\u0001\u0000\u0000\u0000RS\u0005\u0006\u0000\u0000ST\u0003\"\u0011\u0000"+
		"TU\u0005\u0007\u0000\u0000U\u0017\u0001\u0000\u0000\u0000VW\u0005\f\u0000"+
		"\u0000WX\u0003\"\u0011\u0000XY\u0005\r\u0000\u0000Y\u0019\u0001\u0000"+
		"\u0000\u0000Z[\u0005\b\u0000\u0000[\\\u0003\u001e\u000f\u0000\\\u001b"+
		"\u0001\u0000\u0000\u0000]^\u0005\t\u0000\u0000^_\u0003\u001e\u000f\u0000"+
		"_\u001d\u0001\u0000\u0000\u0000`g\u0003\u0018\f\u0000ag\u0003\u0016\u000b"+
		"\u0000bg\u0003$\u0012\u0000cg\u0003&\u0013\u0000dg\u0003\b\u0004\u0000"+
		"eg\u0003\n\u0005\u0000f`\u0001\u0000\u0000\u0000fa\u0001\u0000\u0000\u0000"+
		"fb\u0001\u0000\u0000\u0000fc\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000"+
		"\u0000fe\u0001\u0000\u0000\u0000g\u001f\u0001\u0000\u0000\u0000hj\u0003"+
		"\u0014\n\u0000ih\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000jk\u0001"+
		"\u0000\u0000\u0000kx\u0003\u001e\u000f\u0000ln\u0003\u001a\r\u0000ml\u0001"+
		"\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000np\u0001\u0000\u0000\u0000"+
		"oq\u0003\u001c\u000e\u0000po\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000"+
		"\u0000qy\u0001\u0000\u0000\u0000rt\u0003\u001c\u000e\u0000sr\u0001\u0000"+
		"\u0000\u0000st\u0001\u0000\u0000\u0000tv\u0001\u0000\u0000\u0000uw\u0003"+
		"\u001a\r\u0000vu\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000wy\u0001"+
		"\u0000\u0000\u0000xm\u0001\u0000\u0000\u0000xs\u0001\u0000\u0000\u0000"+
		"y!\u0001\u0000\u0000\u0000z\u0080\u0003 \u0010\u0000{|\u0003\u0012\t\u0000"+
		"|}\u0003 \u0010\u0000}\u007f\u0001\u0000\u0000\u0000~{\u0001\u0000\u0000"+
		"\u0000\u007f\u0082\u0001\u0000\u0000\u0000\u0080~\u0001\u0000\u0000\u0000"+
		"\u0080\u0081\u0001\u0000\u0000\u0000\u0081#\u0001\u0000\u0000\u0000\u0082"+
		"\u0080\u0001\u0000\u0000\u0000\u0083\u0084\u0005\u0011\u0000\u0000\u0084"+
		"\u0085\u0003\u0018\f\u0000\u0085\u0086\u0003\u0018\f\u0000\u0086%\u0001"+
		"\u0000\u0000\u0000\u0087\u0088\u0005\u0012\u0000\u0000\u0088\u0089\u0003"+
		"\u0018\f\u0000\u0089\'\u0001\u0000\u0000\u0000\u000b7=Hfimpsvx\u0080";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}