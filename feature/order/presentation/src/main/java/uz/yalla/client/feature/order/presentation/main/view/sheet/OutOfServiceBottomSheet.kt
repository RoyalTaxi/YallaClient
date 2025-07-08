package uz.yalla.client.feature.order.presentation.main.view.sheet

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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R

@Composable
fun NoServiceBottomSheet(
    setCurrentLocation: () -> Unit,
    onAppear: (Dp) -> Unit
) {
    val density = LocalDensity.current
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = YallaTheme.color.surface,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .onSizeChanged { onAppear(with(density) { it.height.toDp() }) }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .background(
                    color = YallaTheme.color.background,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.no_service_massage),
                style = YallaTheme.font.title,
                color = YallaTheme.color.onBackground
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = YallaTheme.color.background,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .navigationBarsPadding()
        ) {
            PrimaryButton(
                text = stringResource(R.string.new_address),
                onClick = setCurrentLocation,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            )
        }
    }
}