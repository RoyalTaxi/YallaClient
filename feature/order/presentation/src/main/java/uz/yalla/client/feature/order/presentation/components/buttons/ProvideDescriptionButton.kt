package uz.yalla.client.feature.order.presentation.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun ProvideDescriptionButton(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    textColor: Color = YallaTheme.color.onBackground,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(YallaTheme.color.background),
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                )
        ) {
            leadingIcon?.let { it() }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = textColor,
                    style = YallaTheme.font.labelSemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (description?.isNotEmpty() == true) {
                    Text(
                        text = description,
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.body,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            trailingIcon?.let { it() }
        }
    }
}