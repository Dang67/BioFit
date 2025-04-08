package com.example.biofit.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.edit
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biofit.BuildConfig
import com.example.biofit.R
import com.example.biofit.data.model.ChatBotModel
import com.example.biofit.data.model.dto.DailyLogDTO
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.utils.DailyLogSharedPrefsHelper
import com.example.biofit.data.utils.OverviewExerciseSharedPrefsHelper
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.data.utils.UserSharedPrefsHelper.getUserId
import com.example.biofit.ui.activity.LoginActivity
import com.example.biofit.ui.components.ActionPopup
import com.example.biofit.ui.components.BottomBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.screen.HomeScreen
import com.example.biofit.ui.screen.KnowledgeScreen
import com.example.biofit.ui.screen.PlanningScreen
import com.example.biofit.ui.screen.ProfileScreen
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.AIChatbotViewModel
import com.example.biofit.view_model.ExerciseViewModel
import com.example.biofit.view_model.SubscriptionViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    private val viewModel: SubscriptionViewModel by viewModels()
    private var userData: UserDTO? = null

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "USER_DATA") {
            userData = UserSharedPrefsHelper.getUserData(this)
            setContent {
                BioFitTheme {
                    MainScreen(userData ?: UserDTO.default())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefsListener)
        val shouldShowDialog = UserSharedPrefsHelper.shouldShowCongratulationsDialog(this)

        userData = UserSharedPrefsHelper.getUserData(this)

        // nếu gói hết hạn hiển thị thông báo
        val userId = getUserId(this)
        viewModel.checkSubscriptionStatus(userId) { isExpired ->
            if (isExpired) {
                Toast.makeText(this, R.string.check_subscription, Toast.LENGTH_LONG).show()
            }
        }

        setContent {
            BioFitTheme {
                MainScreen(userData ?: UserDTO.default())

                val context = LocalContext.current
                var showCongratulationsDialog by remember { mutableStateOf(shouldShowDialog) }

                // Hiển thị dialog nếu cần
                if (showCongratulationsDialog) {
                    CongratulationsDialog(
                        onDismiss = {
                            showCongratulationsDialog = false
                            // Xóa trạng thái để không hiển thị lại dialog
                            UserSharedPrefsHelper.clearShowCongratulationsDialog(this@MainActivity)
                        },
                        onLogin = {
                            showCongratulationsDialog = false

                            val apiKey = BuildConfig.GOOGLE_API_KEY
                            val userData = UserSharedPrefsHelper.getUserData(context)
                            val dailyWeightData = DailyLogSharedPrefsHelper.getDailyLog(context)
                            val exerciseViewModel = ExerciseViewModel()
                            val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            exerciseViewModel.fetchOverviewExercises(
                                this,
                                userData?.userId ?: UserDTO.default().userId,
                                userData?.createdAccount ?: UserDTO.default().createdAccount,
                                today
                            )
                            val overviewExerciseData = OverviewExerciseSharedPrefsHelper.getListOverviewExercise(this)
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
                                context = context,
                                apiKey = apiKey,
                            )
                            val viewModel = AIChatbotViewModel(model, context)
                            viewModel.clearChatHistory() // Xóa lịch sử chat

                            val activity = context as? Activity
                            val userSharedPreferences = activity?.getSharedPreferences("UserPrefs",
                                MODE_PRIVATE
                            )
                            val dailyLogSharedPreferences = activity?.getSharedPreferences("DailyLogPrefs",
                                MODE_PRIVATE
                            )
                            val overviewExerciseSharedPreferences = activity?.getSharedPreferences("OverviewExercisePrefs",
                                MODE_PRIVATE
                            )

                            // Xóa dữ liệu đăng nhập (SharedPreferences)
                            userSharedPreferences?.edit { clear() }
                            dailyLogSharedPreferences?.edit { clear() }
                            overviewExerciseSharedPreferences?.edit { clear() }

                            // Xóa trạng thái để không hiển thị lại dialog
                            UserSharedPrefsHelper.clearShowCongratulationsDialog(this@MainActivity)

                            // Chuyển hướng tới trang Login
                            val intent = Intent(this, LoginActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

}

@Composable
fun MainScreen(userData: UserDTO) {
    val standardPadding = getStandardPadding().first

    val navController = rememberNavController()
    var showPopup by remember { mutableStateOf(false) }

    val home = stringResource(id = R.string.home)
    val plan = stringResource(id = R.string.plan)
    val knowledge = stringResource(id = R.string.knowledge)
    val profile = stringResource(id = R.string.profile)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                    /*.padding(
                        top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
                        start = standardPadding,
                        end = standardPadding,
                    )*/,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainContent(
                        userData,
                        navController = navController
                    )
                }
            }

            Row {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    AnimatedVisibility(
                        visible = showPopup,
                        modifier = Modifier
                            .shadow(
                                elevation = 10.dp,
                                ambientColor = MaterialTheme.colorScheme.onBackground,
                                spotColor = MaterialTheme.colorScheme.onBackground
                            )
                            .background(MaterialTheme.colorScheme.background),
                        enter = slideInVertically { it } + fadeIn() + expandVertically(),
                        exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
                    ) {
                        ActionPopup(
                            userData = userData,
                            onDismissPopup = { showPopup = false },
                            standardPadding = standardPadding
                        )
                    }

                    BottomBar(
                        onItemSelected = { label ->
                            when (label) {
                                home -> navController.navigate("home")
                                plan -> navController.navigate("plan")
                                "Add" -> showPopup = !showPopup
                                knowledge -> navController.navigate("knowledge")
                                profile -> navController.navigate("profile")
                            }
                        },
                        showPopup = showPopup,
                        onDismissPopup = { showPopup = false },
                        standardPadding = standardPadding
                    )
                }
            }
        }
    }
}

@Composable
fun MainContent(
    userData: UserDTO,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(userData) }
        composable("plan") { PlanningScreen() }
        composable("knowledge") { KnowledgeScreen() }
        composable("profile") { ProfileScreen(userData) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CongratulationsDialog(onDismiss: () -> Unit, onLogin: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false // nhấp ngoài không mất dialog
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_circle),
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .height(40.dp)
                        .size(60.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = stringResource(id = R.string.congratulations_dialog),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.content_dialog),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.loginnow_dialog),
                        style = MaterialTheme.typography.titleMedium
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
private fun MainScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        MainScreen(UserDTO.default())
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MainScreenPreviewInLargePhone() {
    BioFitTheme {
        MainScreen(UserDTO.default())
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
private fun MainScreenPreviewInTablet() {
    BioFitTheme {
        MainScreen(UserDTO.default())
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
private fun MainLandscapeScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        MainScreen(UserDTO.default())
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MainLandscapeScreenPreviewInLargePhone() {
    BioFitTheme {
        MainScreen(UserDTO.default())
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
private fun MainLandscapeScreenPreviewInTablet() {
    BioFitTheme {
        MainScreen(UserDTO.default())
    }
}