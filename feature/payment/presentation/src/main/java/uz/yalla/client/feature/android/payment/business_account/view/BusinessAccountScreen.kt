package uz.yalla.client.feature.android.payment.business_account.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.business_account.components.BusinessAccountItem
import uz.yalla.client.feature.android.payment.business_account.components.EmployeeItem
import uz.yalla.client.feature.android.payment.business_account.model.BusinessAccountUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BusinessAccountScreen(
    uiState: BusinessAccountUIState,
    onIntent: (BusinessAccountIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                title = {
                    Text(
                        text = stringResource(R.string.business_account),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(BusinessAccountIntent.OnNavigateBack) }) {
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
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    BusinessAccountItem(
                        overallBalance = uiState.overallBalance,
                        employeeCount = uiState.employeeCount
                    )
                }

                Text(
                    text = stringResource(R.string.employees),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.title2,
                    modifier = Modifier.padding(20.dp)
                )

                uiState.employees.forEach { employee ->
                    EmployeeItem(
                        name = employee.name,
                        phoneNumber = employee.phoneNumber,
                        balance = employee.balance,
                        tripCount = employee.tripCount,
                        onClick = { onIntent(BusinessAccountIntent.OnClickEmployee) },
                        onChecked = {},
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp),
                    containerColor = YallaTheme.color.black,
                    shape = CircleShape,
                    onClick = {onIntent(BusinessAccountIntent.AddEmployee)},
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null,
                        tint = YallaTheme.color.white
                    )
                }
            }
        }
    )
}