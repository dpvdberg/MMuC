package ModalMu

sealed class ModalFormula(var parent : ModalFormula? = null) {
    private var sequence : Boolean? = null

    var isSentence : Boolean
        get() {
            if (sequence == null) {
                this.computeIsSentence()
            }
            return sequence!!
        }
        set(value) {
            sequence = value
        }
}

class And(var left: ModalFormula, var right: ModalFormula) : ModalFormula()

class Exists(var label: String, var body: ModalFormula) : ModalFormula()

class ForAll(var label: String, var body: ModalFormula) : ModalFormula()

sealed class Operator(var variable: Variable, var body: ModalFormula) : ModalFormula() {

    class Mu(v: Variable, b: ModalFormula) : Operator(v, b)

    class Nu(v: Variable, b: ModalFormula) : Operator(v, b)

}

class Or(var left: ModalFormula, var right: ModalFormula) : ModalFormula()

object TrueProposition : ModalFormula()

object FalseProposition : ModalFormula()

class Variable(var name: Char) : ModalFormula() {
    override fun equals(other: Any?): Boolean {
        if (other is Variable) {
            return name == other.name
        }
        return false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}