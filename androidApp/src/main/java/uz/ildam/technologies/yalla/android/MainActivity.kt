package uz.ildam.technologies.yalla.android

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import uz.ildam.technologies.yalla.android.design.color.YallaBlack
import uz.ildam.technologies.yalla.android.design.color.YallaWhite
import uz.ildam.technologies.yalla.android.ui.screens.login.LoginScreen
import uz.ildam.technologies.yalla.android.ui.screens.onboarding.OnboardingScreen
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                YallaWhite.toArgb(),
                YallaBlack.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                YallaWhite.toArgb(),
                YallaBlack.toArgb()
            )
        )

        setContent {
            Navigator(
                if (AppPreferences.isDeviceRegistered) LoginScreen()
                else OnboardingScreen()
            ) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}
