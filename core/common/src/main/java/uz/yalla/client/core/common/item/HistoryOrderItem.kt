package uz.yalla.client.core.common.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun HistoryOrderItem(
    firstAddress: String,
    modifier: Modifier = Modifier,
    secondAddress: String? = null,
    time: String,
    totalPrice: String,
    status: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        colors = CardDefaults.cardColors(YallaTheme.color.gray2),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .height(IntrinsicSize.Min)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .border(
                                shape = CircleShape,
                                width = 1.dp,
                                color = YallaTheme.color.primary
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = firstAddress,
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelSemiBold
                    )
                }

                secondAddress?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .border(
                                    shape = CircleShape,
                                    width = 1.dp,
                                    color = YallaTheme.color.red
                                )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = it,
                            color = YallaTheme.color.gray,
                            style = YallaTheme.font.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = time,
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.bodySmall,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.heightIn(min = 80.dp)
            ) {
                Text(
                    text = stringResource(R.string.fixed_cost, totalPrice),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold,
                    textAlign = TextAlign.End
                )

                Text(
                    text = status,
                    color = YallaTheme.color.red,
                    style = YallaTheme.font.bodySmall
                )

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(8.dp))

                Image(
                    painter = painterResource(R.drawable.img_default_car),
                    contentDescription = null,
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}