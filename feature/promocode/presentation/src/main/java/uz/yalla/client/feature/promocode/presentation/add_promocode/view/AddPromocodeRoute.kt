package uz.yalla.client.feature.promocode.presentation.add_promocode.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeSideEffect
import uz.yalla.client.feature.promocode.presentation.add_promocode.model.AddPromocodeViewModel

@Composable
fun AddPromocodeRoute(
    onBack: () -> Unit,
    viewModel: AddPromocodeViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusRequester = remember { FocusRequester() }
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is AddPromocodeSideEffect.NavigateBack -> onBack()
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            focusRequester.requestFocus()
        }
    }


    AddPromocodeScreen(
        state = state,
        focusRequester = focusRequester,
        onIntent = viewModel::onIntent
    )
}