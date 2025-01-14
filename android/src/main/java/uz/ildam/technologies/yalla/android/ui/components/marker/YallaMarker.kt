package uz.ildam.technologies.yalla.android.ui.components.marker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.shape.squareSize
import androidx.compose.animation.with
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.coroutineScope


@Composable
fun YallaMarker(
    time: Int?,
    isLoading: Boolean,
    isSearching: Boolean = false,
    isSending: Boolean = false,
    isAppointed: Boolean = false,
    isAtAddress: Boolean = false,
    isInFetters: Boolean = false,
    isCompleted: Boolean = false,
    isRouteEmpty: Boolean = true,
    color: Color = YallaTheme.color.primary,
    selectedAddressName: String?,
    modifier: Modifier = Modifier
) {
    val jumpOffset = remember { Animatable(initialValue = 3f) }
    val rotation = remember { Animatable(initialValue = 0f) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_ripple_default))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        restartOnPlay = true
    )

    LaunchedEffect(isLoading) {
        if (isLoading) {
            if (jumpOffset.value < 9f) {
                jumpOffset.animateTo(
                    targetValue = 17f,
                    animationSpec = tween(durationMillis = 600)
                )
            }

            launch {
                while (true) {
                    jumpOffset.animateTo(
                        targetValue = 9f,
                        animationSpec = tween(durationMillis = 600)
                    )
                    jumpOffset.animateTo(
                        targetValue = 17f,
                        animationSpec = tween(durationMillis = 600)
                    )
                }
            }

            launch {
                while (true) {
                    rotation.animateTo(
                        targetValue = 360f,
                        animationSpec = tween(durationMillis = 1000)
                    )
                    rotation.snapTo(0f)
                }
            }
        } else {
            jumpOffset.animateTo(
                targetValue = 3f,
                animationSpec = tween(durationMillis = 600)
            )
            rotation.snapTo(0f)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isSearching || isSending) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxSize(.8f)
                    .alpha(.5f)
            )
        }

        if (
            (isRouteEmpty || isSearching || isSending) &&
            !isAppointed && !isAtAddress && !isInFetters && !isCompleted
        ) {
            ConstraintLayout(modifier = modifier) {
                val (circle, stick, indicator, addressName) = createRefs()

                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(8.dp)
                        .clip(CircleShape)
                        .background(YallaTheme.color.black)
                        .constrainAs(indicator) {
                            linkTo(start = parent.start, end = parent.end)
                            linkTo(top = parent.top, bottom = parent.bottom)
                        }
                )

                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(2.dp)
                        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                        .background(YallaTheme.color.black)
                        .constrainAs(stick) {
                            linkTo(start = indicator.start, end = indicator.end)
                            // animate the bottom margin from 3..9..17 dp
                            bottom.linkTo(indicator.bottom, margin = jumpOffset.value.dp)
                        }
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color)
                        .squareSize(.5f)
                        .padding(6.dp)
                        .height(IntrinsicSize.Min)
                        .width(IntrinsicSize.Min)
                        .constrainAs(circle) {
                            bottom.linkTo(stick.top)
                            linkTo(start = indicator.start, end = indicator.end)
                        }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = time?.toString().orEmpty(),
                            color = YallaTheme.color.white,
                            style = YallaTheme.font.title
                        )
                        Text(
                            text = if (time != null) stringResource(R.string.min) else "",
                            color = YallaTheme.color.white,
                            style = YallaTheme.font.body
                        )
                    }

                    if (isLoading) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = modifier
                                .fillMaxSize()
                                .background(color)
                                .graphicsLayer { clip = true }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_loading),
                                contentDescription = null,
                                tint = YallaTheme.color.white,
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer {
                                        rotationZ = rotation.value
                                    }
                            )
                        }
                    }

                    if (time == null && isLoading.not()) {
                        Box(
                            modifier = modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(YallaTheme.color.white)
                                .graphicsLayer { clip = true }
                        )
                    }
                }

                Surface(
                    color = YallaTheme.color.black,
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.constrainAs(addressName) {
                        bottom.linkTo(indicator.top, margin = 96.dp)
                        linkTo(start = indicator.start, end = indicator.end)
                    }
                ) {
                    AnimatedContent(
                        contentAlignment = Alignment.Center,
                        targetState = if (!isLoading && selectedAddressName != null) {
                            selectedAddressName
                        } else {
                            stringResource(R.string.loading)
                        },
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(durationMillis = 500)
                            ) togetherWith fadeOut(
                                animationSpec = tween(durationMillis = 500)
                            )
                        }, label = "textAnimation"
                    ) { targetText ->
                        Text(
                            text = targetText,
                            color = YallaTheme.color.white,
                            style = YallaTheme.font.labelSemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}