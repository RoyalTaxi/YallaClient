package uz.yalla.client.feature.order.presentation.client_waiting.view

import android.content.Intent
import android.content.Intent.ACTION_DIAL
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.button.CallButton
import uz.yalla.client.core.common.item.CarNumberItem
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.client_waiting.model.ClientWaitingViewModel

object ClientWaitingSheet {
    private val viewModel: ClientWaitingViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<ClientWaitingIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @Composable
    fun View(
        orderId: Int,
    ) {
        val context = LocalContext.current
        val density = LocalDensity.current
        val state by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.setOrderId(orderId)
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.gray2,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .onSizeChanged {
                    with(density) {
                        viewModel.onIntent(ClientWaitingIntent.SetSheetHeight(it.height.toDp()))
                    }
                }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.coming_to_you),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.black
                )

                state.selectedDriver?.let { executor ->
                    Text(
                        text = "${executor.driver.color.name} ${executor.driver.mark} ${executor.driver.model}",
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.gray
                    )

                    CarNumberItem(
                        code = executor.driver.stateNumber.slice(0..<2),
                        number = "(\\d+|[A-Za-z]+)"
                            .toRegex()
                            .findAll(executor.driver.stateNumber)
                            .map { it.value }
                            .toList()
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(20.dp)
                    .navigationBarsPadding()

            ) {
                CallButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val phoneNumber = state.selectedDriver?.phone ?: return@CallButton
                        val intent = Intent(ACTION_DIAL).apply { data = "tel:$phoneNumber".toUri() }
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    }
                )
            }
        }
    }
}