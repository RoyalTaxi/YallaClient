package uz.yalla.client.ui.screens.offline

import android.content.Intent
import android.content.Intent.ACTION_DIAL
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import uz.yalla.client.R
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun OfflineScreen(
    onRetry: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = koinInject<AppPreferences>()
    val supportNumber by prefs.supportNumber.collectAsStateWithLifecycle("")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures() }
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = YallaTheme.color.background)
            .systemBarsPadding()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.no_internet_connection),
            style = YallaTheme.font.title,
            color = YallaTheme.color.onBackground,
            textAlign = TextAlign.Center
        )

        Image(
            painter = painterResource(id = R.drawable.img_offline),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )

        Text(
            text = stringResource(R.string.no_internet_connection_body),
            style = YallaTheme.font.label,
            color = YallaTheme.color.gray,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = YallaTheme.color.primary
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text = stringResource(R.string.repeat).uppercase(),
                style = YallaTheme.font.labelLarge,
                color = YallaTheme.color.primary,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = stringResource(R.string.call),
            containerColor = YallaTheme.color.primary,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val intent = Intent(ACTION_DIAL).apply {
                    data = "tel:$supportNumber".toUri()
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            }
        )
    }
}