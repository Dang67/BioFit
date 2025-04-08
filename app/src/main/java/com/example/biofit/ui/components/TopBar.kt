package com.example.biofit.ui.components

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.biofit.BuildConfig
import com.example.biofit.R
import com.example.biofit.data.model.ChatBotModel
import com.example.biofit.data.model.dto.DailyLogDTO
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.utils.DailyLogSharedPrefsHelper
import com.example.biofit.data.utils.OverviewExerciseSharedPrefsHelper
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.navigation.MainActivity
import com.example.biofit.ui.activity.BackButton
import com.example.biofit.ui.activity.ChatBubble
import com.example.biofit.ui.animated.BlinkingGradientBox
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.AIChatbotViewModel
import com.example.biofit.view_model.ExerciseViewModel
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TopBarScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        TopBar(
            onBackClick = { },
            title = "Title",
            middleButton = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            rightButton = {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "Add button",
                    modifier = Modifier.size(getStandardPadding().first * 1.5f),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            standardPadding = getStandardPadding().first
        )
    }
}

@Composable
fun TopBar(
    onBackClick: (() -> Unit)? = null,
    title: String? = null,
    middleButton: (@Composable () -> Unit)? = null,
    rightButton: (@Composable () -> Unit)? = null,
    standardPadding: Dp
) {
    var showChatbot by remember { mutableStateOf(false) }

    Box {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(0.5f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row {
                        if (onBackClick != null) {
                            BackButton(
                                onBackClick = onBackClick,
                                standardPadding = standardPadding
                            )
                        }

                        Spacer(modifier = Modifier.width(standardPadding))

                        HomeButton(standardPadding)
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        title?.let {
                            Text(
                                text = title,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }

                        middleButton?.invoke()
                    }
                }

                Column(
                    modifier = Modifier.weight(0.5f),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showChatbot = !showChatbot },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_chatbot_ai),
                                contentDescription = stringResource(R.string.ai_assistant_bionix),
                                modifier = Modifier.size(standardPadding * 4f),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        rightButton?.invoke()
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showChatbot,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn() + expandVertically(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut() + shrinkVertically()
        ) {
            val context = LocalContext.current
            val keyboardController = LocalSoftwareKeyboardController.current
            keyboardController?.show()
            val apiKey = BuildConfig.GOOGLE_API_KEY
            val userData = UserSharedPrefsHelper.getUserData(context)
            val dailyWeightData = DailyLogSharedPrefsHelper.getDailyLog(context)
            val exerciseViewModel = ExerciseViewModel()
            val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            exerciseViewModel.fetchOverviewExercises(
                context,
                userData?.userId ?: UserDTO.default().userId,
                userData?.createdAccount ?: UserDTO.default().createdAccount,
                today
            )
            val overviewExerciseData = OverviewExerciseSharedPrefsHelper.getListOverviewExercise(context)
            val mappedExercises = overviewExerciseData?.map { exercise ->
                val levelStr = when (exercise.level) {
                    0 -> stringResource(R.string.amateur)
                    1 -> stringResource(R.string.professional)
                    else -> stringResource(R.string.unknown)
                }

                val intensityStr = when (exercise.intensity) {
                    0 -> stringResource(R.string.low)
                    1 -> stringResource(R.string.medium)
                    2 -> stringResource(R.string.high)
                    else -> stringResource(R.string.unknown)
                }

                val sessionStr = when (exercise.session) {
                    0 -> stringResource(R.string.morning)
                    1 -> stringResource(R.string.afternoon)
                    2 -> stringResource(R.string.evening)
                    else -> stringResource(R.string.unknown)
                }

                "(${stringResource(R.string.exercise)}: ${exercise.exerciseName}, ${stringResource(R.string.level)}: $levelStr, ${stringResource(R.string.intensity)}: $intensityStr, ${stringResource(R.string.time)}: ${exercise.time} ${stringResource(R.string.minutes)}, ${stringResource(R.string.burned_calories)}: ${exercise.burnedCalories} ${stringResource(R.string.kcal)}, ${stringResource(R.string.session)}: $sessionStr, ${stringResource(R.string.day)}: ${exercise.date})"
            }
            val model = ChatBotModel(
                userData = userData ?: UserDTO.default(),
                dailyLogData = dailyWeightData ?: DailyLogDTO.default(),
                exerciseDone = mappedExercises,
                context = context,
                apiKey = apiKey,
            )
            val viewModel = AIChatbotViewModel(model, context)
            var chatHistory by remember { mutableStateOf(viewModel.chatHistory) }
            val scope = rememberCoroutineScope()
            if (chatHistory.isEmpty()) {
                viewModel.sendMessage(
                    userInput = " " + stringResource(R.string.hello) + " ",
                    scope = scope
                )
                chatHistory = viewModel.chatHistory
            }
            val listState = rememberLazyListState()
            val focusRequester = remember { FocusRequester() }
            var userInput by remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                delay(300)
                focusRequester.requestFocus()
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(standardPadding / 2),
                    verticalArrangement = Arrangement.spacedBy(standardPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SubCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                LazyColumn(
                                    state = listState
                                ) {
                                    item {
                                        Spacer(
                                            modifier = Modifier.padding(
                                                bottom = standardPadding * 3.5f
                                            )
                                        )
                                    }

                                    items(chatHistory) { chat ->
                                        if (chat.userMessage != " " + stringResource(R.string.hello) + " ") {
                                            ChatBubble(
                                                text = chat.userMessage,
                                                isUser = true,
                                                standardPadding = standardPadding,
                                            )
                                        }

                                        ChatBubble(
                                            text = chat.botResponse,
                                            isUser = false,
                                            standardPadding = standardPadding,
                                        )
                                    }

                                    item {
                                        Spacer(
                                            modifier = Modifier.padding(
                                                bottom = WindowInsets.safeDrawing.asPaddingValues()
                                                    .calculateBottomPadding()
                                                        + standardPadding * 10
                                            )
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { showChatbot = !showChatbot }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.minus_circle_fill),
                                            contentDescription = stringResource(R.string.close_chat_box),
                                            modifier = Modifier
                                                .size(standardPadding * 2f)
                                                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }

                                    var isRotating by remember { mutableStateOf(false) }
                                    val rotation by animateFloatAsState(
                                        targetValue = if (isRotating) 360f else 0f,
                                        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
                                        finishedListener = {
                                            isRotating = false
                                        } // Reset trạng thái sau khi xoay xong
                                    )

                                    IconButton(
                                        onClick = {
                                            isRotating = true
                                            viewModel.clearChatHistory()
                                            chatHistory = viewModel.chatHistory
                                        },
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_trianglehead_2_clockwise_rotate_90_circle_fill),
                                            contentDescription = stringResource(R.string.refresh_chat),
                                            modifier = Modifier
                                                .size(standardPadding * 2f)
                                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                                .graphicsLayer(rotationZ = rotation),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        text = stringResource(R.string.bionix),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(standardPadding)
                                            .clickable { showChatbot = !showChatbot }
                                            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            shadow = Shadow(
                                                color = MaterialTheme.colorScheme.primary,
                                                blurRadius = 50f
                                            )
                                        )
                                    )
                                }
                            }

                            LaunchedEffect(chatHistory.size) {
                                if (chatHistory.isNotEmpty()) { // Chỉ cuộn nếu có ít nhất 1 tin nhắn
                                    listState.animateScrollToItem(chatHistory.size)
                                }
                            }
                        }
                    }

                    val imeInsets = WindowInsets.ime.getBottom(LocalDensity.current)
                    val keyboardHeight = with(LocalDensity.current) { imeInsets.toDp() }
                    val bottomPadding = if (keyboardHeight > 0.dp) {
                        standardPadding * 2
                    } else {
                        WindowInsets.safeDrawing.asPaddingValues()
                            .calculateBottomPadding()
                            .plus(standardPadding)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            /*.align(Alignment.BottomCenter)*/
                            .padding(bottom = bottomPadding),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BlinkingGradientBox(
                                alpha = 0.75f,
                                shape = MaterialTheme.shapes.extraLarge
                            ) {
                                OutlinedTextField(
                                    value = userInput,
                                    onValueChange = { userInput = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester),
                                    placeholder = {
                                        Text(
                                            text = stringResource(R.string.enter_message),
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    trailingIcon = {
                                        if (userInput != "") {
                                            IconButton(
                                                onClick = {
                                                    /*viewModel.sendMessage(userInput, scope)
                                                    userInput = ""
                                                    keyboardController?.hide()*/
                                                    if (userInput.isNotEmpty()) {
                                                        viewModel.sendMessage(userInput, scope)
                                                        chatHistory = viewModel.chatHistory
                                                        userInput = ""
                                                        keyboardController?.hide()
                                                    }
                                                },
                                                modifier = Modifier.padding(end = standardPadding)
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_send),
                                                    contentDescription = "Send",
                                                    modifier = Modifier.size(standardPadding * 2),
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    },
                                    maxLines = 4,
                                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                                    keyboardActions = KeyboardActions(
                                        /* onDone = {
                     keyboardController?.hide()
                 },*/
                                        onSend = {
                                            if (userInput.isNotEmpty()) {
                                                viewModel.sendMessage(userInput, scope)
                                                chatHistory = viewModel.chatHistory
                                                userInput = ""
                                                keyboardController?.hide()
                                            }
                                        }
                                    ),
                                    shape = MaterialTheme.shapes.large,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar2(
    onBackClick: (() -> Unit)? = null,
    title: String? = null,
    middleButton: (@Composable () -> Unit)? = null,
    rightButton: (@Composable () -> Unit)? = null,
    standardPadding: Dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.Start
        ) {
            Row {
                if (onBackClick != null) {
                    BackButton(
                        onBackClick = onBackClick,
                        standardPadding = standardPadding
                    )
                }

                Spacer(modifier = Modifier.width(standardPadding))
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                title?.let {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                middleButton?.invoke()
            }
        }

        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.End
        ) {
            rightButton?.invoke()
        }
    }
}

@Composable
fun HomeButton(standardPadding: Dp) {
    val context = LocalContext.current
    val activity = context as? Activity

    IconButton(
        onClick = {
            activity?.let {
                val intent = Intent(it, MainActivity::class.java)
                it.startActivity(intent)
            }
        },
        modifier = Modifier.size(standardPadding * 1.5f),
        enabled = true,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_home),
            contentDescription = "Back Button",
            modifier = Modifier.size(standardPadding * 1.5f),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_UNDEFINED)
@Composable
private fun TopBarScreenDarkModePreview() {
    BioFitTheme {
        TopBarScreen()
    }
}

@Preview
@Composable
private fun TopBarScreenPreview() {
    BioFitTheme {
        TopBarScreen()
    }
}
