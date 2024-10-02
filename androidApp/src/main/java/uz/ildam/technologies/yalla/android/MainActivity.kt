package uz.ildam.technologies.yalla.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.navigator.Navigator
import uz.ildam.technologies.yalla.android.design.color.Black
import uz.ildam.technologies.yalla.android.design.color.White
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.screens.credentials.CredentialsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                White.toArgb(),
                Black.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                White.toArgb(),
                Black.toArgb()
            )
        )
        setContent {
            Navigator(CredentialsScreen())
        }
    }
}