package com.brian.chart

import java.math.BigDecimal
import java.math.RoundingMode


//Any 转换为两位小数点的字符串
fun Any.formatDigitOrNull(decimals: Int = 2): String? {
    return this.toString().toFloatOrNull()?.let {
        String.format("%.${decimals}f", it)
    }?.toString()

}
//Any 转换为两位小数点的Float
fun Any.formatOrNull(decimals: Int = 2): Float? {
    return this.formatDigitOrNull(decimals)?.toFloatOrNull()

}
fun Number.formatOrZero(decimals: Int = 2): Float {
    return this.toFloat().let {
        String.format("%.${decimals}f", it)
    }.toFloatOrZero()

}

fun Double?.format(decimals: Int = 2): String? {
    if (this == null) {
        return ""
    }
    return try {
        BigDecimal(this).setScale(decimals, RoundingMode.HALF_UP).toString()
    } catch (e: Exception) {
        ""
    }
}
fun String.toFloatOrZero(): Float {
    return if (this.isNullOrEmpty()) 0f else this.toFloat()
}


