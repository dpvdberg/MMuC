package ModalMu

import javax.swing.tree.VariableHeightLayoutCache

fun ModalFormula.getFixedPoints() : List<Operator> {
    return emptyList()
}

fun ModalFormula.getSurroundingFixedPoint(x : Variable) : ModalFormula {
    return TrueProposition()
}

fun ModalFormula.computeIsSentence() {
    // a sentence is a modal mu formula with no free fixed point variables
    //https://staff.fnwi.uva.nl/j.vanbenthem/SahlmuFinal.pdf
}