package uz.yalla.client.feature.core.sheets

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.yalla.client.feature.core.components.items.FoundAddressItem
import uz.yalla.client.feature.core.components.text_field.SearchLocationField
import uz.yalla.client.feature.core.design.theme.YallaTheme
import uz.yalla.client.feature.core.sheets.search_address.SearchByNameBottomSheetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDestinationBottomSheet(
    onClickMap: () -> Unit,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onAddressSelected: (String, Double, Double, Int) -> Unit,
    viewModel: SearchByNameBottomSheetViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        launch { viewModel.findAllMapAddresses() }
        launch { viewModel.setQuery("") }
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.gray2,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = {
            viewModel.setQuery("")
            viewModel.setFoundAddresses(emptyList())
            onDismissRequest()
        }
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = YallaTheme.color.white,
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
                .background(YallaTheme.color.white)
        ) {
            items(uiState.foundAddresses) { foundAddress ->
                FoundAddressItem(
                    foundAddress = foundAddress,
                    onClick = {
                        onAddressSelected(it.name, it.lat, it.lng, it.addressId.or0())
                        viewModel.setQuery("")
                        viewModel.setFoundAddresses(emptyList())
                        onDismissRequest()
                    }
                )
            }
        }
    }
}