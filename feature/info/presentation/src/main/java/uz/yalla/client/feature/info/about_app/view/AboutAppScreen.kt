package uz.yalla.client.feature.info.about_app.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import  uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.navigable.ItemNavigable
import uz.yalla.client.core.common.utils.openPlayMarket
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.info.R
import uz.yalla.client.feature.info.about_app.model.AboutAppUIState

@Composable
internal fun AboutAppScreen(
    uiState: AboutAppUIState,
    onIntent: (AboutAppIntent) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier,
        topBar = { TopBar { onIntent(AboutAppIntent.OnNavigateBack) } },
        content = { paddingValues ->
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                AppInfoSection(context)

                PrivacyPolicySection(
                    uiState = uiState,
                    onClickPrivacyPolicy = { url, title ->
                        onIntent(AboutAppIntent.OnClickUrl(url, title))
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                SocialNetworksSection(
                    socialNetworks = uiState.socialNetworks,
                    onClickSocialNetwork = { url, title ->
                        onIntent(AboutAppIntent.OnClickUrl(url, title))
                    }
                )

                PrimaryButton(
                    text = stringResource(id = R.string.rate),
                    onClick = { openPlayMarket(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        title = {
            Text(
                text = stringResource(R.string.about_app),
                color = YallaTheme.color.black,
                style = YallaTheme.font.labelLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun AppInfoSection(context: Context) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )

        Text(
            text = stringResource(R.string.app_name),
            style = YallaTheme.font.title,
            color = YallaTheme.color.black,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(
                R.string.version_x,
                context
                    .packageManager
                    .getPackageInfo(context.packageName, 0)
                    .versionName
                    .orEmpty()
            ),
            style = YallaTheme.font.label,
            color = YallaTheme.color.gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PrivacyPolicySection(
    uiState: AboutAppUIState,
    onClickPrivacyPolicy: (Int, String) -> Unit
) {
    val prefs = koinInject<AppPreferences>()
    val locale by prefs.locale.collectAsState("uz")
    ItemNavigable(
        title = stringResource(R.string.user_agreement),
        onClick = {
            if (locale == "ru") {
                uiState.privacyPolicyRu?.let {
                    onClickPrivacyPolicy(it.second, it.first)
                }
            } else {
                uiState.privacyPolicyUz?.let {
                    onClickPrivacyPolicy(it.second, it.first)
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = YallaTheme.color.gray
            )
        }
    )
}

@Composable
private fun SocialNetworksSection(
    socialNetworks: List<Triple<Int, String, Int>>,
    onClickSocialNetwork: (Int, String) -> Unit
) {
    Text(
        text = stringResource(R.string.we_on_social_networks),
        style = YallaTheme.font.body,
        color = YallaTheme.color.black,
        textAlign = TextAlign.Center
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        socialNetworks.forEach { socialNetwork ->
            IconButton(
                onClick = {
                    onClickSocialNetwork(socialNetwork.third, socialNetwork.second)
                }
            ) {
                Icon(
                    painter = painterResource(socialNetwork.first),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }
        }
    }
}