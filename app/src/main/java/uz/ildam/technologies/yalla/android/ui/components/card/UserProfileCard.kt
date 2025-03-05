package uz.ildam.technologies.yalla.android.ui.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import uz.yalla.client.cor
import uz.ildam.technologies.yalla.android.R
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun UserProfileCard(
    client: Client,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(YallaTheme.color.white),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(20.dp)
        ) {
            AsyncImage(
                model = client.image,
                contentDescription = null,
                error = painterResource(R.drawable.img_default_pfp),
                placeholder = painterResource(R.drawable.img_default_pfp),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(60.dp)
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "${client.givenNames} ${client.surname}",
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelLarge
                )

                Text(
                    text = client.phone,
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body
                )
            }
        }
    }
}