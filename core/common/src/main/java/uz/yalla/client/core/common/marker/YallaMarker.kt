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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.shape.squareSize
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun YallaMarker(
    state: YallaMarkerState,
    modifier: Modifier = Modifier,
    color: Color = YallaTheme.color.primary,
) {
    val jumpOffset = remember { Animatable(initialValue = 3f) }
    val rotation = remember { Animatable(initialValue = 0f) }

    // Use rememberCoroutineScope for better lifecycle management
    val animationScope = rememberCoroutineScope()

    // Track current jobs for proper cancellation
    val currentJobs = remember { mutableStateOf<Pair<Job?, Job?>>(null to null) }

    LaunchedEffect(state) {
        // Cancel any existing animations immediately
        currentJobs.value.first?.cancel()
        currentJobs.value.second?.cancel()

        when (state) {
            is YallaMarkerState.LOADING, is YallaMarkerState.Searching -> {
                // Ensure we start from a consistent state
                if (jumpOffset.value <= 5f) {
                    jumpOffset.snapTo(3f)
                    jumpOffset.animateTo(
                        targetValue = 17f,
                        animationSpec = tween(durationMillis = 400) // Faster initial jump
                    )
                }

                // Start continuous animations
                val jumpJob = animationScope.launch {
                    while (isActive) {
                        try {
                            jumpOffset.animateTo(
                                targetValue = 9f,
                                animationSpec = tween(durationMillis = 600)
                            )
                            jumpOffset.animateTo(
                                targetValue = 17f,
                                animationSpec = tween(durationMillis = 600)
                            )
                        } catch (e: Exception) {
                            // Animation was cancelled, exit gracefully
                            break
                        }
                    }
                }

                val rotationJob = animationScope.launch {
                    while (isActive) {
                        try {
                            rotation.animateTo(
                                targetValue = 360f,
                                animationSpec = tween(durationMillis = 1000)
                            )
                            rotation.snapTo(0f)
                        } catch (e: Exception) {
                            // Animation was cancelled, exit gracefully
                            break
                        }
                    }
                }

                currentJobs.value = jumpJob to rotationJob
            }

            is YallaMarkerState.IDLE -> {
                // Cancel ongoing animations and return to idle state
                currentJobs.value = null to null

                // Smooth transition to idle state
                animationScope.launch {
                    jumpOffset.animateTo(
                        targetValue = 3f,
                        animationSpec = tween(durationMillis = 400)
                    )
                }
                animationScope.launch {
                    rotation.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 200)
                    )
                }
            }
        }
    }

    // Cleanup on disposal
    DisposableEffect(Unit) {
        onDispose {
            currentJobs.value.first?.cancel()
            currentJobs.value.second?.cancel()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        ConstraintLayout(modifier = modifier) {
            val (circle, stick, indicator, locationName, lottie) = createRefs()

            if (state is YallaMarkerState.Searching) {
                SearchAnimation(
                    modifier = Modifier.constrainAs(lottie) {
                        linkTo(start = circle.start, end = circle.end)
                        linkTo(top = circle.top, bottom = circle.bottom)
                    }
                )
            }

            if (state !is YallaMarkerState.Searching) {
                Indicator(
                    Modifier.constrainAs(indicator) {
                        linkTo(start = parent.start, end = parent.end)
                        linkTo(top = parent.top, bottom = parent.bottom)
                    }
                )

                Stick(
                    modifier = Modifier.constrainAs(stick) {
                        linkTo(start = indicator.start, end = indicator.end)
                        bottom.linkTo(indicator.bottom, margin = jumpOffset.value.dp)
                    }
                )
            }

            Circle(
                color = color,
                state = state,
                rotation = rotation.value,
                modifier = Modifier.constrainAs(circle) {
                    if (state !is YallaMarkerState.Searching) {
                        bottom.linkTo(stick.top)
                        linkTo(start = indicator.start, end = indicator.end)
                    } else {
                        linkTo(start = parent.start, end = parent.end)
                        linkTo(top = parent.top, bottom = parent.bottom)
                    }
                }
            )

            if (state !is YallaMarkerState.Searching) AddressName(
                state = state,
                modifier = Modifier.constrainAs(locationName) {
                    bottom.linkTo(indicator.top, margin = 96.dp)
                    linkTo(start = parent.start, end = parent.end)
                }
            )
        }
    }
}

@Composable
private fun SearchAnimation(
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_search))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        restartOnPlay = true
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .fillMaxSize(.8f)
            .alpha(.5f)
    )
}

@Composable
fun Circle(
    color: Color,
    state: YallaMarkerState,
    rotation: Float,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(color)
            .squareSize(.5f)
            .padding(6.dp)
            .height(IntrinsicSize.Min)
            .width(IntrinsicSize.Min)
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
                color = YallaTheme.color.onPrimary,
                style = YallaTheme.font.title
            )
            Text(
                text = when (state) {
                    is YallaMarkerState.IDLE -> if (state.timeout != null) stringResource(R.string.min) else ""
                    else -> ""
                },
                color = YallaTheme.color.onPrimary,
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
                    painter = painterResource(R.drawable.ic_loading_marker),
                    contentDescription = null,
                    tint = YallaTheme.color.onPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            rotationZ = rotation
                        }
                )
            }
        }

        val shouldShowInnerCircle = when (state) {
            is YallaMarkerState.IDLE -> state.timeout == null
            is YallaMarkerState.Searching -> true
            else -> false
        }

        if (shouldShowInnerCircle) {
            Box(
                modifier = modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(YallaTheme.color.onPrimary)
                    .graphicsLayer { clip = true }
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun Stick(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(20.dp)
            .width(2.dp)
            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .background(YallaTheme.color.onBackground)
    )
}

@Composable
private fun Indicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(6.dp)
            .width(8.dp)
            .clip(CircleShape)
            .background(YallaTheme.color.onBackground)

    )
}

@Composable
private fun AddressName(
    state: YallaMarkerState,
    modifier: Modifier = Modifier
) {
    Surface(
        color = YallaTheme.color.black,
        shape = RoundedCornerShape(25.dp),
        modifier = modifier.padding(horizontal = 60.dp)
    ) {
        AnimatedContent(
            contentAlignment = Alignment.Center,
            targetState = when (state) {
                is YallaMarkerState.IDLE -> state.title
                is YallaMarkerState.Searching -> null
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
                color = YallaTheme.color.onBlack,
                style = YallaTheme.font.labelSemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }
    }
}