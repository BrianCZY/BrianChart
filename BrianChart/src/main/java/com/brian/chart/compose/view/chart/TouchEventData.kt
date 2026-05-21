package com.brian.chart.compose.view.chart

import androidx.compose.ui.geometry.Offset

/**
 * 触摸事件数据类
 * @param dataX 触摸点对应的X轴数据值
 * @param dataY 触摸点对应的Y轴数据值（默认使用第一个可用的Y轴）
 * @param pixelX 触摸点的X像素坐标
 * @param pixelY 触摸点的Y像素坐标
 * @param nearestPoint 最近的数据点（可选）
 * @param dataYLeftInside 左内轴对应的Y值（如果存在）
 * @param dataYLeft 左外轴对应的Y值（如果存在）
 * @param dataYRight 右轴对应的Y值（如果存在）
 */
data class TouchEventData(
    val dataX: Float,
    val dataY: Float,
    val pixelX: Float,
    val pixelY: Float,
    val nearestPoint: PointData? = null,
    val dataYLeftInside: Float? = null,
    val dataYLeft: Float? = null,
    val dataYRight: Float? = null
) {
    /**
     * 获取所有可用的Y轴数据值
     * @return Map<轴类型, Y值>
     */
    fun getAllYValues(): Map<String, Float> {
        return buildMap {
            dataYLeftInside?.let { put("左内轴", it) }
            dataYLeft?.let { put("左外轴", it) }
            dataYRight?.let { put("右轴", it) }
        }
    }
}

/**
 * 最近的数据点信息
 * @param point 数据点
 * @param line 所属的Line
 * @param distance 距离触摸点的像素距离
 */
data class PointData(
    val point: Point,
    val line: Line,
    val distance: Float
)
