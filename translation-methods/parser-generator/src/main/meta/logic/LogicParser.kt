package logic
import kotlin.properties.Delegates

class LogicParser(private val lexer: LogicLexer) {
    class ParserException(message: String) : Exception(message)
    class TerminalNode(name: String, val text: String) : LogicNode(name + ": " + text)
    var token = lexer.nextToken()

    fun nextToken() {
        token = lexer.nextToken()
    }

    fun parse(
    
    ): XorContext {
        val res = xor()
    
        if (token.type == LogicTokenType.END) {
            return res
        } else {
            throw ParserException("Expected END, got " + token.type)
        }
    }

    class XorContext(name: String,
    
    ) : LogicNode(name) {
    var or_0_0: OrContext? = null
    
    var xorP_0_1: XorPContext? = null
    
    
        }
    
    fun xor(
        
        ): XorContext {
        val _localctx = XorContext("xor",
        
        )
        when (token.type) {
       LogicTokenType.NOT, LogicTokenType.VAR, LogicTokenType.CONST, LogicTokenType.LBR -> {
        // Non-terminal or
        val or_0_0 = or(
        // no args
        )
        // no alias
        _localctx.or_0_0 = or_0_0
        // no alias
        _localctx.children.add(or_0_0)
    
        // no return
        // Non-terminal xorP
        val xorP_0_1 = xorP(
        // no args
        )
        // no alias
        _localctx.xorP_0_1 = xorP_0_1
        // no alias
        _localctx.children.add(xorP_0_1)
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class XorPContext(name: String,
    
    ) : LogicNode(name) {
    var XOR_0_0: TerminalNode? = null
    
    var and_0_1: AndContext? = null
    
    var xorP_0_2: XorPContext? = null
    
    // eps
    
        }
    
    fun xorP(
        
        ): XorPContext {
        val _localctx = XorPContext("xorP",
        
        )
        when (token.type) {
       LogicTokenType.XOR -> {
        // Terminal XOR_0_0
        if (token.type != LogicTokenType.XOR) {
            throw ParserException("Expected XOR, got " + token.type)
        }
        val XOR_0_0 = TerminalNode("XOR_0_0", token.value)
        // no alias
        _localctx.XOR_0_0 = XOR_0_0
        // no alias
        _localctx.children.add(XOR_0_0)
        nextToken()
    
        // no return
        // Non-terminal and
        val and_0_1 = and(
        // no args
        )
        // no alias
        _localctx.and_0_1 = and_0_1
        // no alias
        _localctx.children.add(and_0_1)
    
        // no return
        // Non-terminal xorP
        val xorP_0_2 = xorP(
        // no args
        )
        // no alias
        _localctx.xorP_0_2 = xorP_0_2
        // no alias
        _localctx.children.add(xorP_0_2)
    
        // no return
       return _localctx
    }
    
    LogicTokenType.END, LogicTokenType.RBR -> {
        // Eps
        // do nothing
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class OrContext(name: String,
    
    ) : LogicNode(name) {
    var and_0_0: AndContext? = null
    
    var orP_0_1: OrPContext? = null
    
    
        }
    
    fun or(
        
        ): OrContext {
        val _localctx = OrContext("or",
        
        )
        when (token.type) {
       LogicTokenType.NOT, LogicTokenType.VAR, LogicTokenType.CONST, LogicTokenType.LBR -> {
        // Non-terminal and
        val and_0_0 = and(
        // no args
        )
        // no alias
        _localctx.and_0_0 = and_0_0
        // no alias
        _localctx.children.add(and_0_0)
    
        // no return
        // Non-terminal orP
        val orP_0_1 = orP(
        // no args
        )
        // no alias
        _localctx.orP_0_1 = orP_0_1
        // no alias
        _localctx.children.add(orP_0_1)
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class OrPContext(name: String,
    
    ) : LogicNode(name) {
    var OR_0_0: TerminalNode? = null
    
    var and_0_1: AndContext? = null
    
    var orP_0_2: OrPContext? = null
    
    // eps
    
        }
    
    fun orP(
        
        ): OrPContext {
        val _localctx = OrPContext("orP",
        
        )
        when (token.type) {
       LogicTokenType.OR -> {
        // Terminal OR_0_0
        if (token.type != LogicTokenType.OR) {
            throw ParserException("Expected OR, got " + token.type)
        }
        val OR_0_0 = TerminalNode("OR_0_0", token.value)
        // no alias
        _localctx.OR_0_0 = OR_0_0
        // no alias
        _localctx.children.add(OR_0_0)
        nextToken()
    
        // no return
        // Non-terminal and
        val and_0_1 = and(
        // no args
        )
        // no alias
        _localctx.and_0_1 = and_0_1
        // no alias
        _localctx.children.add(and_0_1)
    
        // no return
        // Non-terminal orP
        val orP_0_2 = orP(
        // no args
        )
        // no alias
        _localctx.orP_0_2 = orP_0_2
        // no alias
        _localctx.children.add(orP_0_2)
    
        // no return
       return _localctx
    }
    
    LogicTokenType.XOR, LogicTokenType.END, LogicTokenType.RBR -> {
        // Eps
        // do nothing
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class AndContext(name: String,
    
    ) : LogicNode(name) {
    var not_0_0: NotContext? = null
    
    var andP_0_1: AndPContext? = null
    
    
        }
    
    fun and(
        
        ): AndContext {
        val _localctx = AndContext("and",
        
        )
        when (token.type) {
       LogicTokenType.NOT, LogicTokenType.VAR, LogicTokenType.CONST, LogicTokenType.LBR -> {
        // Non-terminal not
        val not_0_0 = not(
        // no args
        )
        // no alias
        _localctx.not_0_0 = not_0_0
        // no alias
        _localctx.children.add(not_0_0)
    
        // no return
        // Non-terminal andP
        val andP_0_1 = andP(
        // no args
        )
        // no alias
        _localctx.andP_0_1 = andP_0_1
        // no alias
        _localctx.children.add(andP_0_1)
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class AndPContext(name: String,
    
    ) : LogicNode(name) {
    var AND_0_0: TerminalNode? = null
    
    var atom_0_1: AtomContext? = null
    
    var andP_0_2: AndPContext? = null
    
    // eps
    
        }
    
    fun andP(
        
        ): AndPContext {
        val _localctx = AndPContext("andP",
        
        )
        when (token.type) {
       LogicTokenType.AND -> {
        // Terminal AND_0_0
        if (token.type != LogicTokenType.AND) {
            throw ParserException("Expected AND, got " + token.type)
        }
        val AND_0_0 = TerminalNode("AND_0_0", token.value)
        // no alias
        _localctx.AND_0_0 = AND_0_0
        // no alias
        _localctx.children.add(AND_0_0)
        nextToken()
    
        // no return
        // Non-terminal atom
        val atom_0_1 = atom(
        // no args
        )
        // no alias
        _localctx.atom_0_1 = atom_0_1
        // no alias
        _localctx.children.add(atom_0_1)
    
        // no return
        // Non-terminal andP
        val andP_0_2 = andP(
        // no args
        )
        // no alias
        _localctx.andP_0_2 = andP_0_2
        // no alias
        _localctx.children.add(andP_0_2)
    
        // no return
       return _localctx
    }
    
    LogicTokenType.XOR, LogicTokenType.END, LogicTokenType.OR, LogicTokenType.RBR -> {
        // Eps
        // do nothing
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class NotContext(name: String,
    
    ) : LogicNode(name) {
    var NOT_0_0: TerminalNode? = null
    
    var not_0_1: NotContext? = null
    
    var atom_1_0: AtomContext? = null
    
    
        }
    
    fun not(
        
        ): NotContext {
        val _localctx = NotContext("not",
        
        )
        when (token.type) {
       LogicTokenType.NOT -> {
        // Terminal NOT_0_0
        if (token.type != LogicTokenType.NOT) {
            throw ParserException("Expected NOT, got " + token.type)
        }
        val NOT_0_0 = TerminalNode("NOT_0_0", token.value)
        // no alias
        _localctx.NOT_0_0 = NOT_0_0
        // no alias
        _localctx.children.add(NOT_0_0)
        nextToken()
    
        // no return
        // Non-terminal not
        val not_0_1 = not(
        // no args
        )
        // no alias
        _localctx.not_0_1 = not_0_1
        // no alias
        _localctx.children.add(not_0_1)
    
        // no return
       return _localctx
    }
    
    LogicTokenType.VAR, LogicTokenType.CONST, LogicTokenType.LBR -> {
        // Non-terminal atom
        val atom_1_0 = atom(
        // no args
        )
        // no alias
        _localctx.atom_1_0 = atom_1_0
        // no alias
        _localctx.children.add(atom_1_0)
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
    
    class AtomContext(name: String,
    
    ) : LogicNode(name) {
    var LBR_0_0: TerminalNode? = null
    
    var xor_0_1: XorContext? = null
    
    var RBR_0_2: TerminalNode? = null
    
    var VAR_1_0: TerminalNode? = null
    
    var CONST_2_0: TerminalNode? = null
    
    
        }
    
    fun atom(
        
        ): AtomContext {
        val _localctx = AtomContext("atom",
        
        )
        when (token.type) {
       LogicTokenType.LBR -> {
        // Terminal LBR_0_0
        if (token.type != LogicTokenType.LBR) {
            throw ParserException("Expected LBR, got " + token.type)
        }
        val LBR_0_0 = TerminalNode("LBR_0_0", token.value)
        // no alias
        _localctx.LBR_0_0 = LBR_0_0
        // no alias
        _localctx.children.add(LBR_0_0)
        nextToken()
    
        // no return
        // Non-terminal xor
        val xor_0_1 = xor(
        // no args
        )
        // no alias
        _localctx.xor_0_1 = xor_0_1
        // no alias
        _localctx.children.add(xor_0_1)
    
        // no return
        // Terminal RBR_0_2
        if (token.type != LogicTokenType.RBR) {
            throw ParserException("Expected RBR, got " + token.type)
        }
        val RBR_0_2 = TerminalNode("RBR_0_2", token.value)
        // no alias
        _localctx.RBR_0_2 = RBR_0_2
        // no alias
        _localctx.children.add(RBR_0_2)
        nextToken()
    
        // no return
       return _localctx
    }
    
    LogicTokenType.VAR -> {
        // Terminal VAR_1_0
        if (token.type != LogicTokenType.VAR) {
            throw ParserException("Expected VAR, got " + token.type)
        }
        val VAR_1_0 = TerminalNode("VAR_1_0", token.value)
        // no alias
        _localctx.VAR_1_0 = VAR_1_0
        // no alias
        _localctx.children.add(VAR_1_0)
        nextToken()
    
        // no return
       return _localctx
    }
    
    LogicTokenType.CONST -> {
        // Terminal CONST_2_0
        if (token.type != LogicTokenType.CONST) {
            throw ParserException("Expected CONST, got " + token.type)
        }
        val CONST_2_0 = TerminalNode("CONST_2_0", token.value)
        // no alias
        _localctx.CONST_2_0 = CONST_2_0
        // no alias
        _localctx.children.add(CONST_2_0)
        nextToken()
    
        // no return
       return _localctx
    }
    
           else -> throw ParserException("Got " + token.type)
       }
    }
    
}