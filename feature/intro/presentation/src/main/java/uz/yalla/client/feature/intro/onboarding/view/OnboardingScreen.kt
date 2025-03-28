package uz.yalla.client.feature.intro.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.shape.squareSize
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.intro.onboarding.components.DotIndicator
import uz.yalla.client.feature.intro.onboarding.components.NextButton

@Composable
internal fun OnboardingScreen(
    scrollState: ScrollState,
    pagerState: PagerState,
    screenContents: List<Page>,
    onIntent: (OnboardingIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
        ) {

            OnboardingContent(
                pagerState = pagerState,
                screenContents = screenContents,
                modifier = Modifier.matchParentSize()
            )

            OnboardingFooter(
                pagerState = pagerState,
                modifier = Modifier.align(Alignment.BottomCenter),
                onNext = { onIntent(OnboardingIntent.Swipe) }
            )
        }
    }
}

@Composable
private fun OnboardingContent(
    pagerState: PagerState,
    screenContents: List<Page>,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) { page ->
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .squareSize()
            ) {
                Image(
                    painter = painterResource(id = screenContents[page].image),
                    contentDescription = null,
                )
            }

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
}

@Composable
private fun OnboardingFooter(
    pagerState: PagerState,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
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
                onClick = onNext
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}