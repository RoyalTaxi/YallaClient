package uz.yalla.client.feature.order.presentation.no_service.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.sheet.AddDestinationBottomSheet
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapView
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.no_service.NO_SERVICE_ROUTE
import uz.yalla.client.feature.order.presentation.no_service.model.NoServiceViewModel

object NoServiceSheet {
    private val viewModel: NoServiceViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<NoServiceIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun View() {
        val density = LocalDensity.current
        val state by viewModel.uiState.collectAsState()
        val addDestinationSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        BackHandler { }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.gray2,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .onSizeChanged {
                        with(density) {
                            SheetCoordinator.updateSheetHeight(
                                route = NO_SERVICE_ROUTE,
                                height = it.height.toDp()
                            )
                        }
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
                    Text(
                        text = stringResource(R.string.no_service_massage),
                        style = YallaTheme.font.title,
                        color = YallaTheme.color.black
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = YallaTheme.color.white,
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )
                        .navigationBarsPadding()
                ) {
                    PrimaryButton(
                        text = stringResource(R.string.new_address),
                        onClick = { viewModel.setSelectAddressVisibility(true) },
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        if (state.setLocationSheetVisibility == true) {
            AddDestinationBottomSheet(
                sheetState = addDestinationSheetState,
                onClickMap = {
                    viewModel.setSelectFromMapVisibility(SelectFromMapViewValue.FOR_START)
                },
                onDismissRequest = {
                    viewModel.setSelectAddressVisibility(false)
                },
                onAddressSelected = {
                    viewModel.onIntent(NoServiceIntent.SetSelectedLocation(it))
                }
            )
        }

        if (state.selectFromMapVisibility != SelectFromMapViewValue.INVISIBLE) {
            SelectFromMapView(
                viewValue = SelectFromMapViewValue.FOR_START,
                startingPoint = null,
                onSelectLocation = {
                    viewModel.onIntent(NoServiceIntent.SetSelectedLocation(it))
                },
                onDismissRequest = {
                    viewModel.setSelectFromMapVisibility(SelectFromMapViewValue.INVISIBLE)
                }
            )
        }
    }
}