package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmCancellationBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.gray2,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(YallaTheme.color.gray2)
                .navigationBarsPadding()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.cancel_order),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.black
                )

                Text(
                    text = stringResource(R.string.cancel_order_definition),
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                YallaButton(
                    text = stringResource(R.string.cancel),
                    onClick = onConfirm,
                    containerColor = YallaTheme.color.gray2,
                    contentColor = YallaTheme.color.gray,
                    contentPadding = PaddingValues(vertical = 20.dp),
                    modifier = Modifier.weight(1f)
                )

                YallaButton(
                    text = stringResource(R.string.wait),
                    onClick = onDismissRequest,
                    contentPadding = PaddingValues(vertical = 20.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}