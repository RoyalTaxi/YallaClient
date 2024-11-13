package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.item.FoundAddressItem
import uz.ildam.technologies.yalla.android.ui.components.text_field.SearchLocationField
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByNameBottomSheet(
    sheetState: SheetState,
    foundAddresses: List<SearchForAddressItemModel>,
    onSearchForAddress: (String) -> Unit,
    onAddressSelected: (SearchForAddressItemModel) -> Unit,
    onDismissRequest: () -> Unit
) {
    var addressName by remember { mutableStateOf("") }

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.gray2,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
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
                value = addressName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                onValueChange = {
                    addressName = it
                    onSearchForAddress(it)
                },
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(.8f)
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                ),
            contentPadding = PaddingValues(20.dp)
        ) {
            items(foundAddresses) { foundAddress ->
                FoundAddressItem(
                    foundAddress = foundAddress,
                    onClick = {
                        onAddressSelected(it)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}