package com.example.biofit.ui.activity

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biofit.R
import com.example.biofit.data.model.dto.ExerciseDTO
import com.example.biofit.data.model.dto.ExerciseDetailDTO
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.ui.components.DefaultDialog
import com.example.biofit.ui.components.ItemCard
import com.example.biofit.ui.components.SelectionDialog
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.ExerciseViewModel

class UpdateExerciseActivity : ComponentActivity() {
    private var exerciseDTO: ExerciseDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        exerciseDTO = intent.getParcelableExtra("exerciseDTO")
        setContent {
            BioFitTheme {
                UpdateExerciseScreen(exerciseDTO = exerciseDTO ?: ExerciseDTO.default())
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun UpdateExerciseScreen(
    exerciseDTO: ExerciseDTO,
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val standardPadding = getStandardPadding().first
    val modifier = getStandardPadding().second

    var exerciseName by rememberSaveable { mutableStateOf(exerciseDTO.exerciseName) }
    var time by rememberSaveable { mutableStateOf("") }
    var caloriesConsumed by rememberSaveable { mutableStateOf("") }

    val exerciseDetailList by exerciseViewModel.exerciseDetail.collectAsState()

    val exerciseGoal = exerciseDetailList?.exerciseGoal
    val intensity = exerciseDetailList?.intensity
    Log.d("UpdateExerciseScreen", "exerciseGoal: $exerciseGoal, intensity: $intensity")

    LaunchedEffect(exerciseDetailList) {
        exerciseDetailList?.let {
            time = it.time.toString()
            caloriesConsumed = it.burnedCalories.toString()
        }
    }

    var showUpdateErrorDialog by rememberSaveable { mutableStateOf(false) }
    if (showUpdateErrorDialog) {
        DefaultDialog(
            title = R.string.exercises_not_updated_yet,
            description = R.string.des_update_exercise,
            actionTextButton = R.string.ok,
            actionTextButtonColor = MaterialTheme.colorScheme.primary,
            onClickActionButton = { showUpdateErrorDialog = false },
            onDismissRequest = { showUpdateErrorDialog = false },
            standardPadding = standardPadding
        )
    }

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
                title = stringResource(R.string.edit_exercise),
                middleButton = null,
                rightButton = {
                    TextButton(
                        onClick = {
                            if (exerciseGoal == 0 && intensity == 0) {
                                val updatedExercise = exerciseDTO.copy(
                                    exerciseName = exerciseName,
                                    detailList = listOf(
                                        ExerciseDetailDTO(
                                            exerciseDetailId = 0L,
                                            exerciseId = exerciseDTO.exerciseId,
                                            exerciseGoal = 0,
                                            intensity = 0,
                                            time = time.toFloatOrNull() ?: 0f,
                                            burnedCalories = caloriesConsumed.toFloatOrNull() ?: 0f
                                        )
                                    )
                                )
                                exerciseViewModel.updateExercise(updatedExercise)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.exercise_updated_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()
                                activity?.finish()
                            } else {
                                showUpdateErrorDialog = true
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

            UpdateExerciseContent(
                exerciseName = exerciseName,
                onExerciseNameChange = { exerciseName = it },
                time = time,
                onTimeChange = { time = it },
                caloriesConsumed = caloriesConsumed,
                onCaloriesConsumedChange = { caloriesConsumed = it },
                exerciseDTO = exerciseDTO,
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun UpdateExerciseContent(
    exerciseName: String,
    onExerciseNameChange: (String) -> Unit,
    time: String,
    onTimeChange: (String) -> Unit,
    caloriesConsumed: String,
    onCaloriesConsumedChange: (String) -> Unit,
    exerciseDTO: ExerciseDTO,
    standardPadding: Dp,
    modifier: Modifier,
    exerciseViewModel: ExerciseViewModel = viewModel(),
) {
    val context = LocalContext.current
    val exerciseId = exerciseDTO.exerciseId
    Log.d("CAUExerciseContent", "exerciseId: $exerciseId")
    val exerciseDetailList by exerciseViewModel.exerciseDetail.collectAsState()
    Log.d("CAUExerciseContent", "exerciseDetail: $exerciseDetailList")

    var level by rememberSaveable { mutableStateOf(value = "") }
    var showLevelDialog by rememberSaveable { mutableStateOf(value = false) }
    var intensity by rememberSaveable { mutableStateOf(value = "") }
    var showIntensityDialog by rememberSaveable { mutableStateOf(value = false) }

    LaunchedEffect(Unit) {
        exerciseViewModel.fetchExerciseDetails(
            exerciseId,
            exerciseDTO.getExerciseGoalInt(context, level),
            exerciseDTO.getIntensityInt(context, intensity)
        )
        Log.d(
            "CAUExerciseContent",
            "exerciseGoal: ${exerciseDTO.getExerciseGoalInt(context, level)}"
        )
        Log.d("CAUExerciseContent", "intensity: ${exerciseDTO.getIntensityInt(context, intensity)}")
    }

    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(standardPadding)
    ) {
        OutlinedTextField(
            value = exerciseName,
            onValueChange = onExerciseNameChange,
            modifier = modifier,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.End
            ),
            placeholder = {
                Text(
                    text = stringResource(R.string.exercise_name),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            prefix = {
                Text(
                    text = stringResource(R.string.exercise),
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            supportingText = {
                Text(
                    text = stringResource(R.string.des_update_exercise),
                    textAlign = TextAlign.Justify,
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        ItemCard(
            onClick = { showLevelDialog = true },
            modifier = modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = standardPadding,
                        vertical = standardPadding / 4
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (level == "") {
                        stringResource(R.string.select_level)
                    } else {
                        level
                    },
                    modifier = Modifier.weight(1f),
                    color = if (level == "") {
                        MaterialTheme.colorScheme.outline
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    style = MaterialTheme.typography.bodySmall
                )

                IconButton(onClick = { showLevelDialog = true }) {
                    Image(
                        painter = painterResource(R.drawable.btn_back),
                        contentDescription = stringResource(R.string.level),
                        modifier = Modifier.rotate(270f)
                    )
                }
            }
        }

        if (showLevelDialog) {
            SelectionDialog(
                selectedOption = level,
                onOptionSelected = { selectedLevel ->
                    level = selectedLevel
                    showLevelDialog = false
                    exerciseViewModel.fetchExerciseDetails(
                        exerciseId,
                        exerciseDTO.getExerciseGoalInt(context, level),
                        exerciseDTO.getIntensityInt(context, intensity)
                    )
                },
                onDismissRequest = { showLevelDialog = false },
                title = R.string.select_level,
                listOptions = listOf(
                    stringResource(R.string.amateur),
                    stringResource(R.string.professional)
                ),
                standardPadding = standardPadding
            )
        }

        ItemCard(
            onClick = { showIntensityDialog = true },
            modifier = modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = standardPadding,
                        vertical = standardPadding / 4
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (intensity == "") {
                        stringResource(R.string.select_intensity)
                    } else {
                        intensity
                    },
                    modifier = Modifier.weight(1f),
                    color = if (intensity == "") {
                        MaterialTheme.colorScheme.outline
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    style = MaterialTheme.typography.bodySmall
                )

                IconButton(onClick = { showIntensityDialog = true }) {
                    Image(
                        painter = painterResource(R.drawable.btn_back),
                        contentDescription = stringResource(R.string.intensity),
                        modifier = Modifier.rotate(270f)
                    )
                }
            }
        }

        if (showIntensityDialog) {
            SelectionDialog(
                selectedOption = intensity,
                onOptionSelected = { selectedIntensity ->
                    intensity = selectedIntensity
                    showIntensityDialog = false
                    exerciseViewModel.fetchExerciseDetails(
                        exerciseId,
                        exerciseDTO.getExerciseGoalInt(context, level),
                        exerciseDTO.getIntensityInt(context, intensity)
                    )
                },
                onDismissRequest = { showIntensityDialog = false },
                title = R.string.select_intensity,
                listOptions = listOf(
                    stringResource(R.string.low),
                    stringResource(R.string.medium),
                    stringResource(R.string.high)
                ),
                standardPadding = standardPadding
            )
        }

        OutlinedTextField(
            value = time,
            onValueChange = onTimeChange,
            modifier = modifier,
            enabled = (level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low)),
            readOnly = !(level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low)),
            textStyle = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.End
            ),
            prefix = {
                Text(
                    text = stringResource(R.string.time),
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            suffix = {
                Text(
                    text = stringResource(R.string.min),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        OutlinedTextField(
            value = caloriesConsumed,
            onValueChange = onCaloriesConsumedChange,
            modifier = modifier,
            enabled = (level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low)),
            readOnly = !(level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low)),
            textStyle = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.End
            ),
            prefix = {
                Text(
                    text = stringResource(R.string.calories_consumed),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            suffix = {
                Text(
                    text = stringResource(R.string.kcal),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = { TODO() }),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )
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
private fun CAUExerciseScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        UpdateExerciseScreen(ExerciseDTO.default())
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun CAUExerciseScreenPreviewInLargePhone() {
    BioFitTheme {
        UpdateExerciseScreen(ExerciseDTO.default())
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
private fun CAUExerciseScreenPreviewInTablet() {
    BioFitTheme {
        UpdateExerciseScreen(ExerciseDTO.default())
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
private fun CAUExerciseScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        UpdateExerciseScreen(ExerciseDTO.default())
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun CAUExerciseScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        UpdateExerciseScreen(ExerciseDTO.default())
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
private fun CAUExerciseScreenLandscapePreviewInTablet() {
    BioFitTheme {
        UpdateExerciseScreen(ExerciseDTO.default())
    }
}