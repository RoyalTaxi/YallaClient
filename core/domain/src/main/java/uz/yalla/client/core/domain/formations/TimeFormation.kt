package uz.yalla.client.core.domain.formations

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object TimeFormation {

    fun Long.toFormattedDate(): String {
        val dateTime = Instant.fromEpochSeconds(this)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val month = dateTime.monthNumber.toString().padStart(2, '0')
        val year = dateTime.year

        return "$day.$month.$year"
    }

    fun Long?.toFormattedTime(): String {
        val time = this ?: return ""
        val dateTime = Instant.fromEpochSeconds(time)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }

    fun String.toFormattedPrice(): String {
        return this.reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()
    }
}