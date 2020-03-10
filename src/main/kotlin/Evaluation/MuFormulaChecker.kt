package Evaluation

import LTS.LabelledTransitionSystem
import ModalMu.ModalFormula

abstract class MuFormulaChecker {
    abstract fun check(lts : LabelledTransitionSystem, formula : ModalFormula) : Boolean

    private fun getTime(useMs : Boolean) : Long {
        return if (useMs) {
            System.currentTimeMillis()
        } else {
            System.nanoTime()
        }
    }

    fun checkTimed(lts : LabelledTransitionSystem, formula : ModalFormula, useMs : Boolean = true) : Pair<Boolean, Long> {
        val start = getTime(useMs)
        val result = check(lts, formula)
        return Pair(result, getTime(useMs) - start)
    }
}