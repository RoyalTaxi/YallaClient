package uz.yalla.client.feature.order.presentation.components.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.presentation.R

@Composable
fun DriverInfoItem(
    driver: ShowOrderModel.Executor,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            )
    ) {
        Text(
            text = stringResource(R.string.driver),
            style = YallaTheme.font.title,
            color = YallaTheme.color.onBackground
        )

        Text(
            text = "${driver.surName} ${driver.givenNames} ${driver.fatherName}",
            style = YallaTheme.font.label,
            color = YallaTheme.color.gray
        )
    }
}