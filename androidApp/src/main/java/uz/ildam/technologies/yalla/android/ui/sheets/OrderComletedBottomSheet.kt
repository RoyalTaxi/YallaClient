package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.item.RatingStars

@Composable
fun OrderCompletedBottomSheet(
    carNumber: String,
    carInfo: String,
    price: String,
    paymentType: String
) {

    val userRating = remember { mutableStateOf(3.0f) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(
                color = YallaTheme.color.gray2,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
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

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                text = price + " " + stringResource(R.string.sum),
                style = YallaTheme.font.title,
                color = YallaTheme.color.black
            )

            Text(
                text = paymentType,
                style = YallaTheme.font.label,
                color = YallaTheme.color.gray
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(20.dp)
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.rate_the_trip),
                style = YallaTheme.font.title,
                color = YallaTheme.color.black
            )

            Spacer(modifier = Modifier.height(20.dp))

            RatingStars(
                currentRating = userRating.value,
                onRatingChange = { newRating ->
                    userRating.value = newRating
                },
                maxRating = 5
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            YallaButton(
                text = stringResource(R.string.new_order),
                onClick = { },
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            )
        }
    }
}
