package ModalMu.Parsing.Grammar

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Test

internal class GrammarTest {

    @Test
    fun parse() {
        val lexer = ModalMuLexer(CharStreams.fromString("nu X. mu Y. ( (<plato>X || <i>Y) || <others>Y)"))
        val tokens = CommonTokenStream(lexer)
        val parser = ModalMuParser(tokens)
        val visitor = VisitModalMu()

        val modalFormula = visitor.visit(parser.formula())

        System.out.println("done.")
    }
}