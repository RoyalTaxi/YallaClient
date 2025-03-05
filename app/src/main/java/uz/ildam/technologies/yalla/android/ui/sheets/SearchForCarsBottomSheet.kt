package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.item.SearchCarItem
import uz.ildam.technologies.yalla.android.ui.components.progress.YallaProgressBar
import uz.ildam.technologies.yalla.android.ui.screens.map.MapUIState
import uz.ildam.technologies.yalla.android.ui.screens.map.MapViewModel
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.yalla.client.feature.core.design.theme.YallaTheme
import kotlin.time.Duration.Companion.seconds

@Composable
fun SearchForCarsBottomSheet(
    uiState: MapUIState,
    currentLocation: MapPoint,
    onClickCancel: () -> Unit,
    onClickDetails: () -> Unit,
    viewModel: MapViewModel,
    onAppear: (Dp) -> Unit
) {
    val density = LocalDensity.current
    val totalTime = uiState.setting?.orderCancelTime.or0()
    var currentTime by rememberSaveable { mutableIntStateOf(0) }
    val progress by animateFloatAsState(
        targetValue = currentTime.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "progress_anim"
    )

    LaunchedEffect(Unit) {
        launch { viewModel.searchForCars(currentLocation) }

        launch {
            while (currentTime < totalTime) {
                delay(1.seconds)
                currentTime += 1
            }
        }

        launch {
            while (true) {
                viewModel.searchForCars(currentLocation)
                delay(5.seconds)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(
                color = YallaTheme.color.gray2,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .navigationBarsPadding()
            .onSizeChanged { with(density) { onAppear(it.height.toDp()) } }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(30.dp)
                )

                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.search_cars),
                color = YallaTheme.color.black,
                style = YallaTheme.font.title
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.it_takes_around_x_minute, uiState.timeout.or0()),
                color = YallaTheme.color.gray,
                style = YallaTheme.font.label
            )

            Spacer(modifier = Modifier.height(20.dp))

            YallaProgressBar(
                progress = progress / totalTime,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(YallaTheme.color.white)
        ) {
            SearchCarItem(
                text = stringResource(R.string.cancel_order),
                imageVector = Icons.Default.Close,
                onClick = onClickCancel
            )

            SearchCarItem(
                text = stringResource(R.string.order_details),
                imageVector = Icons.Outlined.Info,
                onClick = onClickDetails
            )
        }
    }
}