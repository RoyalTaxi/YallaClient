package uz.yalla.client.feature.android.intro.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.android.intro.onboarding.components.DotIndicator
import uz.yalla.client.feature.android.intro.onboarding.components.NextButton
import uz.yalla.client.feature.core.design.theme.YallaTheme


@Composable
internal fun OnboardingScreen(
    scrollState: ScrollState,
    pagerState: PagerState,
    screenContents: List<Page>,
    onIntent: (OnboardingIntent) -> Unit
) {

    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(43.dp))

            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                Column {
                    Image(
                        painter = painterResource(id = screenContents[page].image),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(54.dp))

                    Text(
                        text = stringResource(id = screenContents[page].title),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.headline,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = stringResource(id = screenContents[page].desc),
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.body,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(56.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DotIndicator(
                    pageCount = pagerState.pageCount,
                    pagerState = pagerState
                )

                NextButton(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onClick = { onIntent(OnboardingIntent.Swipe) }
                )
            }
        }
    }
}