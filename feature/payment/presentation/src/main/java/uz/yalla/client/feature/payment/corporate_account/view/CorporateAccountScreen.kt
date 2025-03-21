package uz.yalla.client.feature.payment.corporate_account.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.corporate_account.components.PagerIndicator
import uz.yalla.client.feature.payment.corporate_account.model.CorporateAccountUIState
import uz.yalla.client.feature.payment.corporate_account.pages.AddBankDetailsPage
import uz.yalla.client.feature.payment.corporate_account.pages.AddCompanyPage
import uz.yalla.client.feature.payment.corporate_account.pages.AddLegalAddressPage

@Composable
internal fun CorporateAccountScreen(
    uiState: CorporateAccountUIState,
    pagerState: PagerState,
    onIntent: (CorporateAccountIntent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding(),
        topBar = { CorporateTopBar( onNavigateBack ) },
        content = { paddingValues ->
            CorporateContent(
                uiState = uiState,
                pagerState = pagerState,
                onIntent = onIntent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onNextPage = {
                    coroutineScope.launch {
                        if (pagerState.currentPage < 2) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onNavigateBack()
                        }
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CorporateTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = {}
    )
}

@Composable
private fun CorporateContent(
    uiState: CorporateAccountUIState,
    pagerState: PagerState,
    modifier: Modifier,
    onIntent: (CorporateAccountIntent) -> Unit,
    onNextPage: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        PageIndicatorSection(pagerState = pagerState)

        Spacer(modifier = Modifier.height(20.dp))

        CorporatePager(
            pagerState = pagerState,
            uiState = uiState,
            onIntent = onIntent
        )

        Spacer(modifier = Modifier.weight(1f))

        NextButton(
            pagerState = pagerState,
            onClick = onNextPage,
            isCompanyPageValid = uiState.isCompanyPageValid,
            isBankDetailsPageValid = uiState.isBankDetailsPageValid,
            isLegalAddressPageValid = uiState.isLegalAddressPageValid
        )
    }
}

@Composable
private fun PageIndicatorSection(pagerState: PagerState) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PagerIndicator(
            pageCount = pagerState.pageCount,
            pagerState = pagerState,
        )
    }
}

@Composable
private fun CorporatePager(
    pagerState: PagerState,
    uiState: CorporateAccountUIState,
    onIntent: (CorporateAccountIntent) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        when (page) {
            0 -> AddCompanyPage(uiState, onIntent)
            1 -> AddLegalAddressPage(uiState, onIntent)
            2 -> AddBankDetailsPage(uiState, onIntent)
        }
    }
}

@Composable
private fun NextButton(
    pagerState: PagerState,
    isCompanyPageValid: Boolean,
    isLegalAddressPageValid: Boolean,
    isBankDetailsPageValid: Boolean,
    onClick: () -> Unit
) {
    val buttonEnabled = when (pagerState.currentPage) {
        0 -> isCompanyPageValid
        1 -> isLegalAddressPageValid
        2 -> isBankDetailsPageValid
        else -> false
    }

    PrimaryButton(
        text = stringResource(R.string.next),
        enabled = buttonEnabled,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    )
}