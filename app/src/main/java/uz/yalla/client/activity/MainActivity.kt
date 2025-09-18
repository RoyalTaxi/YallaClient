package uz.yalla.client.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import uz.yalla.client.BuildConfig
import uz.yalla.client.core.common.map.extended.google.GoogleMap
import uz.yalla.client.core.common.map.extended.libre.LibreMap
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.navigation.Navigation
import uz.yalla.client.update.AppUpdateHandler

class MainActivity : ScopeActivity() {

    private lateinit var updateFlowLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val appUpdateHandler: AppUpdateHandler by lazy { AppUpdateHandler(this) }

    private val viewModel: MainViewModel by viewModel()
    private val appPreferences: AppPreferences by inject()
    private val staticPreferences: StaticPreferences by inject()

    private val isAppReady = MutableStateFlow(false)
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreen || !isAppReady.value }

        lifecycleScope.launch {
            setContent {
                val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
                val mapType by appPreferences.mapType.collectAsStateWithLifecycle(null)

                YallaTheme {
                    val map = mapType?.let { type ->
                        when (type) {
                            MapType.Google -> GoogleMap()
                            MapType.Libre -> LibreMap()
                        }
                    }

                    map?.View()

                    Navigation(
                        isConnected = isConnected,
                        navigateToLogin = ::navigateToLoginActivity
                    )
                }
            }

            appPreferences.themeType.collectLatest { prefTheme ->
                val effectiveTheme = when (prefTheme) {
                    ThemeType.SYSTEM -> if (isSystemDark()) ThemeType.DARK else ThemeType.LIGHT
                    else -> prefTheme
                }

                val systemBarStyle = if (effectiveTheme == ThemeType.DARK) {
                    SystemBarStyle.dark(Color.Black.toArgb())
                } else {
                    SystemBarStyle.light(Color.Transparent.toArgb(), Color.Black.toArgb())
                }

                enableEdgeToEdge(
                    statusBarStyle = systemBarStyle,
                    navigationBarStyle = systemBarStyle
                )
            }
        }

        lifecycleScope.launch {
            val accessToken = appPreferences.accessToken.firstOrNull() ?: ""
            if (!staticPreferences.isDeviceRegistered || accessToken.isEmpty()) {
                navigateToLoginActivity()
                return@launch
            }
            keepSplashScreen = false
        }

        lifecycleScope.launch {
            viewModel.isReady.collectLatest { state ->
                if (state is ReadyState.Ready) isAppReady.value = true
            }
        }

        lifecycleScope.launch {
            viewModel.logoutEvent.collectLatest {
                navigateToLoginActivity()
            }
        }

        registerUpdateFlowLauncher()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        checkForUpdate()
    }

    private fun checkForUpdate() {
        if (!BuildConfig.DEBUG) {
            appUpdateHandler.checkForImmediateUpdate(updateFlowLauncher)
        }
    }

    private fun registerUpdateFlowLauncher() {
        updateFlowLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode != RESULT_OK) {
                    checkForUpdate()
                }
            }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isSystemDark(): Boolean {
        val uiMode =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return uiMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}
