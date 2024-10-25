package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.BottomSheetState
import io.morfly.compose.bottomsheet.material3.requireSheetVisibleHeightDp
import uz.ildam.technologies.yalla.android.ui.components.marker.YallaMarker
import uz.ildam.technologies.yalla.android.ui.sheets.OrderTaxiBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapScreen(
    scaffoldState: BottomSheetScaffoldState<SheetValue>,
    sheetState: BottomSheetState<SheetValue>,
    mapState: MapState,
    markerState: MarkerState,
    cameraPositionState: CameraPositionState
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetContainerColor = Color.Black,
        sheetShape = RoundedCornerShape(
            topStart = 30.dp,
            topEnd = 30.dp
        ),
        sheetContent = {
            OrderTaxiBottomSheet(
                "Сайлгох 123",
                "Домой",
                {},
                {}
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
                    properties = mapState.properties,
                    uiSettings = mapState.mapUiSettings,
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize()
                ) { Marker(state = markerState, alpha = .0f) }

                YallaMarker(
                    time = "5",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    )
}
