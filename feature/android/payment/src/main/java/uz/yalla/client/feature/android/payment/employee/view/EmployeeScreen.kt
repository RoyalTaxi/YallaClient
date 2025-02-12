package uz.yalla.client.feature.android.payment.employee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.business_account.components.EmployeeItem
import uz.yalla.client.feature.core.components.items.HistoryOrderItem
import uz.yalla.client.feature.core.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EmployeeScreen(
    onIntent: (EmployeeIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                title = {
                    Text(
                        text = stringResource(R.string.employees),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {onIntent(EmployeeIntent.OnNavigateBack)}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {

                EmployeeItem(
                    name = "Икромов Сардор",
                    phoneNumber = "+998991234567",
                    onClick = {},
                    onChecked = {},
                    isSelected = true
                )

                BalanceCard(
                    balance = "24 000 so'm",
                    addBalance = {},
                )

                Text(
                    text = stringResource(R.string.today),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.title2,
                    modifier = Modifier.padding(vertical = 20.dp)
                )

                HistoryOrderItem(
                    firstAddress = "Ул. Сайлгох 124",
                    secondAddress = "ул. Мустакиллик 124",
                    time = "15:00",
                    totalPrice = "16 000 сум",
                    status = "",
                    onClick = {}
                )
            }
        }
    )
}