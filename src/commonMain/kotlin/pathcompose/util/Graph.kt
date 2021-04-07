package pathcompose.util

interface Graph<T> {
    val vertices: Collection<T>

    fun getEdges(vertex: T): Sequence<T>
}