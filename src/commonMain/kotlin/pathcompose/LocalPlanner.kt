package pathcompose

abstract class LocalPlanner<State> {
    abstract fun check(from: State, to: State): Boolean
}