package uz.yalla.client.core.data.mapper

typealias Mapper<T, R> = (T) -> R

fun Int?.or0() = this ?: 0
fun Long?.or0() = this ?: 0
fun Float?.or0() = this ?: 0f
fun Double?.or0() = this ?: 0.0
fun Boolean?.orFalse() = this ?: false
fun Boolean?.orTrue() = this ?: false