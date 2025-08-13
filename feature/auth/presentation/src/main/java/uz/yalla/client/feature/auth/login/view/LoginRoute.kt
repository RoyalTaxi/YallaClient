package uz.yalla.client.feature.auth.login.view

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.auth.R
import uz.yalla.client.feature.auth.login.intent.LoginSideEffect
import uz.yalla.client.feature.auth.login.model.LoginViewModel
import uz.yalla.client.feature.auth.login.navigation.FromLogin

@Composable
fun LoginRoute(
    navigate: (FromLogin) -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    BackHandler { activity?.moveTaskToBack(true) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            LoginSideEffect.NavigateBack -> navigate(FromLogin.ToBack)
            is LoginSideEffect.NavigateToVerification -> navigate(
                FromLogin.ToVerification(
                    phoneNumber = effect.phoneNumber,
                    seconds = effect.seconds
                )
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                viewModel.onAppear()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                viewModel.onDisappear()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LoginScreen(
        state = state,
        onIntent = viewModel::onIntent
    )

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