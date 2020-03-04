package Evaluation

import LTS.LabelledTransitionSystem
import ModalMu.ModalFormula

abstract class MuFormulaChecker {
    abstract fun check(lts : LabelledTransitionSystem, formula : ModalFormula) : Boolean

    fun checkTimed(lts : LabelledTransitionSystem, formula : ModalFormula) : Pair<Boolean, Long> {
        val start = System.currentTimeMillis()
        val result = check(lts, formula)
        return Pair(result, System.currentTimeMillis() - start)
    }
}