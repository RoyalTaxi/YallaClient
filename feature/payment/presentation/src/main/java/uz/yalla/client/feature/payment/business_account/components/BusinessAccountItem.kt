package uz.yalla.client.feature.payment.business_account.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.payment.R

@Composable
fun BusinessAccountItem(
    overallBalance: String,
    employeeCount: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = YallaTheme.color.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)

    ) {
        Text(
            text = stringResource(R.string.overall_balance),
            color = YallaTheme.color.background,
            style = YallaTheme.font.label
        )

        Text(
            text = overallBalance,
            color = YallaTheme.color.background,
            style = YallaTheme.font.title
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.employees),
            color = YallaTheme.color.background,
            style = YallaTheme.font.label
        )

        Text(
            text = employeeCount,
            color = YallaTheme.color.background,
            style = YallaTheme.font.title
        )
    }
}