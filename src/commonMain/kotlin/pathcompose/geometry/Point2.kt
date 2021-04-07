package pathcompose.geometry

data class Point2(val x: Double, val y: Double) {
    operator fun plus(vector: Vector2) = Point2(x + vector.x, y + vector.y)

    fun to(other: Point2) = Vector2(other.x - x, other.y - y)

    fun asVector() = Vector2(x, y)

    override fun toString() = "($x, $y)"

    companion object {
        val ZERO = Point2(0.0, 0.0)
    }
}
