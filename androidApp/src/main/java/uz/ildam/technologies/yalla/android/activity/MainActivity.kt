package uz.ildam.technologies.yalla.android.activity

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.ildam.technologies.yalla.android.connectivity.AndroidConnectivityObserver
import uz.ildam.technologies.yalla.android.navigation.Navigation

class MainActivity : AppCompatActivity() {
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
            val viewModel = viewModel <MainViewModel> {
                MainViewModel(AndroidConnectivityObserver(this@MainActivity))
            }
            val isConnected by viewModel.isConnected.collectAsState()
            Navigation(isConnected)
        }
    }
}
