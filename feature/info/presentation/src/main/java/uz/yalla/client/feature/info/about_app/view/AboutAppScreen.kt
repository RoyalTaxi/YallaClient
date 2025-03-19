package uz.yalla.client.feature.info.about_app.view

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
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.navigable.ItemNavigable
import uz.yalla.client.core.common.utils.openPlayMarket
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.info.R
import uz.yalla.client.feature.info.about_app.model.AboutAppUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutAppScreen(
    uiState: AboutAppUIState,
    onIntent: (AboutAppIntent) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier,
        topBar = {
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
                    IconButton(onClick = { onIntent(AboutAppIntent.OnNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(60.dp))

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

                ItemNavigable(
                    title = stringResource(R.string.user_agreement),
                    onClick = {
                        if (AppPreferences.locale == "ru") uiState.privacyPolicyRu?.let {
                            onIntent(AboutAppIntent.OnClickUrl(it.second, it.first))
                        }
                        else uiState.privacyPolicyUz?.let {
                            onIntent(AboutAppIntent.OnClickUrl(it.second, it.first))
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

                Spacer(modifier = Modifier.weight(1f))

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
                    uiState.socialNetworks.forEach {
                        IconButton(
                            onClick = { onIntent(AboutAppIntent.OnClickUrl(it.third, it.second)) },
                        ) {
                            Icon(
                                painter = painterResource(it.first),
                                tint = Color.Unspecified,
                                contentDescription = null
                            )
                        }
                    }
                }

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
