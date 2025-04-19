package uz.yalla.client.feature.map.presentation.view.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.map.presentation.R
import uz.yalla.client.feature.map.presentation.components.card.UserProfileCard
import uz.yalla.client.feature.map.presentation.components.item.DrawerItem
import uz.yalla.client.feature.profile.domain.model.response.GetMeModel

@Composable
fun MapDrawer(
    user: GetMeModel?,
    drawerState: DrawerState,
    onIntent: (MapDrawerIntent) -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
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
                user?.let {
                    UserProfileCard(
                        client = user.client,
                        onClick = { onIntent(MapDrawerIntent.Profile) }
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
                        painter = painterResource(R.drawable.ic_coin),
                        onClick = { onIntent(MapDrawerIntent.Bonus) }
                    )

                    DrawerItem(
                        title = stringResource(R.string.payment_type),
                        description = when (val paymentType = AppPreferences.paymentType) {
                            is PaymentType.CARD -> paymentType.cardNumber
                            is PaymentType.CASH -> stringResource(R.string.cash)
                        },
                        painter = painterResource(
                            when (val paymentType = AppPreferences.paymentType) {
                                is PaymentType.CARD -> when (paymentType.cardId.length) {
                                    16 -> R.drawable.img_logo_humo
                                    32 -> R.drawable.img_logo_uzcard
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
                        //description = stringResource(R.string.win_x_sums, 5000),
                        painter = painterResource(R.drawable.ic_invite),
                        onClick = {
                            onIntent(
                                MapDrawerIntent.InviteFriend(
                                    title = context.getString(R.string.invite_friends),
                                    url = AppPreferences.inviteFriends
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
                                    url = AppPreferences.becomeDrive
                                )
                            )
                        }
                    )

                    DrawerItem(
                        title = stringResource(R.string.contuct_us),
                        painter = painterResource(R.drawable.ic_contact_us),
                        onClick = { onIntent(MapDrawerIntent.ContactUs) }
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