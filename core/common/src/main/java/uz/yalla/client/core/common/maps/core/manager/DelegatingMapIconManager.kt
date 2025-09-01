package uz.yalla.client.core.common.maps.core.manager

import android.content.Context
import uz.yalla.client.core.common.maps.google.manager.GMapIconManager
import uz.yalla.client.core.common.maps.libre.manager.LMapIconManager

/**
 * Calls both providers' icon operations. Used only by VM for cache/init hooks.
 * Element managers use their own icon managers, so require* methods are not used here.
 */
class DelegatingMapIconManager(
    context: Context,
    private val gIcon: GMapIconManager,
    private val lIcon: LMapIconManager
) : MapIconManager(context) {
    override fun initializeIcons() {
        gIcon.initializeIcons()
        lIcon.initializeIcons()
    }

    override fun createMarkerIcons(carArrivesInMinutes: Int?, orderEndsInMinutes: Int?, hasOrder: Boolean, isDark: Boolean) {
        gIcon.createMarkerIcons(carArrivesInMinutes, orderEndsInMinutes, hasOrder, isDark)
        lIcon.createMarkerIcons(carArrivesInMinutes, orderEndsInMinutes, hasOrder, isDark)
    }

    override fun requireDriverIcon(): Any = gIcon.requireDriverIcon()
    override fun requireOriginIcon(): Any = gIcon.requireOriginIcon()
    override fun requireMiddleIcon(): Any = gIcon.requireMiddleIcon()
    override fun requireDestinationIcon(): Any = gIcon.requireDestinationIcon()
    override fun clearCache() { gIcon.clearCache(); lIcon.clearCache() }
}

