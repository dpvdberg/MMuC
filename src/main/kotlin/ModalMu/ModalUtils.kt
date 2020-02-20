package ModalMu

import javax.swing.tree.VariableHeightLayoutCache

fun ModalFormula.getVariables() : List<Variable> {
    return emptyList()
}

fun ModalFormula.getFixedPointVariable(x : Variable) : FixedPoint {
    return FixedPoint.MU
}

fun ModalFormula.getSurroundingFixedPoint(x : Variable) : FixedPoint {
    return FixedPoint.MU
}

fun ModalFormula.getSubVariables() : List<Variable> {
    return emptyList()
}

fun ModalFormula.isSentence() : Boolean {
    return false
}