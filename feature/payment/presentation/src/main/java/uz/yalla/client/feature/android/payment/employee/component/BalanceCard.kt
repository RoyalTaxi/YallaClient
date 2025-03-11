package uz.yalla.client.feature.android.payment.employee.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.android.payment.R

@Composable
fun BalanceCard(
    balance: String,
    addBalance: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = YallaTheme.color.gray2),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.balance),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.label
                )

                Text(
                    text = balance,
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.title
                )
            }

            Button(
                onClick = addBalance,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YallaTheme.color.primary)
            ) {
                Text(
                    text = stringResource(R.string.top_up),
                    color = YallaTheme.color.white,
                    style = YallaTheme.font.label
                )
            }
        }
    }
}