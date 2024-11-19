package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.item.DrawerItem

@Composable
fun MapDrawer(
    uiState: MapUIState,
    drawerState: DrawerState,
    onIntent: (MapDrawerIntent) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp),
                drawerContainerColor = YallaTheme.color.gray2,
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

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
                        title = stringResource(R.string.bonuses_and_discounts),
                        description = stringResource(R.string.you_have_x_sums, 70_000),
                        painter = painterResource(R.drawable.ic_discount),
                        onClick = { onIntent(MapDrawerIntent.BonusesAndDiscounts) }
                    )

                    DrawerItem(
                        title = stringResource(R.string.payment_type),
                        description = stringResource(R.string.cash),
                        painter = painterResource(R.drawable.img_money),
                        onClick = { onIntent(MapDrawerIntent.PaymentType) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    DrawerItem(
                        title = stringResource(R.string.invite_friends),
                        description = stringResource(R.string.win_x_sums, 5000),
                        painter = painterResource(R.drawable.ic_invite),
                        onClick = { onIntent(MapDrawerIntent.InviteFriend) }
                    )

                    DrawerItem(
                        title = stringResource(R.string.become_a_driver),
                        painter = painterResource(R.drawable.ic_driver),
                        onClick = { onIntent(MapDrawerIntent.BecomeADriver) }
                    )

                    DrawerItem(
                        title = stringResource(R.string.contuct_us),
                        painter = painterResource(R.drawable.ic_contact_us),
                        onClick = { onIntent(MapDrawerIntent.ContactUs) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

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

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    ) { content() }
}