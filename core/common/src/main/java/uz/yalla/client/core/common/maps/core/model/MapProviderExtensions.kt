package uz.yalla.client.core.common.maps.core.model

import uz.yalla.client.core.domain.model.MapType

fun MapType.toProvider(): MapProvider = when (this) {
    MapType.Google -> MapProvider.GOOGLE
    MapType.Libre  -> MapProvider.LIBRE
}