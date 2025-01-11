package uz.ildam.technologies.yalla.android2gis

import uz.ildam.technologies.yalla.android2gis.Meter

val Int.meter
    get() = Meter(this.toFloat())
