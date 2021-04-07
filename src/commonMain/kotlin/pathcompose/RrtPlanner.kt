package pathcompose

import pathcompose.util.HashTree
import pathcompose.util.MutableTree

class RrtPlanner<State>(
    val localPlanner: LocalPlanner<State>,
    val maxSize: Int = 2500
) : Planner<State>() {
    val tree: MutableTree<State> = HashTree()

    @OptIn(ExperimentalStdlibApi::class)
    override fun solve(problemDefinition: ProblemDefinition<State>): List<State> {
        tree.clear(problemDefinition.start)

        while (tree.size < maxSize) {
            val sample = problemDefinition.space.sample()
            // TODO: Optimize nearest-neighbor
            val nearest = tree.nodes.sortedBy { problemDefinition.space.distance(it, sample) }.first()

            if (localPlanner.check(nearest, sample)) {
                tree.add(sample, nearest)

                if (problemDefinition.space.distance(sample, problemDefinition.goal) <= problemDefinition.goalZone) {
                    // TODO: Path is successful
                    println("Path is successful")
                    return buildList {
                        add(problemDefinition.goal)
                        var state: State? = sample
                        do {
                            add(state!!)
                            state = tree.getParent(state)
                        } while (state != null)
                    }.reversed()
                }
            }
        }

        // Path is not successful
        print("Path is not successful")
        return emptyList()
    }
}