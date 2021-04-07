package pathcompose.geometry

interface Transform2 {
    operator fun times(point: Point2): Point2

    operator fun times(vector: Vector2): Vector2
}
