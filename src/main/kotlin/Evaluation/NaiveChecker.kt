package Evaluation

import LTS.LabelledTransitionSystem
import LTS.Node
import ModalMu.*
import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.yellow
import printdbg
import printlndbg

class NaiveChecker : MuFormulaChecker() {
    var iteration = 0

    override fun check(lts: LabelledTransitionSystem, formula: ModalFormula): Boolean {
        printlndbg("Checking formula against LTS.".green())
        iteration = 0
        printdbg("Iteration: ".green())

        val states: Set<Node> = eval(lts, formula, mutableMapOf())

        // Go to next line
        printlndbg("")

        printdbg("Valid in states:".green())
        printlndbg("{${states.joinToString { n -> n.index.toString() }}}".yellow())

        return lts.initialNode in states
    }

    private fun eval(
        lts: LabelledTransitionSystem,
        f: ModalFormula,
        environment: MutableMap<Variable, Set<Node>>
    ): Set<Node> {
        printdbg("${iteration++}, ".yellow())
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
                    n.transitions.none {t ->
                        t.label == f.label && t.destination !in subset
                    }
                }.toSet()
            }
            is Operator ->
                when (f) {
                    is Operator.Mu -> {
                        environment[f.variable] = emptySet()
                        var fpit = 0
                        printlndbg("Entering Mu fixed point loop".green())
                        do {
                            s = environment[f.variable]!!
                            environment[f.variable] = eval(lts, f.body, environment)
                            fpit++
                        } while (environment[f.variable]!! != s)
                        printlndbg("Mu fixed point operations: $fpit".green())
                    }
                    is Operator.Nu -> {
                        environment[f.variable] = lts.nodes.toSet()
                        var fpit = 0
                        printlndbg("Entering Nu fixed point loop".green())
                        do {
                            s = environment[f.variable]!!
                            environment[f.variable] = eval(lts, f.body, environment)
                            fpit++
                        } while (environment[f.variable]!! != s)
                        printlndbg("Nu fixed point operations: $fpit".green())
                    }
                }
        }

        return s
    }
}