package ModalMu

sealed class ModalFormula

class And(var left : ModalFormula, var right : ModalFormula) : ModalFormula()

class Exists(var label : String, var body : ModalFormula) : ModalFormula()

class ForAll(var label : String, var body : ModalFormula) : ModalFormula()

class Mu(var variable : Variable, var body : ModalFormula) : ModalFormula()

class Nu(var variable : Variable, var body : ModalFormula) : ModalFormula()

class Or(var left : ModalFormula, var right : ModalFormula) : ModalFormula()

class TrueProposition() : ModalFormula()

class FalseProposition() : ModalFormula()

class Variable(var name : Char) : ModalFormula()