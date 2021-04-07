package pathcompose.util

interface MutableGraph<T> : Graph<T> {
    fun addVertex(vertex: T)

    fun removeVertex(vertex: T): Boolean

    fun addEdge(a: T, b: T)

    fun removeEdge(a: T, b: T): Boolean

    fun clear()
}