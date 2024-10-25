package uz.ildam.technologies.yalla.android.ui.screens.language

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.item.LanguageItem
import uz.ildam.technologies.yalla.android.ui.components.toolbar.YallaToolbar

@Composable
internal fun LanguageScreen(
    uiState: LanguageUIState,
    onIntent: (LanguageIntent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.white)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        YallaToolbar(onClick = { onIntent(LanguageIntent.NavigateBack) })

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
            LanguageItem(
                text = stringResource(id = lang.stringResId),
                isSelected = uiState.selectedLanguage?.languageTag == lang.languageTag,
                onSelect = { onIntent(LanguageIntent.SetLanguage(lang)) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        YallaButton(
            text = stringResource(id = R.string.next),
            enabled = uiState.selectedLanguage?.languageTag.isNullOrBlank().not(),
            onClick = { onIntent(LanguageIntent.NavigateNext) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        )
    }
}