package pathcompose

class ProblemDefinition<State>(
    val space: FiniteSpace<State>,
    val start: State,
    val goal: State,
    val goalZone: Double
)