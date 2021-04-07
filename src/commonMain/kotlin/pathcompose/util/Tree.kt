package pathcompose.util

interface Tree<T> {
    val root: T?

    val size: Int

    val nodes: Set<T>

    fun getChildren(node: T): Collection<T>

    fun getParent(node: T): T?

    operator fun contains(node: T): Boolean
}