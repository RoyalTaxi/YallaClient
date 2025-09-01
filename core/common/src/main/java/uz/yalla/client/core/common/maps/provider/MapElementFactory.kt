package uz.yalla.client.core.common.maps.provider

import uz.yalla.client.core.common.maps.core.manager.MapElementManager
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.core.model.MapProvider
import uz.yalla.client.core.common.maps.google.manager.GMapElementManager
import uz.yalla.client.core.common.maps.google.manager.GMapIconManager
import uz.yalla.client.core.common.maps.libre.manager.LMapElementManager
import uz.yalla.client.core.common.maps.libre.manager.LMapIconManager

object MapElementFactory {
    fun create(
        provider: MapProvider,
        iconManager: MapIconManager
    ): MapElementManager {
        return when (provider) {
            MapProvider.GOOGLE -> GMapElementManager(iconManager as GMapIconManager)
            MapProvider.LIBRE  -> LMapElementManager(iconManager as LMapIconManager)
        }
    }
}
