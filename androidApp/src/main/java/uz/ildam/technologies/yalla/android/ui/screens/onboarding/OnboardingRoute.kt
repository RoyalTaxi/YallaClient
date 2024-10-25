package uz.ildam.technologies.yalla.android.ui.screens.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R

internal data class Page(
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val desc: Int,
)

@Composable
internal fun OnboardingRoute(
    onNext: () -> Unit
) {
    val pagerState = rememberPagerState { 4 }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) onNext() }

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
                ),
                Page(
                    R.drawable.img_onboarding_4,
                    R.string.onboarding_4,
                    R.string.onboarding_4_desc
                )
            )
        )
    }

    LaunchedEffect(Unit) {
        scope.launch { scrollState.animateScrollTo(scrollState.maxValue) }
    }

    OnboardingScreen(
        scrollState = scrollState,
        pagerState = pagerState,
        screenContents = screenContents,
        onIntent = { intent ->
            when (intent) {
                OnboardingIntent.NavigateNext -> locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                OnboardingIntent.Swipe -> scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            }
        }
    )
}