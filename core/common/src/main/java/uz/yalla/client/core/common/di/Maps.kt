package uz.yalla.client.core.common.di

import android.content.Context
import org.koin.dsl.module
import uz.yalla.client.core.common.maps.core.manager.MapElementManager
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager
import uz.yalla.client.core.common.maps.core.manager.DelegatingMapElementManager
import uz.yalla.client.core.common.maps.core.manager.DelegatingMapIconManager
import uz.yalla.client.core.common.maps.core.manager.DelegatingMapThemeManager
import uz.yalla.client.core.common.maps.google.manager.GMapIconManager
import uz.yalla.client.core.common.maps.google.manager.GMapThemeManager
import uz.yalla.client.core.common.maps.google.controller.GMapController
import uz.yalla.client.core.common.maps.libre.manager.LMapIconManager
import uz.yalla.client.core.common.maps.libre.manager.LMapThemeManager
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.maps.core.controller.MapController
import uz.yalla.client.core.common.maps.libre.controller.LMapController
import uz.yalla.client.core.common.maps.core.controller.DelegatingMapController

object Maps {
    val module = module {
        factory {
            GMapIconManager(
                context = get<Context>(),
                driverResId = R.drawable.img_car_marker,
                originResId = R.drawable.ic_origin_marker,
                middleResId = R.drawable.ic_middle_marker,
                destinationResId = R.drawable.ic_destination_marker
            )
        }

        factory {
            LMapIconManager(
                context = get<Context>(),
                driverResId = R.drawable.img_car_marker,
                originResId = R.drawable.ic_origin_marker,
                middleResId = R.drawable.ic_middle_marker,
                destinationResId = R.drawable.ic_destination_marker
            )
        }

        factory<MapElementManager> {
            DelegatingMapElementManager(
                googleIconManager = get<GMapIconManager>(),
                libreIconManager = get<LMapIconManager>()
            )
        }

        factory { GMapThemeManager() }
        factory {
            LMapThemeManager(
                "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json",
                "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
            )
        }
        factory<MapThemeManager> { DelegatingMapThemeManager(get(), get()) }

        factory<MapIconManager> { DelegatingMapIconManager(get(), get(), get()) }

        factory<MapController> { DelegatingMapController(GMapController(), LMapController()) }
    }
}
