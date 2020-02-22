package Evaluation

import LTS.LabelledTransitionSystem
import LTS.Node
import ModalMu.*

class ImprovedChecker : MuFormulaChecker {


    override fun check(lts: LabelledTransitionSystem, formula: ModalFormula): Boolean {
        val map = mutableMapOf<Variable, Set<Node>>()
        for (x in formula.getFixedPoints()) {
            when (x) {
                is Operator.Mu ->
                    map[x.variable] = emptySet()
                is Operator.Nu ->
                    map[x.variable] = lts.nodes.toSet()
            }
        }

        val alreadyEvaluated = mutableMapOf<ModalFormula, Boolean>().withDefault { false }
        val values = mutableMapOf<ModalFormula, Set<Node>>()

        val states: Set<Node> = eval(lts, formula, mutableMapOf(), alreadyEvaluated, values)
        return lts.initialNode in states
    }

    private fun eval(
        lts: LabelledTransitionSystem, f: ModalFormula, environment: MutableMap<Variable, Set<Node>>,
        alreadyEvaluated: MutableMap<ModalFormula, Boolean>, values: MutableMap<ModalFormula, Set<Node>>
    ): Set<Node> {
        var s = emptySet<Node>()

        if (alreadyEvaluated.getOrDefault(f, false)) {
            return values[f]!!
        }

        when (f) {
            is TrueProposition -> s = lts.nodes.toSet()
            is FalseProposition -> s = emptySet()
            is Variable -> s = environment.getOrDefault(f, emptySet())
            is And -> s = eval(lts, f.left, environment, alreadyEvaluated, values).intersect(
                eval(
                    lts,
                    f.right,
                    environment,
                    alreadyEvaluated,
                    values
                )
            )
            is Or -> s = eval(lts, f.left, environment, alreadyEvaluated, values).union(
                eval(
                    lts,
                    f.right,
                    environment,
                    alreadyEvaluated,
                    values
                )
            )
            is Exists -> {
                val subset = eval(lts, f.body, environment, alreadyEvaluated, values)
                // {s in S | exists t in subset : transition (s,t) has label a}
                s = lts.nodes.filter {
                    it.transitions.any {
                        it.destination in subset && it.label == f.label
                    }
                }.toSet()
            }
            is ForAll -> {
                val subset = eval(lts, f.body, environment, alreadyEvaluated, values)
                // {n in S | forall m in S: transition (n,m) has label a => m in subset}
                // equal to:
                // {n in S | forall m in S: !(transition (n,m) has label a) || m in subset}
                s = lts.nodes.filter { n ->
                    lts.nodes.all { m ->
                        n.transitions.none {
                            (it.destination.equals(m) && it.label == f.label)
                        } || m in subset
                    }
                }.toSet()
            }
            is Operator ->
                when (f) {
                    is Operator.Mu -> {
                        if (f.getSurroundingFixedPoint(f.variable) is Operator.Nu) {
                            environment[f.variable] = emptySet()
                            for (g in f.getSubVariables()) {
                                environment[g] = emptySet()
                            }
                        }
                        do {
                            s = environment[f.variable]!!
                            environment[f.variable] = eval(lts, f.body, environment, alreadyEvaluated, values)
                        } while (environment[f.variable]!! != s)
                    }
                    is Operator.Nu -> {
                        if (f.getSurroundingFixedPoint(f.variable) is Operator.Mu) {
                            environment[f.variable] = lts.nodes.toSet()
                            for (g in f.getSubVariables()) {
                                environment[g] = emptySet()
                            }
                        }
                        do {
                            s = environment[f.variable]!!
                            environment[f.variable] = eval(lts, f.body, environment, alreadyEvaluated, values)
                        } while (environment[f.variable]!! != s)
                    }
                }
        }


        if (f.isSentence()) {
            alreadyEvaluated[f] = true
            values[f] = s
        }

        return s
    }
}