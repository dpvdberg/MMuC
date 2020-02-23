package ModalMu.Parsing

import LTS.LabelledTransitionSystem
import LTS.Node
import LTS.Transition
import ModalMu.*
import ModalMu.Parsing.Grammar.ModalMuLexer
import ModalMu.Parsing.Grammar.ModalMuParser
import ModalMu.Parsing.Grammar.VisitModalMu
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class ModalMuParser {
    companion object {
        fun parse(formula : String): ModalFormula {
            val lexer = ModalMuLexer(CharStreams.fromString(formula))
            val tokens = CommonTokenStream(lexer)
            val parser = ModalMuParser(tokens)
            val visitor = VisitModalMu()

            val modalFormula = visitor.visit(parser.formula())
            setParent(modalFormula)
            return modalFormula
        }

        private fun setParent(formula: ModalFormula, parent : ModalFormula? = null) {
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