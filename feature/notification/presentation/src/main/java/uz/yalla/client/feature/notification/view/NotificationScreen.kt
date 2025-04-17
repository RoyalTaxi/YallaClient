package uz.yalla.client.feature.notification.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.notification.model.NotificationUIState
import uz.yalla.client.feature.notification.presentation.R

@Composable
internal fun NotificationScreen(
    uiState: NotificationUIState,
    onIntent: (NotificationIntent) -> Unit
) {
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        containerColor = YallaTheme.color.white,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = { NotificationTopBar { onIntent(NotificationIntent.OnNavigateBack) }},
        content = { paddingValues ->
            NotificationContent(modifier = Modifier.padding(paddingValues))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationTopBar(
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotificationContent(
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier) {
        stickyHeader {
            Spacer(modifier = Modifier.height(40.dp))

            NotificationHeader()

            Spacer(modifier = Modifier.height(20.dp))

            Spacer(modifier = Modifier.height(50.dp))

            EmptyNotifications()
        }
    }
}

@Composable
private fun NotificationHeader() {
    Text(
        text = stringResource(R.string.notifications),
        color = YallaTheme.color.black,
        style = YallaTheme.font.headline,
        modifier = Modifier.padding(start = 20.dp, end = 60.dp)
    )
}

@Composable
private fun EmptyNotifications() {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.img_no_notification),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(160.dp)
        )

        Text(
            text = stringResource(R.string.no_notification_body),
            style = YallaTheme.font.label,
            color = YallaTheme.color.gray
        )
    }
}