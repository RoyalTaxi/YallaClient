package uz.yalla.client.feature.core.utils

fun extractCode(message: String?): String {
    if (message == null) return ""
    val regex = Regex("(\\d{5})")
    val match = regex.find(message)
    return match?.value ?: ""
}