package uz.yalla.client.core.common.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationBottomSheet(
    sheetState: SheetState,
    title: String,
    actionEnabled: Boolean = true,
    description: String? = null,
    actionText: String,
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.surface,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(YallaTheme.color.surface)
                .navigationBarsPadding()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.background,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.onBackground
                )

                description?.let {
                    Text(
                        text = description,
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.gray
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.background,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                PrimaryButton(
                    text = actionText,
                    enabled = actionEnabled,
                    onClick = onConfirm,
                    containerColor = YallaTheme.color.surface,
                    contentColor = YallaTheme.color.onBackground,
                    contentPadding = PaddingValues(vertical = 20.dp),
                    modifier = Modifier.weight(1f)
                )

                dismissText?.let {
                    PrimaryButton(
                        text = dismissText,
                        onClick = onDismissRequest,
                        contentPadding = PaddingValues(vertical = 20.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}