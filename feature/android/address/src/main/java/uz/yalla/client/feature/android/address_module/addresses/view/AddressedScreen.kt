package uz.yalla.client.feature.android.address_module.addresses.view

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
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType
import uz.yalla.client.feature.android.address.R
import uz.yalla.client.feature.android.address_module.addresses.model.AddressesUIState
import uz.yalla.client.feature.core.components.items.OrderOptionsItem
import uz.yalla.client.feature.core.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddressesScreen(
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
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddressesIntent.OnNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { paddingValue ->
            LazyColumn(modifier = Modifier.padding(paddingValue)) {
                stickyHeader {
                    Spacer(modifier = Modifier.height(40.dp))

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

                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
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
                        onClick = { onIntent(AddressesIntent.OnAddNewAddress(AddressType.HOME)) }
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
                        onClick = { onIntent(AddressesIntent.OnAddNewAddress(AddressType.WORK)) }
                    )
                }

                uiState.addresses?.let { addresses ->
                    items(
                        items = addresses,
                        key = { it.id }
                    ) { address ->
                        OrderOptionsItem(
                            title = address.name,
                            description = address.address,
                            leadingIcon = {
                                Icon(
                                    contentDescription = null,
                                    tint = YallaTheme.color.gray,
                                    painter = painterResource(
                                        when (address.type) {
                                            AddressType.HOME -> R.drawable.ic_home
                                            AddressType.WORK -> R.drawable.ic_work
                                            AddressType.OTHER -> R.drawable.ic_other
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
                            onClick = {
                                onIntent(
                                    AddressesIntent.OnClickAddress(
                                        address.id,
                                        address.type
                                    )
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = YallaTheme.color.black,
                onClick = { onIntent(AddressesIntent.OnAddNewAddress(AddressType.OTHER)) },
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
    )
}