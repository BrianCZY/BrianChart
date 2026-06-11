package com.brian.chart.compose.widgets.util

import androidx.compose.ui.graphics.Path
import com.brian.chart.compose.widgets.model.Coordinate
import kotlin.math.abs

/**
 * Utility for building chart paths with various interpolation methods
 */
object PathBuilder {

    /**
     * Builds a straight-line (linear) path through data points
     */
    fun buildLinearPath(
        points: List<Coordinate>,
        originX: Float,
        originY: Float,
        scaleX: Float,
        scaleY: Float,
        offsetX: Float,
        offsetY: Float,
        zoom: Float = 1f,
        cachedPath: Path? = null
    ): Path {
        val path = cachedPath ?: Path()
        path.rewind()
        if (points.isEmpty()) return path

        val baseX = originX - offsetX
        val baseY = originY + offsetY
        val scaledStep = scaleX * zoom
        val array = points.toTypedArray()

        val first = array[0]
        path.moveTo(baseX + first.x * scaledStep, baseY - first.y * scaleY)

        var i = 1
        while (i < array.size) {
            path.lineTo(
                baseX + array[i].x * scaledStep,
                baseY - array[i].y * scaleY
            )
            i++
        }
        return path
    }

    /**
     * Builds a cubic Bezier path through data points
     */
    fun buildCubicBezierPath(
        points: List<Coordinate>,
        originX: Float,
        originY: Float,
        scaleX: Float,
        scaleY: Float,
        offsetX: Float,
        offsetY: Float,
        zoom: Float = 1f,
        cachedPath: Path? = null
    ): Path {
        val path = cachedPath ?: Path()
        path.rewind()
        val size = points.size
        if (size == 0) return path

        val baseX = originX - offsetX
        val baseY = originY + offsetY
        val scaledStep = scaleX * zoom
        val array = points.toTypedArray()

        val first = array[0]
        val firstX = baseX + first.x * scaledStep
        val firstY = baseY - first.y * scaleY
        path.moveTo(firstX, firstY)
        if (size == 1) return path

        var i = 1
        var lastX = firstX
        var lastY = firstY
        while (i < size) {
            val pt = array[i]
            val cx = baseX + pt.x * scaledStep
            val cy = baseY - pt.y * scaleY
            val midX = (lastX + cx) * 0.5f
            path.cubicTo(midX, lastY, midX, cy, cx, cy)
            lastX = cx
            lastY = cy
            i++
        }
        return path
    }

    /**
     * Builds a Catmull-Rom spline path with configurable tension
     */
    fun buildCatmullRomPath(
        points: List<Coordinate>,
        originX: Float,
        originY: Float,
        scaleX: Float,
        scaleY: Float,
        offsetX: Float,
        offsetY: Float,
        zoom: Float = 1f,
        cachedPath: Path? = null,
        tension: Float = 0.5f,
        skipEdges: Int = 1
    ): Path {
        val path = cachedPath ?: Path()
        path.rewind()
        val size = points.size
        if (size == 0) return path

        val baseX = originX - offsetX
        val baseY = originY + offsetY
        val scaledStep = scaleX * zoom
        val array = points.toTypedArray()

        val first = array[0]
        val firstX = baseX + first.x * scaledStep
        val firstY = baseY - first.y * scaleY
        path.moveTo(firstX, firstY)
        if (size == skipEdges) return path

        if (size < skipEdges + 3) {
            var i = 1
            var lx = firstX
            var ly = firstY
            while (i < size) {
                val pt = array[i]
                val cx = baseX + pt.x * scaledStep
                val cy = baseY - pt.y * scaleY
                val mx = (lx + cx) * 0.5f
                path.cubicTo(mx, ly, mx, cy, cx, cy)
                lx = cx
                ly = cy
                i++
            }
            return path
        }

        var i = 0
        while (i < size) {
            when (i) {
                0 -> { i++; continue }
                in 1 until skipEdges -> {
                    val x = baseX + array[i].x * scaledStep
                    val y = baseY - array[i].y * scaleY
                    path.lineTo(x, y)
                }
                skipEdges -> {
                    val p0 = if (skipEdges > 2) array[skipEdges - 2] else array[0]
                    val p1 = array[skipEdges - 1]
                    val p2 = array[skipEdges]
                    val p3 = array[skipEdges + 1]
                    drawCatmullSegment(path, p0, p1, p2, p3, baseX, baseY, scaledStep, scaleY, tension)
                }
                in skipEdges + 1 until size - skipEdges -> {
                    val p0 = array[i - 2]
                    val p1 = array[i - 1]
                    val p2 = array[i]
                    val p3 = array[i + 1]
                    drawCatmullSegment(path, p0, p1, p2, p3, baseX, baseY, scaledStep, scaleY, tension)
                }
                size - skipEdges -> {
                    val p0 = array[i - 2]
                    val p1 = array[i - 1]
                    val p2 = array[i]
                    val p3 = array[i]
                    drawCatmullSegment(path, p0, p1, p2, p3, baseX, baseY, scaledStep, scaleY, tension)
                }
                else -> {
                    path.lineTo(
                        baseX + array[i].x * scaledStep,
                        baseY - array[i].y * scaleY
                    )
                }
            }
            i++
        }
        return path
    }

    private fun drawCatmullSegment(
        path: Path,
        p0: Coordinate, p1: Coordinate, p2: Coordinate, p3: Coordinate,
        baseX: Float, baseY: Float, scaleX: Float, scaleY: Float, tension: Float
    ) {
        val x0 = baseX + p0.x * scaleX; val y0 = baseY - p0.y * scaleY
        val x1 = baseX + p1.x * scaleX; val y1 = baseY - p1.y * scaleY
        val x2 = baseX + p2.x * scaleX; val y2 = baseY - p2.y * scaleY
        val x3 = baseX + p3.x * scaleX; val y3 = baseY - p3.y * scaleY

        val minY = minOf(y1, y2)
        val maxY = maxOf(y1, y2)

        val ctrl1X = x1 + (x2 - x0) * tension * 0.5f
        val ctrl1Y = (y1 + (y2 - y0) * tension * 0.5f).coerceIn(minY, maxY)
        val ctrl2X = x2 - (x3 - x1) * tension * 0.5f
        val ctrl2Y = (y2 - (y3 - y1) * tension * 0.5f).coerceIn(minY, maxY)

        path.cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2)
    }

    /**
     * Converts data points to pixel offsets
     */
    fun toPixelOffsets(
        points: List<Coordinate>,
        originX: Float,
        originY: Float,
        scaleX: Float,
        scaleY: Float,
        offsetX: Float,
        offsetY: Float,
        zoom: Float = 1f
    ): List<androidx.compose.ui.geometry.Offset> {
        if (points.isEmpty()) return emptyList()
        val baseX = originX - offsetX
        val baseY = originY + offsetY
        val scaledStep = scaleX * zoom
        return points.map { pt ->
            androidx.compose.ui.geometry.Offset(
                x = baseX + pt.x * scaledStep,
                y = baseY - pt.y * scaleY
            )
        }
    }
}
