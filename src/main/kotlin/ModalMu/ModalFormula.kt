package ModalMu

sealed class ModalFormula(var isSentence : Boolean? = null)

class And(var left : ModalFormula, var right : ModalFormula) : ModalFormula()

class Exists(var label : String, var body : ModalFormula) : ModalFormula()

class ForAll(var label : String, var body : ModalFormula) : ModalFormula()

sealed class Operator(var variable : Variable, var body : ModalFormula) : ModalFormula() {

    class Mu(var v : Variable, var b : ModalFormula) : Operator(v, b)

    class Nu(var v : Variable, var b : ModalFormula) : Operator(v, b)

}

class Or(var left : ModalFormula, var right : ModalFormula) : ModalFormula()

class TrueProposition() : ModalFormula()

class FalseProposition() : ModalFormula()

class Variable(var name : Char) : ModalFormula()