package uz.yalla.client.feature.payment.add_card.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
internal fun CardViewCard(
    cardNumber: String,
    cardDate: String,
    onCardNumberChange: (String) -> Unit,
    onCardDateChange: (String) -> Unit,
    onClickCamera: () -> Unit
) {
    val focusRequestPan = remember { FocusRequester() }
    val focusRequestExpiry = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            focusRequestPan.requestFocus()
        }
    }

    Card(
        colors = CardDefaults.cardColors(YallaTheme.color.background),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Spacer(modifier = Modifier.height(55.dp))

            CardNumberField(
                number = cardNumber,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequestPan),
                onClickCamera = onClickCamera,
                onNumberChange = {
                    if (it.length == 16) focusRequestExpiry.requestFocus()
                    onCardNumberChange(it)
                }
            )

            CardDateField(
                date = cardDate,
                modifier = Modifier.focusRequester(focusRequestExpiry),
                onDateChange = {
                    if (it.length == 6) focusRequestExpiry.freeFocus()
                    onCardDateChange(it)
                }
            )
        }
    }
}