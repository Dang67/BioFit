package com.example.biofit.ui.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biofit.R
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.ui.components.ItemCard
import com.example.biofit.ui.components.MainCard
import com.example.biofit.ui.components.SelectionDialog
import com.example.biofit.ui.components.SubCard
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.DailyLogViewModel
import com.example.biofit.view_model.LoginViewModel
import com.example.biofit.view_model.UpdateUserViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SettingActivity : ComponentActivity() {
    private var userData: UserDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userData = UserSharedPrefsHelper.getUserData(this)
        setContent {
            BioFitTheme {
                SettingScreen(userData ?: UserDTO.default())
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun SettingScreen(
    userData: UserDTO,
    updateViewModel: UpdateUserViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val standardPadding = getStandardPadding().first
    val modifier = getStandardPadding().second

    val userId = userData.userId

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
                    start = standardPadding,
                    end = standardPadding,
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                onBackClick = { activity?.finish() },
                title = stringResource(R.string.setting),
                middleButton = null,
                rightButton = {
                    TextButton(
                        onClick = {
                            updateViewModel.updateUser(context, userId, loginViewModel) {
                                if (updateViewModel.updatedState.value == true) {
                                    activity?.finish()
                                } else {
                                    Toast.makeText(
                                        context,
                                        updateViewModel.updatedMessage.value,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                standardPadding = standardPadding
            )

            val updatedMessage = updateViewModel.updatedMessage.value
            LaunchedEffect(updatedMessage) {
                updatedMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    updateViewModel.updatedMessage.value = null
                }
            }

            SettingContent(
                userData = userData,
                standardPadding,
                modifier
            )
        }
    }
}

@Composable
fun SettingContent(
    userData: UserDTO,
    standardPadding: Dp,
    modifier: Modifier,
    updateViewModel: UpdateUserViewModel = viewModel(),
    dailyLogViewModel: DailyLogViewModel = viewModel()
) {
    // Lấy dữ liệu thông tin người dùng
    val context = LocalContext.current
    var gender by rememberSaveable {
        mutableStateOf(
            userData.getGenderString(
                context,
                updateViewModel.gender.value
            )
        )
    }
    var dateOfBirth by rememberSaveable { mutableStateOf(updateViewModel.dateOfBirth.value) }
    val height = (userData.height ?: UserDTO.default().height) ?: 0f
    dailyLogViewModel.getLatestDailyLog(context, userData.userId)
    val memoryWeight by produceState(initialValue = 0f, key1 = dailyLogViewModel.memoryWeight) {
        value = if (dailyLogViewModel.memoryWeight.value != 0f) {
            dailyLogViewModel.memoryWeight.value
        } else {
            userData.weight!!
        }
    }
    var createdAccount by rememberSaveable { mutableStateOf(userData.createdAccount) }
    Log.d("createdAccount", createdAccount)
    /*
    ****************************************************************************************************
    */
    // Lấy dữ liệu calo tiêu thụ
    val caloOfDaily = when (userData.gender) {
        0 -> when (userData.getAgeInt(userData.dateOfBirth)) {
            in 0..45 -> 2000f
            else -> 1500f
        }

        1 -> when (userData.getAgeInt(userData.dateOfBirth)) {
            in 0..30 -> 1500f
            else -> 1200f
        }

        else -> 0f
    }

    val caloOfWeekly = caloOfDaily * 7

    val caloOfDailyBMR = BigDecimal(
        ((10 * memoryWeight) + (6.25f * height) - (5 * userData.getAgeInt(userData.dateOfBirth)) +
                if (userData.gender == 0) 5 else -161
                ).toDouble()
    )
        .setScale(2, RoundingMode.HALF_UP)
        .toFloat()

    val caloOfWeeklyTDEE = BigDecimal(caloOfDailyBMR.toDouble() * 1.55)
        .setScale(2, RoundingMode.HALF_UP)
        .toFloat()
    /*
    ***************************************************************************************************
    */
    // Đặt các trạng thái cho các trường dữ liệu
    val focusManager = LocalFocusManager.current
    var showGenderDialog by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var isEmailVisible by remember { mutableStateOf(false) }
    val interactionSources = remember { List(5) { MutableInteractionSource() } }
    interactionSources.forEach { source ->
        val isPressed by source.collectIsPressedAsState()
        if (isPressed) {
            showGenderDialog = false
        }
    }
    LaunchedEffect(Unit) {
        if (updateViewModel.fullName.value == null) {
            updateViewModel.fullName.value = userData.fullName
        }
        if (updateViewModel.gender.value == null) {
            updateViewModel.gender.value = userData.gender
        }
        if (updateViewModel.dateOfBirth.value == null) {
            updateViewModel.dateOfBirth.value = userData.dateOfBirth
        }
        if (updateViewModel.height.value == null) {
            updateViewModel.height.value = userData.height
        }
        if (updateViewModel.weight.value == null) {
            updateViewModel.weight.value = userData.weight
        }
        if (updateViewModel.email.value == null) {
            updateViewModel.email.value = userData.email
        }
    }
    var showBMRPopup by remember { mutableStateOf(false) }
    var showTDEEPopup by remember { mutableStateOf(false) }
    var showCalorieIntakePopup by remember { mutableStateOf(false) }
    /*
    ***************************************************************************************************
    */
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(standardPadding * 2)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = standardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.my_profile),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(standardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = updateViewModel.fullName.value ?: "",
                    onValueChange = { updateViewModel.fullName.value = it },
                    modifier = modifier.shadow(
                        elevation = 6.dp,
                        shape = MaterialTheme.shapes.large
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.End
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.enter_your_full_name),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_person),
                            contentDescription = stringResource(R.string.name),
                            modifier = Modifier.size(standardPadding * 1.5f),
                            tint = Color(0xFF2962FF)
                        )
                    },
                    prefix = { Text(text = stringResource(R.string.name)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    interactionSource = interactionSources[0],
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )

                ItemCard(
                    onClick = {
                        showGenderDialog = !showGenderDialog
                        focusManager.clearFocus()
                    },
                    modifier = modifier
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = standardPadding / 4
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                showGenderDialog = !showGenderDialog
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.figure_stand_dress_line_vertical_figure),
                                contentDescription = stringResource(R.string.gender),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = Color(0xFFC51162)
                            )
                        }

                        Spacer(modifier = Modifier.width(standardPadding / 2))

                        Text(
                            text = stringResource(R.string.gender),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Text(
                            text = if (updateViewModel.gender.value == null) {
                                stringResource(R.string.select_gender)
                            } else {
                                userData.getGenderString(context, updateViewModel.gender.value)
                            },
                            modifier = Modifier.weight(1f),
                            color = if (updateViewModel.gender.value == null) {
                                MaterialTheme.colorScheme.outline
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            },
                            textAlign = TextAlign.End
                        )

                        IconButton(
                            onClick = {
                                showGenderDialog = !showGenderDialog
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = stringResource(R.string.gender),
                                modifier = Modifier
                                    .size(standardPadding)
                                    .rotate(if (showGenderDialog) 90f else 270f),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showGenderDialog,
                        enter = slideInVertically { it } + fadeIn() + expandVertically(),
                        exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
                    ) {
                        val listOptions = listOf(
                            stringResource(R.string.male),
                            stringResource(R.string.female)
                        )

                        Column {
                            listOptions.forEach { selectGender ->
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.1f
                                    )
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            gender = selectGender
                                            updateViewModel.gender.value = userData.getGenderInt(
                                                context,
                                                selectGender
                                            )
                                            showGenderDialog = false
                                        },
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = selectGender,
                                        modifier = Modifier.padding(standardPadding),
                                    )
                                }
                            }
                        }
                    }
                }

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                ItemCard(
                    onClick = {
                        showDatePicker = true
                        showGenderDialog = false
                        focusManager.clearFocus()
                    },
                    modifier = modifier
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = standardPadding / 4)
                            .padding(end = standardPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                showDatePicker = true
                                showGenderDialog = false
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.birthday_cake_fill),
                                contentDescription = stringResource(R.string.gender),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = Color(0xFF6200EA)
                            )
                        }

                        Spacer(modifier = Modifier.width(standardPadding / 2))

                        Text(
                            text = stringResource(R.string.date_of_birth),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Text(
                            text = updateViewModel.dateOfBirth.value?.let { dateString ->
                                try {
                                    val parsedDate =
                                        dateFormat.parse(dateString)
                                    dateFormat.format(parsedDate ?: UserDTO.default().dateOfBirth)
                                } catch (e: Exception) {
                                    stringResource(R.string.select_date_of_birth)
                                }
                            } ?: stringResource(R.string.select_date_of_birth),
                            modifier = Modifier.weight(1f),
                            color = if (updateViewModel.dateOfBirth.value == null) {
                                MaterialTheme.colorScheme.outline
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            },
                            textAlign = TextAlign.End
                        )
                    }
                }

                if (showDatePicker) {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, selectedYear, selectedMonth, selectedDay ->
                            calendar.set(selectedYear, selectedMonth, selectedDay)

                            dateOfBirth = dateFormat.format(calendar.time)

                            updateViewModel.dateOfBirth.value = dateOfBirth
                            showDatePicker = false
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                    showDatePicker = false
                }

                OutlinedTextField(
                    value = updateViewModel.height.value.toString(),
                    onValueChange = { updateViewModel.height.value = it.toFloatOrNull() ?: 0f },
                    modifier = modifier.shadow(
                        elevation = 6.dp,
                        shape = MaterialTheme.shapes.large
                    ),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.figure_stand),
                            contentDescription = stringResource(R.string.height),
                            modifier = Modifier.size(standardPadding * 1.5f),
                            tint = Color(0xFF00C853)
                        )
                    },
                    prefix = { Text(text = stringResource(R.string.height)) },
                    suffix = { Text(text = stringResource(R.string.cm)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    interactionSource = interactionSources[1],
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )

                OutlinedTextField(
                    value = updateViewModel.weight.value.toString(),
                    onValueChange = { updateViewModel.weight.value = it.toFloatOrNull() ?: 0f },
                    modifier = modifier.shadow(
                        elevation = 6.dp,
                        shape = MaterialTheme.shapes.large
                    ),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.scalemass),
                            contentDescription = stringResource(R.string.starting_weight),
                            modifier = Modifier.size(standardPadding * 1.5f),
                            tint = Color(0xFFFF6D00)
                        )
                    },
                    prefix = { Text(text = stringResource(R.string.starting_weight)) },
                    suffix = { Text(text = stringResource(R.string.kg)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    interactionSource = interactionSources[2],
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )

                OutlinedTextField(
                    value = createdAccount,
                    onValueChange = { createdAccount = it },
                    modifier = modifier.shadow(
                        elevation = 6.dp,
                        shape = MaterialTheme.shapes.large
                    ),
                    readOnly = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.calendar),
                            contentDescription = stringResource(R.string.account_creation_date),
                            modifier = Modifier.size(standardPadding * 1.5f),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    prefix = { Text(text = stringResource(R.string.account_creation_date)) },
                    singleLine = true,
                    interactionSource = interactionSources[4],
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                    )
                )

                ItemCard(
                    onClick = {
                        isEmailVisible = !isEmailVisible
                        focusManager.clearFocus()
                    },
                    modifier = modifier
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = standardPadding / 4
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                isEmailVisible = !isEmailVisible
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.person_crop_square_filled_and_at_rectangle_fill),
                                contentDescription = stringResource(R.string.email),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = Color(0xFF00BFA5)
                            )
                        }

                        Spacer(modifier = Modifier.width(standardPadding / 2))

                        Text(
                            text = stringResource(R.string.email),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Text(
                            text = updateViewModel.email.value ?: "",
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = standardPadding)
                                .blur(
                                    radius = if (isEmailVisible) 0.dp else 10.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SubCard(
                    modifier = modifier
                ) {
                    Column(
                        modifier = Modifier.padding(standardPadding),
                        verticalArrangement = Arrangement.spacedBy(standardPadding),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.calorie_intake_target),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge
                            )

                            IconButton(
                                onClick = {
                                    showCalorieIntakePopup = !showCalorieIntakePopup
                                    showGenderDialog = false
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.info_circle),
                                    contentDescription = stringResource(R.string.calorie_intake_target),
                                    modifier = Modifier.size(standardPadding * 1.5f),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = showCalorieIntakePopup,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.large)
                                .shadow(
                                    elevation = 10.dp,
                                    ambientColor = MaterialTheme.colorScheme.onSurface,
                                    spotColor = MaterialTheme.colorScheme.onSurface
                                )
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            enter = slideInVertically { it } + fadeIn() + expandVertically(),
                            exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = stringResource(R.string.calorie_intake_target_des),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Justify,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        val textWithIcon = buildAnnotatedString {
                            append(
                                stringResource(
                                    R.string.estimate_calories_needed_for_daily_activities
                                ) + " "
                            )
                            appendInlineContent("fireIcon", "[icon]")
                        }

                        val inlineContent = mapOf(
                            "fireIcon" to InlineTextContent(
                                placeholder = Placeholder(
                                    width = 16.sp,
                                    height = 16.sp,
                                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.flame_fill),
                                    contentDescription = stringResource(R.string.calorie_intake_target),
                                    modifier = Modifier.size(standardPadding * 2f),
                                    tint = Color(0xFFDD2C00)
                                )
                            }
                        )

                        Text(
                            text = textWithIcon,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outline,
                            inlineContent = inlineContent,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            modifier = Modifier.padding(top = standardPadding),
                            horizontalArrangement = Arrangement.spacedBy(standardPadding),
                        ) {
                            MainCard(
                                modifier = Modifier.weight(1f)
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = standardPadding,
                                            vertical = standardPadding * 2
                                        ),
                                    verticalArrangement = Arrangement.spacedBy(standardPadding),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_loaded_cal),
                                        contentDescription = stringResource(R.string.calorie_intake_target),
                                        modifier = Modifier.size(standardPadding * 1.5f),
                                    )

                                    Text(
                                        text = caloOfDaily.toInt().toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.headlineSmall
                                    )

                                    Text(
                                        text = stringResource(R.string.kcal_day),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }

                            MainCard(
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = standardPadding,
                                            vertical = standardPadding * 2
                                        ),
                                    verticalArrangement = Arrangement.spacedBy(standardPadding),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.bolt_fill),
                                        contentDescription = stringResource(R.string.calorie_intake_target),
                                        modifier = Modifier.size(standardPadding * 1.5f),
                                    )

                                    Text(
                                        text = caloOfWeekly.toInt().toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.headlineSmall
                                    )

                                    Text(
                                        text = stringResource(R.string.kcal_week),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.basal_metabolic_rate_bmr),
                                modifier = Modifier.weight(0.4f),
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.bodySmall
                            )

                            TextButton(
                                onClick = {
                                    showBMRPopup = !showBMRPopup
                                    showGenderDialog = false
                                },
                                modifier = Modifier.weight(0.3f),
                            ) {
                                Text(
                                    text = stringResource(R.string.learn_more),
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Text(
                                text = caloOfDailyBMR.toString() + " " +
                                        stringResource(R.string.kcal_day),
                                modifier = Modifier.weight(0.3f),
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        AnimatedVisibility(
                            visible = showBMRPopup,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.large)
                                .shadow(
                                    elevation = 10.dp,
                                    ambientColor = MaterialTheme.colorScheme.onSurface,
                                    spotColor = MaterialTheme.colorScheme.onSurface
                                )
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            enter = slideInVertically { it } + fadeIn() + expandVertically(),
                            exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = stringResource(R.string.bmr_des),
                                modifier = Modifier.padding(standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Justify,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.total_energy_expenditure_tdee),
                                modifier = Modifier.weight(0.4f),
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.bodySmall
                            )

                            TextButton(
                                onClick = {
                                    showTDEEPopup = !showTDEEPopup
                                    showGenderDialog = false
                                },
                                modifier = Modifier.weight(0.3f),
                            ) {
                                Text(
                                    text = stringResource(R.string.learn_more),
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Text(
                                text = caloOfWeeklyTDEE.toString() + " " +
                                        stringResource(R.string.kcal_week),
                                modifier = Modifier.weight(0.3f),
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        AnimatedVisibility(
                            visible = showTDEEPopup,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.large)
                                .shadow(
                                    elevation = 10.dp,
                                    ambientColor = MaterialTheme.colorScheme.onSurface,
                                    spotColor = MaterialTheme.colorScheme.onSurface
                                )
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            enter = slideInVertically { it } + fadeIn() + expandVertically(),
                            exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = stringResource(R.string.tdee_des),
                                modifier = Modifier.padding(standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Justify,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(
                modifier = Modifier.padding(
                    bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding() * 2
                            + standardPadding
                )
            )
        }
    }
}

@Preview(
    device = "id:pixel",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    locale = "vi"
)
@Composable
private fun SettingPortraitScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        SettingScreen(UserDTO.default())
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SettingPortraitScreenPreviewInLargePhone() {
    BioFitTheme {
        SettingScreen(UserDTO.default())
    }
}

@Preview(
    device = "spec:parent=Nexus 10,orientation=portrait",
    locale = "vi",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SettingPortraitScreenPreviewInTablet() {
    BioFitTheme {
        SettingScreen(UserDTO.default())
    }
}

@Preview(
    device = "spec:parent=pixel,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    locale = "vi"
)
@Composable
private fun SettingLandscapeScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        SettingScreen(UserDTO.default())
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SettingLandscapeScreenPreviewInLargePhone() {
    BioFitTheme {
        SettingScreen(UserDTO.default())
    }
}

@Preview(
    device = "spec:parent=Nexus 10",
    locale = "vi",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SettingLandscapeScreenPreviewInTablet() {
    BioFitTheme {
        SettingScreen(UserDTO.default())
    }
}