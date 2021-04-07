package pathcompose.util

class HashGraph<T> : MutableGraph<T> {
    private val vertexMap: HashMap<T, MutableSet<T>> = hashMapOf()

    override val vertices: Collection<T> get() = vertexMap.keys

    override fun getEdges(vertex: T): Sequence<T> = vertexMap[vertex].orEmpty().asSequence()

    override fun addVertex(vertex: T) {
        vertexMap.getOrPut(vertex) { hashSetOf() }
    }

    override fun removeVertex(vertex: T): Boolean {
        for (edge in getEdges(vertex)) {
            removeEdge(vertex, edge)
        }

        return vertexMap.remove(vertex) != null
    }

    override fun addEdge(a: T, b: T) {
        vertexMap[a]?.add(b)
        vertexMap[b]?.add(a)
    }

    override fun removeEdge(a: T, b: T): Boolean {
        var present = false

        present = present or (vertexMap[a]?.remove(b) != null)
        present = present or (vertexMap[b]?.remove(a) != null)

        return present
    }

    override fun clear() {
        vertexMap.clear()
    }
}