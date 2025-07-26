package uz.yalla.client.core.common.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.field.SearchLocationField
import uz.yalla.client.core.common.item.AddressNotFound
import uz.yalla.client.core.common.item.FoundAddressItem
import uz.yalla.client.core.common.item.FoundAddressShimmer
import uz.yalla.client.core.common.sheet.search_address.SingleAddressViewModel
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDestinationBottomSheet(
    onClickMap: () -> Unit,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    nearbyAddress: Destination? = null,
    onAddressSelected: (Location) -> Unit,
    viewModel: SingleAddressViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    DisposableEffect(Unit) {
        scope.launch(Dispatchers.Main) {
            coroutineScope {
                nearbyAddress?.point?.let { point ->
                    viewModel.setCurrentLocation(point.lat, point.lng)
                } ?: run {
                    getCurrentLocation(context) { location ->
                        viewModel.setCurrentLocation(location.latitude, location.longitude)
                    }
                }
            }

            withContext(Dispatchers.IO) { viewModel.getSecondaryAddresses() }

            focusRequester.requestFocus()
        }

        onDispose {
            viewModel.resetSearchState()
        }
    }

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.surface,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = {
            viewModel.resetSearchState()
            onDismissRequest()
        }
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = YallaTheme.color.background,
                    shape = RoundedCornerShape(30.dp)
                )
                .imePadding()
        ) {
            SearchLocationField(
                value = uiState.query,
                isForDestination = true,
                isFocused = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .padding(20.dp),
                onClickMap = {
                    onClickMap()
                    onDismissRequest()
                },
                onValueChange = viewModel::setQuery,
                clearDestination = {},
                onFocusChange = {}
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.8f)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(YallaTheme.color.background)
        ) {
            when {
                uiState.loading -> {
                    items(3) {
                        FoundAddressShimmer()
                    }
                }

                uiState.foundAddresses.isNotEmpty() -> {
                    items(uiState.foundAddresses) { foundAddress ->
                        FoundAddressItem(
                            foundAddress = foundAddress,
                            onClick = {
                                onAddressSelected(
                                    Location(
                                        name = it.name,
                                        point = MapPoint(it.lat, it.lng),
                                        addressId = it.addressId.or0()
                                    )
                                )
                                onDismissRequest()
                            }
                        )
                    }
                }

                uiState.query.isBlank() && uiState.recommendedAddresses.isNotEmpty() -> {
                    items(uiState.recommendedAddresses) { recommendedAddress ->
                        FoundAddressItem(
                            foundAddress = recommendedAddress,
                            onClick = {
                                onAddressSelected(
                                    Location(
                                        name = it.name,
                                        point = MapPoint(it.lat, it.lng),
                                        addressId = it.addressId.or0()
                                    )
                                )
                                onDismissRequest()
                            }
                        )
                    }
                }

                else -> {
                    item {
                        AddressNotFound()
                    }
                }
            }
        }
    }
}