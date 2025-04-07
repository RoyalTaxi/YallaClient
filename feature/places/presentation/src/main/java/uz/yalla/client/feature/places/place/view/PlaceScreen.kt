package uz.yalla.client.feature.places.place.view

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
import uz.yalla.client.core.common.button.SelectCurrentLocationButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.places.place.components.AddressFormField
import uz.yalla.client.feature.places.place.model.PlaceUIState
import uz.yalla.client.feature.places.presentation.R
import uz.yalla.client.core.domain.model.type.PlaceType

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
            AddressTopBar(
                placeType = uiState.placeType,
                id = id,
                onNavigateBack = { onIntent(PlaceIntent.OnNavigateBack) },
                onDelete = { onIntent(PlaceIntent.OnDelete(id!!)) }
            )
        },
        content = { paddingValues ->
            AddressContent(
                paddingValues = paddingValues,
                uiState = uiState,
                onIntent = onIntent
            )
        },
        snackbarHost = {
            AddressSnackbarHost(snackbarHostState = snackbarHostState)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressTopBar(
    placeType: PlaceType,
    id: Int?,
    onNavigateBack: () -> Unit,
    onDelete: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        title = {
            Text(
                text = stringResource(
                    when (placeType) {
                        PlaceType.HOME -> R.string.home
                        PlaceType.WORK -> R.string.work
                        PlaceType.OTHER -> R.string.other
                    }
                ),
                color = YallaTheme.color.black,
                style = YallaTheme.font.labelLarge
            )
        },
        actions = {
            if (id != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = YallaTheme.color.black
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun AddressContent(
    paddingValues: PaddingValues,
    uiState: PlaceUIState,
    onIntent: (PlaceIntent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp)
    ) {
        SelectCurrentLocationButton(
            text = uiState.selectedAddress?.name ?: stringResource(R.string.enter_the_address),
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

        ApartmentDetailsRow(
            apartment = uiState.apartment,
            entrance = uiState.entrance,
            floor = uiState.floor,
            onChangeApartment = { if (it.length <= 5) onIntent(PlaceIntent.OnChangeApartment(it)) },
            onChangeEntrance = { if (it.length <= 5) onIntent(PlaceIntent.OnChangeEntrance(it)) },
            onChangeFloor = { if (it.length <= 5) onIntent(PlaceIntent.OnChangeFloor(it)) }
        )

        AddressFormField(
            value = uiState.comment,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { if (it.length <= 100) onIntent(PlaceIntent.OnChangeComment(it)) },
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
}

@Composable
private fun ApartmentDetailsRow(
    apartment: String,
    entrance: String,
    floor: String,
    onChangeApartment: (String) -> Unit,
    onChangeEntrance: (String) -> Unit,
    onChangeFloor: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AddressFormField(
            value = apartment,
            modifier = Modifier.weight(1f),
            onValueChange = onChangeApartment,
            placeHolder = stringResource(R.string.apartment)
        )

        AddressFormField(
            value = entrance,
            modifier = Modifier.weight(1f),
            onValueChange = onChangeEntrance,
            placeHolder = stringResource(R.string.entrance)
        )

        AddressFormField(
            value = floor,
            modifier = Modifier.weight(1f),
            onValueChange = onChangeFloor,
            placeHolder = stringResource(R.string.floor)
        )
    }
}

@Composable
private fun AddressSnackbarHost(
    snackbarHostState: SnackbarHostState
) {
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