package uz.yalla.client.feature.payment.business_account.view

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
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.business_account.components.BusinessAccountItem
import uz.yalla.client.feature.payment.business_account.components.EmployeeItem
import uz.yalla.client.feature.payment.business_account.model.BusinessAccountUIState
import uz.yalla.client.feature.payment.business_account.model.EmployeeUIModel

@Composable
internal fun BusinessAccountScreen(
    uiState: BusinessAccountUIState,
    onIntent: (BusinessAccountIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.background,
        modifier = Modifier.imePadding(),
        topBar = { BusinessTopBar { onIntent(BusinessAccountIntent.OnNavigateBack) } },
        content = { paddingValues ->
            BusinessContent(
                uiState = uiState,
                onIntent = onIntent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BusinessTopBar(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.background),
        title = {
            Text(
                text = stringResource(R.string.business_account),
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
private fun BusinessContent(
    uiState: BusinessAccountUIState,
    modifier: Modifier,
    onIntent: (BusinessAccountIntent) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        AccountSummarySection(
            overallBalance = uiState.overallBalance,
            employeeCount = uiState.employeeCount
        )

        EmployeesSection(
            employees = uiState.employees,
            onEmployeeClick = { onIntent(BusinessAccountIntent.OnClickEmployee) }
        )

        Spacer(modifier = Modifier.weight(1f))

        AddEmployeeButton(
            onClick = { onIntent(BusinessAccountIntent.AddEmployee) },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        )
    }
}

@Composable
private fun AccountSummarySection(
    overallBalance: String,
    employeeCount: String
) {
    Column(modifier = Modifier.padding(20.dp)) {
        BusinessAccountItem(
            overallBalance = overallBalance,
            employeeCount = employeeCount
        )
    }
}

@Composable
private fun EmployeesSection(
    employees: List<EmployeeUIModel>,
    onEmployeeClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.employees),
        color = YallaTheme.color.onBackground,
        style = YallaTheme.font.title2,
        modifier = Modifier.padding(20.dp)
    )

    employees.forEach { employee ->
        EmployeeItem(
            name = employee.name,
            phoneNumber = employee.phoneNumber,
            balance = employee.balance,
            tripCount = employee.tripCount,
            onClick = onEmployeeClick,
            onChecked = {},
        )
    }
}

@Composable
private fun AddEmployeeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        modifier = modifier,
        containerColor = YallaTheme.color.onBackground,
        shape = CircleShape,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = null,
            tint = YallaTheme.color.background
        )
    }
}