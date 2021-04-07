package pathcompose.space

import pathcompose.FiniteSpace
import pathcompose.geometry.Isometry2
import pathcompose.geometry.Point2
import pathcompose.geometry.Rotation2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.random.Random

class Se2Sample(val position: Point2, val rotation: Rotation2) {
    constructor(x: Double, y: Double, rotation: Rotation2) : this(Point2(x, y), rotation)
    internal constructor(position: CartesianPoint, rotation: Rotation2) : this(position[0], position[1], rotation)

    inline val x: Double get() = position.x
    inline val y: Double get() = position.y
    inline val radians: Double get() = rotation.radians
    inline val degrees: Double get() = rotation.degrees

    fun asIsometry(): Isometry2 = Isometry2(position.asVector(), rotation)

    override fun toString() = "{$position, $rotation}"
}

class So2Space(val random: Random = Random.Default) : FiniteSpace<Rotation2> {
    override fun distance(a: Rotation2, b: Rotation2): Double {
        return PI - abs(abs(a.radians - b.radians) - PI)
    }

    override fun sample() = Rotation2.fromRadians(random.nextDouble(2.0 * PI))

    override fun contains(x: Rotation2) = true

    override fun interpolate(from: Rotation2, to: Rotation2, t: Double): Rotation2 {
        var fromValue = from.radians
        var toValue = to.radians

        if (abs(fromValue - toValue) > PI) {
            if (fromValue < toValue) {
                fromValue += 2.0 * PI
            } else {
                toValue += 2.0 * PI
            }
        }

        var value = fromValue + ((toValue - fromValue) * t)
        value %= 2.0 * PI
        if (value < 0.0) {
            value += 2.0 * PI
        }

        return Rotation2.fromRadians(value)
    }
}

open class Se2Space(
    xBounds: ClosedFloatingPointRange<Double>,
    yBounds: ClosedFloatingPointRange<Double>,
    random: Random = Random.Default
) : FiniteSpace<Se2Sample> {
    val positionSpace = FiniteCartesianSpace(random, xBounds, yBounds)
    val rotationSpace = So2Space(random)

    override fun sample() = Se2Sample(
        positionSpace.sample(),
        rotationSpace.sample()
    )

    override fun distance(a: Se2Sample, b: Se2Sample): Double {
        // TODO: Improve distance calculation
        return a.position.to(b.position).norm + rotationSpace.distance(a.rotation, b.rotation)
    }

    override fun contains(x: Se2Sample) =
        CartesianPoint(doubleArrayOf(x.x, x.y)) in positionSpace &&
                x.rotation in rotationSpace

    override fun interpolate(from: Se2Sample, to: Se2Sample, t: Double) = Se2Sample(
        positionSpace.interpolate(
            CartesianPoint(doubleArrayOf(from.x, from.y)),
            CartesianPoint(doubleArrayOf(to.x, to.y)),
            t
        ),
        rotationSpace.interpolate(from.rotation, to.rotation, t)
    )
}
