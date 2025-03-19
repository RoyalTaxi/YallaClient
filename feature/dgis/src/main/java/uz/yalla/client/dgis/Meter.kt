package uz.yalla.client.dgis

val Int.meter
    get() = Meter(this.toFloat())
