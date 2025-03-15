package com.example.biofit.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.biofit.R
import com.example.biofit.navigation.WeekNavigationBar
import com.example.biofit.ui.components.OverviewExerciseCard
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import java.time.LocalDate

class OverviewExerciseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BioFitTheme {
                OverviewExerciseScreen()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun OverviewExerciseScreen() {
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
                title = stringResource(R.string.exercise),
                middleButton = null,
                rightButton = {
                    IconButton(
                        onClick = {
                            activity?.let {
                                val intent = Intent(it, UpdateExerciseActivity::class.java)
                                intent.putExtra("EXERCISE", "")
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
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun OverviewExerciseContent(
    standardPadding: Dp,
    modifier: Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(standardPadding)
    ) {
        WeekNavigationBar(
            selectedDate = selectedDate,
            onDateChange = { newDate -> selectedDate = newDate },
            standardPadding
        )

        LazyColumn {
            item {
                Column(
                    modifier = Modifier.padding(top = standardPadding),
                    verticalArrangement = Arrangement.spacedBy(standardPadding)
                ) {
                    listOverviewExercise.forEach { (exerciseName, time, calories) ->
                        OverviewExerciseCard(
                            exerciseName = exerciseName,
                            time = time,
                            calories = calories,
                            onClick = {
                                activity?.let {
                                    val intent =
                                        Intent(it, UpdateExerciseActivity::class.java)
                                    intent.putExtra("EXERCISE", exerciseName)
                                    it.startActivity(intent)
                                }
                            },
                            standardPadding = standardPadding

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

val listOverviewExercise = listOf(
    Triple("Jumping Jacks", 10, 100f),
    Triple("Push-ups", 15, 150f),
    Triple("Squats", 12, 120f),
    Triple("Lunges", 10, 90f),
    Triple("Burpees", 8, 140f),
    Triple("Plank", 5, 50f),
    Triple("Mountain Climbers", 7, 110f),
    Triple("High Knees", 6, 95f),
    Triple("Jump Rope", 10, 130f),
    Triple("Bicycle Crunches", 12, 105f)
)


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
        OverviewExerciseScreen()
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
        OverviewExerciseScreen()
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
        OverviewExerciseScreen()
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
        OverviewExerciseScreen()
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
        OverviewExerciseScreen()
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
        OverviewExerciseScreen()
    }
}