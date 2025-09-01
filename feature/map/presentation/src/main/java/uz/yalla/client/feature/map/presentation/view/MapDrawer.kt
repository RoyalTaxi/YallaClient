package uz.yalla.client.feature.map.presentation.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.koin.compose.koinInject
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.core.domain.model.Client
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.map.presentation.R
import uz.yalla.client.feature.map.presentation.components.card.DefaultUserProfileCard
import uz.yalla.client.feature.map.presentation.components.card.UserProfileCard
import uz.yalla.client.feature.map.presentation.components.item.DrawerItem
import uz.yalla.client.feature.map.presentation.intent.MapDrawerIntent
import kotlin.math.roundToInt

@Composable
fun NavigationDrawer(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    client: Client?,
    notificationsCount: Int = 0,
    onIntent: (MapDrawerIntent) -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.8f

    val targetOffset = if (isOpen) 0f else -with(density) { drawerWidth.toPx() }
    val animatedOffset by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 300),
        label = "drawer_offset"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (isOpen) 0.5f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "overlay_alpha"
    )

    var dragOffset by remember { mutableFloatStateOf(0f) }
    var initialDragOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            dragOffset = 0f
            initialDragOffset = 0f
        }
    }

    BackHandler(isOpen) { onDismiss() }

    if (isOpen || animatedOffset > -with(density) { drawerWidth.toPx() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1000f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isOpen) onDismiss()
                    }
            )

            Surface(
                modifier = Modifier
                    .width(drawerWidth)
                    .fillMaxHeight()
                    .offset { IntOffset((animatedOffset + dragOffset).roundToInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                initialDragOffset = dragOffset
                            },
                            onDragEnd = {
                                if (dragOffset < -200) {
                                    onDismiss()
                                } else {
                                    dragOffset = initialDragOffset
                                }
                            }
                        ) { _, dragAmount ->
                            val newOffset = dragOffset + dragAmount
                            if (newOffset <= 0) {
                                dragOffset = newOffset
                            }
                        }
                    },
                color = YallaTheme.color.surface,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ) {
                DrawerContent(
                    client = client,
                    notificationsCount = notificationsCount,
                    onIntent = onIntent,
                    modifier = Modifier.statusBarsPadding()
                )
            }
        }
    }
}

@Composable
private fun DrawerContent(
    client: Client?,
    notificationsCount: Int,
    onIntent: (MapDrawerIntent) -> Unit,
    modifier: Modifier = Modifier,
    appPreferences: AppPreferences = koinInject(),
    staticPreferences: StaticPreferences = koinInject()
) {
    val context = LocalContext.current
    val paymentType by appPreferences.paymentType.collectAsState(initial = PaymentType.CASH)
    val inviteUrl by appPreferences.inviteFriends.collectAsState(initial = "")
    val driveUrl by appPreferences.becomeDrive.collectAsState(initial = "")

    Column(modifier = modifier) {
        if (staticPreferences.isDeviceRegistered) client?.let {
            UserProfileCard(
                client = client,
                onClick = { onIntent(MapDrawerIntent.Profile) }
            )
        } else {
            DefaultUserProfileCard {
                onIntent(MapDrawerIntent.RegisterDevice)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(YallaTheme.color.background)
        ) {
            DrawerItem(
                title = stringResource(R.string.orders_history),
                painter = painterResource(R.drawable.ic_order_history),
                onClick = { onIntent(MapDrawerIntent.OrdersHistory) }
            )

            DrawerItem(
                title = stringResource(R.string.my_places),
                painter = painterResource(R.drawable.ic_addresses),
                onClick = { onIntent(MapDrawerIntent.MyPlaces) }
            )

            DrawerItem(
                title = stringResource(R.string.bonus_and_discounts),
                description = stringResource(
                    R.string.you_have_x_bonus,
                    client?.balance.or0().toString()
                ),
                painter = painterResource(R.drawable.ic_coin),
                tintColor = Color.Unspecified,
                onClick = { onIntent(MapDrawerIntent.Bonus) }
            )

            DrawerItem(
                title = stringResource(R.string.payment_type),
                description = when (paymentType) {
                    is PaymentType.CARD -> (paymentType as PaymentType.CARD).cardNumber
                    is PaymentType.CASH -> stringResource(R.string.cash)
                },
                painter = painterResource(
                    when (paymentType) {
                        is PaymentType.CARD -> when ((paymentType as PaymentType.CARD).cardId.length) {
                            16 -> R.drawable.ic_humo
                            32 -> R.drawable.ic_uzcard
                            else -> uz.yalla.client.feature.order.presentation.R.drawable.ic_money
                        }

                        is PaymentType.CASH -> R.drawable.ic_money
                    }
                ),
                tintColor = when (paymentType) {
                    is PaymentType.CARD -> when ((paymentType as PaymentType.CARD).cardId.length) {
                        16 -> Color.Unspecified
                        32 -> Color.Unspecified
                        else -> YallaTheme.color.onBackground
                    }

                    is PaymentType.CASH -> YallaTheme.color.onBackground
                },
                onClick = { onIntent(MapDrawerIntent.PaymentType) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(YallaTheme.color.background)
        ) {
            DrawerItem(
                title = stringResource(R.string.invite_friends),
                painter = painterResource(R.drawable.ic_invite),
                onClick = {
                    onIntent(
                        MapDrawerIntent.InviteFriend(
                            title = context.getString(R.string.invite_friends),
                            url = inviteUrl
                        )
                    )
                }
            )

            DrawerItem(
                title = stringResource(R.string.become_a_driver),
                painter = painterResource(R.drawable.ic_driver),
                onClick = {
                    onIntent(
                        MapDrawerIntent.BecomeADriver(
                            title = context.getString(R.string.become_a_driver),
                            url = driveUrl
                        )
                    )
                }
            )

            DrawerItem(
                title = stringResource(R.string.contuct_us),
                painter = painterResource(R.drawable.ic_contact_us),
                onClick = { onIntent(MapDrawerIntent.ContactUs) }
            )

            DrawerItem(
                title = stringResource(R.string.notifications),
                painter = painterResource(R.drawable.ic_bell),
                onClick = { onIntent(MapDrawerIntent.Notifications) },
                trailingIcon = {
                    if (notificationsCount != 0) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(YallaTheme.color.red)
                                .size(14.dp)
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(YallaTheme.color.background)
                .weight(1f)
        ) {
            DrawerItem(
                title = stringResource(R.string.settings),
                painter = painterResource(R.drawable.ic_setting_line),
                onClick = { onIntent(MapDrawerIntent.Settings) }
            )

            DrawerItem(
                title = stringResource(R.string.about_app),
                painter = painterResource(R.drawable.ic_info),
                onClick = { onIntent(MapDrawerIntent.AboutTheApp) }
            )
        }
    }
}
