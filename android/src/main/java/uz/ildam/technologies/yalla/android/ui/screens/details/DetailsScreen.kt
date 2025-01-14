package uz.ildam.technologies.yalla.android.ui.screens.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.sheets.OrderDetailsBottomSheet
import uz.ildam.technologies.yalla.android.utils.vectorToBitmapDescriptor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    uiState: DetailsUIState,
    loading: Boolean,
    cameraPositionState: CameraPositionState,
    onIntent: (DetailsIntent) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = YallaTheme.color.gray2,
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
            if (loading.not()) Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
            ) {
                GoogleMap(
                    properties = uiState.properties,
                    uiSettings = uiState.mapUiSettings,
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.height(300.dp),
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
                                    vectorToBitmapDescriptor(
                                        context,
                                        R.drawable.ic_destination_marker
                                    )
                                        ?: BitmapDescriptorFactory.defaultMarker()
                                }
                            )
                        }
                    }
                )

                uiState.orderDetails?.let { OrderDetailsBottomSheet(order = it) }
            }
        }
    )
}