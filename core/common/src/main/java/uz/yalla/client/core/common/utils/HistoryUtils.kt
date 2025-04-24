package uz.yalla.client.core.common.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

fun getRelativeDate(date: String, today: String, yesterday: String): String {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val orderDate = try {
        val (day, month, year) = date.split(".").map { it.toInt() }
        LocalDate(year, month, day)
    } catch (e: Exception) {
        return date
    }

    return when (orderDate) {
        currentDate -> today
        currentDate.minus(1, DateTimeUnit.DAY) -> yesterday
        else -> "${orderDate.dayOfMonth}.${orderDate.month.number}.${orderDate.year}"
    }
}