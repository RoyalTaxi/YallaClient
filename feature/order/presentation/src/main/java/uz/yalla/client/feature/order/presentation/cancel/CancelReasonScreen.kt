package uz.yalla.client.feature.order.presentation.cancel

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
import uz.yalla.client.feature.order.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelReasonScreen(
    uiState: CancelReasonUIState,
    onIntent: (CancelReasonIntent) -> Unit,
    onSelect: () -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onIntent(CancelReasonIntent.NavigateBack) }) {
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
                    .padding(paddingValues)
                    .padding(top = 20.dp)
            ) {
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

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(
                        items = uiState.reasons,
                        key = { it.id }
                    ) { reason ->
                        ItemTextSelectable(
                            text = reason.name,
                            isSelected = uiState.selectedReason == reason,
                            onSelect = { onIntent(CancelReasonIntent.OnSelect(reason)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = stringResource(R.string.choose),
                    enabled = uiState.selectedReason != null,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    onClick = { uiState.selectedReason?.let { onSelect() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }
        }
    )
}

