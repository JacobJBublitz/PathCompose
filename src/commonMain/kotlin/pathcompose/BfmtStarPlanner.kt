package pathcompose

import pathcompose.util.HashTree
import pathcompose.util.MutableTree

class BfmtStarPlanner<State>(
    private val space: Space<State>,
    private val localPlanner: LocalPlanner<State>,
    private val radius: Double,
    private val numSamples: Int = 1000
) : Planner<State>() {
    var activeTree: TreeData<State>? = null
    var inactiveTree: TreeData<State>? = null

    private fun initialize(nodes: Set<State>, root: State): TreeData<State> {
        return TreeData(
//            hashSetOf(root),
//            hashSetOf(),
//            nodes.subtract(listOf(root)).toMutableSet(),
//            hashSetOf(root)
            HashTree(root),
            hashSetOf(root),
            nodes.subtract(listOf(root)).toMutableSet()
        )
    }

    override fun solve(problemDefinition: ProblemDefinition<State>): List<State> {
        val nodes = setOf(
            problemDefinition.start,
            problemDefinition.goal
        ).union(List(numSamples) { problemDefinition.space.sample() })

        activeTree = initialize(nodes, problemDefinition.start)
        inactiveTree = initialize(nodes, problemDefinition.goal)

        var currentNode = problemDefinition.start
        var c: State? = null
        while (true) {
            c = expand(activeTree!!, currentNode, c)

            if (c != null) { // TODO: This is first path criterion, also allow best path criterion
                val path = path(c, activeTree!!)
                path.addAll(path(c, inactiveTree!!))
                return path
            } else {
                if (activeTree!!.frontierNodes.isEmpty() and inactiveTree!!.frontierNodes.isEmpty())
                // Failure
                    return emptyList()

                // TODO: This is alternating trees criterion, also allow balanced trees criterion
                currentNode =
                    inactiveTree!!.frontierNodes.minByOrNull { cost(it, inactiveTree!!) } ?: return emptyList()
                val tmp = activeTree
                activeTree = inactiveTree
                inactiveTree = tmp
            }

            // TODO: Termination check
        }
    }

    private fun cost(x: State, t: TreeData<State>): Double {
        var current = x
        var parent = t.tree.getParent(current)
        var cost = 0.0
        while (parent != null) {
            cost += cost(current, parent)

            current = parent
            parent = t.tree.getParent(current)
        }

        return cost
    }

    private fun cost(a: State, b: State): Double {
        return space.distance(a, b)
    }

    private fun path(c: State, t: TreeData<State>): MutableList<State> {
        return mutableListOf()
    }

    private fun complement(t: TreeData<State>): TreeData<State> = TODO()

    private fun expand(t: TreeData<State>, z: State, c: State?): State? {
        var bestConnection: State? = c
        val newFrontier = hashSetOf<State>()
        val zNear = near(t.tree.nodes.subtract(listOf(z)), z, radius).intersect(t.unexploredNodes)
        for (x in zNear) {
            val xNear = near(t.tree.nodes.subtract(listOf(z)), x, radius).intersect(t.frontierNodes)
            val xMin = xNear.minByOrNull { cost(it, t) + cost(it, x) }
                ?: throw IllegalStateException() // TODO: Better errors
            if (localPlanner.check(xMin, x)) {
                t.tree.add(x, xMin)
                newFrontier.add(x)
                t.unexploredNodes.remove(x)
                if (x in complement(t).tree.nodes)
                    when {
                        bestConnection == null -> bestConnection = x
                        cost(x, t) + cost(x, complement(t)) < cost(bestConnection, t) + cost(
                            bestConnection,
                            complement(t)
                        ) -> bestConnection = x
                    }
            }
        }

        t.frontierNodes.addAll(newFrontier)
        t.frontierNodes.remove(z)

        return bestConnection
    }

    private fun near(vertices: Set<State>, z: State, radius: Double) =
        vertices.filter { cost(z, it) < radius }.toSet()

    data class TreeData<State>(
        val tree: MutableTree<State>,
        val frontierNodes: MutableSet<State>,
        val unexploredNodes: MutableSet<State>
    )
}