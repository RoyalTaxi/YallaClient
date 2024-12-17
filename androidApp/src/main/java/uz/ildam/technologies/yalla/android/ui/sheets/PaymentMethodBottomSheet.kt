package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import okhttp3.Request
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.item.BonuseItem
import uz.ildam.technologies.yalla.android.ui.components.item.SelectPaymentTypeItem
import uz.ildam.technologies.yalla.android.ui.screens.card_list.CardListIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodBottomSheet(
    sheetState: SheetState,
    bonuseAmount: String,
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
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.payment_method),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.black
                )

                Text(
                    text = stringResource(R.string.choose_payment_method),
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.gray
                )
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
            ) {

                BonuseItem(
                    bonuses = bonuseAmount,
                    isSelected = false,
                    onChecked = {}
                )

                SelectPaymentTypeItem(
                    isSelected = true,
                    tint = YallaTheme.color.gray,
                    painter = painterResource(R.drawable.ic_money),
                    text = stringResource(R.string.cash),
                    onSelect = { },
                )

                SelectPaymentTypeItem(
                    isSelected = false,
                    tint = YallaTheme.color.gray,
                    painter = painterResource(R.drawable.ic_add),
                    text = stringResource(R.string.add_card),
                    onSelect = { },
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
                    text = stringResource(R.string.add),
                    onClick = { },
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }

}