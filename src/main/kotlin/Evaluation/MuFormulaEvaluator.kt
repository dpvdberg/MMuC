package Evaluation

import LTS.LabelledTransitionSystem
import ModalMu.ModalFormula

interface MuFormulaEvaluator {
    fun evaluate(lts : LabelledTransitionSystem, formula : ModalFormula) : Boolean
}