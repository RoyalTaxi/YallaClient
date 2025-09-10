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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.sheet.AddDestinationBottomSheet
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapView
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.no_service.NO_SERVICE_ROUTE
import uz.yalla.client.feature.order.presentation.no_service.model.NoServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoServiceSheet(
    viewModel: NoServiceViewModel = koinViewModel()
) {
    val density = LocalDensity.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val addDestinationSheetState = rememberModalBottomSheetState(true)
    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler { }

    LaunchedEffect(Unit) {
        NoServiceSheetChannel.register(lifecycleOwner)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.surface,
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
                viewModel.onIntent(NoServiceSheetIntent.SetSelectedLocation(it))
            }
        )
    }

    if (state.selectFromMapVisibility != SelectFromMapViewValue.INVISIBLE) {
        SelectFromMapView(
            viewValue = SelectFromMapViewValue.FOR_START,
            startingPoint = null,
            onSelectLocation = {
                viewModel.onIntent(NoServiceSheetIntent.SetSelectedLocation(it))
            },
            onDismissRequest = {
                viewModel.setSelectFromMapVisibility(SelectFromMapViewValue.INVISIBLE)
            }
        )
    }
}