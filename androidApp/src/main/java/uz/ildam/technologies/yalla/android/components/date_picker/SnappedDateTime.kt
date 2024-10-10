package uz.ildam.technologies.yalla.android.components.date_picker

import org.threeten.bp.LocalDate

internal sealed class SnappedDate(val snappedLocalDate: LocalDate, val snappedIndex: Int) {
    data class DayOfMonth(val localDate: LocalDate, val index: Int) : SnappedDate(localDate, index)
    data class Month(val localDate: LocalDate, val index: Int) : SnappedDate(localDate, index)
    data class Year(val localDate: LocalDate, val index: Int) : SnappedDate(localDate, index)
}
