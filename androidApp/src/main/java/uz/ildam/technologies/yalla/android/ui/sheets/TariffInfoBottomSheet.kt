package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TariffInfoBottomSheet(
    sheetState: SheetState,
    tariff: GetTariffsModel.Tariff,
    arrivingTime: Int,
    onDismissRequest: () -> Unit
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
                verticalArrangement = Arrangement.Top,
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
                ) {
                    AsyncImage(
                        model = tariff.photo,
                        contentDescription = null,
                        modifier = Modifier.width(200.dp),
                        placeholder = painterResource(R.drawable.img_default_car),
                        error = painterResource(R.drawable.img_default_car)
                    )

                    Image(
                        modifier = Modifier.size(width = 60.dp, height = 60.dp),
                        painter = painterResource(R.drawable.img_flash),
                        contentDescription = null,
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = tariff.name,
                            style = YallaTheme.font.title,
                            color = YallaTheme.color.black
                        )

                        Text(
                            text = stringResource(id = R.string.starting_cost, tariff.cost),
                            style = YallaTheme.font.label,
                            color = YallaTheme.color.gray
                        )
                    }

                    Text(
                        text = stringResource(R.string.minute, arrivingTime.toString()),
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.gray,
                        textAlign = TextAlign.End
                    )
                }
            }

            if (tariff.description.isNotEmpty()) Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.tariff_title),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.black
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = tariff.description,
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.label
                )
            }

            Card(
                colors = CardDefaults.cardColors(YallaTheme.color.white),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                YallaButton(
                    text = stringResource(R.string.select_tariff),
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}