package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.CallButton
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.item.CarNumberItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientWaitingBottomSheet(
    sheetState: SheetState,
    carInfo: String,
    code: String,
    number: List<String>,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.gray2,
        dragHandle = null,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.coming_to_you),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.black
                )

                Text(
                    text = carInfo,
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.gray
                )

                CarNumberItem(
                    code = code.slice(0..<2),
                    number = "(\\d+|[A-Za-z]+)"
                        .toRegex()
                        .findAll(number.toString()) // Need to be check
                        .map { it.value }
                        .toList()
                )
            }

            Column(
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(20.dp)
            ) {
                CallButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}