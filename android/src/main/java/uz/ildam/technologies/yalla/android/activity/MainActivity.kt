package uz.ildam.technologies.yalla.android.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.BuildConfig
import uz.ildam.technologies.yalla.android.navigation.Navigation
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var updateFlowLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        setContent {
            val viewModel: MainViewModel = koinViewModel()
            val isConnected by viewModel.isConnected.collectAsState()
            Navigation(isConnected)
        }

        if (!BuildConfig.DEBUG) {
            updateFlowLauncher = registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { activityResult ->
                if (activityResult.resultCode != RESULT_OK) finish()
            }

            checkForImmediateUpdate()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = AppPreferences.locale
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        val localizedContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(localizedContext)
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
}