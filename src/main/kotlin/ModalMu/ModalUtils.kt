package ModalMu

import javax.swing.tree.VariableHeightLayoutCache

fun ModalFormula.getFixedPoints() : List<Operator> {
    return when (this) {
        is Operator.Mu -> listOf(this) + this.body.getFixedPoints()
        is Operator.Nu -> listOf(this) + this.body.getFixedPoints()
        is And -> this.left.getFixedPoints() + this.right.getFixedPoints()
        is Or -> this.left.getFixedPoints() + this.right.getFixedPoints()
        is Exists -> this.body.getFixedPoints()
        is ForAll -> this.body.getFixedPoints()
        is TrueProposition -> emptyList()
        is FalseProposition -> emptyList()
        is Variable -> emptyList()
    }
}

fun Operator.getSurroundingFixedPoint() : ModalFormula {
    return TrueProposition
}

fun ModalFormula.computeIsSentence() {
    // a sentence is a modal mu formula with no free fixed point variables
    //https://staff.fnwi.uva.nl/j.vanbenthem/SahlmuFinal.pdf
}