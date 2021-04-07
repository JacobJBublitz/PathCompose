package pathcompose.util

class HashTree<T>(root: T? = null) : MutableTree<T> {
    private val parents: MutableMap<T, T> = hashMapOf()
    private val children: MutableMap<T, MutableSet<T>> = hashMapOf()

    init {
        if (root != null)
            children[root] = hashSetOf()
    }

    override var root: T? = root
        private set

    override val size: Int
        get() = if (root != null) 1 + parents.size else 0

    override val nodes: Set<T>
        get() = parents.keys.union(listOfNotNull(root))

    override fun contains(node: T) =
        (root.hashCode() == node.hashCode()) or (node in parents.keys)

    override fun clear(root: T?) {
        parents.clear()
        children.clear()
        this.root = root
    }

    override fun add(node: T, parent: T) {
        if (root == null)
            throw IllegalStateException("A root node must be set using clear before nodes can be added to the tree")
        if (parent !in this)
            throw IllegalArgumentException("Parent node must be present in the tree")
        if (node.hashCode() == root.hashCode())
            throw IllegalArgumentException("Cannot change parent of root node")
        if (node in parents.keys)
            throw IllegalArgumentException("Cannot move nodes")
        parents[node] = parent
        children[parent]?.add(node)
        children[node] = hashSetOf()
    }

    override fun remove(node: T): Boolean {
        // Remove all children
        getChildren(node).forEach { remove(it) }
        // Check if this is the root node
        TODO()
    }

    override fun getChildren(node: T): Collection<T> =
        children[node].orEmpty()

    override fun getParent(node: T): T? = parents[node]
}