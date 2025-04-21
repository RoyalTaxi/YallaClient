package uz.yalla.client.core.common.sheet.search_address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.field.SearchLocationField
import uz.yalla.client.core.common.item.FoundAddressItem
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByNameBottomSheet(
    initialAddress: String? = null,
    initialDestination: String? = null,
    sheetState: SheetState,
    onAddressSelected: (String, Double, Double, Int) -> Unit,
    onDestinationSelected: ((String, Double, Double, Int) -> Unit) = { _, _, _, _ -> },
    onDismissRequest: () -> Unit,
    onClickMap: (forDestination: Boolean) -> Unit,
    isForDestination: Boolean,
    deleteDestination: (String) -> Unit,
    viewModel: SearchByNameBottomSheetViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val addressFocusRequester = remember { FocusRequester() }
    val destinationFocusRequester = remember { FocusRequester() }
    var lastFocusedForDestination by remember { mutableStateOf(isForDestination) }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            initialAddress?.let { viewModel.setInitialQuery(it) }
            initialDestination?.let { viewModel.setInitialDestinationQuery(it) }

            viewModel.fetchPolygons()
        }

        launch(Dispatchers.Main) {
            getCurrentLocation(context) { location ->
                viewModel.setCurrentLocation(location.latitude, location.longitude)
                viewModel.getSecondaryAddresses()
            }
        }
    }

    LaunchedEffect(isForDestination) {
        launch(Dispatchers.Main) {
            if (isForDestination) {
                destinationFocusRequester.requestFocus()
            } else {
                addressFocusRequester.requestFocus()
            }
        }
    }

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.gray2,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = {
            viewModel.resetSearchState()
            onDismissRequest()
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(30.dp)
                )
                .imePadding()
                .padding(20.dp)
        ) {
            val (focusedField, setFocusedField) = remember {
                mutableStateOf<String?>(if (isForDestination) "destination" else "current")
            }

            SearchLocationField(
                value = uiState.query,
                isForDestination = false,
                isFocused = focusedField == "current",
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(addressFocusRequester),
                onClickMap = {
                    onClickMap(false)
                    onDismissRequest()
                },
                onValueChange = {
                    viewModel.setQuery(it)
                    lastFocusedForDestination = false
                },
                clearDestination = {},
                onFocusChange = { isFocused ->
                    if (isFocused) setFocusedField("current")
                }
            )

            SearchLocationField(
                value = uiState.destinationQuery,
                isForDestination = true,
                isFocused = focusedField == "destination",
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(destinationFocusRequester),
                onClickMap = {
                    onClickMap(true)
                    onDismissRequest()
                },
                onValueChange = {
                    viewModel.setDestinationQuery(it)
                    lastFocusedForDestination = true
                },
                clearDestination = { deleteDestination(uiState.destinationQuery) },
                onFocusChange = { isFocused ->
                    if (isFocused) setFocusedField("destination")
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(.8f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(YallaTheme.color.white)
            ) {
                // Show search results if we have any
                if (uiState.foundAddresses.isNotEmpty()) {
                    items(uiState.foundAddresses) { foundAddress ->
                        FoundAddressItem(
                            foundAddress = foundAddress,
                            onClick = {
                                if (lastFocusedForDestination)
                                    onDestinationSelected(
                                        it.name,
                                        it.lat,
                                        it.lng,
                                        it.addressId.or0()
                                    )
                                else
                                    onAddressSelected(it.name, it.lat, it.lng, it.addressId.or0())
                                viewModel.resetSearchState()
                                onDismissRequest()
                            }
                        )
                    }
                }
                // Show recommended addresses when search is empty and we have a focused field
                else if ((lastFocusedForDestination && uiState.destinationQuery.isBlank()) ||
                    (!lastFocusedForDestination && uiState.query.isBlank())
                ) {

                    items(uiState.recommendedAddresses) { recommendedAddress ->
                        FoundAddressItem(
                            foundAddress = recommendedAddress,
                            onClick = {
                                if (lastFocusedForDestination)
                                    onDestinationSelected(
                                        it.name,
                                        it.lat,
                                        it.lng,
                                        it.addressId.or0()
                                    )
                                else
                                    onAddressSelected(it.name, it.lat, it.lng, it.addressId.or0())
                                viewModel.resetSearchState()
                                onDismissRequest()
                            }
                        )
                    }
                }
            }

            if (uiState.loading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .imePadding(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingDialog()
                }
            }
        }
    }
}