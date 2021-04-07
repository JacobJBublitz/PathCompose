package pathcompose.geometry

import kotlin.math.sqrt

data class Vector2(val x: Double, val y: Double) {
    inline val norm: Double get() = sqrt(x * x + y * y)
}
