package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun CancelReasonItem(
    text: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{},
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = text,
                style = YallaTheme.font.labelSemiBold,
                color = YallaTheme.color.black
            )

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = YallaTheme.color.gray2,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(YallaTheme.color.white)
                )
            }

//            Box(
//                modifier = Modifier
//                    .size(60.dp)
//                    .background(
//                        color = YallaTheme.color.gray2,
//                        shape = CircleShape
//                ),
//            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ItemCheck(){
    CancelReasonItem("Hello, Welcome to Office")

}