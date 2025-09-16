package uz.yalla.client.feature.history.history_details.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.core.common.map.static.google.StaticGoogleMap
import uz.yalla.client.core.common.map.static.libre.StaticLibreMap
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.feature.history.R
import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsSideEffect
import uz.yalla.client.feature.history.history_details.model.HistoryDetailsViewModel
import uz.yalla.client.feature.history.history_details.model.onIntent
import uz.yalla.client.feature.history.history_details.navigation.FromHistoryDetails

@Composable
fun HistoryDetailsRoute(
    navigateTo: (FromHistoryDetails) -> Unit,
    orderId: Int,
    viewModel: HistoryDetailsViewModel = koinViewModel(
        parameters = { parametersOf(orderId) }
    )
) {
    val prefs = koinInject<AppPreferences>()
    val lifecycleOwner = LocalLifecycleOwner.current

    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    val mapType by prefs.mapType.collectAsStateWithLifecycle(null)

    val map = remember(mapType) {
        when (mapType) {
            MapType.Google -> StaticGoogleMap()
            MapType.Libre -> StaticLibreMap()
            null -> null
        }
    }

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            HistoryDetailsSideEffect.NavigateBack -> navigateTo(FromHistoryDetails.NavigateBack)
        }
    }

    HistoryDetailsScreen(
        state = state,
        onIntent = viewModel::onIntent
    ) { modifier ->
        map?.View(
            modifier = modifier,
            viewModel = viewModel.staticMapViewModel
        )
    }

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { viewModel.dismissErrorDialog() },
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }

    if (loading) {
        LoadingDialog()
    }
}
