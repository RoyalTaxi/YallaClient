package uz.yalla.client.feature.android.places.place.view

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.android.places.place.components.AddressFormField
import uz.yalla.client.feature.android.places.place.model.PlaceUIState
import uz.yalla.client.feature.android.places.presentation.R
import uz.yalla.client.feature.core.components.buttons.SelectCurrentLocationButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddressScreen(
    id: Int?,
    uiState: PlaceUIState,
    snackbarHostState: SnackbarHostState,
    onIntent: (PlaceIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                title = {
                    Text(
                        text = stringResource(
                            when (uiState.addressType) {
                                AddressType.HOME -> R.string.home
                                AddressType.WORK -> R.string.work
                                AddressType.OTHER -> R.string.other
                            }
                        ),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge
                    )
                },
                actions = {
                    if (id != null) IconButton(onClick = { onIntent(PlaceIntent.OnDelete(id)) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = YallaTheme.color.black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(PlaceIntent.OnNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
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
                    onClick = { onIntent(PlaceIntent.OpenSearchSheet) }
                )

                AddressFormField(
                    value = uiState.addressName,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { if (it.length <= 100) onIntent(PlaceIntent.OnChangeName(it)) },
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
                                onIntent(PlaceIntent.OnChangeApartment(it))
                        },
                        placeHolder = stringResource(R.string.apartment)
                    )

                    AddressFormField(
                        value = uiState.entrance,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            if (it.length <= 5)
                                onIntent(PlaceIntent.OnChangeEntrance(it))
                        },
                        placeHolder = stringResource(R.string.entrance)
                    )

                    AddressFormField(
                        value = uiState.floor,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            if (it.length <= 5)
                                onIntent(PlaceIntent.OnChangeFloor(it))
                        },
                        placeHolder = stringResource(R.string.floor)
                    )
                }

                AddressFormField(
                    value = uiState.comment,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        if (it.length <= 100)
                            onIntent(PlaceIntent.OnChangeComment(it))
                    },
                    placeHolder = stringResource(R.string.comment)
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = stringResource(R.string.save),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    enabled = uiState.selectedAddress != null,
                    onClick = { onIntent(PlaceIntent.OnSave) }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.imePadding(),
                snackbar = { snackbarData: SnackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = YallaTheme.color.red,
                        contentColor = YallaTheme.color.white
                    )
                }
            )
        }
    )
}