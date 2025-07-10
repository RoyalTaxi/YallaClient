package uz.yalla.client.feature.intro.language.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.selectable.ItemTextSelectable
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.intro.R
import uz.yalla.client.feature.intro.language.model.Language
import uz.yalla.client.feature.intro.language.model.LanguageUIState

@Composable
internal fun LanguageScreen(
    uiState: LanguageUIState,
    onIntent: (LanguageIntent) -> Unit
) {
    key(uiState.selectedLanguage) {
        Scaffold(
            containerColor = YallaTheme.color.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                LanguageHeader()

                Spacer(modifier = Modifier.height(20.dp))

                LanguageOptions(
                    languages = uiState.languages,
                    selectedLanguageTag = uiState.selectedLanguage?.languageTag,
                    onLanguageSelected = { lang -> onIntent(LanguageIntent.SetLanguage(lang)) }
                )

                Spacer(modifier = Modifier.weight(1f))

                LanguageFooter(
                    isButtonEnabled = uiState.selectedLanguage?.languageTag.isNullOrBlank().not(),
                    onNext = { onIntent(LanguageIntent.NavigateNext) }
                )
            }
        }
    }
}

@Composable
private fun LanguageHeader() {
    Text(
        text = stringResource(id = R.string.choose_language),
        color = YallaTheme.color.onBackground,
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
}

@Composable
private fun LanguageOptions(
    languages: List<Language>,
    selectedLanguageTag: String?,
    onLanguageSelected: (Language) -> Unit
) {
    languages.forEach { language ->
        ItemTextSelectable(
            text = stringResource(id = language.stringResId),
            isSelected = selectedLanguageTag == language.languageTag,
            onSelect = { onLanguageSelected(language) }
        )
    }
}

@Composable
private fun LanguageFooter(
    isButtonEnabled: Boolean,
    onNext: () -> Unit
) {
    PrimaryButton(
        text = stringResource(id = R.string.next),
        enabled = isButtonEnabled,
        onClick = onNext,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    )
}