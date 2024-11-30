package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.item.OptionsItem
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetOrderOptionsBottomSheet(
    sheetState: SheetState,
    selectedTariff: GetTariffsModel.Tariff,
    options: List<GetTariffsModel.Tariff.Service>,
    selectedOptions: List<GetTariffsModel.Tariff.Service>,
    onSave: (List<GetTariffsModel.Tariff.Service>) -> Unit,
    onDismissRequest: () -> Unit
) {
    val newSelectedOptions = remember { mutableStateListOf(*selectedOptions.toTypedArray()) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        containerColor = YallaTheme.color.gray2,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = selectedTariff.name,
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.title
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (selectedTariff.fixedType) stringResource(
                        R.string.fixed_cost,
                        selectedTariff.cost
                    ) else stringResource(R.string.starting_cost, selectedTariff.fixedPrice),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.label
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (options.isNotEmpty()) LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(30.dp)
                ),
                contentPadding = PaddingValues(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
            ) {
                items(options.size) { optionIndex ->
                    val option = options[optionIndex]
                    OptionsItem(
                        isSelected = option in newSelectedOptions,
                        option = option,
                        onChecked = { isSelected ->
                            if (isSelected) {
                                if (option !in newSelectedOptions) {
                                    newSelectedOptions.add(option)
                                }
                            } else {
                                newSelectedOptions.remove(option)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(20.dp)
            ) {
                YallaButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.save),
                    onClick = {
                        onSave(newSelectedOptions)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}