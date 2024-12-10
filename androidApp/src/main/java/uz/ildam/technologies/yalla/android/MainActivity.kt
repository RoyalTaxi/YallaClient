package uz.ildam.technologies.yalla.android

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import uz.ildam.technologies.yalla.android.navigation.Navigation
import uz.ildam.technologies.yalla.android.ui.components.card.CardViewCard
import uz.ildam.technologies.yalla.android.ui.components.text_field.CardDateField
import uz.ildam.technologies.yalla.android.ui.components.text_field.CardNumberField
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

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
//            Navigation()
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                var number by remember { mutableStateOf("") }
                var date by remember { mutableStateOf("") }

                CardViewCard(
                    cardNumber = number,
                    cardDate = date,
                    onCardNumberChange = {
                        if (it.length <= 16 && it.all { c -> c.isDigit() }) number = it
                    },
                    onCardDateChange = {
                        if (it.length <= 16 && it.all { c -> c.isDigit() }) date = it
                    },
                    onClickCamera = {}
                )
            }
        }
    }
}
