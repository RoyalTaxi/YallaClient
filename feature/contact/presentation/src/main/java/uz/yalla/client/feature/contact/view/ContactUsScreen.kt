package uz.yalla.client.feature.contact.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.contact.R
import uz.yalla.client.feature.contact.components.ContactUsCard
import uz.yalla.client.feature.contact.model.ContactUsUIState

@Composable
internal fun ContactUsScreen(
    uiState: ContactUsUIState,
    onIntent: (ContactUsIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier,
        topBar = { TopAppBar(onNavigateBack = { onIntent(ContactUsIntent.OnNavigateBack) }) },
        content = { paddingValues ->
            SocialNetworksList(
                uiState = uiState,
                onIntent = onIntent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(20.dp)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        title = {
            Text(
                text = stringResource(R.string.contuct_us),
                style = YallaTheme.font.labelLarge,
                color = YallaTheme.color.black
            )
        },
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
private fun SocialNetworksList(
    uiState: ContactUsUIState,
    onIntent: (ContactUsIntent) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(uiState.socialNetworks) { socialNetwork ->
            ContactUsCard(
                socialNetwork = socialNetwork,
                onClick = {
                    when (socialNetwork.third) {
                        R.string.email -> onIntent(
                            ContactUsIntent.OnClickEmail(socialNetwork.second)
                        )

                        R.string.contuct_us -> onIntent(
                            ContactUsIntent.OnClickPhone(socialNetwork.second)
                        )

                        else -> onIntent(
                            ContactUsIntent.OnClickUrl(
                                socialNetwork.third,
                                socialNetwork.second
                            )
                        )
                    }
                }
            )
        }
    }
}

