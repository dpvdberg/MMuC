package Facade

import Evaluation.ImprovedChecker
import Evaluation.MuFormulaChecker
import Evaluation.NaiveChecker
import LTS.LabelledTransitionSystem
import LTS.Parsing.AldebaranParser
import ModalMu.Parsing.ModalMuParser

class ModalMuFacade {
    companion object {
        fun checkFormulaOnLTS(lts: String, formula: String, checker: MuFormulaChecker) : Boolean {
            val parsedLTS = AldebaranParser.parse(lts.lineSequence())
            val f = ModalMuParser.parse(formula)

            return checker.check(parsedLTS, f)
        }
    }
}

fun LabelledTransitionSystem.checkNaiveFormula(formula: String) : Boolean {
    val f = ModalMuParser.parse(formula)
    val naiveChecker = NaiveChecker()

    return naiveChecker.check(this, f)
}

fun LabelledTransitionSystem.checkImprovedFormula(formula: String) : Boolean {
    val f = ModalMuParser.parse(formula)
    val improvedChecker = ImprovedChecker()

    return improvedChecker.check(this, f)
}