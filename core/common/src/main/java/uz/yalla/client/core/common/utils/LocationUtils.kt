package uz.yalla.client.core.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.bundle.Bundle
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

const val timeoutMillis: Long = 10000
const val maxAgeMillis: Long = 5 * 60 * 1000

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onPermissionDenied: () -> Unit = {},
    onLocationFetched: (LatLng) -> Unit,
) {

    if (!hasLocationPermission(context)) {
        onPermissionDenied()
        return
    }

    var locationFound = false
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    tryGetLastKnownLocation(context)?.let {
        locationFound = true
        onLocationFetched(LatLng(it.latitude, it.longitude))
        return
    }

    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        tryGpsLocation(locationManager) { location ->
            if (!locationFound) {
                locationFound = true
                onLocationFetched(LatLng(location.latitude, location.longitude))
            }
        }
    }

    Handler(Looper.getMainLooper()).postDelayed({
        if (!locationFound && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            tryNetworkLocation(locationManager) { location ->
                if (!locationFound) {
                    locationFound = true
                    onLocationFetched(LatLng(location.latitude, location.longitude))
                }
            }
        }
    }, timeoutMillis / 2)

    Handler(Looper.getMainLooper()).postDelayed({
        if (!locationFound) {
            tryFusedLocation(context) { location ->
                if (!locationFound) {
                    locationFound = true
                    onLocationFetched(LatLng(location.latitude, location.longitude))
                }
            }
        }
    }, timeoutMillis * 3 / 4)

    Handler(Looper.getMainLooper()).postDelayed({
        if (!locationFound) {
            tryBestProvider(locationManager) { location ->
                if (!locationFound) {
                    locationFound = true
                    onLocationFetched(LatLng(location.latitude, location.longitude))
                }
            }
        }
    }, timeoutMillis)
}

fun hasLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Try to get a reasonably recent cached location
 */
@SuppressLint("MissingPermission")
private fun tryGetLastKnownLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = locationManager.getProviders(true)

    var bestLocation: Location? = null
    val minTime = System.currentTimeMillis() - maxAgeMillis

    // Try GPS first
    if (providers.contains(LocationManager.GPS_PROVIDER)) {
        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (gpsLocation != null && gpsLocation.time >= minTime) {
            return gpsLocation
        }
        bestLocation = gpsLocation
    }

    // Then try network provider
    if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (networkLocation != null && networkLocation.time >= minTime) {
            return networkLocation
        }
        if (bestLocation == null ||
            (networkLocation != null && networkLocation.time > bestLocation.time)
        ) {
            bestLocation = networkLocation
        }
    }

    // Then try Fused location
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    try {
        val task = fusedLocationClient.lastLocation
        if (task.isComplete && task.isSuccessful && task.result != null) {
            val fusedLocation = task.result
            if (fusedLocation.time >= minTime) {
                return fusedLocation
            }
            if (bestLocation == null || fusedLocation.time > bestLocation.time) {
                bestLocation = fusedLocation
            }
        }
    } catch (e: Exception) {
        // Ignore errors with fused location
    }

    return bestLocation
}

/**
 * Try to get location from GPS provider
 */
@SuppressLint("MissingPermission")
private fun tryGpsLocation(
    locationManager: LocationManager,
    onLocationReceived: (Location) -> Unit
) {
    val gpsListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationManager.removeUpdates(this)
            onLocationReceived(location)
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    try {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            gpsListener,
            Looper.getMainLooper()
        )

        // Remove listener after timeout
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                locationManager.removeUpdates(gpsListener)
            } catch (e: Exception) {
                // Ignore
            }
        }, timeoutMillis)
    } catch (e: Exception) {
        // Ignore if provider is not available
    }
}

/**
 * Try to get location from network provider
 */
@SuppressLint("MissingPermission")
private fun tryNetworkLocation(
    locationManager: LocationManager,
    onLocationReceived: (Location) -> Unit
) {
    val networkListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationManager.removeUpdates(this)
            onLocationReceived(location)
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    try {
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            networkListener,
            Looper.getMainLooper()
        )

        // Remove listener after timeout
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                locationManager.removeUpdates(networkListener)
            } catch (e: Exception) {
                // Ignore
            }
        }, timeoutMillis)
    } catch (e: Exception) {
        // Ignore if provider is not available
    }
}

/**
 * Try to get location using Fused Location Provider
 */
@SuppressLint("MissingPermission")
private fun tryFusedLocation(
    context: Context,
    onLocationReceived: (Location) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeoutMillis)
        .setWaitForAccurateLocation(false)
        .setMinUpdateIntervalMillis(0)
        .setMaxUpdateDelayMillis(timeoutMillis)
        .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            fusedLocationClient.removeLocationUpdates(this)
            val location = locationResult.lastLocation
            if (location != null) {
                onLocationReceived(location)
            }
        }
    }

    try {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        // Remove callback after timeout
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            } catch (e: Exception) {
                // Ignore
            }
        }, timeoutMillis)
    } catch (_: Exception) {
    }
}


@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
private fun tryBestProvider(
    locationManager: LocationManager,
    onLocationReceived: (Location) -> Unit
) {
    val criteria = Criteria()
    criteria.accuracy = Criteria.ACCURACY_FINE
    criteria.powerRequirement = Criteria.POWER_HIGH

    val bestProvider = locationManager.getBestProvider(criteria, true) ?: return

    val bestListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationManager.removeUpdates(this)
            onLocationReceived(location)
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    try {
        locationManager.requestLocationUpdates(
            bestProvider,
            0,
            0f,
            bestListener,
            Looper.getMainLooper()
        )

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                locationManager.removeUpdates(bestListener)
            } catch (_: Exception) {
            }
        }, 5000)
    } catch (_: Exception) {
    }
}