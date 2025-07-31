package uz.yalla.client.activity

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import uz.yalla.client.BuildConfig
import uz.yalla.client.R
import uz.yalla.client.core.common.maps.MapsFragment
import uz.yalla.client.core.common.maps.MapsViewModel
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.databinding.ActivityMainBinding
import uz.yalla.client.navigation.Navigation
import uz.yalla.client.update.AppUpdateHandler
import uz.yalla.client.activity.ReadyState

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var updateFlowLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val appUpdateHandler: AppUpdateHandler by lazy { AppUpdateHandler(this) }

    private val viewModel: MainViewModel by viewModel()
    private val mapsViewModel: MapsViewModel by viewModel()

    // Keep track of whether the app is ready to be shown
    private val isAppReady = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen before calling super.onCreate
        val splashScreen = installSplashScreen()

        // Keep the splash screen visible until the app is ready
        splashScreen.setKeepOnScreenCondition { !isAppReady.value }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.onAppear()

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
            .add(R.id.fragmentContainerHost, MapsFragment())
            .commit()

        // Observe both MainViewModel readiness and MapsViewModel map readiness
        lifecycleScope.launch {
            combine(
                viewModel.isReady,
                mapsViewModel.container.stateFlow
            ) { mainReady, mapsState ->
                mainReady is ReadyState.Ready && mapsState.isMapReady
            }.collectLatest { ready ->
                isAppReady.value = ready
            }
        }

        binding.mainView.setContent {
            val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
            YallaTheme {
                Navigation(isConnected = isConnected)
            }
        }
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
}
