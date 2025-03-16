package uz.yalla.client.feature.order.presentation.search.view

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.progress.YallaProgressBar
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.SearchCarItem
import uz.yalla.client.feature.order.presentation.search.model.SearchCarSheetViewModel
import kotlin.time.Duration.Companion.seconds


object SearchCarSheet {
    private val viewModel: SearchCarSheetViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<SearchCarSheetIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @Composable
    fun View(
        point: MapPoint,
        tariffId: Int
    ) {
        val density = LocalDensity.current
        val state by viewModel.uiState.collectAsState()
        var currentTime by rememberSaveable { mutableIntStateOf(0) }
        val progress by animateFloatAsState(
            targetValue = currentTime.toFloat(),
            animationSpec = tween(durationMillis = 1000),
            label = "progress_anim"
        )

        LaunchedEffect(Unit) {
            launch {
                viewModel.setPoint(point)
                viewModel.setTariffId(tariffId)
            }

            launch {
                while (currentTime > state.setting?.orderCancelTime.or0()) {
                    delay(1.seconds)
                    currentTime += 1
                }
            }

            launch {
                while (true) {
                    viewModel.searchCar()
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
                .onSizeChanged {
                    with(density) {
                        viewModel.onIntent(SearchCarSheetIntent.SetSheetHeight(it.height.toDp()))
                    }
                }
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
                    text = stringResource(
                        R.string.it_takes_around_x_minute,
                        state.cars?.timeout.or0()
                    ),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.label
                )

                Spacer(modifier = Modifier.height(20.dp))

                YallaProgressBar(
                    progress = progress / (state.setting?.orderCancelTime ?: 1),
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
                    onClick = { viewModel.setCancelBottomSheetVisibility(true) }
                )

                SearchCarItem(
                    text = stringResource(R.string.order_details),
                    imageVector = Icons.Outlined.Info,
                    onClick = { viewModel.setDetailsBottomSheetVisibility(true) }
                )
            }
        }
    }
}