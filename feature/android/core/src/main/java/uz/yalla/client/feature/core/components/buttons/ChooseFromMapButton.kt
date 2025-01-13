package uz.yalla.client.feature.core.components.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.yalla.client.feature.core.R
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun ChooseFromMapButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(YallaTheme.color.white),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_pin),
                contentDescription = null,
                tint = YallaTheme.color.black
            )

            Text(
                text = stringResource(R.string.map),
                color = YallaTheme.color.black,
                style = YallaTheme.font.bodySmall.copy(fontSize = 10.sp)
            )
        }
    }
}