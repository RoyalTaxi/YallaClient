package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.OptionsButton
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnTheRideBottomSheet(
    carNumber: String,
    carInfo: String,
    sheetState: SheetState,
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
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = stringResource(R.string.on_the_way),
                        style = YallaTheme.font.title,
                        color = YallaTheme.color.black
                    )

                    Text(
                        text = carNumber,
                        style = YallaTheme.font.labelSemiBold,
                        color = YallaTheme.color.black
                    )
                }

                Text(
                    text = carInfo,
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(20.dp)
            ) {

                OptionsButton(
                    modifier = Modifier.fillMaxHeight(),
                    painter = painterResource(R.drawable.ic_return),
                    onClick = {}
                )

                YallaButton(
                    text = stringResource(R.string.lets_go),
                    contentPadding = PaddingValues(vertical = 20.dp),
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                OptionsButton(
                    modifier = Modifier.fillMaxHeight(),
                    painter = painterResource(R.drawable.img_options),
                    tint = YallaTheme.color.black,
                    onClick = { }
                )
            }
        }
    }
}