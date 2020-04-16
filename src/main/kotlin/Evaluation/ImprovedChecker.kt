package Evaluation

import LTS.LabelledTransitionSystem
import LTS.Node
import ModalMu.*
import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.red
import com.andreapivetta.kolor.yellow
import printdbg
import printlndbg

class ImprovedChecker : MuFormulaChecker() {
    var iteration = 0

    override fun check(lts: LabelledTransitionSystem, formula: ModalFormula): Boolean {
        printlndbg("Checking formula against LTS.".green())
        iteration = 0
        printdbg("Iteration: ".green())

        val environment = mutableMapOf<Variable, Set<Node>>()
        for (x in formula.getFixedPoints()) {
            when (x) {
                is Operator.Mu ->
                    environment[x.variable] = emptySet()
                is Operator.Nu ->
                    environment[x.variable] = lts.nodes.toSet()
            }
        }

        val alreadyEvaluated = mutableMapOf<ModalFormula, Boolean>().withDefault { false }
        val values = mutableMapOf<ModalFormula, Set<Node>>()

        val states: Set<Node> = eval(lts, formula, environment, alreadyEvaluated, values)

        // Go to next line
        printlndbg("")

        printdbg("Valid in states: ".green())
        printlndbg("{${states.joinToString { n -> n.index.toString() }}}".yellow())

        return lts.initialNode in states
    }

    private fun eval(
        lts: LabelledTransitionSystem, f: ModalFormula, environment: MutableMap<Variable, Set<Node>>,
        alreadyEvaluated: MutableMap<ModalFormula, Boolean>, values: MutableMap<ModalFormula, Set<Node>>
    ): Set<Node> {
        printdbg("${iteration++}, ".yellow())
        printlndbg("$f".red())
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
                    n.transitions.none {t ->
                        t.label == f.label && t.destination !in subset
                    }
                }.toSet()
            }
            is Operator ->
                when (f) {
                    is Operator.Mu -> {
                        if (f.getSurroundingFixedPoint() is Operator.Nu) {
                            environment[f.variable] = emptySet()
                            for (g in f.getOpenSubOperators()) {
                                environment[g.variable] = emptySet()
                            }
                        }
                        do {
                            s = environment.getOrDefault(f.variable, emptySet())
                            environment[f.variable] = eval(lts, f.body, environment, alreadyEvaluated, values)
                        } while (environment[f.variable]!! != s)
                    }
                    is Operator.Nu -> {
                        if (f.getSurroundingFixedPoint() is Operator.Mu) {
                            environment[f.variable] = lts.nodes.toSet()
                            for (g in f.getOpenSubOperators()) {
                                environment[g.variable] = lts.nodes.toSet()
                            }
                        }
                        do {
                            s = environment.getOrDefault(f.variable, emptySet())
                            environment[f.variable] = eval(lts, f.body, environment, alreadyEvaluated, values)
                        } while (environment[f.variable]!! != s)
                    }
                }
        }

        if (f.isSentence) {
            alreadyEvaluated[f] = true
            values[f] = s
        }

        printlndbg("\r\nYield set: {${s.joinToString { n -> n.index.toString() }}}".yellow())
        return s
    }
}