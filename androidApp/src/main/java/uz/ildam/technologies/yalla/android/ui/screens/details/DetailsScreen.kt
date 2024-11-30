package uz.ildam.technologies.yalla.android.ui.screens.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import io.morfly.compose.bottomsheet.material3.requireSheetVisibleHeightDp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.sheets.OrderDetailsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue
import uz.ildam.technologies.yalla.android.utils.vectorToBitmapDescriptor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailsScreen(
    uiState: DetailsUIState,
    loading: Boolean,
    cameraPositionState: CameraPositionState,
    onIntent: (DetailsIntent) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        defineValues = {
            SheetValue.Collapsed at offset(50)
            SheetValue.PartiallyExpanded at offset(50)
            SheetValue.Expanded at contentHeight
        }
    )
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState = sheetState)
    val bottomPadding by remember {
        derivedStateOf { scaffoldState.sheetState.requireSheetVisibleHeightDp() }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RectangleShape,
        sheetContainerColor = YallaTheme.color.white,
        sheetDragHandle = null,
        modifier = Modifier.fillMaxSize(),
        containerColor = YallaTheme.color.white,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                navigationIcon = {
                    IconButton(onClick = { onIntent(DetailsIntent.NavigateBack) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.order_details),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge
                    )
                }
            )
        },
        content = { paddingValues ->
            if (loading.not()) GoogleMap(
                properties = uiState.properties,
                uiSettings = uiState.mapUiSettings,
                cameraPositionState = cameraPositionState,
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(bottom = bottomPadding),
                content = {
                    Polyline(points = uiState.routes.map { LatLng(it.lat, it.lng) })

                    if (uiState.orderDetails?.taxi?.routes?.isNotEmpty() == true) {
                        Marker(
                            state = remember {
                                MarkerState(
                                    position = LatLng(
                                        uiState.orderDetails.taxi.routes.first().cords.lat,
                                        uiState.orderDetails.taxi.routes.first().cords.lng,
                                    )
                                )
                            },
                            icon = remember {
                                vectorToBitmapDescriptor(context, R.drawable.ic_origin_marker)
                                    ?: BitmapDescriptorFactory.defaultMarker()
                            }
                        )
                    }

                    if ((uiState.orderDetails?.taxi?.routes?.size ?: 0) > 1) {
                        Marker(
                            state = remember {
                                MarkerState(
                                    position = LatLng(
                                        uiState.orderDetails?.taxi?.routes?.last()?.cords?.lat
                                            ?: 0.0,
                                        uiState.orderDetails?.taxi?.routes?.last()?.cords?.lng
                                            ?: 0.0,
                                    )
                                )
                            },
                            icon = remember {
                                vectorToBitmapDescriptor(context, R.drawable.ic_destination_marker)
                                    ?: BitmapDescriptorFactory.defaultMarker()
                            }
                        )
                    }
                }
            )
        },
        sheetContent = {
            uiState.orderDetails?.let { OrderDetailsBottomSheet(order = it) }
        }
    )
}