package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.item.SearchCarItem
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.core.components.buttons.YButton
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun DriverWaitingBottomSheet(
    car: ShowOrderModel.Executor,
    timer: String,
    onCancel: () -> Unit,
    onClickCall: (String) -> Unit,
    onOptionsClick: () -> Unit,
    onAppear: (Dp) -> Unit
) {
    val density = LocalDensity.current
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = YallaTheme.color.gray2,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .onSizeChanged { size ->
                with(density) { onAppear(size.height.toDp()) }
            }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.waiting_for_you),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.black
                )

                Text(
                    text = car.driver.stateNumber,
                    style = YallaTheme.font.labelSemiBold,
                    color = YallaTheme.color.black
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()

            ) {
                Text(
                    text = "${car.driver.color.name} ${car.driver.mark} ${car.driver.model}",
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.gray
                )

                Text(
                    text = timer,
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.primary
                )
            }
        }

        SearchCarItem(
            modifier = Modifier.clip(RoundedCornerShape(30.dp)),
            text = stringResource(R.string.cancel_order),
            imageVector = Icons.Default.Close,
            onClick = onCancel
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
//            OptionsButton(
//                modifier = Modifier.fillMaxHeight(),
//                tint = YallaTheme.color.red,
//                painter = painterResource(R.drawable.ic_x),
//                onClick = onCancel
//            )

            YButton(
                text = stringResource(R.string.connect),
                contentPadding = PaddingValues(vertical = 20.dp),
                onClick = { onClickCall(car.phone) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

//            OptionsButton(
//                modifier = Modifier.fillMaxHeight(),
//                painter = painterResource(R.drawable.img_options),
//                tint = YallaTheme.color.black,
//                onClick = onOptionsClick
//            )
        }
    }
}