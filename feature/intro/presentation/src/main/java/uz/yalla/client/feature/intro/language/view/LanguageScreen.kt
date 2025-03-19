package uz.yalla.client.feature.intro.language.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.selectable.ItemTextSelectable
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.intro.R
import uz.yalla.client.feature.intro.language.model.LanguageUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LanguageScreen(
    uiState: uz.yalla.client.feature.intro.language.model.LanguageUIState,
    onIntent: (LanguageIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onIntent(LanguageIntent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(id = R.string.choose_language),
                color = YallaTheme.color.black,
                style = YallaTheme.font.headline,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.choose_language_desc),
                color = YallaTheme.color.gray,
                style = YallaTheme.font.body,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            uiState.languages.forEach { lang ->
                ItemTextSelectable(
                    text = stringResource(id = lang.stringResId),
                    isSelected = uiState.selectedLanguage?.languageTag == lang.languageTag,
                    onSelect = { onIntent(LanguageIntent.SetLanguage(lang)) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(id = R.string.next),
                enabled = uiState.selectedLanguage?.languageTag.isNullOrBlank().not(),
                onClick = { onIntent(LanguageIntent.NavigateNext) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            )
        }
    }
}