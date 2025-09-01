package uz.yalla.client.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import uz.yalla.client.BuildConfig
import uz.yalla.client.R
import uz.yalla.client.core.common.maps.ui.MapHostFragment
import uz.yalla.client.core.common.maps.core.viewmodel.MapsViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.core.domain.model.MapType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.koin.core.parameter.parametersOf
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.databinding.ActivityMainBinding
import uz.yalla.client.navigation.Navigation
import uz.yalla.client.update.AppUpdateHandler

class MainActivity : ScopeActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var updateFlowLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val appUpdateHandler: AppUpdateHandler by lazy { AppUpdateHandler(this) }

    private val viewModel: MainViewModel by viewModel()
    private val mapsViewModel: MapsViewModel by inject()
    private val appPreferences: AppPreferences by inject()
    private val staticPreferences: StaticPreferences by inject()

    private val isAppReady = MutableStateFlow(false)
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreen || !isAppReady.value }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val accessToken = appPreferences.accessToken.firstOrNull() ?: ""
            if (!staticPreferences.isDeviceRegistered || accessToken.isEmpty()) {
                navigateToLoginActivity()
                return@launch
            }
            keepSplashScreen = false
        }

        registerUpdateFlowLauncher()

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

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerHost, MapHostFragment())
            .commit()

        lifecycleScope.launch {
            // Add a timeout to ensure the app doesn't get stuck waiting for the map
            kotlinx.coroutines.withTimeoutOrNull(5000) { // 5 second timeout
                combine(
                    viewModel.isReady,
                    mapsViewModel.state
                ) { mainReady, mapsState ->
                    mainReady is ReadyState.Ready && mapsState.isMapReady
                }.collectLatest { ready ->
                    if (ready) {
                        isAppReady.value = true
                    }
                }
            }

            // If timeout occurs, proceed anyway
            if (!isAppReady.value) {
                isAppReady.value = true
            }
        }

        binding.mainView.setContent {
            val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
            YallaTheme {
                Navigation(
                    isConnected = isConnected,
                    navigateToLogin = ::navigateToLoginActivity
                )
            }
        }

        lifecycleScope.launch {
            viewModel.logoutEvent.collectLatest {
                navigateToLoginActivity()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Ensure map style is reapplied if system theme changed while app was backgrounded
        mapsViewModel.onStartReapplyIfNeeded()
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
}
