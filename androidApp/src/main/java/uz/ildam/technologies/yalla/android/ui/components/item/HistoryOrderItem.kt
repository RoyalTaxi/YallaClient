package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryItem

@Composable
fun HistoryOrderItem(
    order: OrderHistoryItem,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(YallaTheme.color.gray),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                order.taxi.routes.firstOrNull()?.let {
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
                            text = it.fullAddress,
                            color = YallaTheme.color.black,
                            style = YallaTheme.font.labelSemiBold
                        )
                    }
                }

                order.taxi.routes.lastOrNull()?.let {
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
                            text = it.fullAddress,
                            color = YallaTheme.color.gray,
                            style = YallaTheme.font.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = order.time,
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.bodySmall,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = order.taxi.totalPrice.toString(),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold,
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(28.dp))

                Image(
                    painter = painterResource(R.drawable.img_default_car),
                    contentDescription = null,
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}