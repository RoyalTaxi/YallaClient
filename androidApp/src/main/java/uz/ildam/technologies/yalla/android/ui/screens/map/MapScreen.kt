package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.BottomSheetState
import io.morfly.compose.bottomsheet.material3.requireSheetVisibleHeightDp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.button.MapButton
import uz.ildam.technologies.yalla.android.ui.components.marker.YallaMarker
import uz.ildam.technologies.yalla.android.ui.sheets.OrderTaxiBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapScreen(
    uiState: MapUIState,
    isLoading: Boolean,
    scaffoldState: BottomSheetScaffoldState<SheetValue>,
    sheetState: BottomSheetState<SheetValue>,
    markerState: MarkerState,
    cameraPositionState: CameraPositionState,
    onIntent: (MapIntent) -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetContainerColor = Color.Black,
        sheetShape = RectangleShape,
        sheetContent = {
            OrderTaxiBottomSheet(
                isLoading = isLoading,
                selectedTariff = uiState.selectedTariff,
                tariffs = uiState.tariffs?.tariff.orEmpty(),
                onSelectTariff = { selectedTariff, wasSelected ->
                    onIntent(MapIntent.SelectTariff(selectedTariff, wasSelected))
                }
            )
        },
        content = {
            val bottomPadding by remember {
                derivedStateOf { sheetState.requireSheetVisibleHeightDp() }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = remember(bottomPadding) { bottomPadding })
            ) {
                GoogleMap(
                    properties = uiState.properties,
                    uiSettings = uiState.mapUiSettings,
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize(),
                    content = { Marker(state = markerState, alpha = .0f) }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    YallaMarker(
                        time = uiState.timeout.toString(),
                        isLoading = isLoading,
                        selectedAddressName = uiState.selectedAddressName,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    MapButton(
                        painter = painterResource(R.drawable.ic_location),
                        modifier = Modifier.align(Alignment.BottomEnd),
                        onClick = { onIntent(MapIntent.MoveToMyLocation) }
                    )
                }
            }
        }
    )
}
