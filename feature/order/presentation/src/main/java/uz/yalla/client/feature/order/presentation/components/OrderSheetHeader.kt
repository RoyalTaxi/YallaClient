package uz.yalla.client.feature.order.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.item.CarNumberItem
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

@Composable
fun OrderSheetHeader(
    text: String,
    modifier:Modifier = Modifier,
    timer: String? = null,
    selectedDriver: ShowOrderModel? = null
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = YallaTheme.color.white,
                shape = RoundedCornerShape(30.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = text,
            style = YallaTheme.font.title,
            color = YallaTheme.color.black
        )

        selectedDriver?.let { driver ->
            Text(
                text = "${driver.executor.driver.color.name} ${driver.executor.driver.mark} ${driver.executor.driver.model}",
                style = YallaTheme.font.label,
                color = YallaTheme.color.gray
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()

            ) {

                CarNumberItem(
                    code = driver.executor.driver.stateNumber.slice(0..<2),
                    number = "(\\d+|[A-Za-z]+)"
                        .toRegex()
                        .findAll(driver.executor.driver.stateNumber.substring(2))
                        .map { it.value }
                        .toList()
                )

                timer?.let {
                    Text(
                        text = it,
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.primary
                    )
                }
            }
        }
    }
}
