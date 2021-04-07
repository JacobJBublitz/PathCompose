package pathcompose.geometry

import koma.end
import koma.mat
import koma.matrix.Matrix

data class Isometry2(val translation: Vector2, val rotation: Rotation2) : Transform2 {
    fun asMatrix(): IsometryMatrix2 =
        IsometryMatrix2(mat[rotation.cos, -rotation.sin, translation.x end rotation.sin, rotation.cos, translation.y end 0.0, 0.0, 1.0])

    override fun times(point: Point2) = rotation * point + translation

    override fun times(vector: Vector2) = rotation * vector
}

data class IsometryMatrix2(val matrix: Matrix<Double>)
