package uz.yalla.client.feature.intro.notification_permission.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun NotificationPermissionRoute(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var isNotificationPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val notificationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> isNotificationPermissionGranted = granted }

    LaunchedEffect(isNotificationPermissionGranted) {
        launch(Dispatchers.Main) {
            if (isNotificationPermissionGranted) {
                onPermissionGranted()
            }
        }
    }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.Main) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    NotificationPermissionScreen(
        scrollState = scrollState,
        onIntent = { intent ->
            when (intent) {
                NotificationPermissionIntent.GrantPermission -> {
                    if (isNotificationPermissionGranted) {
                        onPermissionGranted()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onPermissionGranted()
                        }
                    }
                }

                NotificationPermissionIntent.Skip -> {
                    onPermissionGranted()
                }
            }
        }
    )
}