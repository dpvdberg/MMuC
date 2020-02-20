package Evaluation

import LTS.LabelledTransitionSystem
import LTS.Node
import ModalMu.*

class ImprovedChecker : MuFormulaChecker {


    override fun check(lts: LabelledTransitionSystem, formula: ModalFormula): Boolean {
        var map = mutableMapOf<Variable, Set<Node>>()
        for (x in formula.getVariables()) {
            if (formula.getFixedPointVariable(x) == FixedPoint.MU) {
                map[x] = emptySet()
            } else {
                map[x] = lts.nodes.toSet()
            }
        }

        var alreadyEvaluated = mutableMapOf<ModalFormula, Boolean>().withDefault { false }
        var values = mutableMapOf<ModalFormula, Set<Node>>()

        var states : Set<Node> = eval(lts, formula, mutableMapOf(), alreadyEvaluated, values)
        return lts.initialNode in states
    }

    private fun eval(lts: LabelledTransitionSystem, f: ModalFormula, environment : MutableMap<Variable, Set<Node>>,
                     alreadyEvaluated  : MutableMap<ModalFormula,Boolean>, values : MutableMap<ModalFormula, Set<Node>>): Set<Node> {
        var s = emptySet<Node>()

        if (alreadyEvaluated.getOrDefault(f, false)) {
            return values[f]!!
        }

        when (f) {
            is TrueProposition -> s = lts.nodes.toSet()
            is FalseProposition -> s = emptySet()
            is Variable -> s = environment.getOrDefault(f, emptySet())
            is And -> s = eval(lts, f.left, environment, alreadyEvaluated, values).intersect(eval(lts, f.right, environment, alreadyEvaluated, values))
            is Or -> s = eval(lts, f.left, environment, alreadyEvaluated, values).union(eval(lts, f.right, environment, alreadyEvaluated, values))
            is Exists -> {
                var subset = eval(lts, f.body, environment, alreadyEvaluated, values)
                // {s in S | exists t in subset : transition (s,t) has label a}
                s = lts.nodes.filter{
                    it.transitions.any {
                        it.destination in subset && it.label == f.label
                    }
                }.toSet()
            }
            is ForAll -> {
                var subset = eval(lts, f.body, environment, alreadyEvaluated, values)
                // {n in S | forall m in S: transition (n,m) has label a => m in subset}
                // equal to:
                // {n in S | forall m in S: !(transition (n,m) has label a) || m in subset}
                s = lts.nodes.filter{n ->
                    lts.nodes.all{m ->
                        n.transitions.none {
                            (it.destination.equals(m) && it.label == f.label)
                        } || m in subset
                    }
                }.toSet()
            }
            is Mu -> {
                if (f.getSurroundingFixedPoint(f.variable).equals(FixedPoint.NU)) {
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
            is Nu -> {
                if (f.getSurroundingFixedPoint(f.variable).equals(FixedPoint.MU)) {
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


        if (f.isSentence()) {
            alreadyEvaluated[f] = true
            values[f] = s
        }

        return s
    }
}