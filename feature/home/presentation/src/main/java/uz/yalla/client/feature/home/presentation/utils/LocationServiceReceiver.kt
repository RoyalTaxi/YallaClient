package uz.yalla.client.feature.home.presentation.utils

import android.content.Context
import android.content.Intent

class LocationServiceReceiver(
    private val onLocationServiceChanged: (Boolean) -> Unit
) : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == android.location.LocationManager.PROVIDERS_CHANGED_ACTION) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            val isEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
            onLocationServiceChanged(isEnabled)
        }
    }
}