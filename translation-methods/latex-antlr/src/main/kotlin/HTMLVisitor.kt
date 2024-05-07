class HTMLVisitor : TeXBaseVisitor<String>() {
    override fun visitDocument(ctx: TeXParser.DocumentContext): String {
        val body = visit(ctx.body())
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Document</title>
            </head>
            <body>
            $body
            </body>
            </html>
        """.trimIndent()
    }

    override fun visitBody(ctx: TeXParser.BodyContext): String {
        val mathExpressions = ctx.math().map { visit(it) }
        return mathExpressions.joinToString("\n")
    }

    override fun visitMath(ctx: TeXParser.MathContext): String {
        val mathExpression = visit(ctx.mathExpression())
        return "<math display=\"block\">$mathExpression</math>"
    }

    override fun visitMathExpression(ctx: TeXParser.MathExpressionContext): String {
        val elements = mutableListOf<String>()
        for (i in 0 until ctx.mathObject().size) {
            val mathObject = visit(ctx.mathObject(i))
            elements.add(mathObject)
            if (i < ctx.mathOperator().size) {
                val mathOperator = ctx.mathOperator(i).text
                elements.add(convertOperator(mathOperator))
            }
        }
        val mathExpression = elements.joinToString("")
        return "<mrow>$mathExpression</mrow>"
    }

    private fun convertOperator(operator: String): String {
        return when (operator) {
            "+" -> "<mo>+</mo>"
            "-" -> "<mo>-</mo>"
            "*" -> "<mo>&#x22C5;</mo>"  // Unicode for dot operator
            "/" -> "<mo>&#xF7;</mo>"  // Unicode for division operator
            "=" -> "<mo>=</mo>"
            else -> ""
        }
    }

    override fun visitMathObject(ctx: TeXParser.MathObjectContext): String {
    val mathUnaryOperator = ctx.mathUnaryOperator()?.text ?: ""
    val mathUnit = visit(ctx.mathUnit())
    val unitWithUnaryOperator = "${convertOperator(mathUnaryOperator)}$mathUnit"
    val mathSubscript = ctx.mathSubscript()?.let { visit(it.mathUnit()) } ?: ""
    val mathSuperscript = ctx.mathSuperscript()?.let { visit(it.mathUnit()) } ?: ""
    return when {
        mathSubscript.isNotEmpty() && mathSuperscript.isNotEmpty() ->
            "<msubsup>$unitWithUnaryOperator$mathSubscript$mathSuperscript</msubsup>"
        mathSubscript.isNotEmpty() -> "<msub>$unitWithUnaryOperator$mathSubscript</msub>"
        mathSuperscript.isNotEmpty() -> "<msup>$unitWithUnaryOperator$mathSuperscript</msup>"
        else -> unitWithUnaryOperator
    }
}


    override fun visitMathUnit(ctx: TeXParser.MathUnitContext): String {
        return when {
            ctx.mathBracket() != null -> visit(ctx.mathBracket())
            ctx.mathBlock() != null -> visit(ctx.mathBlock())
            ctx.mathFraction() != null -> visit(ctx.mathFraction())
            ctx.mathSquareRoot() != null -> visit(ctx.mathSquareRoot())
            ctx.number() != null -> "<mn>${ctx.number().text}</mn>"
            ctx.variable() != null -> "<mi>${ctx.variable().text}</mi>"
            else -> throw IllegalArgumentException("Unknown math unit: ${ctx.text}")
        }
    }

    override fun visitMathBracket(ctx: TeXParser.MathBracketContext): String {
        val mathExpression = visit(ctx.mathExpression())
        return "<mrow><mo>(</mo>$mathExpression<mo>)</mo></mrow>"
    }

    override fun visitMathBlock(ctx: TeXParser.MathBlockContext): String {
        return visit(ctx.mathExpression())
    }

    override fun visitMathFraction(ctx: TeXParser.MathFractionContext): String {
        val numerator = visit(ctx.mathBlock(0))
        val denominator = visit(ctx.mathBlock(1))
        return "<mfrac>$numerator$denominator</mfrac>"
    }

    override fun visitMathSquareRoot(ctx: TeXParser.MathSquareRootContext): String {
        val mathExpression = visit(ctx.mathBlock())
        return "<msqrt>$mathExpression</msqrt>"
    }
}