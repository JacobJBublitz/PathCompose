package pathcompose

interface Space<State> {
    fun distance(a: State, b: State): Double

    fun interpolate(from: State, to: State, t: Double): State

    operator fun contains(x: State): Boolean
}
