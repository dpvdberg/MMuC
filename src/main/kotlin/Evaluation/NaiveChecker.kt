package Evaluation

import LTS.LabelledTransitionSystem
import LTS.Node
import ModalMu.*

class NaiveChecker : MuFormulaChecker {


    override fun check(lts: LabelledTransitionSystem, formula: ModalFormula): Boolean {
        var states : Set<Node> = eval(lts, formula, mutableMapOf())
        return lts.initialNode in states
    }

    private fun eval(lts: LabelledTransitionSystem, f: ModalFormula, environment : MutableMap<Variable, Set<Node>>): Set<Node> {
        var s = emptySet<Node>()

        when (f) {
            is TrueProposition -> s = lts.nodes.toSet()
            is FalseProposition -> s = emptySet()
            is Variable -> s = environment.getOrDefault(f, emptySet())
            is And -> s = eval(lts, f.left, environment).intersect(eval(lts, f.right, environment))
            is Or -> s = eval(lts, f.left, environment).union(eval(lts, f.right, environment))
            is Exists -> {
                var subset = eval(lts, f.body, environment)
                // {s in S | exists t in subset : transition (s,t) has label a}
                s = lts.nodes.filter{
                    it.transitions.any {
                        it.destination in subset && it.label == f.label
                    }
                }.toSet()
            }
            is ForAll -> {
                var subset = eval(lts, f.body, environment)
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
            }
            is Nu -> {

            }
        }

        return s
    }
}