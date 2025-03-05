package uz.yalla.client.core.common.formation

fun String.toFormattedPrice(): String {
    return this.reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()
}