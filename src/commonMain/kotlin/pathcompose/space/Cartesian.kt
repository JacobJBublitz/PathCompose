package pathcompose.space

import pathcompose.FiniteSpace
import pathcompose.Space
import kotlin.math.sqrt
import kotlin.random.Random

inline class CartesianPoint(val data: DoubleArray) {
    constructor(size: Int, initializer: (Int) -> Double)
            : this(DoubleArray(size, initializer))

    inline val size: Int get() = data.size

    operator fun get(idx: Int) = data[idx]

    override fun toString(): String = data.joinToString(prefix = "(", postfix = ")")
}

open class CartesianSpace(
    val size: Int,
) : Space<CartesianPoint> {
    init {
        require(size > 0)
    }

    override fun distance(a: CartesianPoint, b: CartesianPoint): Double {
        require(a.size == size && b.size == size)
        return sqrt(
            (0 until size).sumByDouble { i -> (a[i] - b[i]) * (a[i] - b[i]) }
        )
    }

    override fun interpolate(from: CartesianPoint, to: CartesianPoint, t: Double) = CartesianPoint(size) {
        from[it] + (to[it] - from[it]) * t
    }

    override fun contains(x: CartesianPoint) = true
}

class FiniteCartesianSpace(
    size: Int,
    private val lowerBounds: DoubleArray,
    private val upperBounds: DoubleArray,
    private val random: Random = Random.Default,
) : CartesianSpace(size), FiniteSpace<CartesianPoint> {
    constructor(random: Random, vararg bounds: ClosedFloatingPointRange<Double>)
            : this(
        bounds.size,
        DoubleArray(bounds.size) { bounds[it].start },
        DoubleArray(bounds.size) { bounds[it].endInclusive },
        random
    )

    constructor(vararg bounds: ClosedFloatingPointRange<Double>) : this(Random.Default, *bounds)

    override fun sample(): CartesianPoint {
        return CartesianPoint(size) { i ->
            random.nextDouble(lowerBounds[i], upperBounds[i])
        }
    }

    override fun contains(x: CartesianPoint): Boolean {
        for (i in 0 until size) {
            if (x[i] !in lowerBounds[i]..upperBounds[i]) {
                return false
            }
        }

        return true
    }
}
