package uz.yalla.client.core.common.utils

import org.threeten.bp.LocalDate


fun LocalDate?.formatWithDashesDMY() =
    if (this == null) "" else
        "${this.toString().slice(8..9)}-${this.toString().slice(5..6)}-${
            this.toString().slice(0..3)
        }"

fun LocalDate?.formatWithDotsDMY() =
    if (this == null) "" else
        "${this.toString().slice(8..9)}.${this.toString().slice(5..6)}.${
            this.toString().slice(0..3)
        }"

