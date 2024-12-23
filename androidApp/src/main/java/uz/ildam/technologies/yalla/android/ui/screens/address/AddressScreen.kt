package uz.ildam.technologies.yalla.android.ui.screens.address

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.SelectCurrentLocationButton
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.text_field.AddressFormField
import uz.ildam.technologies.yalla.android.ui.sheets.search_address.SearchByNameBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.select_from_map.SelectFromMapBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    id: Int?,
    uiState: AddressUIState,
    onIntent: (AddressIntent) -> Unit
) {
    var searchLocationVisibility by remember { mutableStateOf(false) }
    var openMapVisibility by remember { mutableStateOf(false) }
    val searchLocationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                title = {
                    Text(
                        text = uiState.addressType.typeName,
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge
                    )
                },
                actions = {
                    if (id != null) IconButton(onClick = { onIntent(AddressIntent.OnDelete(id)) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = YallaTheme.color.black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddressIntent.OnNavigateBack) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                SelectCurrentLocationButton(
                    text =
                    if (uiState.selectedAddress?.name != null) uiState.selectedAddress.name
                    else stringResource(R.string.enter_the_address),
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(YallaTheme.color.white)
                                .border(
                                    width = 4.dp,
                                    color = YallaTheme.color.primary,
                                    shape = CircleShape
                                )
                        )
                    },
                    onClick = { searchLocationVisibility = true }
                )

                AddressFormField(
                    value = uiState.addressName,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { if (it.length <= 100) onIntent(AddressIntent.OnChangeName(it)) },
                    placeHolder = stringResource(R.string.address_name)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AddressFormField(
                        value = uiState.apartment,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            if (it.length <= 5)
                                onIntent(AddressIntent.OnChangeApartment(it))
                        },
                        placeHolder = stringResource(R.string.apartment)
                    )
                    AddressFormField(
                        value = uiState.entrance,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            if (it.length <= 5)
                                onIntent(AddressIntent.OnChangeEntrance(it))
                        },
                        placeHolder = stringResource(R.string.entrance)
                    )
                    AddressFormField(
                        value = uiState.floor,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            if (it.length <= 5)
                                onIntent(AddressIntent.OnChangeFloor(it))
                        },
                        placeHolder = stringResource(R.string.floor)
                    )
                }

                AddressFormField(
                    value = uiState.comment,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        if (it.length <= 100)
                            onIntent(AddressIntent.OnChangeComment(it))
                    },
                    placeHolder = stringResource(R.string.comment)
                )

                Spacer(modifier = Modifier.weight(1f))

                YallaButton(
                    text = stringResource(R.string.save),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    enabled = uiState.selectedAddress != null,
                    onClick = { onIntent(AddressIntent.OnSave) }
                )
            }

            AnimatedVisibility(
                visible = searchLocationVisibility,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
            ) {
                if (searchLocationVisibility) SearchByNameBottomSheet(
                    sheetState = searchLocationState,
                    isForDestination = false,
                    onAddressSelected = { dest ->
                        searchLocationVisibility = false
                        onIntent(
                            AddressIntent.OnAddressSelected(
                                address = AddressUIState.Location(
                                    name = dest.name,
                                    lat = dest.lat,
                                    lng = dest.lng
                                )
                            )
                        )
                    },
                    onClickMap = { openMapVisibility = true },
                    onDismissRequest = { searchLocationVisibility = false }
                )
            }

            if (openMapVisibility) SelectFromMapBottomSheet(
                modifier = Modifier.padding(paddingValues),
                isForDestination = false,
                onSelectLocation = { name, location, _ ->
                    onIntent(
                        AddressIntent.OnAddressSelected(
                            address = AddressUIState.Location(
                                name = name,
                                lat = location.latitude,
                                lng = location.longitude
                            )
                        )
                    )
                },
                onDismissRequest = { openMapVisibility = false }
            )
        }
    )
}