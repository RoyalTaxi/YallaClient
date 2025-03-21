package uz.yalla.client.feature.places.places.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.item.OrderOptionsItem
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.PlaceModel
import uz.yalla.client.feature.places.places.model.AddressesUIState
import uz.yalla.client.feature.places.presentation.R
import uz.yalla.client.feature.order.domain.model.type.PlaceType

@Composable
internal fun AddressesScreen(
    uiState: AddressesUIState,
    onIntent: (AddressesIntent) -> Unit
) {
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        containerColor = YallaTheme.color.white,
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.white)
            .imePadding()
            .navigationBarsPadding(),
        topBar = { AddressesTopBar { onIntent(AddressesIntent.OnNavigateBack) } },
        content = { paddingValue ->
            AddressesContent(
                modifier = Modifier.padding(paddingValue),
                addresses = uiState.addresses,
                onAddNewAddress = { type -> onIntent(AddressesIntent.OnAddNewAddress(type)) },
                onClickAddress = { id, type -> onIntent(AddressesIntent.OnClickAddress(id, type)) }
            )
        },
        floatingActionButton = {
            AddAddressButton { onIntent(AddressesIntent.OnAddNewAddress(PlaceType.OTHER)) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressesTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        title = {},
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AddressesContent(
    modifier: Modifier,
    addresses: List<PlaceModel>?,
    onAddNewAddress: (PlaceType) -> Unit,
    onClickAddress: (Int, PlaceType) -> Unit
) {
    LazyColumn(modifier = modifier) {
        stickyHeader {
            Spacer(modifier = Modifier.height(40.dp))

            AddressesHeader()

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            AddDefaultAddressItems(
                onAddHomeAddress = { onAddNewAddress(PlaceType.HOME) },
                onAddWorkAddress = { onAddNewAddress(PlaceType.WORK) }
            )
        }

        addresses?.let { addressList ->
            items(
                items = addressList,
                key = { it.id }
            ) { address ->
                AddressItem(
                    address = address,
                    onClick = { onClickAddress(address.id, address.type) }
                )
            }
        }
    }
}

@Composable
private fun AddressesHeader() {
    Text(
        text = stringResource(R.string.my_places),
        color = YallaTheme.color.black,
        style = YallaTheme.font.headline,
        modifier = Modifier.padding(start = 20.dp, end = 60.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.my_places_desc),
        color = YallaTheme.color.black,
        style = YallaTheme.font.body,
        modifier = Modifier.padding(start = 20.dp, end = 60.dp)
    )
}

@Composable
private fun AddDefaultAddressItems(
    onAddHomeAddress: () -> Unit,
    onAddWorkAddress: () -> Unit
) {
    OrderOptionsItem(
        title = stringResource(R.string.add_home_address),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_home),
                contentDescription = null,
                tint = YallaTheme.color.gray
            )
            Spacer(modifier = Modifier.width(16.dp))
        },
        trailingIcon = {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                tint = YallaTheme.color.gray
            )
        },
        onClick = onAddHomeAddress
    )

    OrderOptionsItem(
        title = stringResource(R.string.add_work_address),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_work),
                contentDescription = null,
                tint = YallaTheme.color.gray
            )
            Spacer(modifier = Modifier.width(16.dp))
        },
        trailingIcon = {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                tint = YallaTheme.color.gray
            )
        },
        onClick = onAddWorkAddress
    )
}

@Composable
private fun AddressItem(
    address: PlaceModel,
    onClick: () -> Unit
) {
    OrderOptionsItem(
        title = address.name,
        description = address.address,
        leadingIcon = {
            Icon(
                contentDescription = null,
                tint = YallaTheme.color.gray,
                painter = painterResource(
                    when (address.type) {
                        PlaceType.HOME -> R.drawable.ic_home
                        PlaceType.WORK -> R.drawable.ic_work
                        PlaceType.OTHER -> R.drawable.ic_other
                    }
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
        },
        trailingIcon = {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = YallaTheme.color.gray
            )
        },
        onClick = onClick
    )
}

@Composable
private fun AddAddressButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        shape = CircleShape,
        containerColor = YallaTheme.color.black,
        onClick = onClick,
        modifier = Modifier.padding(20.dp),
        content = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = YallaTheme.color.white
            )
        }
    )
}