package ModalMu

import javax.swing.tree.VariableHeightLayoutCache

fun ModalFormula.getFixedPoints() : List<ModalFormula> {
    return emptyList()
}

fun ModalFormula.getSurroundingFixedPoint(x : Variable) : ModalFormula {
    return TrueProposition()
}

fun ModalFormula.getSubVariables() : List<Variable> {
    return emptyList()
}

fun ModalFormula.isSentence() : Boolean {
    return false
}