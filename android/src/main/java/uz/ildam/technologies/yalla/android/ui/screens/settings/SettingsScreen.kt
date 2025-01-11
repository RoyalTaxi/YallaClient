package uz.ildam.technologies.yalla.android.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.SettingButton
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUIState,
    onIntent: (SettingsIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(SettingsIntent.OnNavigateBack) }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.setting),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.headline
                    )
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                SettingButton(
                    title = stringResource(R.string.app_language),
                    description = stringResource(uiState.selectedLanguage.stringResId),
                    onClick = { onIntent(SettingsIntent.OnClickLanguage) }
                )
            }
        }
    )
}