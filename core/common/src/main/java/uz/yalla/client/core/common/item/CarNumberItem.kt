package uz.yalla.client.core.common.item

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun CarNumberItem(
    code: String,
    number: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(
                shape = RoundedCornerShape(4.dp),
                color = YallaTheme.color.onBlack,
                width = 1.dp
            )
            .padding(1.dp)
            .border(
                shape = RoundedCornerShape(3.dp),
                color = YallaTheme.color.black,
                width = 1.dp
            )
    ) {
        Box(
            modifier = Modifier
                .border(
                    shape = RoundedCornerShape(3.dp),
                    color = YallaTheme.color.black,
                    width = 1.dp
                )
                .padding(2.dp)
        ) {
            Text(
                text = code,
                color = YallaTheme.color.black,
                style = YallaTheme.font.customNumber
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(
                    shape = RoundedCornerShape(3.dp),
                    color = YallaTheme.color.black,
                    width = 1.dp
                )
                .padding(start = 4.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
        ) {
            for (part in number) {
                Text(
                    text = part,
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.customNumber
                )

                Spacer(modifier = Modifier.width(3.dp))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_flag_uz),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Text(
                    text = "UZ",
                    color = Color(0xFF1D83D1),
                    style = YallaTheme.font.customNumberText
                )
            }
        }
    }
}