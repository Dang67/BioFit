package com.example.biofit.ui.activity

import android.R.attr.onClick
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.biofit.BuildConfig
import com.example.biofit.R
import com.example.biofit.data.model.ChatBotModel
import com.example.biofit.data.model.dto.DailyLogDTO
import com.example.biofit.data.model.dto.OverviewExerciseDTO
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.utils.ChatPreferencesHelper
import com.example.biofit.data.utils.DailyLogSharedPrefsHelper
import com.example.biofit.data.utils.OverviewExerciseSharedPrefsHelper
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.ui.animated.AnimatedGradientText
import com.example.biofit.ui.animated.BlinkingGradientBox
import com.example.biofit.ui.animated.OneTimeAnimatedGradientText
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.TopBar2
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.AIChatbotViewModel
import com.example.biofit.view_model.ExerciseViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AIChatbotActivity : ComponentActivity() {
    private lateinit var chatViewModel: AIChatbotViewModel
    private var userData: UserDTO? = null
    private var dailyWeightData: DailyLogDTO? = null
    private var overviewExerciseData: List<OverviewExerciseDTO>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val apiKey = BuildConfig.GOOGLE_API_KEY
        /*val googleApiKey = BuildConfig.GOOGLE_SEARCH_API_KEY
        val searchEngineId = BuildConfig.SEARCH_ENGINE_ID*/
        userData = UserSharedPrefsHelper.getUserData(this)
        dailyWeightData = DailyLogSharedPrefsHelper.getDailyLog(this)
        val exerciseViewModel = ExerciseViewModel()
        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        exerciseViewModel.fetchOverviewExercises(
            this,
            userData?.userId ?: UserDTO.default().userId,
            userData?.createdAccount ?: UserDTO.default().createdAccount,
            today
        )
        overviewExerciseData = OverviewExerciseSharedPrefsHelper.getListOverviewExercise(this)
        val mappedExercises = overviewExerciseData?.map { exercise ->
            val levelStr = when (exercise.level) {
                0 -> this.getString(R.string.amateur)
                1 -> this.getString(R.string.professional)
                else -> this.getString(R.string.unknown)
            }

            val intensityStr = when (exercise.intensity) {
                0 -> this.getString(R.string.low)
                1 -> this.getString(R.string.medium)
                2 -> this.getString(R.string.high)
                else -> this.getString(R.string.unknown)
            }

            val sessionStr = when (exercise.session) {
                0 -> this.getString(R.string.morning)
                1 -> this.getString(R.string.afternoon)
                2 -> this.getString(R.string.evening)
                else -> this.getString(R.string.unknown)
            }

            "(${this.getString(R.string.exercise)}: ${exercise.exerciseName}, ${this.getString(R.string.level)}: $levelStr, ${this.getString(R.string.intensity)}: $intensityStr, ${this.getString(R.string.time)}: ${exercise.time} ${this.getString(R.string.minutes)}, ${this.getString(R.string.burned_calories)}: ${exercise.burnedCalories} ${this.getString(R.string.kcal)}, ${this.getString(R.string.session)}: $sessionStr, ${this.getString(R.string.day)}: ${exercise.date})"
        }
        val model = ChatBotModel(
            userData = userData ?: UserDTO.default(),
            dailyLogData = dailyWeightData ?: DailyLogDTO.default(),
            exerciseDone = mappedExercises,
            context = this,
            apiKey = apiKey,
            /*googleApiKey = googleApiKey,
            searchEngineId = searchEngineId*/
        )
        chatViewModel = AIChatbotViewModel(model, this)
        setContent {
            BioFitTheme {
                AIChatbotScreen(viewModel = chatViewModel)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun AIChatbotScreen(viewModel: AIChatbotViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.show()

    val standardPadding = getStandardPadding().first

    var chatHistory by remember { mutableStateOf(viewModel.chatHistory) }
    val scope = rememberCoroutineScope()
    if (chatHistory.isEmpty()) {
        viewModel.sendMessage(
            userInput = " " + stringResource(R.string.hello) + " ",
            scope = scope
        )
        chatHistory = viewModel.chatHistory
    }
    val focusRequester = remember { FocusRequester() }
    var userInput by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    val listState = rememberLazyListState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
                    start = standardPadding,
                    end = standardPadding,
                    bottom = standardPadding
                ),
            verticalArrangement = Arrangement.spacedBy(standardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar2(
                onBackClick = { activity?.finish() },
                title = stringResource(R.string.ai_assistant_bionix),
                middleButton = null,
                rightButton = {
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
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_trianglehead_2_clockwise_rotate_90_circle_fill),
                            contentDescription = stringResource(R.string.refresh_chat),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(standardPadding * 2f)
                                .graphicsLayer(rotationZ = rotation)
                        )
                    }
                },
                standardPadding = standardPadding
            )

            LazyColumn(
                state = listState
            ) {
                items(chatHistory) { chat ->
                    if (chat.userMessage != " " + stringResource(R.string.hello) + " ") {
                        ChatBubble(
                            text = chat.userMessage,
                            isUser = true,
                            standardPadding = standardPadding
                        )
                    }

                    ChatBubble(
                        text = chat.botResponse,
                        isUser = false,
                        standardPadding = standardPadding
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

            LaunchedEffect(chatHistory.size) {
                if (chatHistory.isNotEmpty()) { // Chỉ cuộn nếu có ít nhất 1 tin nhắn
                    listState.animateScrollToItem(chatHistory.size)
                }
            }
        }

        val imeInsets = WindowInsets.ime.getBottom(LocalDensity.current)
        val keyboardHeight = with(LocalDensity.current) { imeInsets.toDp() }
        val bottomPadding = if (keyboardHeight > 0.dp) {
            standardPadding * 2 // Thêm khoảng cách 16dp giữa nội dung và bàn phím
        } else {
            WindowInsets.safeDrawing.asPaddingValues()
                .calculateBottomPadding()
                .plus(standardPadding) // Khoảng cách mặc định khi bàn phím chưa mở
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = standardPadding,
                    end = standardPadding,
                    bottom = bottomPadding
                ),
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
                        modifier = Modifier.focusRequester(focusRequester),
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

@Composable
fun ChatBubble(
    text: String,
    isUser: Boolean,
    standardPadding: Dp,
) {
    val context = LocalContext.current
    val isAnimationFinished = remember { mutableStateOf(ChatPreferencesHelper.hasMessageBeenAnimated(context, text)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(standardPadding),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isUser) {
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    } else {
                        Color.Transparent
                    },
                    shape = MaterialTheme.shapes.extraLarge.copy(
                        bottomEnd = CornerSize(15f)
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    } else {
                        Color.Transparent
                    },
                    shape = MaterialTheme.shapes.extraLarge.copy(
                        bottomEnd = CornerSize(15f)
                    )
                )
                .padding(if (isUser) standardPadding else 0.dp)
        ) {
            if (isUser) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                if (!isAnimationFinished.value) {
                    if (text == stringResource(R.string.composing_a_message)) {
                        AnimatedGradientText(
                            highlightColor = Color(0xFFAEEA00),
                            textBodyColor1 = MaterialTheme.colorScheme.primary,
                            textBodyColor2 = MaterialTheme.colorScheme.primary,
                            text = text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        OneTimeAnimatedGradientText(
                            highlightColor = MaterialTheme.colorScheme.primary,
                            baseColor = MaterialTheme.colorScheme.onBackground,
                            hideColor = Color.Transparent,
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            onAnimationEnd = {
                                isAnimationFinished.value = true
                                ChatPreferencesHelper.markMessageAsAnimated(context, text)
                            }
                        )
                    }
                } else {
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
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
private fun BioAIChatbotScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        lateinit var chatBotViewModel: AIChatbotViewModel
        val model = ChatBotModel(
            userData = UserDTO.default(),
            dailyLogData = DailyLogDTO.default(),
            exerciseDone = null,
            context = LocalContext.current,
            apiKey = BuildConfig.GOOGLE_API_KEY
        )
        chatBotViewModel = AIChatbotViewModel(model, LocalContext.current)
        AIChatbotScreen(viewModel = chatBotViewModel)
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun BioAIChatbotScreenPreviewInLargePhone() {
    BioFitTheme {
        lateinit var chatBotViewModel: AIChatbotViewModel
        val model = ChatBotModel(
            userData = UserDTO.default(),
            dailyLogData = DailyLogDTO.default(),
            exerciseDone = null,
            context = LocalContext.current,
            apiKey = BuildConfig.GOOGLE_API_KEY
        )
        chatBotViewModel = AIChatbotViewModel(model, LocalContext.current)
        AIChatbotScreen(viewModel = chatBotViewModel)
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
private fun BioAIChatbotScreenPreviewInTablet() {
    BioFitTheme {
        lateinit var chatBotViewModel: AIChatbotViewModel
        val model = ChatBotModel(
            userData = UserDTO.default(),
            dailyLogData = DailyLogDTO.default(),
            exerciseDone = null,
            context = LocalContext.current,
            apiKey = BuildConfig.GOOGLE_API_KEY
        )
        chatBotViewModel = AIChatbotViewModel(model, LocalContext.current)
        AIChatbotScreen(viewModel = chatBotViewModel)
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
private fun BioAIChatbotScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        lateinit var chatBotViewModel: AIChatbotViewModel
        val model = ChatBotModel(
            userData = UserDTO.default(),
            dailyLogData = DailyLogDTO.default(),
            exerciseDone = null,
            context = LocalContext.current,
            apiKey = BuildConfig.GOOGLE_API_KEY
        )
        chatBotViewModel = AIChatbotViewModel(model, LocalContext.current)
        AIChatbotScreen(viewModel = chatBotViewModel)
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun BioAIChatbotScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        lateinit var chatBotViewModel: AIChatbotViewModel
        val model = ChatBotModel(
            userData = UserDTO.default(),
            dailyLogData = DailyLogDTO.default(),
            exerciseDone = null,
            context = LocalContext.current,
            apiKey = BuildConfig.GOOGLE_API_KEY
        )
        chatBotViewModel = AIChatbotViewModel(model, LocalContext.current)
        AIChatbotScreen(viewModel = chatBotViewModel)
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
private fun BioAIChatbotScreenLandscapePreviewInTablet() {
    BioFitTheme {
        lateinit var chatBotViewModel: AIChatbotViewModel
        val model = ChatBotModel(
            userData = UserDTO.default(),
            dailyLogData = DailyLogDTO.default(),
            exerciseDone = null,
            context = LocalContext.current,
            apiKey = BuildConfig.GOOGLE_API_KEY
        )
        chatBotViewModel = AIChatbotViewModel(model, LocalContext.current)
        AIChatbotScreen(viewModel = chatBotViewModel)
    }
}
