package uz.yalla.client.feature.android.places.address.view

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
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType
import uz.yalla.client.feature.android.places.R
import uz.yalla.client.feature.android.places.address.components.AddressFormField
import uz.yalla.client.feature.android.places.address.model.AddressUIState
import uz.yalla.client.feature.core.components.buttons.SelectCurrentLocationButton
import uz.yalla.client.feature.core.components.buttons.YButton
import uz.yalla.client.feature.core.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddressScreen(
    id: Int?,
    uiState: AddressUIState,
    snackbarHostState: SnackbarHostState,
    onIntent: (AddressIntent) -> Unit
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
                    onClick = { onIntent(AddressIntent.OpenSearchSheet) }
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

                YButton(
                    text = stringResource(R.string.save),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    enabled = uiState.selectedAddress != null,
                    onClick = { onIntent(AddressIntent.OnSave) }
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