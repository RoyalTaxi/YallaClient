package uz.ildam.technologies.yalla.android.ui.screens.credentials

import DatePickerBottomSheet
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.components.button.GenderButton
import uz.ildam.technologies.yalla.android.components.button.YallaButton
import uz.ildam.technologies.yalla.android.components.textfield.YallaTextField
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.utils.Utils.formatWithDashesDMY

data class CredentialsScreen(
    val number: String,
    val secretKey: String
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<CredentialsModel>()
        val uiState by screenModel.uiState.collectAsState()
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current as Activity
        val scope = rememberCoroutineScope()
        val state = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        )

        BackHandler(onBack = context::finish)

        LaunchedEffect(Unit) {
            screenModel.updateNumber(number)
            screenModel.updateSecretKey(secretKey)
        }

        BottomSheetScaffold(
            sheetDragHandle = null,
            sheetSwipeEnabled = false,
            scaffoldState = state,
            containerColor = YallaTheme.color.white,
            sheetContainerColor = YallaTheme.color.gray2,
            sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            sheetContent = {
                DatePickerBottomSheet(
                    startDate = uiState.dateOfBirth ?: LocalDate.now(),
                    onSelectDate = screenModel::updateDateOfBirth,
                    onDismissRequest = { scope.launch { state.bottomSheetState.hide() } }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(YallaTheme.color.white)
                        .padding(20.dp)
                        .systemBarsPadding()
                ) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        item { Spacer(modifier = Modifier.height(80.dp)) }

                        item {
                            Text(
                                text = stringResource(id = R.string.lets_meet),
                                color = YallaTheme.color.black,
                                style = YallaTheme.font.headline
                            )
                        }

                        item { Spacer(modifier = Modifier.height(20.dp)) }

                        item {
                            Text(
                                text = stringResource(id = R.string.lets_meet_desc),
                                color = YallaTheme.color.gray,
                                style = YallaTheme.font.body
                            )
                        }

                        item { Spacer(modifier = Modifier.height(20.dp)) }

                        item {
                            YallaTextField(
                                text = uiState.name,
                                onChangeText = screenModel::updateName,
                                placeHolderText = stringResource(id = R.string.name),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item { Spacer(modifier = Modifier.height(10.dp)) }

                        item {
                            YallaTextField(
                                text = uiState.surname,
                                onChangeText = screenModel::updateSurname,
                                placeHolderText = stringResource(id = R.string.surname)
                            )
                        }

                        item { Spacer(modifier = Modifier.height(10.dp)) }

                        item {
                            YallaTextField(
                                text = uiState.dateOfBirth?.formatWithDashesDMY() ?: "",
                                placeHolderText = stringResource(id = R.string.date_of_birth),
                                onChangeText = {},
                                trailingIcon = painterResource(id = R.drawable.ic_calendar),
                                onClick = {
                                    scope.launch { state.bottomSheetState.show() }
                                    focusManager.clearFocus(true)
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(10.dp)) }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GenderButton(
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(id = R.string.gender_m),
                                    isSelected = uiState.gender == "MALE",
                                    onSelect = { screenModel.updateGender("MALE") }
                                )

                                GenderButton(
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(id = R.string.gender_f),
                                    isSelected = uiState.gender == "FEMALE",
                                    onSelect = { screenModel.updateGender("FEMALE") }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    YallaButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.next),
                        onClick = screenModel::register,
                        enabled = uiState.buttonEnabled
                    )
                }

                AnimatedVisibility(
                    visible = state.bottomSheetState.isVisible,
                    enter = fadeIn(initialAlpha = 0.3f),
                    exit = fadeOut(targetAlpha = 1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable(
                                onClick = { scope.launch { state.bottomSheetState.hide() } },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    )
                }
            }
        )
    }
}