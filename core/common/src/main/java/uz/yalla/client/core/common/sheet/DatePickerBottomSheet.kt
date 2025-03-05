package uz.yalla.client.core.common.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.date_picker.WheelDatePicker
import uz.yalla.client.core.common.date_picker.WheelPickerDefaults
import uz.yalla.client.core.common.formation.formatWithDashesDMY
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun DatePickerBottomSheet(
    startDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var snappedDate by remember { mutableStateOf(startDate) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.navigationBarsPadding()
    ) {
        Card(
            colors = CardDefaults.cardColors(YallaTheme.color.white),
            shape = RoundedCornerShape(30.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.choose_date),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.title
                )

                Text(
                    text = snappedDate.formatWithDashesDMY(),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.label
                )
            }
        }

        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(YallaTheme.color.white)
        ) {
            WheelDatePicker(
                rowCount = 5,
                textColor = YallaTheme.color.black,
                textStyle = YallaTheme.font.labelSemiBold,
                onSnappedDate = { snappedDate = it },
                maxDate = LocalDate.now(),
                size = DpSize(width = 192.dp, height = 170.dp),
                yearsRange = IntRange(1900, 2024),
                startDate = snappedDate,
                selectorProperties = WheelPickerDefaults.selectorProperties(false),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YallaTheme.color.white)
            )
        }

        Card(
            colors = CardDefaults.cardColors(YallaTheme.color.white),
            shape = RoundedCornerShape(30.dp)
        ) {
            PrimaryButton(
                text = stringResource(id = R.string.choose_date),
                onClick = {
                    onSelectDate(snappedDate)
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
        }
    }
}