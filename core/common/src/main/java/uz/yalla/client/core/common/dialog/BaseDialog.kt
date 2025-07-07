package uz.yalla.client.core.common.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun BaseDialog(
    title: String,
    description: String? = null,

    actionText: String,
    dismissText: String? = null,

    onAction: () -> Unit,
    onDismiss: () -> Unit,

    properties: DialogProperties = DialogProperties()
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = properties
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(YallaTheme.color.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = title,
                    color = YallaTheme.color.onBackground,
                    style = YallaTheme.font.title,
                    textAlign = TextAlign.Center
                )

                description?.let {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = description,
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.label,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = actionText,
                    onClick = onAction,
                    containerColor = YallaTheme.color.onBackground,
                    contentColor = YallaTheme.color.background,
                    contentPadding = PaddingValues(20.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                dismissText?.let {
                    Spacer(modifier = Modifier.height(10.dp))

                    PrimaryButton(
                        text = dismissText,
                        onClick = onDismiss,
                        containerColor = YallaTheme.color.surface,
                        contentColor = YallaTheme.color.onBackground,
                        contentPadding = PaddingValues(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTitleDescriptionActionDismiss() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BaseDialog(
            title = "This is title",
            description = "This is description",
            actionText = "Action",
            dismissText = "Dismiss",
            onAction = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewTitleDescriptionAction() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BaseDialog(
            title = "This is title",
            description = "This is description",
            actionText = "Action",
            onAction = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewTitleAction() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BaseDialog(
            title = "This is title",
            actionText = "Action",
            onAction = {},
            onDismiss = {}
        )
    }
}