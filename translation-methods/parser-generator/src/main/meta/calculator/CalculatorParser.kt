package calculator
import kotlin.properties.Delegates

class CalculatorParser(private val lexer: CalculatorLexer) {
    class ParserException(message: String) : Exception(message)
    class TerminalNode(name: String, val text: String) : CalculatorNode(name + ": " + text)
    var token = lexer.nextToken()

    fun nextToken() {
        token = lexer.nextToken()
    }

    fun parse(
    
    ): StartContext {
        val res = start()
    
        if (token.type == CalculatorTokenType.END) {
            return res
        } else {
            throw ParserException("Expected END, got " + token.type)
        }
    }

    class StartContext(name: String,
    
    ) : CalculatorNode(name) {
    var expr_0_0: ExprContext? = null
    var e: ExprContext? = null
       var res: Double by Delegates.notNull<Double>()
        }
    
    fun start(
        
        ): StartContext {
        val _localctx = StartContext("start",
        
        )
        when (token.type) {
       CalculatorTokenType.NUMBER, CalculatorTokenType.LBR, CalculatorTokenType.MINUS -> {
        // Non-terminal expr
        val expr_0_0 = expr(
        // no args
        )
        val e = expr_0_0
        _localctx.expr_0_0 = expr_0_0
        _localctx.e = expr_0_0
        _localctx.children.add(expr_0_0)
    
        _localctx.res = e.value
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class ExprContext(name: String,
    
    ) : CalculatorNode(name) {
    var term_0_0: TermContext? = null
    var t: TermContext? = null
    var exprP_0_1: ExprPContext? = null
    var ep: ExprPContext? = null
       var value: Double by Delegates.notNull<Double>()
        }
    
    fun expr(
        
        ): ExprContext {
        val _localctx = ExprContext("expr",
        
        )
        when (token.type) {
       CalculatorTokenType.NUMBER, CalculatorTokenType.LBR, CalculatorTokenType.MINUS -> {
        // Non-terminal term
        val term_0_0 = term(
        // no args
        )
        val t = term_0_0
        _localctx.term_0_0 = term_0_0
        _localctx.t = term_0_0
        _localctx.children.add(term_0_0)
    
        // no return
        // Non-terminal exprP
        val exprP_0_1 = exprP(
        t.value
        )
        val ep = exprP_0_1
        _localctx.exprP_0_1 = exprP_0_1
        _localctx.ep = exprP_0_1
        _localctx.children.add(exprP_0_1)
    
        _localctx.value = ep.res
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class ExprPContext(name: String,
        val lhs: Double
    ) : CalculatorNode(name) {
    var exprOp_0_0: ExprOpContext? = null
    var eop: ExprOpContext? = null
    var term_0_1: TermContext? = null
    var t: TermContext? = null
    var exprP_0_2: ExprPContext? = null
    var ep: ExprPContext? = null
    // eps
       var res: Double by Delegates.notNull<Double>()
        }
    
    fun exprP(
            lhs: Double
        ): ExprPContext {
        val _localctx = ExprPContext("exprP",
            lhs
        )
        when (token.type) {
       CalculatorTokenType.PLUS, CalculatorTokenType.MINUS -> {
        // Non-terminal exprOp
        val exprOp_0_0 = exprOp(
        // no args
        )
        val eop = exprOp_0_0
        _localctx.exprOp_0_0 = exprOp_0_0
        _localctx.eop = exprOp_0_0
        _localctx.children.add(exprOp_0_0)
    
        // no return
        // Non-terminal term
        val term_0_1 = term(
        // no args
        )
        val t = term_0_1
        _localctx.term_0_1 = term_0_1
        _localctx.t = term_0_1
        _localctx.children.add(term_0_1)
    
        _localctx.res = if (eop.pl != null) _localctx.lhs + t.value else _localctx.lhs - t.value 
        // Non-terminal exprP
        val exprP_0_2 = exprP(
        _localctx.res
        )
        val ep = exprP_0_2
        _localctx.exprP_0_2 = exprP_0_2
        _localctx.ep = exprP_0_2
        _localctx.children.add(exprP_0_2)
    
        _localctx.res = ep.res
       return _localctx
    }
    
    CalculatorTokenType.END, CalculatorTokenType.RBR -> {
        // Eps
        // do nothing
    
        _localctx.res = _localctx.lhs
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class TermContext(name: String,
    
    ) : CalculatorNode(name) {
    var factorM_0_0: FactorMContext? = null
    var fm: FactorMContext? = null
    var termP_0_1: TermPContext? = null
    var tp: TermPContext? = null
       var value: Double by Delegates.notNull<Double>()
        }
    
    fun term(
        
        ): TermContext {
        val _localctx = TermContext("term",
        
        )
        when (token.type) {
       CalculatorTokenType.NUMBER, CalculatorTokenType.LBR, CalculatorTokenType.MINUS -> {
        // Non-terminal factorM
        val factorM_0_0 = factorM(
        // no args
        )
        val fm = factorM_0_0
        _localctx.factorM_0_0 = factorM_0_0
        _localctx.fm = factorM_0_0
        _localctx.children.add(factorM_0_0)
    
        // no return
        // Non-terminal termP
        val termP_0_1 = termP(
        fm.value
        )
        val tp = termP_0_1
        _localctx.termP_0_1 = termP_0_1
        _localctx.tp = termP_0_1
        _localctx.children.add(termP_0_1)
    
        _localctx.value = tp.res
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class TermPContext(name: String,
        val lhs: Double
    ) : CalculatorNode(name) {
    var termOp_0_0: TermOpContext? = null
    var top: TermOpContext? = null
    var factorM_0_1: FactorMContext? = null
    var fm: FactorMContext? = null
    var termP_0_2: TermPContext? = null
    var tp: TermPContext? = null
    // eps
       var res: Double by Delegates.notNull<Double>()
        }
    
    fun termP(
            lhs: Double
        ): TermPContext {
        val _localctx = TermPContext("termP",
            lhs
        )
        when (token.type) {
       CalculatorTokenType.DIV, CalculatorTokenType.MUL -> {
        // Non-terminal termOp
        val termOp_0_0 = termOp(
        // no args
        )
        val top = termOp_0_0
        _localctx.termOp_0_0 = termOp_0_0
        _localctx.top = termOp_0_0
        _localctx.children.add(termOp_0_0)
    
        // no return
        // Non-terminal factorM
        val factorM_0_1 = factorM(
        // no args
        )
        val fm = factorM_0_1
        _localctx.factorM_0_1 = factorM_0_1
        _localctx.fm = factorM_0_1
        _localctx.children.add(factorM_0_1)
    
        _localctx.res = if (top.MUL_0_0 != null) _localctx.lhs * fm.value else _localctx.lhs / fm.value
        // Non-terminal termP
        val termP_0_2 = termP(
        _localctx.res
        )
        val tp = termP_0_2
        _localctx.termP_0_2 = termP_0_2
        _localctx.tp = termP_0_2
        _localctx.children.add(termP_0_2)
    
        _localctx.res = tp.res
       return _localctx
    }
    
    CalculatorTokenType.END, CalculatorTokenType.RBR, CalculatorTokenType.PLUS, CalculatorTokenType.MINUS -> {
        // Eps
        // do nothing
    
        _localctx.res = _localctx.lhs
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class FactorMContext(name: String,
    
    ) : CalculatorNode(name) {
    var MINUS_0_0: TerminalNode? = null
    
    var factor_0_1: FactorContext? = null
    var lf: FactorContext? = null
    var factor_1_0: FactorContext? = null
    var rf: FactorContext? = null
       var value: Double by Delegates.notNull<Double>()
        }
    
    fun factorM(
        
        ): FactorMContext {
        val _localctx = FactorMContext("factorM",
        
        )
        when (token.type) {
       CalculatorTokenType.MINUS -> {
        // Terminal MINUS_0_0
        if (token.type != CalculatorTokenType.MINUS) {
            throw ParserException("Expected MINUS, got " + token.type)
        }
        val MINUS_0_0 = TerminalNode("MINUS_0_0", token.value)
        // no alias
        _localctx.MINUS_0_0 = MINUS_0_0
        // no alias
        _localctx.children.add(MINUS_0_0)
        nextToken()
    
        // no return
        // Non-terminal factor
        val factor_0_1 = factor(
        // no args
        )
        val lf = factor_0_1
        _localctx.factor_0_1 = factor_0_1
        _localctx.lf = factor_0_1
        _localctx.children.add(factor_0_1)
    
        _localctx.value = -lf.value
       return _localctx
    }
    
    CalculatorTokenType.NUMBER, CalculatorTokenType.LBR -> {
        // Non-terminal factor
        val factor_1_0 = factor(
        // no args
        )
        val rf = factor_1_0
        _localctx.factor_1_0 = factor_1_0
        _localctx.rf = factor_1_0
        _localctx.children.add(factor_1_0)
    
        _localctx.value = rf.value
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class FactorContext(name: String,
    
    ) : CalculatorNode(name) {
    var atom_0_0: AtomContext? = null
    var a: AtomContext? = null
    var factorP_0_1: FactorPContext? = null
    var f: FactorPContext? = null
       var value: Double by Delegates.notNull<Double>()
        }
    
    fun factor(
        
        ): FactorContext {
        val _localctx = FactorContext("factor",
        
        )
        when (token.type) {
       CalculatorTokenType.NUMBER, CalculatorTokenType.LBR -> {
        // Non-terminal atom
        val atom_0_0 = atom(
        // no args
        )
        val a = atom_0_0
        _localctx.atom_0_0 = atom_0_0
        _localctx.a = atom_0_0
        _localctx.children.add(atom_0_0)
    
        // no return
        // Non-terminal factorP
        val factorP_0_1 = factorP(
        a.value
        )
        val f = factorP_0_1
        _localctx.factorP_0_1 = factorP_0_1
        _localctx.f = factorP_0_1
        _localctx.children.add(factorP_0_1)
    
        _localctx.value = f.value
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class FactorPContext(name: String,
        val lhs: Double
    ) : CalculatorNode(name) {
    var LOG_0_0: TerminalNode? = null
    
    var factor_0_1: FactorContext? = null
    var f: FactorContext? = null
    // eps
       var value: Double by Delegates.notNull<Double>()
        }
    
    fun factorP(
            lhs: Double
        ): FactorPContext {
        val _localctx = FactorPContext("factorP",
            lhs
        )
        when (token.type) {
       CalculatorTokenType.LOG -> {
        // Terminal LOG_0_0
        if (token.type != CalculatorTokenType.LOG) {
            throw ParserException("Expected LOG, got " + token.type)
        }
        val LOG_0_0 = TerminalNode("LOG_0_0", token.value)
        // no alias
        _localctx.LOG_0_0 = LOG_0_0
        // no alias
        _localctx.children.add(LOG_0_0)
        nextToken()
    
        // no return
        // Non-terminal factor
        val factor_0_1 = factor(
        // no args
        )
        val f = factor_0_1
        _localctx.factor_0_1 = factor_0_1
        _localctx.f = factor_0_1
        _localctx.children.add(factor_0_1)
    
        _localctx.value = Math.log(_localctx.lhs) / Math.log(f.value)
       return _localctx
    }
    
    CalculatorTokenType.DIV, CalculatorTokenType.END, CalculatorTokenType.RBR, CalculatorTokenType.MUL, CalculatorTokenType.PLUS, CalculatorTokenType.MINUS -> {
        // Eps
        // do nothing
    
        _localctx.value = _localctx.lhs
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class AtomContext(name: String,
    
    ) : CalculatorNode(name) {
    var LBR_0_0: TerminalNode? = null
    
    var expr_0_1: ExprContext? = null
    var e: ExprContext? = null
    var RBR_0_2: TerminalNode? = null
    
    var NUMBER_1_0: TerminalNode? = null
    var n: TerminalNode? = null
       var value: Double by Delegates.notNull<Double>()
        }
    
    fun atom(
        
        ): AtomContext {
        val _localctx = AtomContext("atom",
        
        )
        when (token.type) {
       CalculatorTokenType.LBR -> {
        // Terminal LBR_0_0
        if (token.type != CalculatorTokenType.LBR) {
            throw ParserException("Expected LBR, got " + token.type)
        }
        val LBR_0_0 = TerminalNode("LBR_0_0", token.value)
        // no alias
        _localctx.LBR_0_0 = LBR_0_0
        // no alias
        _localctx.children.add(LBR_0_0)
        nextToken()
    
        // no return
        // Non-terminal expr
        val expr_0_1 = expr(
        // no args
        )
        val e = expr_0_1
        _localctx.expr_0_1 = expr_0_1
        _localctx.e = expr_0_1
        _localctx.children.add(expr_0_1)
    
        // no return
        // Terminal RBR_0_2
        if (token.type != CalculatorTokenType.RBR) {
            throw ParserException("Expected RBR, got " + token.type)
        }
        val RBR_0_2 = TerminalNode("RBR_0_2", token.value)
        // no alias
        _localctx.RBR_0_2 = RBR_0_2
        // no alias
        _localctx.children.add(RBR_0_2)
        nextToken()
    
        _localctx.value = e.value
       return _localctx
    }
    
    CalculatorTokenType.NUMBER -> {
        // Terminal NUMBER_1_0
        if (token.type != CalculatorTokenType.NUMBER) {
            throw ParserException("Expected NUMBER, got " + token.type)
        }
        val NUMBER_1_0 = TerminalNode("NUMBER_1_0", token.value)
        val n = NUMBER_1_0
        _localctx.NUMBER_1_0 = NUMBER_1_0
        _localctx.n = NUMBER_1_0
        _localctx.children.add(NUMBER_1_0)
        nextToken()
    
        _localctx.value = n.text.toDouble()
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class ExprOpContext(name: String,
    
    ) : CalculatorNode(name) {
    var PLUS_0_0: TerminalNode? = null
    var pl: TerminalNode? = null
    var MINUS_1_0: TerminalNode? = null
    var mn: TerminalNode? = null
    
        }
    
    fun exprOp(
        
        ): ExprOpContext {
        val _localctx = ExprOpContext("exprOp",
        
        )
        when (token.type) {
       CalculatorTokenType.PLUS -> {
        // Terminal PLUS_0_0
        if (token.type != CalculatorTokenType.PLUS) {
            throw ParserException("Expected PLUS, got " + token.type)
        }
        val PLUS_0_0 = TerminalNode("PLUS_0_0", token.value)
        val pl = PLUS_0_0
        _localctx.PLUS_0_0 = PLUS_0_0
        _localctx.pl = PLUS_0_0
        _localctx.children.add(PLUS_0_0)
        nextToken()
    
        // no return
       return _localctx
    }
    
    CalculatorTokenType.MINUS -> {
        // Terminal MINUS_1_0
        if (token.type != CalculatorTokenType.MINUS) {
            throw ParserException("Expected MINUS, got " + token.type)
        }
        val MINUS_1_0 = TerminalNode("MINUS_1_0", token.value)
        val mn = MINUS_1_0
        _localctx.MINUS_1_0 = MINUS_1_0
        _localctx.mn = MINUS_1_0
        _localctx.children.add(MINUS_1_0)
        nextToken()
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class TermOpContext(name: String,
    
    ) : CalculatorNode(name) {
    var MUL_0_0: TerminalNode? = null
    var ml: TerminalNode? = null
    var DIV_1_0: TerminalNode? = null
    var dv: TerminalNode? = null
    
        }
    
    fun termOp(
        
        ): TermOpContext {
        val _localctx = TermOpContext("termOp",
        
        )
        when (token.type) {
       CalculatorTokenType.MUL -> {
        // Terminal MUL_0_0
        if (token.type != CalculatorTokenType.MUL) {
            throw ParserException("Expected MUL, got " + token.type)
        }
        val MUL_0_0 = TerminalNode("MUL_0_0", token.value)
        val ml = MUL_0_0
        _localctx.MUL_0_0 = MUL_0_0
        _localctx.ml = MUL_0_0
        _localctx.children.add(MUL_0_0)
        nextToken()
    
        // no return
       return _localctx
    }
    
    CalculatorTokenType.DIV -> {
        // Terminal DIV_1_0
        if (token.type != CalculatorTokenType.DIV) {
            throw ParserException("Expected DIV, got " + token.type)
        }
        val DIV_1_0 = TerminalNode("DIV_1_0", token.value)
        val dv = DIV_1_0
        _localctx.DIV_1_0 = DIV_1_0
        _localctx.dv = DIV_1_0
        _localctx.children.add(DIV_1_0)
        nextToken()
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
}