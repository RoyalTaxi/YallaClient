package uz.yalla.client.feature.intro.permission.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.intro.R

@Composable
internal fun PermissionScreen(
    scrollState: ScrollState,
    onIntent: (PermissionIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(43.dp))

            PermissionContent()

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(56.dp))

            PermissionFooter { onIntent(PermissionIntent.GrantPermission) }
        }
    }
}

@Composable
private fun PermissionContent ()
{
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
}

@Composable
private fun PermissionFooter(
    onClickPermission: () -> Unit
) {
    PrimaryButton(
        text = stringResource(id = R.string.next),
        onClick = onClickPermission,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    )
}