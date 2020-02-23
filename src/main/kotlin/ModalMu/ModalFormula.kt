package ModalMu

sealed class ModalFormula(var isSentence : Boolean? = null, var parent : ModalFormula? = null)

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

class Variable(var name: Char) : ModalFormula()