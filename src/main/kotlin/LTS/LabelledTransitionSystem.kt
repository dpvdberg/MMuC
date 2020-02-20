package LTS

class LabelledTransitionSystem(
    initialNode: Node,
    transitionCount: Int,
    nodes: List<Node>
) {
    var transitionCount: Int = transitionCount
        private set
    var nodes: List<Node> = nodes
        private set
}