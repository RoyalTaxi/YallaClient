package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.CallButton
import uz.ildam.technologies.yalla.android.ui.components.item.CarNumberItem
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel

@Composable
fun ClientWaitingBottomSheet(
    car: ShowOrderModel.Executor,
    onClickCall: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = YallaTheme.color.gray2,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .navigationBarsPadding()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.coming_to_you),
                style = YallaTheme.font.title,
                color = YallaTheme.color.black
            )

            Text(
                text = "${car.driver.color.name} ${car.driver.mark} ${car.driver.model}",
                style = YallaTheme.font.label,
                color = YallaTheme.color.gray
            )

            CarNumberItem(
                code = car.driver.stateNumber.slice(0..<2),
                number = "(\\d+|[A-Za-z]+)"
                    .toRegex()
                    .findAll(car.driver.stateNumber)
                    .map { it.value }
                    .toList()
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(20.dp)
        ) {
            CallButton(
                onClick = { onClickCall(car.phone) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}