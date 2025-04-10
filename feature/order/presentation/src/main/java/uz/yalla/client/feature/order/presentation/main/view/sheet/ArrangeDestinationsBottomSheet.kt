package uz.yalla.client.feature.order.presentation.main.view.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.utils.dragContainer
import uz.yalla.client.core.common.utils.draggableItems
import uz.yalla.client.core.common.utils.rememberDragDropState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.items.ArrangeDestinationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArrangeDestinationsBottomSheet(
    sheetState: SheetState,
    destinations: List<Destination>,
    onAddNewDestinationClick: () -> Unit,
    onDismissRequest: (List<Destination>) -> Unit
) {
    var orderedDestinations by remember { mutableStateOf(destinations) }
    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(
        lazyListState = listState,
        draggableItemsNum = destinations.size,
        onMove = { fromIndex, toIndex ->
            orderedDestinations = orderedDestinations.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
        }
    )

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = YallaTheme.color.gray2,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        onDismissRequest = { onDismissRequest(orderedDestinations) }
    ) {
        if (destinations.isNotEmpty()) LazyColumn(
            state = listState,
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .dragContainer(dragDropState)
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(30.dp)
                ),
        ) {
            draggableItems(
                items = orderedDestinations,
                dragDropState = dragDropState
            ) { modifier, item ->
                ArrangeDestinationItem(
                    modifier = modifier.fillMaxWidth(),
                    destination = item,
                    onDelete = {
                        val mutableOrderedDestinations = orderedDestinations.toMutableList()
                        mutableOrderedDestinations.remove(item)
                        orderedDestinations = mutableOrderedDestinations.toMutableList()

                        if (mutableOrderedDestinations.isEmpty()) onDismissRequest(emptyList())
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier.background(
                color = YallaTheme.color.white,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
        ) {
            PrimaryButton(
                text = stringResource(R.string.add),
                onClick = {
                    onDismissRequest(orderedDestinations)
                    onAddNewDestinationClick()
                },
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            )
        }
    }
}