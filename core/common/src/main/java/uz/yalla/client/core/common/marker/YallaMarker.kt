package uz.yalla.client.core.common.marker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.shape.squareSize
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.map.presentation.components.marker.YallaMarkerState

@Composable
fun YallaMarker(
    state: YallaMarkerState,
    modifier: Modifier = Modifier,
    color: Color = YallaTheme.color.primary,
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

    var jumpJob: Job? by remember { mutableStateOf(null) }
    var rotationJob: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(state) {
        when (state) {
            is YallaMarkerState.LOADING, is YallaMarkerState.Searching -> {
                if (jumpOffset.value < 9f) {
                    jumpOffset.animateTo(
                        targetValue = 17f,
                        animationSpec = tween(durationMillis = 600)
                    )
                }

                jumpJob = launch {
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

                rotationJob = launch {
                    while (true) {
                        rotation.animateTo(
                            targetValue = 360f,
                            animationSpec = tween(durationMillis = 1000)
                        )
                        rotation.snapTo(0f)
                    }
                }
            }
            is YallaMarkerState.IDLE -> {
                jumpOffset.animateTo(
                    targetValue = 3f,
                    animationSpec = tween(durationMillis = 600)
                )
                rotation.snapTo(0f)
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        ConstraintLayout(modifier = modifier) {
            val (circle, stick, indicator, locationName, lottie) = createRefs()

            if (state is YallaMarkerState.Searching) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxSize(.8f)
                        .alpha(.5f)
                        .constrainAs(lottie) {
                            linkTo(start = circle.start, end = circle.end)
                            linkTo(top = circle.top, bottom = circle.bottom)
                        }
                )
            }

            if (state !is YallaMarkerState.Searching) {
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
                            bottom.linkTo(indicator.bottom, margin = jumpOffset.value.dp)
                        }
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color)
                    .squareSize(.5f)
                    .padding(6.dp)
                    .height(IntrinsicSize.Min)
                    .width(IntrinsicSize.Min)
                    .constrainAs(circle) {
                        if (state !is YallaMarkerState.Searching) {
                            bottom.linkTo(stick.top)
                            linkTo(start = indicator.start, end = indicator.end)
                        } else {
                            linkTo(start = parent.start, end = parent.end)
                            linkTo(top = parent.top, bottom = parent.bottom)
                        }
                    }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when (state) {
                            is YallaMarkerState.IDLE -> state.timeout?.toString().orEmpty()
                            else -> ""
                        },
                        color = YallaTheme.color.white,
                        style = YallaTheme.font.title
                    )
                    Text(
                        text = when (state) {
                            is YallaMarkerState.IDLE -> if (state.timeout != null) stringResource(R.string.min) else ""
                            else -> ""
                        },
                        color = YallaTheme.color.white,
                        style = YallaTheme.font.body
                    )
                }

                if (state is YallaMarkerState.LOADING) {
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

                if (state is YallaMarkerState.IDLE && state.timeout == null) {
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
                modifier = Modifier
                    .padding(horizontal = 60.dp)
                    .constrainAs(locationName) {
                        when (state) {
                            is YallaMarkerState.IDLE -> {
                                bottom.linkTo(circle.top, margin = 96.dp)
                                linkTo(start = parent.start, end = parent.end)
                            }
                            is YallaMarkerState.Searching -> {
                                bottom.linkTo(indicator.top, margin = 96.dp)
                                linkTo(start = indicator.start, end = indicator.end)
                            }
                            is YallaMarkerState.LOADING -> {
                                bottom.linkTo(circle.top, margin = 96.dp)
                                linkTo(start = parent.start, end = parent.end)
                            }
                        }
                    }
            ) {
                AnimatedContent(
                    contentAlignment = Alignment.Center,
                    targetState = when (state) {
                        is YallaMarkerState.IDLE -> state.title
                        is YallaMarkerState.Searching -> state.title
                        is YallaMarkerState.LOADING -> stringResource(R.string.loading)
                    } ?: stringResource(R.string.loading),
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}