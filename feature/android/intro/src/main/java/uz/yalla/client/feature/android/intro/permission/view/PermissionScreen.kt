package uz.yalla.client.feature.android.intro.permission.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.android.intro.R
import uz.yalla.client.feature.core.components.buttons.YButton
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
internal fun PermissionScreen(
    scrollState: ScrollState,
    onIntent: (PermissionIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(43.dp))

            Image(
                painter = painterResource(id = R.drawable.img_onboarding_4),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(54.dp))

            Text(
                text = stringResource(id = R.string.onboarding_4),
                color = YallaTheme.color.black,
                style = YallaTheme.font.headline,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.onboarding_4_desc),
                color = YallaTheme.color.gray,
                style = YallaTheme.font.body,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(56.dp))

            YButton(
                text = stringResource(id = R.string.next),
                onClick = { onIntent(PermissionIntent.GrantPermission) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}