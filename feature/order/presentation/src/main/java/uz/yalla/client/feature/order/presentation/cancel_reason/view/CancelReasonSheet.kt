package uz.yalla.client.feature.order.presentation.cancel_reason.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.selectable.ItemTextSelectable
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.cancel_reason.model.CancelReasonActionState
import uz.yalla.client.feature.order.presentation.cancel_reason.model.CancelReasonViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CancelReasonSheet(
    orderId: Int,
    viewModel: CancelReasonViewModel = koinViewModel()
) {
    var loading by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val scaffoldState = rememberBottomSheetScaffoldState(
        rememberBottomSheetState(
            initialValue = SheetValue.Expanded,
            defineValues = {
                SheetValue.Expanded at contentHeight
            }
        )
    )

    LaunchedEffect(key1 = true) {
        CancelReasonSheetChannel.register(lifecycleOwner)

        viewModel.actionState.collect { action ->
            loading = when (action) {
                is CancelReasonActionState.Error -> {
                    CancelReasonSheetChannel.sendIntent(CancelReasonIntent.NavigateBack)
                    false
                }
                CancelReasonActionState.GettingSuccess -> false
                CancelReasonActionState.Loading -> true
                CancelReasonActionState.SettingSuccess -> {
                    CancelReasonSheetChannel.sendIntent(CancelReasonIntent.NavigateBack)
                    false
                }
            }
        }
    }

    BackHandler {
        CancelReasonSheetChannel.sendIntent(CancelReasonIntent.NavigateBack)
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetContainerColor = YallaTheme.color.surface,
        content = {},
        sheetContent = {
            Column(
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.background,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(top = 20.dp)
                    .navigationBarsPadding()
            ) {
                CancelReasonInfo()

                CancelReasons(
                    reasons = uiState.reasons,
                    selectedReason = uiState.selectedReason,
                    onSelectReason = { 
                        viewModel.updateSelectedReason(it)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = stringResource(R.string.choose),
                    enabled = uiState.selectedReason != null,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    onClick = {
                        uiState.selectedReason?.let {
                            viewModel.cancelReason(orderId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }
        }
    )

    if (loading) LoadingDialog()

    DisposableEffect(Unit) {
        onDispose {
            // Cleanup if needed
        }
    }
}

@Composable
private fun CancelReasonInfo() {
    Text(
        text = stringResource(R.string.why_canceled),
        color = YallaTheme.color.onBackground,
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
    reasons: List<uz.yalla.client.feature.order.domain.model.response.order.SettingModel.CancelReason>,
    selectedReason: uz.yalla.client.feature.order.domain.model.response.order.SettingModel.CancelReason?,
    onSelectReason: (uz.yalla.client.feature.order.domain.model.response.order.SettingModel.CancelReason) -> Unit
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
