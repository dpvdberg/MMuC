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

class And(var left: ModalFormula, var right: ModalFormula) : ModalFormula() {
    override fun toString(): String {
        return "($left && $right)"
    }
}

class Exists(var label: String, var body: ModalFormula) : ModalFormula() {
    override fun toString(): String {
        return "<$label>$body"
    }
}

class ForAll(var label: String, var body: ModalFormula) : ModalFormula() {
    override fun toString(): String {
        return "[$label]$body"
    }
}

sealed class Operator(var variable: Variable, var body: ModalFormula) : ModalFormula() {

    class Mu(v: Variable, b: ModalFormula) : Operator(v, b) {
        override fun toString(): String {
            return "mu $variable. $body"
        }
    }

    class Nu(v: Variable, b: ModalFormula) : Operator(v, b) {
        override fun toString(): String {
            return "nu $variable. $body"
        }
    }

}

class Or(var left: ModalFormula, var right: ModalFormula) : ModalFormula() {
    override fun toString(): String {
        return "($left || $right)"
    }
}

object TrueProposition : ModalFormula() {
    override fun toString(): String {
        return "true"
    }
}

object FalseProposition : ModalFormula() {
    override fun toString(): String {
        return "false"
    }
}

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

    override fun toString(): String {
        return name.toString()
    }
}