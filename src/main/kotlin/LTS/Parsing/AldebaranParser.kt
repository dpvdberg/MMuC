package LTS.Parsing

import LTS.LabelledTransitionSystem
import LTS.Node
import LTS.Transition
import java.io.File
import java.lang.IllegalArgumentException

class AldebaranParser {
    companion object {
        fun parse(lines: Sequence<String>): LabelledTransitionSystem {
            val lineIterator = lines.iterator()

            val first = lineIterator.next()
            if (!first.startsWith("des (")) {
                throw IllegalStateException("First line of Aldebaran file malformed.")
            }

            // Remove parentheses at the end.
            val stringValues = first.substringAfter('(').substringBefore(')').split(',')
            val (initIndex, transitionCount, nodeCount) = stringValues.map { it.toInt() }

            val nodes = (0 until nodeCount).map { i -> Node(i) }.toList()

            lineIterator.forEachRemaining { s ->
                if (s.isNotEmpty()) {
                    parseTransition(s, nodes)
                }
            }

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