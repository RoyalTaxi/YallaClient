package uz.yalla.client.feature.map.presentation.view.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.map.presentation.R
import uz.yalla.client.feature.map.presentation.components.card.DefaultUserProfileCard
import uz.yalla.client.feature.map.presentation.components.card.UserProfileCard
import uz.yalla.client.feature.map.presentation.components.item.DrawerItem
import uz.yalla.client.feature.profile.domain.model.response.GetMeModel

@Composable
fun MapDrawer(
    user: GetMeModel?,
    drawerState: DrawerState,
    notificationsCount: Int = 0,
    bonusAmount: Int = 0,
    onIntent: (MapDrawerIntent) -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val prefs = koinInject<AppPreferences>()
    val paymentType by prefs.paymentType.collectAsState(initial = PaymentType.CASH)
    val inviteUrl by prefs.inviteFriends.collectAsState(initial = "")
    val driveUrl by prefs.becomeDrive.collectAsState(initial = "")
    val isDeviceRegistered by prefs.isDeviceRegistered.collectAsState(initial = true)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp),
                drawerContainerColor = YallaTheme.color.gray2,
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                if (isDeviceRegistered) user?.let {
                    UserProfileCard(
                        client = user.client,
                        onClick = { onIntent(MapDrawerIntent.Profile) }
                    )
                } else DefaultUserProfileCard {
                    onIntent(MapDrawerIntent.RegisterDevice)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(YallaTheme.color.white)
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
                            bonusAmount.toString()
                        ),
                        painter = painterResource(R.drawable.ic_coin),
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
                        onClick = { onIntent(MapDrawerIntent.PaymentType) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(YallaTheme.color.white)
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
                        .clip(RoundedCornerShape(30.dp))
                        .background(YallaTheme.color.white)
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
    ) { content() }
}