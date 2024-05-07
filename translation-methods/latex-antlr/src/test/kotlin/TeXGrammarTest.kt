import org.antlr.v4.runtime.*
import kotlin.test.*

class TeXGrammarTest {
    private fun removeSpacesBetweenTags(html: String): String {
        val pattern = Regex("(>\\s*<)")
        return pattern.replace(html, ">\n<")
    }

    private fun test(input: String, expectedOutput: String) {
        try {
            val parser = TeXParser(CommonTokenStream(TeXLexer(CharStreams.fromString(input))))
            val visitor = HTMLVisitor()
            val html = visitor.visit(parser.document())
            assertEquals(removeSpacesBetweenTags(expectedOutput), removeSpacesBetweenTags(html))
            assertEquals(0, parser.numberOfSyntaxErrors)
        } catch (e: Exception) {
            fail(e.message)
        }
    }

    @Test
    fun testValidDocument() {
        val input = """
            \begin{document}
            $1+2$
            \end{document}
        """.trimIndent()
        val expectedOutput = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Document</title>
            </head>
            <body>
                <math display="block">
                    <mrow>
                        <mn>1</mn>
                        <mo>+</mo>
                        <mn>2</mn>
                    </mrow>
                </math>
            </body>
            </html>
        """.trimIndent()
        test(input, expectedOutput)
    }

    private fun testMath(input: String, expectedOutput: String) {
        try {
            val parser = TeXParser(CommonTokenStream(TeXLexer(CharStreams.fromString(input))))
            val visitor = HTMLVisitor()
            val html = visitor.visit(parser.mathExpression())
            assertEquals(removeSpacesBetweenTags(expectedOutput), removeSpacesBetweenTags(html))
            assertEquals(0, parser.numberOfSyntaxErrors)
        } catch (e: Exception) {
            fail(e.message)
        }
    }

    @Test
    fun testValidMath() {
        val input = "1+2+3"
        val expectedOutput = """
            <mrow>
                <mn>1</mn>
                <mo>+</mo>
                <mn>2</mn>
                <mo>+</mo>
                <mn>3</mn>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }

    @Test
    fun testUnaryMinus() {
        val input = "-1+2"
        val expectedOutput = """
            <mrow>
                <mo>-</mo>
                <mn>1</mn>
                <mo>+</mo>
                <mn>2</mn>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }

    @Test
    fun testBrackets() {
        val input = "(1+2)+3"
        val expectedOutput = """
            <mrow>
                <mrow>
                    <mo>(</mo>
                    <mrow>
                        <mn>1</mn>
                        <mo>+</mo>
                        <mn>2</mn>
                    </mrow>
                    <mo>)</mo>
                </mrow>
                <mo>+</mo>
                <mn>3</mn>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }

    @Test
    fun testCurlyBrackets() {
        val input = "{1+2}+3"
        val expectedOutput = """
            <mrow>
                <mrow>
                    <mn>1</mn>
                    <mo>+</mo>
                    <mn>2</mn>
                </mrow>
                <mo>+</mo>
                <mn>3</mn>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }

    @Test
    fun testSubscript() {
        val input = "a_1+2"
        val expectedOutput = """
            <mrow>
                <msub>
                    <mi>a</mi>
                    <mn>1</mn>
                </msub>
                <mo>+</mo>
                <mn>2</mn>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }

    @Test
    fun testSuperscript() {
        val input = "a^1+2"
        val expectedOutput = """
            <mrow>
                <msup>
                    <mi>a</mi>
                    <mn>1</mn>
                </msup>
                <mo>+</mo>
                <mn>2</mn>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }

    @Test
    fun testSubscriptAndSuperscript() {
        val input = "a_1^2+3"
        val expectedOutput = """
            <mrow>
                <msubsup>
                    <mi>a</mi>
                    <mn>1</mn>
                    <mn>2</mn>
                </msubsup>
                <mo>+</mo>
                <mn>3</mn>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }

    @Test
    fun testSubscriptAndSuperscriptWithBrackets() {
        val input = "a_{1+2}^{3+4}"
        val expectedOutput = """
            <mrow>
                <msubsup>
                    <mi>a</mi>
                    <mrow>
                        <mn>1</mn>
                        <mo>+</mo>
                        <mn>2</mn>
                    </mrow>
                    <mrow>
                        <mn>3</mn>
                        <mo>+</mo>
                        <mn>4</mn>
                    </mrow>
                </msubsup>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }

    private fun expectError(input: String) {
        try {
            val parser = TeXParser(CommonTokenStream(TeXLexer(CharStreams.fromString(input))))
            val visitor = HTMLVisitor()
            visitor.visit(parser.document())
            assertNotEquals(0, parser.numberOfSyntaxErrors)
        } catch (e: Exception) {
            // OK
        }
    }

    @Test
    fun testInvalidDocumentStructure() {
        val input = """
            \begin{document}
            $1+2$
            """
        expectError(input)
    }

    @Test
    fun testInvalidMathExpression() {
        val input = "1 +"
        expectError(input)
    }

    @Test
    fun testInvalidSubscript() {
        val input = "a_1+_2"
        expectError(input)
    }

    @Test
    fun testInvalidSuperscript() {
        val input = "a^1+^2"
        expectError(input)
    }

    @Test
    fun testInvalidSubscriptAndSuperscript() {
        val input = "a_1^2+^3"
        expectError(input)
    }

    @Test
    fun testInvalidSubscriptAndSuperscriptWithBrackets() {
        val input = "a_{1+2}^{3+4}+^5"
        expectError(input)
    }

    @Test
    fun testInvalidBrackets() {
        val input = "(1+2"
        expectError(input)
    }

    @Test
    fun testInvalidCurlyBrackets() {
        val input = "{1+2"
        expectError(input)
    }

    @Test
    fun testInvalidUnaryMinus() {
        val input = "-1+"
        expectError(input)
    }

    @Test
    fun testFrac() {
        val input = "\\frac{1}{2}"
        val expectedOutput = """
            <mrow>
                <mfrac>
                    <mrow>
                        <mn>1</mn>
                    </mrow>
                    <mrow>
                        <mn>2</mn>
                    </mrow>
                </mfrac>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }

    @Test
    fun testSqrt() {
        val input = "\\sqrt{2}"
        val expectedOutput = """
            <mrow>
                <msqrt>
                    <mrow>
                        <mn>2</mn>
                    </mrow>
                </msqrt>
            </mrow>
        """.trimIndent()
        testMath(input, expectedOutput)
    }
}