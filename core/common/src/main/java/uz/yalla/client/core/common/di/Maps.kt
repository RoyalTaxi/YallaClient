package uz.yalla.client.core.common.di

import android.content.Context
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import uz.yalla.client.core.common.maps.manager.MapIconManager
import uz.yalla.client.core.common.maps.manager.MapElementManager
import uz.yalla.client.core.common.maps.manager.MapThemeManager

/**
 * Dependency injection module for map-related components
 */
object Maps {
    val module = module {
        // Register MapIconManager
        factory { MapIconManager(get<Context>()) }
        
        // Register MapElementManager
        factory { MapElementManager(get<MapIconManager>()) }
        
        // Register MapThemeManager
        factoryOf(::MapThemeManager)
    }
}