package LTS.Parsing

import LTS.LabelledTransitionSystem
import LTS.Node
import LTS.Transition
import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.yellow
import printlndbg

class AldebaranParser {
    companion object {
        fun parse(lines: Sequence<String>): LabelledTransitionSystem {
            printlndbg("Parsing LTS".yellow())
            val lineIterator = lines.iterator()

            val first = lineIterator.next()
            printlndbg("Parsing first line: $first".yellow())
            if (!first.startsWith("des (")) {
                throw IllegalStateException("First line of Aldebaran file malformed.")
            }

            // Remove parentheses at the end.
            val stringValues = first.substringAfter('(').substringBefore(')').split(',')
            val (initIndex, transitionCount, nodeCount) = stringValues.map { it.toInt() }

            printlndbg("Init node: $initIndex, transition count: $transitionCount, node count: $nodeCount".yellow())

            val nodes = (0 until nodeCount).map { i -> Node(i) }.toList()

            lineIterator.forEachRemaining { s ->
                if (s.isNotEmpty()) {
                    printlndbg("Parsing transition: $s".yellow())
                    parseTransition(s, nodes)
                }
            }

            printlndbg("Parsed LTS".green())
            return LabelledTransitionSystem(nodes[initIndex], transitionCount, nodes)
        }

        private fun parseTransition(line: String, nodes: List<Node>) {
            val trimmedLine = line.trim('(').trim(')')
            val from = trimmedLine.substringBefore(',').toInt()
            val to = trimmedLine.substringAfterLast(',').toInt()
            val label = trimmedLine.substringAfter('"').substringBefore('"')

            val transition = Transition(label, nodes[to])

            nodes[from].transitions.add(transition)
        }
    }
}