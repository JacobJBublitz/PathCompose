package pathcompose.space

import pathcompose.geometry.Point2
import kotlin.random.Random

data class Robot(val length: Double, val width: Double) {
    inline val corners: Collection<Point2>
        get() = listOf(
            Point2(length / 2.0, width / 2.0),
            Point2(length / 2.0, -width / 2.0),
            Point2(-length / 2.0, -width / 2.0),
            Point2(-length / 2.0, width / 2.0)
        )
}

class Workspace(
    val robot: Robot,
    val width: Double,
    val length: Double,
    val obstacles: List<FieldObstacle>,
    random: Random = Random.Default
) : Se2Space(0.0..length, -width / 2.0..width / 2.0, random) {
    override fun sample(): Se2Sample {
        for (i in 0 until 10000) {
            val x = super.sample()
            if (x in this)
                return x
        }

        throw RuntimeException("Unable to find a valid sample")
    }

    override fun contains(x: Se2Sample): Boolean {
        for (corner in robot.corners) {
            val p = x.asIsometry() * corner
            if (p.x !in 0.0..length || p.y !in -width / 2.0..width / 2.0) {
                return false
            }
        }

        for (obstacle in obstacles) {
            if (obstacle.checkCollision(robot, x)) {
                return false
            }
        }

        return true
    }
}

interface FieldObstacle {
    fun checkCollision(robot: Robot, sample: Se2Sample): Boolean
}

class CircleFieldObstacle(val position: Point2, val radius: Double) : FieldObstacle {
    override fun checkCollision(robot: Robot, sample: Se2Sample): Boolean {
        // TODO: Improve collision detection by handling cases where the robot corners are not in the obstacle
        for (corner in robot.corners) {
            if (position.to(sample.rotation * corner + sample.position.asVector()).norm <= radius) {
                return true
            }
        }

        return false
    }
}
