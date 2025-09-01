package uz.yalla.client.feature.map.presentation.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.app.ActivityCompat

fun requestPermission(
    context: Context,
    requestLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
        (context as Activity),
        locationPermission
    )

    if (shouldShowRationale) requestLauncher.launch(locationPermission)
    else {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        )
    }
}

fun showEnableLocationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}

fun checkLocation(
    context: Context,
    provideLocationState: (Boolean) -> Unit,
    providePermissionState: (Boolean) -> Unit,
    providePermissionVisibility: (Boolean) -> Unit
) {
    val finePermission = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val coarsePermission = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    provideLocationState(
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    )

    providePermissionState(
        finePermission == PackageManager.PERMISSION_GRANTED ||
                coarsePermission == PackageManager.PERMISSION_GRANTED
    )

    providePermissionVisibility(
        finePermission != PackageManager.PERMISSION_GRANTED ||
                coarsePermission != PackageManager.PERMISSION_GRANTED
    )
}