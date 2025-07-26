package uz.yalla.client.feature.profile.edit_profile.model

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import java.util.*

fun EditProfileViewModel.parseBirthdayOrNull(birthdayStr: String?): LocalDate? {
    if (birthdayStr.isNullOrBlank()) return null
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    return try {
        LocalDate.parse(birthdayStr, formatter)
    } catch (e: DateTimeParseException) {
        null
    }
}