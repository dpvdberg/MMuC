package Evaluation

import LTS.LabelledTransitionSystem
import ModalMu.ModalFormula

interface MuFormulaChecker {
    fun check(lts : LabelledTransitionSystem, formula : ModalFormula) : Boolean
}