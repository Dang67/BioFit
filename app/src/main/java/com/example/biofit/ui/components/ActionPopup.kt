package com.example.biofit.ui.components

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biofit.R
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.utils.DailyLogSharedPrefsHelper
import com.example.biofit.ui.activity.AIChatbotActivity
import com.example.biofit.ui.activity.AddActivity
import com.example.biofit.ui.activity.ExerciseActivity
import com.example.biofit.ui.activity.UpdateWeightActivity
import com.example.biofit.ui.animated.BlinkingGradientBox
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.DailyLogViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ActionPopup(
    userData: UserDTO,
    dailyLogViewModel: DailyLogViewModel = viewModel(),
    onDismissPopup: () -> Unit,
    standardPadding: Dp
) {
    val context = LocalContext.current
    val activity = context as? Activity


    val itemPopupList = listOf(
        Pair(R.drawable.ic_exercise_2, R.string.exercise),
        Pair(R.drawable.ic_weight, R.string.weight),
        /*Pair(R.drawable.ic_drink_water, R.string.drinking_water)*/
    )
    val sessionPopupList = listOf(
        Pair(R.drawable.ic_morning_2, R.string.morning),
        Pair(R.drawable.ic_afternoon_2, R.string.afternoon),
        Pair(R.drawable.ic_evening_2, R.string.evening),
        Pair(R.drawable.ic_snack_2, R.string.snack)
    )

    /*var drinkWaterPopupState by rememberSaveable { mutableStateOf(false) }*/

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(standardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*if (!drinkWaterPopupState) {*/
                Row(
                    modifier = Modifier.padding(
                        top = standardPadding,
                        start = standardPadding / 2,
                        end = standardPadding / 2
                    )
                ) {
                    itemPopupList.forEach { (icon, title) ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(MaterialTheme.shapes.large)
                                .clickable {
                                    when (title) {
                                        R.string.exercise ->
                                            activity?.let {
                                                val intent =
                                                    Intent(it, ExerciseActivity::class.java)
                                                it.startActivity(intent)
                                            }

                                        R.string.weight ->
                                            activity?.let {
                                                val intent =
                                                    Intent(it, UpdateWeightActivity::class.java)
                                                it.startActivity(intent)
                                            }

                                        /*R.string.drinking_water ->
                                            drinkWaterPopupState = true*/
                                    }

                                    /*if (title != R.string.drinking_water) {
                                        onDismissPopup()
                                    }*/

                                    onDismissPopup()
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            /*Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(standardPadding / 2)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                        shape = MaterialTheme.shapes.large
                                    ),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            )*/
                            MainCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(standardPadding / 2)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(icon),
                                        contentDescription = stringResource(R.string.activity),
                                        modifier = Modifier
                                            .padding(standardPadding)
                                            .size(standardPadding * 2),
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            }

                            Text(
                                text = stringResource(title),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = standardPadding,
                                        end = standardPadding,
                                        bottom = standardPadding / 2
                                    ),
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.large)
                            .clickable {
                                val intent = Intent(context, AIChatbotActivity::class.java)
                                context.startActivity(intent)

                                onDismissPopup()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.padding(standardPadding / 2)
                        ) {
                            BlinkingGradientBox(
                                alpha = 0.5f,
                                shape = MaterialTheme.shapes.extraLarge
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_chat_bot),
                                        contentDescription = stringResource(R.string.activity),
                                        modifier = Modifier
                                            .padding(standardPadding)
                                            .size(standardPadding * 2),
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.padding(
                                start = standardPadding,
                                end = standardPadding,
                                bottom = standardPadding / 2
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.ai_assistant_bionix),
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(
                        start = standardPadding / 2,
                        end = standardPadding / 2,
                        bottom = standardPadding
                    )
                ) {
                    sessionPopupList.forEach { (icon, title) ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(MaterialTheme.shapes.large)
                                .clickable {
                                    activity?.let {
                                        val intent = Intent(it, AddActivity::class.java)
                                        intent.putExtra(
                                            "SESSION_TITLE", when (title) {
                                                R.string.morning -> R.string.morning
                                                R.string.afternoon -> R.string.afternoon
                                                R.string.evening -> R.string.evening
                                                else -> R.string.snack
                                            }
                                        )
                                        it.startActivity(intent)
                                    }

                                    onDismissPopup()
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            /*Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(standardPadding / 2)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                        shape = MaterialTheme.shapes.large
                                    ),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            )*/
                            SubCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(standardPadding / 2)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(icon),
                                        contentDescription = stringResource(R.string.activity),
                                        modifier = Modifier
                                            .padding(standardPadding)
                                            .size(standardPadding * 2),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Text(
                                text = stringResource(title),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = standardPadding,
                                        end = standardPadding,
                                        bottom = standardPadding / 2
                                    ),
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            /*} else {
                val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val targetWater = 2f
                dailyLogViewModel.getLatestDailyLog(context, userData.userId)
                val memoryWater by produceState(
                    initialValue = 0f,
                    key1 = dailyLogViewModel.memoryWater
                ) {
                    value = dailyLogViewModel.memoryWater.value
                }
                val needWater = targetWater - memoryWater
                LaunchedEffect(Unit) {
                    if (dailyLogViewModel.water.value == null) {
                        dailyLogViewModel.water.value = memoryWater
                    }
                }

                var currentWater by rememberSaveable { mutableFloatStateOf(0f) }

                Column(
                    modifier = Modifier.padding(
                        top = standardPadding / 2,
                        start = standardPadding / 2,
                        end = standardPadding / 2
                    )
                ) {
                    Text(
                        text = stringResource(R.string.drinking_water),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(standardPadding),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displaySmall
                    )

                    Text(
                        text = if (DailyLogSharedPrefsHelper.getDailyLog(context)?.date != today) {
                            stringResource(R.string.you_need_to_drink_another) +
                                    " $targetWater " +
                                    stringResource(R.string.l_of_water_to_complete_your_goal)
                        } else {
                            if (memoryWater < targetWater) {
                                stringResource(R.string.you_need_to_drink_another) +
                                        " $needWater " +
                                        stringResource(R.string.l_of_water_to_complete_your_goal)
                            } else {
                                stringResource(R.string.congratulations_on_completing_your_water_drinking_goal_today)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = standardPadding,
                                end = standardPadding,
                                bottom = standardPadding
                            ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary)

                Column(
                    modifier = Modifier.padding(
                        start = standardPadding / 2,
                        end = standardPadding / 2,
                        bottom = standardPadding / 2
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val waterQuantity = currentWater
                                if (waterQuantity > 0) {
                                    currentWater = BigDecimal(currentWater.toDouble())
                                        .subtract(BigDecimal(0.1))
                                        .setScale(1, RoundingMode.HALF_UP)
                                        .toFloat()
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_less_update_weight),
                                contentDescription = stringResource(R.string.drinking_water),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        OutlinedTextField(
                            value = currentWater.toString(),
                            onValueChange = { input ->
                                currentWater = input.toFloat()
                            },
                            modifier = Modifier
                                .widthIn(min = 10.dp, max = standardPadding * 10)
                                .width(IntrinsicSize.Min),
                            textStyle = MaterialTheme.typography.displaySmall,
                            suffix = {
                                Text(
                                    text = "L",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.displaySmall
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(
                                onDone = { TODO() },
                                onGo = { TODO() },
                                onNext = { TODO() },
                                onPrevious = { TODO() },
                                onSearch = { TODO() },
                                onSend = { TODO() }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        IconButton(
                            onClick = {
                                currentWater = BigDecimal(currentWater.toDouble())
                                    .add(BigDecimal(0.1))
                                    .setScale(1, RoundingMode.HALF_UP)
                                    .toFloat()
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add_update_weight),
                                contentDescription = stringResource(R.string.drinking_water),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    var showDrinkingWaterFailed by rememberSaveable { mutableStateOf(false) }
                    if (showDrinkingWaterFailed) {
                        DefaultDialog(
                            title = R.string.you_have_not_drank_water,
                            description = R.string.because_the_amount_of_water_you_just_entered_is_0_l,
                            actionTextButton = R.string.ok,
                            actionTextButtonColor = MaterialTheme.colorScheme.primary,
                            onClickActionButton = { showDrinkingWaterFailed = false },
                            onDismissRequest = { showDrinkingWaterFailed = false },
                            standardPadding = standardPadding
                        )
                    }

                    val oldDatePrefs = DailyLogSharedPrefsHelper.getDailyLog(context)?.date

                    Button(
                        onClick = {
                            if (currentWater > 0) {
                                dailyLogViewModel.water.value = if (oldDatePrefs == today) {
                                    memoryWater + currentWater
                                } else {
                                    currentWater
                                }
                                dailyLogViewModel.saveDailyLog(context, userData.userId)
                                Toast.makeText(context, R.string.well_done, Toast.LENGTH_SHORT)
                                    .show()
                                onDismissPopup()
                            } else {
                                showDrinkingWaterFailed = true
                            }
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }*/
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_UNDEFINED)
@Composable
private fun ActionPopupDarkModePreview() {
    BioFitTheme {
        AnimatedVisibility(
            visible = true,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            enter = slideInVertically { it } + fadeIn() + expandVertically(),
            exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
        ) {
            ActionPopup(
                userData = UserDTO(
                    userId = 0,
                    fullName = "John",
                    email = "john@email.com",
                    gender = 0,
                    height = 180f,
                    weight = 70f,
                    targetWeight = 75f,
                    dateOfBirth = "2004-01-01",
                    avatar = "",
                    createdAccount = "2025-01-01"
                ),
                onDismissPopup = { },
                standardPadding = getStandardPadding().first
            )
        }
    }
}

@Preview
@Composable
private fun ActionPopupPreview() {
    BioFitTheme {
        AnimatedVisibility(
            visible = true,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            enter = slideInVertically { it } + fadeIn() + expandVertically(),
            exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
        ) {
            ActionPopup(
                userData = UserDTO(
                    userId = 0,
                    fullName = "John",
                    email = "john@email.com",
                    gender = 0,
                    height = 180f,
                    weight = 70f,
                    targetWeight = 75f,
                    dateOfBirth = "2004-01-01",
                    avatar = "",
                    createdAccount = "2025-01-01"
                ),
                onDismissPopup = { },
                standardPadding = getStandardPadding().first
            )
        }
    }
}