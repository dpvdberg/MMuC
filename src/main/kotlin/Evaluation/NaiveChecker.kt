package Evaluation

import LTS.LabelledTransitionSystem
import LTS.Node
import ModalMu.*

class NaiveChecker : MuFormulaChecker {



    override fun check(lts: LabelledTransitionSystem, formula: ModalFormula): Boolean {
        val states: Set<Node> = eval(lts, formula, mutableMapOf())
        return lts.initialNode in states
    }

    private fun eval(
        lts: LabelledTransitionSystem,
        f: ModalFormula,
        environment: MutableMap<Variable, Set<Node>>
    ): Set<Node> {
        var s = emptySet<Node>()

        when (f) {
            is TrueProposition -> s = lts.nodes.toSet()
            is FalseProposition -> s = emptySet()
            is Variable -> s = environment.getOrDefault(f, emptySet())
            is And -> s = eval(lts, f.left, environment).intersect(eval(lts, f.right, environment))
            is Or -> s = eval(lts, f.left, environment).union(eval(lts, f.right, environment))
            is Exists -> {
                val subset = eval(lts, f.body, environment)
                // {s in S | exists t in subset : transition (s,t) has label a}
                s = lts.nodes.filter { n ->
                    n.transitions.any { t ->
                        t.destination in subset && t.label == f.label
                    }
                }.toSet()
            }
            is ForAll -> {
                val subset = eval(lts, f.body, environment)
                // {n in S | forall m in S: transition (n,m) has label a => m in subset}
                // equal to:
                // {n in S | forall m in S: !(transition (n,m) has label a) || m in subset}
                s = lts.nodes.filter { n ->
                    lts.nodes.all { m ->
                        n.transitions.none { t ->
                            (t.destination.equals(m) && t.label == f.label)
                        } || m in subset
                    }
                }.toSet()
            }
            is Operator ->
                when (f) {
                    is Operator.Mu -> {
                        environment[f.variable] = emptySet()
                        do {
                            s = environment[f.variable]!!
                            environment[f.variable] = eval(lts, f.body, environment)
                        } while (environment[f.variable]!! != s)
                    }
                    is Operator.Nu -> {
                        environment[f.variable] = lts.nodes.toSet()
                        do {
                            s = environment[f.variable]!!
                            environment[f.variable] = eval(lts, f.body, environment)
                        } while (environment[f.variable]!! != s)
                    }
                }
        }

        return s
    }
}