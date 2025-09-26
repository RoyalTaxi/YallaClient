package uz.yalla.client.feature.home.presentation.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import uz.yalla.client.feature.home.presentation.intent.HomeIntent.HomeOverlayIntent.*
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.home.presentation.components.card.NoInternetCard
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent
import uz.yalla.client.feature.home.presentation.intent.HomeIntent
import uz.yalla.client.feature.home.presentation.intent.HomeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    networkState: Boolean,
    isDrawerOpen: Boolean,
    isOrderCanceledVisible: Boolean,
    onIntent: (HomeIntent) -> Unit,
    onDrawerIntent: (HomeDrawerIntent) -> Unit = {}
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (networkState) snackBarHostState.currentSnackbarData?.dismiss()
        else snackBarHostState.showSnackbar(message = "")
    }

    BackHandler(isDrawerOpen) { onIntent(CloseDrawer) }

    NavigationDrawer(
        isOpen = isDrawerOpen,
        onDismiss = { onIntent(CloseDrawer) },
        client = state.client,
        notificationsCount = state.notificationCount ?: 0,
        onIntent = { drawerIntent ->
            onIntent(CloseDrawer)
            onDrawerIntent(drawerIntent)
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isOrderCanceledVisible) {
            HomeOverlay(
                state = state,
                onIntent = onIntent,
                modifier = Modifier.padding(bottom = state.overlayPadding)
            )
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .consumeWindowInsets(WindowInsets.statusBars.asPaddingValues())
                .fillMaxWidth(.9f)
                .clip(RoundedCornerShape(16.dp)),
            snackbar = {
                Snackbar(
                    containerColor = YallaTheme.color.red,
                    contentColor = YallaTheme.color.background,
                    content = { NoInternetCard(Modifier.fillMaxWidth()) }
                )
            }
        )
    }
}
