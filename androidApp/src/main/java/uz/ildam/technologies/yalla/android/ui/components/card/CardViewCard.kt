package uz.ildam.technologies.yalla.android.ui.components.card

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.text_field.CardDateField
import uz.ildam.technologies.yalla.android.ui.components.text_field.CardNumberField

@Composable
fun CardViewCard(
    cardNumber: String,
    cardDate: String,
    onCardNumberChange: (String) -> Unit,
    onCardDateChange: (String) -> Unit,
    onClickCamera: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(YallaTheme.color.white),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Spacer(modifier = Modifier.height(55.dp))

            CardNumberField(
                number = cardNumber,
                modifier = Modifier.fillMaxWidth(),
                onClickCamera = onClickCamera,
                onNumberChange = onCardNumberChange
            )
            CardDateField(
                date = cardDate,
                onDateChange = onCardDateChange
            )
        }
    }
}