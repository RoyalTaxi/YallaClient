package uz.yalla.client.feature.order.presentation.cancel_reason.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.selectable.ItemTextSelectable
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.SettingModel
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.cancel_reason.model.CancelReasonUIState

@Composable
fun CancelReasonScreen(
    uiState: CancelReasonUIState,
    onIntent: (CancelReasonIntent) -> Unit,
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = { CancelReasonTopBar { onIntent(CancelReasonIntent.NavigateBack) } },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(top = 20.dp)
            ) {
                CancelReasonInfo()

                CancelReasons(
                    reasons = uiState.reasons,
                    selectedReason = uiState.selectedReason,
                    onSelectReason = { onIntent(CancelReasonIntent.OnSelect(it)) }
                )

                CancelReasonFooter(
                    modifier = Modifier.weight(1f),
                    isButtonEnabled = uiState.selectedReason != null,
                    onClickButton = {
                        uiState.selectedReason?.let {
                            onIntent(CancelReasonIntent.OnSelected)
                        }
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CancelReasonTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        title = {},
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun CancelReasonInfo() {
    Text(
        text = stringResource(R.string.why_canceled),
        color = YallaTheme.color.black,
        style = YallaTheme.font.headline,
        modifier = Modifier.padding(20.dp)
    )

    Text(
        text = stringResource(R.string.we_need_cause),
        color = YallaTheme.color.gray,
        style = YallaTheme.font.body,
        modifier = Modifier.padding(horizontal = 20.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun CancelReasons(
    reasons: List<SettingModel.CancelReason>,
    selectedReason: SettingModel.CancelReason?,
    onSelectReason: (SettingModel.CancelReason) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        items(
            items = reasons,
            key = { it.id }
        ) { reason ->
            ItemTextSelectable(
                text = reason.name,
                isSelected = selectedReason == reason,
                onSelect = { onSelectReason(reason) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CancelReasonFooter(
    isButtonEnabled: Boolean,
    onClickButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier)

    PrimaryButton(
        text = stringResource(R.string.choose),
        enabled = isButtonEnabled,
        contentPadding = PaddingValues(vertical = 16.dp),
        onClick = onClickButton,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    )
}