package pathcompose.geometry

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class Rotation2(val cos: Double, val sin: Double) : Transform2 {
    inline val radians: Double get() = atan2(sin, cos)
    inline val degrees: Double get() = radians * 180.0 / PI

    override operator fun times(point: Point2) = Point2(
        point.x * cos - point.y * sin,
        point.x * sin + point.y * cos
    )

    override operator fun times(vector: Vector2) = Vector2(
        vector.x * cos - vector.y * sin,
        vector.x * sin + vector.y * cos
    )

    override fun toString() = "$degrees deg"

    companion object {
        val ZERO: Rotation2 = Rotation2(1.0, 0.0)

        fun fromRadians(angle: Double) = Rotation2(cos(angle), sin(angle))
    }
}