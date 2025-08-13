package uz.yalla.client.feature.payment.employee.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.business_account.components.EmployeeItem
import uz.yalla.client.feature.payment.employee.component.BalanceCard

@Composable
 fun EmployeeScreen(
    onIntent: (EmployeeIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.background,
        topBar = { EmployeeTopBar { onIntent(EmployeeIntent.OnNavigateBack) } },
        content = { paddingValues ->
            EmployeeContent(
                modifier = Modifier.padding(paddingValues),
                onAddBalance = { onIntent(EmployeeIntent.AddBalance) }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeTopBar(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.background),
        title = {
            Text(
                text = stringResource(R.string.employees),
                color = YallaTheme.color.onBackground,
                style = YallaTheme.font.labelLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = YallaTheme.color.onBackground
                )
            }
        }
    )
}

@Composable
private fun EmployeeContent(
    modifier: Modifier,
    onAddBalance: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        EmployeeItem(
            name = "Икромов Сардор",
            phoneNumber = "+998991234567",
            onClick = {},
            onChecked = {},
            isSelected = true
        )

        Column(modifier = Modifier.padding(20.dp)) {
            BalanceCard(
                balance = "24 000 so'm",
                addBalance = onAddBalance,
            )

            EmployeeHistorySection()
        }
    }
}

@Composable
private fun EmployeeHistorySection() {
    Text(
        text = stringResource(R.string.today),
        color = YallaTheme.color.onBackground,
        style = YallaTheme.font.title2,
        modifier = Modifier.padding(vertical = 20.dp)
    )
}