package uz.yalla.client.core.domain.formations

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

object TimeFormation {

    fun Long.toFormattedDate(): String {
        val dateTime = Instant.fromEpochMilliseconds(this * 1000L)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val day = dateTime.dayOfMonth.toString().let { if (it.length == 2) it else "0$it" }
        val month = dateTime.month.number.toString().let { if (it.length == 2) it else "0$it" }
        val year = dateTime.year

        return "$day.$month.$year"
    }

    fun Long?.toFormattedTime(): String {
        val time = this ?: return ""
        val dateTime = Instant.fromEpochMilliseconds(time)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.hour.toString().let { if (it.length == 2) it else "0$it" }}:${
            dateTime.minute.toString().let { if (it.length == 2) it else "0$it" }
        }"
    }

    fun String.toFormattedPrice(): String {
        return this.reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()
    }
}