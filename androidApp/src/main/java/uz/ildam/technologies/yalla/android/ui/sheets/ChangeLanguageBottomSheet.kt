package uz.ildam.technologies.yalla.android.ui.sheets

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.item.ItemTextSelectable
import uz.ildam.technologies.yalla.android.ui.screens.settings.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeLanguageBottomSheet(
    languages: List<Language>,
    sheetState: SheetState,
    currentLanguage: Language?,
    onLanguageSelected: (Language) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        Toast.makeText(context, "launched", Toast.LENGTH_SHORT).show()
    }

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = null
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.gray2,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_language),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.black,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(color = YallaTheme.color.white)
            ) {
                languages.forEach {
                    ItemTextSelectable(
                        text = stringResource(it.stringResId),
                        isSelected = currentLanguage == it,
                        onSelect = { onLanguageSelected(it) }
                    )
                }
            }
        }
    }
}