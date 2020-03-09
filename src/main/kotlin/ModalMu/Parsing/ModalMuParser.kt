package ModalMu.Parsing

import Exception.ParsingException
import ModalMu.*
import ModalMu.Parsing.Grammar.ModalMuLexer
import ModalMu.Parsing.Grammar.ModalMuParser
import ModalMu.Parsing.Grammar.VisitModalMu
import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.red
import com.andreapivetta.kolor.yellow
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import printlndbg

class ModalMuParser {
    companion object {
        fun parse(formula: String): ModalFormula {
            printlndbg("Parsing to modal mu formula: $formula".yellow())

            val lexer = ModalMuLexer(CharStreams.fromString(formula))
            val tokens = CommonTokenStream(lexer)
            val parser = ModalMuParser(tokens)
            val visitor = VisitModalMu()

            val modalFormula = visitor.visit(parser.formula())

            if (parser.numberOfSyntaxErrors > 0) {
                printlndbg("Errors found during mu formula parsing.".red())
                throw ParsingException("Syntax error in formula: $formula")
            }

            setParent(modalFormula)
            printlndbg("Parse success".green())
            return modalFormula
        }

        private fun setParent(formula: ModalFormula, parent: ModalFormula? = null) {
            when (formula) {
                is Operator -> {
                    formula.parent = parent
                    setParent(formula.body, formula)
                }
                is TrueProposition, is FalseProposition, is Variable -> formula.parent = parent
                is Or -> {
                    formula.parent = parent
                    setParent(formula.left, formula)
                    setParent(formula.right, formula)
                }
                is And -> {
                    formula.parent = parent
                    setParent(formula.left, formula)
                    setParent(formula.right, formula)
                }
                is Exists -> {
                    formula.parent = parent
                    setParent(formula.body, formula)
                }
                is ForAll -> {
                    formula.parent = parent
                    setParent(formula.body, formula)
                }
            }
        }
    }
}