package ModalMu.Parsing.Grammar

import ModalMu.*
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

        assert(modalFormula is Nu)
        assert((modalFormula as Nu).body is Mu)
        assert((modalFormula.body as Mu).body is Or)

        assert(((modalFormula.body as Mu).body as Or).left is Or)

        assert((((modalFormula.body as Mu).body as Or).left as Or).right is Exists)
        assert(((((modalFormula.body as Mu).body as Or).left as Or).right as Exists).body is Variable)
        assert(((((modalFormula.body as Mu).body as Or).left as Or).right as Exists).label == "i")

        assert((((modalFormula.body as Mu).body as Or).left as Or).left is Exists)
        assert(((((modalFormula.body as Mu).body as Or).left as Or).left as Exists).body is Variable)
        assert(((((modalFormula.body as Mu).body as Or).left as Or).left as Exists).label == "plato")

        assert(((modalFormula.body as Mu).body as Or).right is Exists)
        assert((((modalFormula.body as Mu).body as Or).right as Exists).body is Variable)
        assert((((modalFormula.body as Mu).body as Or).right as Exists).label == "others")
    }
}