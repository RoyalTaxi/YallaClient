package uz.yalla.client.core.common.state

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun rememberPermissionState(permissions: List<String>): State<Boolean> {
    val context = LocalContext.current
    val arePermissionsGranted = rememberSaveable {
        mutableStateOf(
            permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            arePermissionsGranted.value = result.values.all { it }
        }
    )

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            if (!arePermissionsGranted.value) {
                permissionLauncher.launch(permissions.toTypedArray())
            }
        }
    }

    return arePermissionsGranted
}