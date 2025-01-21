package uz.yalla.client.feature.android.setting.settings.view

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
import uz.yalla.client.feature.android.setting.settings.components.ChangeLanguageBottomSheet
import uz.yalla.client.feature.android.setting.settings.components.SettingButton
import uz.yalla.client.feature.android.setting.settings.model.SettingsUIState
import uz.yalla.client.feature.android.settings.R
import uz.yalla.client.feature.core.design.theme.YallaTheme

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
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(SettingsIntent.OnNavigateBack) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {}
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
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
                    description = stringResource(uiState.selectedLanguage.stringResId),
                    onClick = { onIntent(SettingsIntent.OnClickLanguage) }
                )
            }

            if (changeLanguageSheetVisibility) ChangeLanguageBottomSheet(
                languages = uiState.languages,
                sheetState = changeLanguageSheetState,
                currentLanguage = uiState.selectedLanguage,
                onLanguageSelected = { onIntent(SettingsIntent.OnUpdateLanguage(it)) },
                onDismissRequest = onDismissRequest
            )
        }
    )
}