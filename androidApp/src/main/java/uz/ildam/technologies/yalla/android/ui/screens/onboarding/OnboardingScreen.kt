package uz.ildam.technologies.yalla.android.ui.screens.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.components.button.NextButton
import uz.ildam.technologies.yalla.android.components.button.YallaButton
import uz.ildam.technologies.yalla.android.components.indicator.DotIndicator
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.screens.language.LanguageScreen
import uz.ildam.technologies.yalla.android.ui.screens.login.LoginScreen

class OnboardingScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val pagerState = rememberPagerState { 4 }
        val scope = rememberCoroutineScope()
        val locationPermissionRequest = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted -> if (isGranted) navigator push LanguageScreen() }

        val screenContents by remember {
            mutableStateOf(
                listOf(
                    OnboardingUIState(
                        R.drawable.img_onboarding_1,
                        R.string.onboarding_1,
                        R.string.onboarding_1_desc
                    ),
                    OnboardingUIState(
                        R.drawable.img_onboarding_2,
                        R.string.onboarding_2,
                        R.string.onboarding_2_desc
                    ),
                    OnboardingUIState(
                        R.drawable.img_onboarding_3,
                        R.string.onboarding_3,
                        R.string.onboarding_3_desc
                    ),
                    OnboardingUIState(
                        R.drawable.img_onboarding_4,
                        R.string.onboarding_4,
                        R.string.onboarding_4_desc
                    )
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YallaTheme.color.white)
                .systemBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(43.dp))

            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                Column {
                    Image(
                        painter = painterResource(id = screenContents[page].image),
                        contentDescription = null
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

            Spacer(modifier = Modifier.height(54.dp))

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
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                )
            } else YallaButton(
                text = stringResource(id = R.string.next),
                onClick = { locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}