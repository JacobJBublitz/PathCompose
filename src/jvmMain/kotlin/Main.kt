import androidx.compose.desktop.Window
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import pathcompose.BfmtStarPlanner
import pathcompose.DiscreteLocalPlanner
import pathcompose.ProblemDefinition
import pathcompose.geometry.Point2
import pathcompose.geometry.Rotation2
import pathcompose.space.*
import pathcompose.util.Tree

const val CANVAS_WIDTH = 1000.0
const val CANVAS_HEIGHT = 500.0

const val FIELD_WIDTH = 27.0 * 12.0
const val FIELD_LENGTH = 54.0 * 12.0

val ROBOT = Robot(32.0, 28.0)

val WORKSPACE = Workspace(
    ROBOT,
    FIELD_WIDTH,
    FIELD_LENGTH,
    listOf(
        CircleFieldObstacle(Point2(FIELD_LENGTH / 2.0, -FIELD_WIDTH / 4.0), 48.0),
        CircleFieldObstacle(Point2(FIELD_LENGTH / 2.0, FIELD_WIDTH / 4.0), 48.0),
        CircleFieldObstacle(Point2((5.0 / 16.0) * FIELD_LENGTH, 0.0), 48.0),
        CircleFieldObstacle(Point2((11.0 / 16.0) * FIELD_LENGTH, 0.0), 48.0),
    )
)

val PROBLEM = ProblemDefinition(
    WORKSPACE,
    Se2Sample(ROBOT.length, 0.0, Rotation2.ZERO),
    Se2Sample(FIELD_LENGTH - ROBOT.length, 0.0, Rotation2.ZERO),
    12.0
)

fun Se2Sample.toOffset(): Offset = Offset(x.toFloat(), y.toFloat())
fun Point2.toOffset(): Offset = Offset(x.toFloat(), y.toFloat())

fun DrawScope.drawRobot(robot: Robot, sample: Se2Sample) {
    withTransform(transformBlock = {
        translate(sample.x.toFloat(), sample.y.toFloat())
        rotate(sample.degrees.toFloat(), Offset.Zero)
    }) {
        drawRect(
            Color.Red,
            Offset(-robot.length.toFloat() / 2.0f, -robot.width.toFloat() / 2.0f),
            Size(robot.length.toFloat(), robot.width.toFloat())
        )

        drawPath(
            Path().apply {
                moveTo(robot.length.toFloat() / 2.0f * 0.75f, 0.0f)
                lineTo(0.0f, robot.width.toFloat() / 6.0f)
                lineTo(0.0f, -robot.width.toFloat() / 6.0f)
                close()
            },
            Color.White
        )

    }
}

fun DrawScope.drawObstacle(obstacle: FieldObstacle) {
    if (obstacle is CircleFieldObstacle) {
        drawCircle(Color.DarkGray, obstacle.radius.toFloat(), obstacle.position.toOffset())
    } else {
        println("[WARNING]: Unable to draw unknown obstacle $obstacle")
    }
}

fun DrawScope.drawTree(color: Color, tree: Tree<Se2Sample>) {
    for (vertex in tree.nodes) {
        for (child in tree.getChildren(vertex)) {
            drawLine(color, vertex.toOffset(), child.toOffset())
        }
    }
}

fun DrawScope.drawPath(color: Color, path: List<Se2Sample>) {
    for (i in 1 until path.size) {
        drawLine(color, path[i - 1].toOffset(), path[i].toOffset())
    }
}

fun main() = Window(size = IntSize(1280, 720)) {
    val planner by remember {
        mutableStateOf(BfmtStarPlanner(WORKSPACE, DiscreteLocalPlanner(WORKSPACE), 24.0, 2500))
    }
    val path by remember { mutableStateOf(planner.solve(PROBLEM)) }

    MaterialTheme {
        Canvas(Modifier.size(CANVAS_WIDTH.dp, CANVAS_HEIGHT.dp)) {
            withTransform(transformBlock = {
                scale(
                    (CANVAS_WIDTH / FIELD_LENGTH).toFloat(),
                    (CANVAS_HEIGHT / FIELD_WIDTH).toFloat(),
                    pivot = Offset.Zero
                )
                translate(0.0f, FIELD_WIDTH.toFloat() / 2.0f)
            }) {

                for (obstacle in WORKSPACE.obstacles)
                    drawObstacle(obstacle)

                drawCircle(Color.Green, 3.0f, PROBLEM.start.toOffset())
                drawCircle(Color.Blue, 3.0f, PROBLEM.goal.toOffset())

                drawTree(Color.LightGray, planner.activeTree!!.tree)
                drawTree(Color.LightGray, planner.inactiveTree!!.tree)
                drawPath(Color.Magenta, path)

                drawRobot(ROBOT, WORKSPACE.sample())
            }
        }
    }
}
