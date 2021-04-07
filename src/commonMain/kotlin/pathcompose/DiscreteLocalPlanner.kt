package pathcompose

class DiscreteLocalPlanner<State>(val space: Space<State>, val iterations: Int = 100) : LocalPlanner<State>() {
    override fun check(from: State, to: State): Boolean {
        if (from !in space || to !in space)
            return false

        for (i in 1 until iterations) {
            val sample = space.interpolate(from, to, i.toDouble() / iterations)
            if (sample !in space) return false
        }

        return true
    }
}