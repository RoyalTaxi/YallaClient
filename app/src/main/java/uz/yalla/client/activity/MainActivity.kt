package uz.yalla.client.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import org.koin.androidx.viewmodel.ext.android.viewModel
import uz.yalla.client.BuildConfig
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.navigation.Navigation

class MainActivity : AppCompatActivity() {

    private lateinit var updateFlowLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private val viewModel: MainViewModel by viewModel()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                this,
                "Bildirishnomalar o'chiq. Kerakli habarlani o'tkizib yuborishingiz mumkin",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.isReady.value is ReadyState.Loading
        }

        updateFlowLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            if (activityResult.resultCode != RESULT_OK) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Black.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Black.toArgb()
            )
        )

        viewModel.refreshFCMToken()

        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }

        setContent {
            val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
            val isDeviceRegistered by viewModel.isDeviceRegistered.collectAsStateWithLifecycle()
            val skipOnboarding by viewModel.skipOnboarding.collectAsStateWithLifecycle()

            if (isDeviceRegistered != null && skipOnboarding != null) {
                YallaTheme {
                    Navigation(
                        isConnected = isConnected,
                        isDeviceRegistered = isDeviceRegistered!!,
                        skipOnboarding = skipOnboarding!!
                    )
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkForImmediateUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val updateAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            if (updateAvailable) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateFlowLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                        .setAllowAssetPackDeletion(true)
                        .build()
                )
            }
        }

        appUpdateInfoTask.addOnFailureListener { exception ->
            showErrorDialog(exception.message ?: "Неизвестная ошибка")
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Проверка обновлений не удалась")
            .setMessage("Ошибка: $message")
            .setCancelable(false)
            .setNegativeButton("Закрыть") { _, _ -> finish() }
            .show()
    }

//    override fun onResume() {
//        super.onResume()
//        if (!BuildConfig.DEBUG) {
//            checkForImmediateUpdate()
//        }
//    }
}