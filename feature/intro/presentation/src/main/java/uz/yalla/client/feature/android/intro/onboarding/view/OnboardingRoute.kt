package uz.yalla.client.feature.android.intro.onboarding.view

import android.Manifest
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import uz.yalla.client.feature.android.intro.R

internal data class Page(
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val desc: Int,
)

@Composable
internal fun OnboardingRoute(
    onNext: () -> Unit,
    onJumpNext: () -> Unit
) {
    val context = LocalContext.current
    val isPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    val screenContents by remember {
        mutableStateOf(
            listOf(
                Page(
                    R.drawable.img_onboarding_1,
                    R.string.onboarding_1,
                    R.string.onboarding_1_desc
                ),
                Page(
                    R.drawable.img_onboarding_2,
                    R.string.onboarding_2,
                    R.string.onboarding_2_desc
                ),
                Page(
                    R.drawable.img_onboarding_3,
                    R.string.onboarding_3,
                    R.string.onboarding_3_desc
                )
            )
        )
    }
    val pagerState = rememberPagerState { screenContents.size }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val nextNavigation = if (isPermissionGranted) onJumpNext else onNext

    LaunchedEffect(Unit) { scope.launch { scrollState.animateScrollTo(scrollState.maxValue) } }

    OnboardingScreen(
        scrollState = scrollState,
        pagerState = pagerState,
        screenContents = screenContents,
        onIntent = { intent ->
            when (intent) {
                OnboardingIntent.Swipe -> scope.launch {
                    if (pagerState.currentPage == 2) nextNavigation()
                    else pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
    )
}