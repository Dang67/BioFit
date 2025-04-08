package com.example.biofit.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biofit.R
import com.example.biofit.data.model.dto.OverviewExerciseDTO
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.navigation.WeekNavigationBar
import com.example.biofit.ui.components.DefaultDialog
import com.example.biofit.ui.components.OverviewExerciseCard
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.ExerciseViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

class OverviewExerciseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userId = intent.getLongExtra("USER_ID", 0L)
        setContent {
            BioFitTheme {
                OverviewExerciseScreen(userId = userId)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun OverviewExerciseScreen(userId: Long) {
    val context = LocalContext.current
    val activity = context as? Activity

    val standardPadding = getStandardPadding().first
    val modifier = getStandardPadding().second

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
                    start = standardPadding,
                    end = standardPadding,
                ),
            verticalArrangement = Arrangement.spacedBy(standardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                onBackClick = { activity?.finish() },
                title = stringResource(R.string.exercise_done),
                middleButton = null,
                rightButton = {
                    IconButton(
                        onClick = {
                            activity?.let {
                                val intent = Intent(it, ExerciseActivity::class.java)
                                it.startActivity(intent)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_plus),
                            contentDescription = "Add button",
                            modifier = Modifier.size(standardPadding * 1.5f),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                standardPadding = standardPadding
            )

            OverviewExerciseContent(
                userId = userId,
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun OverviewExerciseContent(
    userId: Long,
    standardPadding: Dp,
    modifier: Modifier,
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatterForShow = DateTimeFormatter.ofPattern(
        if (Locale.getDefault().language == "vi")
            "EEEE, 'ngày' d 'tháng' M 'năm' yyyy"
        else
            "EEEE, MMMM d, yyyy"
    )
    val startOfWeek = selectedDate.with(
        TemporalAdjusters.previousOrSame(
            if (Locale.getDefault().language == "vi") DayOfWeek.MONDAY else DayOfWeek.SUNDAY
        )
    )
    val startOfWeekFormatted = startOfWeek.format(formatter)
    val endOfWeek = startOfWeek.plusDays(6)
    val endOfWeekFormatted = endOfWeek.format(formatter)

    val overviewList by exerciseViewModel.overviewExerciseList.collectAsState()

    Log.d("userId", "$userId")
    LaunchedEffect(userId, startOfWeekFormatted, endOfWeekFormatted) {
        exerciseViewModel.fetchOverviewExercises(context, userId, startOfWeekFormatted, endOfWeekFormatted)
    }
    Log.d("listOverviewExercise", "$overviewList")

    Column(
        modifier = modifier,
    ) {
        WeekNavigationBar(
            selectedDate = selectedDate,
            onDateChange = { newDate -> selectedDate = newDate },
            standardPadding
        )

        // Quản lý trạng thái thẻ đang mở bằng cặp (date, index)
        val expandedState = remember { mutableStateOf<Pair<String?, Int?>>(null to null) }

        LazyColumn {
            if (overviewList == emptyList<OverviewExerciseDTO>()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (selectedDate != LocalDate.now()) {
                            Text(
                                text = stringResource(R.string.you_haven_t_done_any_exercises_during_this_time_period),
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.titleSmall
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.you_haven_t_done_any_exercises_this_week),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = stringResource(R.string.des_do_exercise),
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )

                            TextButton(
                                onClick = {
                                    activity?.let {
                                        val intent = Intent(it, ExerciseActivity::class.java)
                                        it.startActivity(intent)
                                    }
                                },
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_plus),
                                        contentDescription = stringResource(R.string.do_exercise),
                                        modifier = Modifier.size(standardPadding),
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = stringResource(R.string.do_exercise),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                val groupedExercises = overviewList
                    .sortedByDescending { it.date }
                    .groupBy { it.date }

                groupedExercises.forEach { (date, exerciseDones) ->
                    val localeDate = LocalDate.parse(date, formatter)
                    val showDate = localeDate.format(formatterForShow)
                    item {
                        Text(
                            text = showDate,
                            modifier = modifier.padding(top = standardPadding * 2),
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    itemsIndexed(exerciseDones) { index, exerciseDone ->
                        Spacer(modifier = Modifier.padding(top = standardPadding))

                        OverviewExerciseCard(
                            onDeleteClick = {},
                            startOfWeek = startOfWeek,
                            exerciseName = exerciseDone.exerciseName,
                            level = exerciseDone.level,
                            intensity = exerciseDone.intensity,
                            time = exerciseDone.time,
                            calories = exerciseDone.burnedCalories,
                            session = exerciseDone.session,
                            standardPadding = standardPadding,
                            isExpanded = expandedState.value == (date to index), // So sánh cặp (date, index)
                            onExpandChange = { isExpanded ->
                                expandedState.value = if (isExpanded) (date to index) else (null to null)
                            }
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.padding(
                        bottom = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateBottomPadding() * 2
                                + standardPadding
                    )
                )
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
private fun OverviewExerciseScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        OverviewExerciseScreen(UserDTO.default().userId)
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun OverviewExerciseScreenPreviewInLargePhone() {
    BioFitTheme {
        OverviewExerciseScreen(UserDTO.default().userId)
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
private fun OverviewExerciseScreenPreviewInTablet() {
    BioFitTheme {
        OverviewExerciseScreen(UserDTO.default().userId)
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
private fun OverviewExerciseScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        OverviewExerciseScreen(UserDTO.default().userId)
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun OverviewExerciseScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        OverviewExerciseScreen(UserDTO.default().userId)
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
private fun OverviewExerciseScreenLandscapePreviewInTablet() {
    BioFitTheme {
        OverviewExerciseScreen(UserDTO.default().userId)
    }
}