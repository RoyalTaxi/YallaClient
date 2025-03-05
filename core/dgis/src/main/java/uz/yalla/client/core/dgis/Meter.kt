package uz.yalla.client.core.dgis

val Int.meter
    get() = Meter(this.toFloat())
