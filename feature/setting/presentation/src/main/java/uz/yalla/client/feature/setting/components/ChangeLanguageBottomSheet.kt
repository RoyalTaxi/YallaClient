package uz.yalla.client.feature.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.selectable.ItemTextSelectable
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.settings.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChangeLanguageBottomSheet(
    languages: List<Language>,
    sheetState: SheetState,
    currentLanguage: Language?,
    onLanguageSelected: (Language) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetState = sheetState,
        containerColor = YallaTheme.color.background,
        onDismissRequest = onDismissRequest,
        dragHandle = null
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.surface,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.background,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_language),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.onBackground
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(color = YallaTheme.color.background)
            ) {
                languages.forEach { language ->
                    ItemTextSelectable(
                        text = stringResource(language.stringResId),
                        isSelected = currentLanguage == language,
                        onSelect = { onLanguageSelected(language) }
                    )
                }
            }
        }
    }
}