package pathcompose

interface FiniteSpace<State> : Space<State> {
    fun sample(): State
}
