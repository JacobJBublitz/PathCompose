package pathcompose.util

interface MutableTree<T> : Tree<T> {
    fun clear(root: T? = null)

    fun add(node: T, parent: T)

    fun remove(node: T): Boolean
}