package uz.yalla.client.feature.order.presentation.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun ConfirmationDialog(
    title: String,
    description: String? = null,
    actionText: String? = null,
    dismissText: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit = { }
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = YallaTheme.color.background,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = title,
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!description.isNullOrEmpty()) {
                    Text(
                        text = description,
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrimaryButton(
                        text = dismissText,
                        onClick = onDismissRequest,
                        containerColor = YallaTheme.color.surface,
                        contentColor = YallaTheme.color.onBackground,
                        contentPadding = PaddingValues(vertical = 20.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!actionText.isNullOrEmpty()) {
                        PrimaryButton(
                            text = actionText,
                            onClick = onConfirm,
                            containerColor = YallaTheme.color.onBackground,
                            contentColor = YallaTheme.color.background,
                            contentPadding = PaddingValues(vertical = 20.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}