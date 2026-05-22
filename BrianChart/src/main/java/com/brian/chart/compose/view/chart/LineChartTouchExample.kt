package com.brian.chart.compose.view.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * LineChart 触摸功能使用示例
 */
@Composable
fun LineChartTouchExample() {
    var touchInfo by remember { mutableStateOf<String>("点击图表查看数据") }
    
    // 创建测试数据
    val lineData = remember {
        LineChartData(
            lineList = listOf(
                Line(
                    pointList = listOf(
                        Point(0f, 0f),
                        Point(25f, 50f),
                        Point(50f, 100f),
                        Point(75f, 75f),
                        Point(100f, 150f),
                        Point(125f, 120f),
                        Point(150f, 200f)
                    ),
                    color = Color.Blue,
                    isDrawCubic = true,
                    isDrawPath = true
                )
            ),
            xAxis = Axis(
                min = 0f,
                max = 200f,
                labelInterval = 50f,
                scaleInterval = 25f
            ),
            yLeftAxis = Axis(
                min = 0f,
                max = 250f,
                labelInterval = 50f,
                scaleInterval = 25f
            ),
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "LineChart 触摸示例",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.LightGray.copy(alpha = 0.2f)),
            data = lineData,
            onTouch = { touchEvent ->
                // 处理触摸事件
                touchInfo = buildString {
                    append("触摸位置:\n")
                    append("数据坐标: X=%.2f, Y=%.2f\n".format(touchEvent.dataX, touchEvent.dataY))
                    append("像素坐标: X=%.0f, Y=%.0f\n".format(touchEvent.pixelX, touchEvent.pixelY))
                    
                    touchEvent.nearestPoint?.let { nearest ->
                        append("\n最近的数据点:\n")
                        append("点坐标: X=%.2f, Y=%.2f\n".format(nearest.point.x, nearest.point.y))
                        append("距离: %.2f px".format(nearest.distance))
                    }
                }
            }
        )
        
        Text(
            text = touchInfo,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .background(Color.White)
                .padding(8.dp)
        )
    }
}

/**
 * 多轴触摸示例
 */
@Composable
fun MultiAxisTouchExample() {
    var touchInfo by remember { mutableStateOf<String>("点击图表查看数据") }
    
    val lineData = remember {
        LineChartData(
            lineList = listOf(
                Line(
                    pointList = listOf(
                        Point(0f, 10f),
                        Point(50f, 80f),
                        Point(100f, 40f),
                        Point(150f, 120f)
                    ),
                    color = Color.Red,
                    axisType = AxisType.LEFT,
                    isDrawCubic = true
                ),
                Line(
                    pointList = listOf(
                        Point(0f, 100f),
                        Point(50f, 150f),
                        Point(100f, 180f),
                        Point(150f, 200f)
                    ),
                    color = Color.Green,
                    axisType = AxisType.RIGHT,
                    isDrawCubic = true
                )
            ),
            xAxis = Axis(
                min = 0f,
                max = 200f,
                labelInterval = 50f
            ),
            yLeftAxis = Axis(
                min = 0f,
                max = 200f,
                labelInterval = 50f,
                name = "左轴"
            ),
            yRightAxis = Axis(
                min = 0f,
                max = 300f,
                labelInterval = 50f,
                name = "右轴"
            ),
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "多轴触摸示例",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.LightGray.copy(alpha = 0.2f)),
            data = lineData,
            onTouch = { touchEvent ->
                touchInfo = buildString {
                    append("📊 多轴触摸数据:\n")
                    append("X轴: %.2f\n".format(touchEvent.dataX))
                    
                    // 显示所有Y轴的值
                    touchEvent.dataYLeftInside?.let {
                        append("左内轴 Y: %.2f\n".format(it))
                    }
                    touchEvent.dataYLeft?.let {
                        append("左外轴 Y: %.2f\n".format(it))
                    }
                    touchEvent.dataYRight?.let {
                        append("右轴 Y: %.2f\n".format(it))
                    }
                    
                    append("\n像素坐标: (%.0f, %.0f)\n".format(touchEvent.pixelX, touchEvent.pixelY))
                    
                    // 使用便捷方法获取所有Y值
                    val allYValues = touchEvent.getAllYValues()
                    if (allYValues.isNotEmpty()) {
                        append("\n所有Y轴值:\n")
                        allYValues.forEach { (axisName, value) ->
                            append("  $axisName: %.2f\n".format(value))
                        }
                    }
                    
                    touchEvent.nearestPoint?.let { nearest ->
                        append("\n🎯 最近点: (%.2f, %.2f), 所属Line: %s".format(
                            nearest.point.x, 
                            nearest.point.y,
                            nearest.line.axisType.name
                        ))
                    }
                }
            }
        )
        
        Text(
            text = touchInfo,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}
