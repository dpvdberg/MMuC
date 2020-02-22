package ModalMu.Parsing.Grammar

import ModalMu.*

class VisitModalMu : ModalMuBaseVisitor<ModalFormula>() {
    override fun visitTrueFormula(ctx: ModalMuParser.TrueFormulaContext?): TrueProposition {
        return TrueProposition
    }

    override fun visitFalseFormula(ctx: ModalMuParser.FalseFormulaContext?): FalseProposition {
        return FalseProposition
    }

    override fun visitBoxFormula(ctx: ModalMuParser.BoxFormulaContext?): ForAll {
        return ForAll(ctx!!.label.text, this.visit(ctx.formula()))
    }

    override fun visitDiamondFormula(ctx: ModalMuParser.DiamondFormulaContext?): Exists {
        return Exists(ctx!!.label.text, this.visit(ctx.formula()))
    }

    override fun visitVarFormula(ctx: ModalMuParser.VarFormulaContext?): Variable {
        return Variable(getChar(ctx!!.variable.text))
    }

    override fun visitMuFormula(ctx: ModalMuParser.MuFormulaContext?): Operator.Mu {
        return Operator.Mu(Variable(getChar(ctx!!.variable.text)), this.visit(ctx.formula()))
    }

    override fun visitNuFormula(ctx: ModalMuParser.NuFormulaContext?): Operator.Nu {
        return Operator.Nu(Variable(getChar(ctx!!.variable.text)), this.visit(ctx.formula()))
    }

    override fun visitOrAndFormula(ctx: ModalMuParser.OrAndFormulaContext?): ModalFormula {
        val left = this.visit(ctx!!.left)
        val right = this.visit(ctx.right)

        return when (ctx.op.type) {
            ModalMuParser.OR -> Or(left, right)
            ModalMuParser.AND -> And(left, right)
            else -> throw IllegalStateException("operation not found")
        }
    }

    private fun getChar(variable: String): Char {
        val charArray = variable.toCharArray()
        if (charArray.size > 1) {
            throw IllegalStateException("Found malformed variable of illegal size")
        }
        return charArray[0]
    }
}