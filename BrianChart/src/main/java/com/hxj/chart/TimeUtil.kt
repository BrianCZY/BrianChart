package com.hxj.chart

import java.text.SimpleDateFormat
import java.util.Date

object TimeUtil {
    var dateFormatYMD = "yyyy-MM-dd"
    /**
     * 描述：Date类型转化为String类型.
     *
     * @param date   the date
     * @param format the format
     * @return String String类型日期时间
     */
    fun getStringByFormat(date: Date?, format: String?): String? {
        val mSimpleDateFormat = SimpleDateFormat(format)
        var strDate: String? = null
        try {
            strDate = mSimpleDateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strDate
    }

    /**
     * 描述：获取milliseconds表示的日期时间的字符串.
     *
     * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 日期时间字符串
     */
    fun getStringByFormat(milliseconds: Long, format: String?): String? {
        var thisDateTime: String? = null
        try {
            val mSimpleDateFormat = SimpleDateFormat(format)
            thisDateTime = mSimpleDateFormat.format(milliseconds)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return thisDateTime
    }
}