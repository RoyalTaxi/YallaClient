package uz.ildam.technologies.yalla.android.ui.screens.onboarding

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.NextButton
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.indicator.DotIndicator


@Composable
internal fun OnboardingScreen(
    scrollState: ScrollState,
    pagerState: PagerState,
    screenContents: List<Page>,
    onSwipe: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.white)
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

        if (pagerState.currentPage != 3) Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DotIndicator(
                pageCount = pagerState.pageCount - 1,
                pagerState = pagerState
            )

            NextButton(
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = onSwipe
            )
        } else YallaButton(
            text = stringResource(id = R.string.next),
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}
