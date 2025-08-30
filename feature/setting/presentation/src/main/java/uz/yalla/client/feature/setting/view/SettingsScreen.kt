package uz.yalla.client.feature.setting.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.setting.components.SettingButton
import uz.yalla.client.feature.setting.intent.SettingsIntent
import uz.yalla.client.feature.setting.intent.SettingsState
import uz.yalla.client.feature.settings.R

@Composable
internal fun SettingsScreen(
    uiState: SettingsState,
    onIntent: (SettingsIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.background,
        topBar = { SettingsTopBar(onNavigateBack = { onIntent(SettingsIntent.OnNavigateBack) }) },
        content = { paddingValues ->
            SettingsContent(
                modifier = Modifier.padding(paddingValues),
                selectedThemeId = uiState.selectedTheme?.stringResId,
                selectedLanguageID = uiState.selectedLanguage?.stringResId,
                onIntent = onIntent
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.background),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = YallaTheme.color.onBackground
                )
            }
        },
        title = {}
    )
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    selectedLanguageID: Int?,
    selectedThemeId: Int?,
    onIntent: (SettingsIntent) -> Unit
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.setting),
            color = YallaTheme.color.onBackground,
            style = YallaTheme.font.headline,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SettingButton(
            title = stringResource(R.string.app_language),
            description = stringResource(selectedLanguageID ?: R.string.uzbek),
            onClick = { onIntent(SettingsIntent.OnClickLanguage) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        SettingButton(
            title = stringResource(R.string.theme),
            description = stringResource(selectedThemeId ?: R.string.system_mode),
            onClick = { onIntent(SettingsIntent.OnClickTheme) }
        )
    }
}