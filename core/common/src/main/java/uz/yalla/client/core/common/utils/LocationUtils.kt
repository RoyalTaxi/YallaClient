package uz.yalla.client.core.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val LOCATION_TIMEOUT_MS = 10000L
private const val LOCATION_MAX_AGE_MS = 5 * 60 * 1000L

fun Context.hasLocationPermission(): Boolean {
    val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}

suspend fun getCurrentLocationSafely(
    context: Context,
    onPermissionDenied: () -> Unit = {},
    onLocationFetched: (LatLng) -> Unit,
) = withContext(Dispatchers.Main) {
    if (!context.hasLocationPermission()) {
        onPermissionDenied()
        return@withContext
    }

    try {
        getLastKnownLocation(context)?.let {
            onLocationFetched(LatLng(it.latitude, it.longitude))
            return@withContext
        }

        withTimeout(LOCATION_TIMEOUT_MS.milliseconds) {
            val location = tryGetFreshLocation(context)
            onLocationFetched(LatLng(location.latitude, location.longitude))
        }
    } catch (e: TimeoutCancellationException) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        tryBestProviderLocation(locationManager)?.let {
            onLocationFetched(LatLng(it.latitude, it.longitude))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
    }
}

@SuppressLint("MissingPermission")
private suspend fun tryGetFreshLocation(context: Context): Location = withContext(Dispatchers.IO) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val completable = CompletableDeferred<Location>()

    val onLocationReceived: (Location) -> Unit = { location ->
        if (!completable.isCompleted) {
            completable.complete(location)
        }
    }

    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        tryGpsLocation(locationManager, onLocationReceived)
    }

    kotlinx.coroutines.delay(2.seconds)
    if (!completable.isCompleted && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        tryNetworkLocation(locationManager, onLocationReceived)
    }

    kotlinx.coroutines.delay(2.seconds)
    if (!completable.isCompleted) {
        tryFusedLocation(context, onLocationReceived)
    }

    completable.await()
}

@SuppressLint("MissingPermission")
private fun getLastKnownLocation(context: Context): Location? {
    if (!context.hasLocationPermission()) return null

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = locationManager.getProviders(true)

    var bestLocation: Location? = null
    val minTime = System.currentTimeMillis() - LOCATION_MAX_AGE_MS

    if (providers.contains(LocationManager.GPS_PROVIDER)) {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { location ->
            if (location.time >= minTime) return location
            bestLocation = location
        }
    }

    if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let { location ->
            if (location.time >= minTime) return location
            if (bestLocation == null || location.time > bestLocation!!.time) {
                bestLocation = location
            }
        }
    }

    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val task = fusedLocationClient.lastLocation
        if (task.isComplete && task.isSuccessful && task.result != null) {
            val fusedLocation = task.result
            if (fusedLocation.time >= minTime) return fusedLocation
            if (bestLocation == null || fusedLocation.time > bestLocation!!.time) {
                bestLocation = fusedLocation
            }
        }
    } catch (_: Exception) {
    }

    return bestLocation
}

@SuppressLint("MissingPermission")
private fun tryGpsLocation(
    locationManager: LocationManager,
    onLocationReceived: (Location) -> Unit
) {
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return

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
    } catch (_: Exception) {
    }
}

@SuppressLint("MissingPermission")
private fun tryNetworkLocation(
    locationManager: LocationManager,
    onLocationReceived: (Location) -> Unit
) {
    if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) return

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
    } catch (_: Exception) {
    }
}

@SuppressLint("MissingPermission")
private fun tryFusedLocation(
    context: Context,
    onLocationReceived: (Location) -> Unit
) {
    if (!context.hasLocationPermission()) return

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_TIMEOUT_MS)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(0)
            .setMaxUpdateDelayMillis(LOCATION_TIMEOUT_MS)
            .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            fusedLocationClient.removeLocationUpdates(this)
            locationResult.lastLocation?.let { location ->
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
    } catch (_: Exception) {
    }
}

@SuppressLint("MissingPermission")
private fun tryBestProviderLocation(
    locationManager: LocationManager
): Location? {
    val criteria = Criteria().apply {
        accuracy = Criteria.ACCURACY_FINE
        powerRequirement = Criteria.POWER_HIGH
    }

    val bestProvider = locationManager.getBestProvider(criteria, true) ?: return null

    return try {
        locationManager.getLastKnownLocation(bestProvider)
    } catch (e: Exception) {
        null
    }
}

fun getCurrentLocation(
    context: Context,
    onPermissionDenied: () -> Unit = {},
    onLocationFetched: (LatLng) -> Unit,
) {
    MainScope().launch {
        getCurrentLocationSafely(context, onPermissionDenied, onLocationFetched)
    }
}