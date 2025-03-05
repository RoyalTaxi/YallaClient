package uz.yalla.client.feature.android.contact.contact_us.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import uz.yalla.client.feature.android.contact.R
import uz.yalla.client.feature.android.contact.contact_us.components.ContactUsCard
import uz.yalla.client.feature.android.contact.contact_us.model.ContactUsUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ContactUsScreen(
    uiState: ContactUsUIState,
    onIntent: (ContactUsIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier,
        topBar = {
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
                    IconButton(onClick = { onIntent(ContactUsIntent.OnNavigateBack) }) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {

                uiState.socialNetworks.forEach { socialNetwork ->
                    ContactUsCard(
                        socialNetwork = socialNetwork,
                        onClick = {
                            when (socialNetwork.third) {
                                R.string.email -> onIntent(
                                    ContactUsIntent.OnClickEmail(
                                        socialNetwork.second
                                    )
                                )

                                R.string.contuct_us -> onIntent(
                                    ContactUsIntent.OnClickPhone(
                                        socialNetwork.second
                                    )
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
    )
}