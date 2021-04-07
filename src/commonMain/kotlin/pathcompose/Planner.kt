package pathcompose

abstract class Planner<State> {
    abstract fun solve(problemDefinition: ProblemDefinition<State>): List<State>
}
