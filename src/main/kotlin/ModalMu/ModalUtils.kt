package ModalMu

import javax.swing.tree.VariableHeightLayoutCache

fun ModalFormula.getFixedPoints(): List<Operator> {
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

fun ModalFormula.getSurroundingFixedPoint(): Operator? {
    var f: ModalFormula? = this

    do {
        f = f?.parent
    } while (f != null && f !is Operator)

    return f as Operator?
}

fun ModalFormula.computeIsSentence() : Set<Variable> {
    // a sentence is a modal mu formula with no free fixed point variables
    //https://staff.fnwi.uva.nl/j.vanbenthem/SahlmuFinal.pdf

    when (this) {
        is Operator -> {
            val freeVar = this.body.computeIsSentence()
            if (freeVar.equals(setOf(this.variable))) {
                this.isSentence = true
                return emptySet()
            } else {
                this.isSentence = false
                return freeVar.minus(this.variable)
            }
        }
        is TrueProposition, is FalseProposition -> {
            this.isSentence = true
            return emptySet()
        }
        is Variable -> {
            this.isSentence = false
            return setOf(this)
        }
        is And -> {
            val freeVar = this.left.computeIsSentence().union(this.right.computeIsSentence())
            this.isSentence = freeVar.isEmpty()
            return freeVar
        }
        is Or -> {
            val freeVar = this.left.computeIsSentence().union(this.right.computeIsSentence())
            this.isSentence = freeVar.isEmpty()
            return freeVar
        }
        is Exists -> {
            val freeVar = this.body.computeIsSentence()
            this.isSentence = freeVar.isEmpty()
            return freeVar
        }
        is ForAll -> {
            val freeVar = this.body.computeIsSentence()
            this.isSentence = freeVar.isEmpty()
            return freeVar
        }
    }
}




















