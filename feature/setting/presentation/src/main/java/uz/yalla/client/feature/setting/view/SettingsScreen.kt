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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.setting.components.ChangeLanguageBottomSheet
import uz.yalla.client.feature.setting.components.SettingButton
import uz.yalla.client.feature.setting.model.SettingsUIState
import uz.yalla.client.feature.settings.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    uiState: SettingsUIState,
    changeLanguageSheetVisibility: Boolean,
    changeLanguageSheetState: SheetState,
    onDismissRequest: () -> Unit,
    onIntent: (SettingsIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = { SettingsTopBar(onNavigateBack = { onIntent(SettingsIntent.OnNavigateBack) }) },
        content = { paddingValues ->
            SettingsContent(
                modifier = Modifier.padding(paddingValues),
                selectedLanguageID = uiState.selectedLanguage?.stringResId,
                onClickLanguage = { onIntent(SettingsIntent.OnClickLanguage) }
            )

            if (changeLanguageSheetVisibility) {
                ChangeLanguageBottomSheet(
                    languages = uiState.languages,
                    sheetState = changeLanguageSheetState,
                    currentLanguage = uiState.selectedLanguage,
                    onLanguageSelected = { onIntent(SettingsIntent.OnUpdateLanguage(it)) },
                    onDismissRequest = onDismissRequest
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
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
    onClickLanguage: () -> Unit
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.setting),
            color = YallaTheme.color.black,
            style = YallaTheme.font.headline,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SettingButton(
            title = stringResource(R.string.app_language),
            description = stringResource(selectedLanguageID ?: R.string.uzbek),
            onClick = onClickLanguage
        )
    }
}