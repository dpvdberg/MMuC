package LTS

class LabelledTransitionSystem(
    initialNode: Node,
    transitionCount: Int,
    nodes: List<Node>
) {
    var initialNode: Node = initialNode
        private set
    var transitionCount: Int = transitionCount
        private set
    var nodes: List<Node> = nodes
        private set
}