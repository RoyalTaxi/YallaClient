package uz.ildam.technologies.yalla.android

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import sp.bvantur.inspektify.ktor.InspektifyKtor
import uz.ildam.technologies.yalla.android.design.color.YallaBlack
import uz.ildam.technologies.yalla.android.design.color.YallaWhite
import uz.ildam.technologies.yalla.android.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InspektifyKtor.startInspektify()
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
            Navigation()
        }
    }
}
