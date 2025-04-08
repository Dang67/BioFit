package com.example.biofit.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Icon
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.biofit.ui.components.DefaultDialog
import com.example.biofit.ui.components.ItemCard
import com.example.biofit.ui.components.SelectionDialog
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.animatedRotation
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.ExerciseViewModel

class UpdateExerciseActivity : ComponentActivity() {
    private var exerciseDTO: ExerciseDTO? = null
    private var title: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        exerciseDTO = intent.getParcelableExtra("exerciseDTO")
        title = intent.getIntExtra("title", R.string.edit_exercise)
        setContent {
            BioFitTheme {
                UpdateExerciseScreen(
                    title = title ?: R.string.edit_exercise,
                    exerciseDTO = exerciseDTO ?: ExerciseDTO.default()
                )
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
    title: Int,
    exerciseDTO: ExerciseDTO,
) {
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
                title = stringResource(title),
                middleButton = null,
                rightButton = null,
                standardPadding = standardPadding
            )

            UpdateExerciseContent(
                title = title,
                exerciseDTO = exerciseDTO,
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun UpdateExerciseContent(
    title: Int,
    exerciseDTO: ExerciseDTO,
    standardPadding: Dp,
    modifier: Modifier,
    exerciseViewModel: ExerciseViewModel = viewModel(),
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val exerciseId = exerciseDTO.exerciseId
    Log.d("CAUExerciseContent", "exerciseId: $exerciseId")
    val exerciseDetailList by exerciseViewModel.exerciseDetail.collectAsState()
    Log.d("CAUExerciseContent", "exerciseDetail: $exerciseDetailList")

    val exerciseGoal = exerciseDetailList?.exerciseGoal
    val intensity2 = exerciseDetailList?.intensity
    Log.d("UpdateExerciseScreen", "exerciseGoal: $exerciseGoal, intensity: $intensity2")

    var exerciseName by rememberSaveable { mutableStateOf(exerciseDTO.exerciseName) }
    var time by rememberSaveable { mutableStateOf("") }
    var caloriesConsumed by rememberSaveable { mutableStateOf("") }
    var level by rememberSaveable {
        mutableStateOf(
            value = exerciseDTO.getExerciseGoalString(
                context,
                exerciseDTO.detailList[0].exerciseGoal
            )
        )
    }
    var showLevelDialog by rememberSaveable { mutableStateOf(value = false) }
    var intensity by rememberSaveable {
        mutableStateOf(
            value = exerciseDTO.getIntensityString(
                context,
                exerciseDTO.detailList[0].intensity
            )
        )
    }
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

    LaunchedEffect(exerciseDetailList) {
        exerciseDetailList?.let {
            time = it.time.toString()
            caloriesConsumed = it.burnedCalories.toString()
        }
    }

    val focusManager = LocalFocusManager.current
    val interactionSources = remember { List(3) { MutableInteractionSource() } }
    interactionSources.forEach { source ->
        val isPressed by source.collectIsPressedAsState()
        if (isPressed) {
            showLevelDialog = false
            showIntensityDialog = false
        }
    }

    var initialName = remember { exerciseDTO.exerciseName }
    var initialTime = remember { exerciseDTO.detailList[0].time.toString() }
    var initialCalories = remember { exerciseDTO.detailList[0].burnedCalories.toString() }
    Log.d(
        "UpdateExerciseScreen",
        "initialName: $initialName, initialTime: $initialTime, initialCalories: $initialCalories"
    )

    val doExerciseText = stringResource(R.string.do_exercise)
    val saveText = stringResource(R.string.save)
    var buttonText by remember {
        mutableStateOf(
            /*doExerciseText*/
            if (title == R.string.edit_exercise) {
                saveText
            } else {
                doExerciseText
            }
        )
    }
    /*
    ____________________________________________________________________________________________________
    */
    val rotation = animatedRotation(
        targetRotation = if (showLevelDialog) 90f else 270f
    )
    val rotation2 = animatedRotation(
        targetRotation = if (showIntensityDialog) 90f else 270f
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(standardPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = exerciseName,
            onValueChange = { exerciseName = it },
            modifier = modifier,
            enabled = if (title == R.string.edit_exercise) {
                (level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low))
            } else false,
            readOnly = !(level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low)),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            placeholder = {
                Text(
                    text = stringResource(R.string.exercise_name),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            trailingIcon = if (exerciseName != initialName) {
                if (exerciseGoal == 0 && intensity2 == 0) {
                    {
                        IconButton(onClick = { exerciseName = initialName }) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_trianglehead_counterclockwise),
                                contentDescription = "Recover",
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else null
            } else null,
            prefix = { Text(text = stringResource(R.string.exercise)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            interactionSource = interactionSources[0],
            shape = MaterialTheme.shapes.large
        )

        if (title == R.string.do_exercise) {
            ItemCard(
                onClick = {
                    if (title == R.string.do_exercise) {
                        if (exerciseName != initialName ||
                            time != initialTime ||
                            caloriesConsumed != initialCalories
                        ) {
                            if (exerciseGoal == 0 && intensity2 == 0) {
                                null
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.des_no_select_level),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                showLevelDialog = !showLevelDialog
                                showIntensityDialog = false
                                focusManager.clearFocus()
                            }
                        } else {
                            showLevelDialog = !showLevelDialog
                            showIntensityDialog = false
                            focusManager.clearFocus()
                        }

                        Log.d(
                            "UpdateExerciseScreen",
                            "exerciseName: $exerciseName, time: $time, caloriesConsumed: $caloriesConsumed"
                        )
                        Log.d(
                            "UpdateExerciseScreen",
                            "initialName: $initialName, initialTime: $initialTime, initialCalories: $initialCalories"
                        )
                    } else null
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
                            if (title == R.string.do_exercise) {
                                if (exerciseName != initialName ||
                                    time != initialTime ||
                                    caloriesConsumed != initialCalories
                                ) {
                                    if (exerciseGoal == 0 && intensity2 == 0) {
                                        null
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.des_no_select_level),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        showLevelDialog = !showLevelDialog
                                        showIntensityDialog = false
                                        focusManager.clearFocus()
                                    }
                                } else {
                                    showLevelDialog = !showLevelDialog
                                    showIntensityDialog = false
                                    focusManager.clearFocus()
                                }
                            } else null
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.decrease_quotelevel),
                            contentDescription = stringResource(R.string.level),
                            modifier = Modifier.size(standardPadding * 1.5f),
                            tint = Color(0xFFFFAB00)
                        )
                    }

                    Spacer(modifier = Modifier.width(standardPadding / 2))

                    Text(
                        text = stringResource(R.string.level),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

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
                        textAlign = TextAlign.End
                    )

                    IconButton(
                        onClick = {
                            if (title == R.string.do_exercise) {
                                if (exerciseName != initialName ||
                                    time != initialTime ||
                                    caloriesConsumed != initialCalories
                                ) {
                                    if (exerciseGoal == 0 && intensity2 == 0) {
                                        null
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.des_no_select_level),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        showLevelDialog = !showLevelDialog
                                        showIntensityDialog = false
                                        focusManager.clearFocus()
                                    }
                                } else {
                                    showLevelDialog = !showLevelDialog
                                    showIntensityDialog = false
                                    focusManager.clearFocus()
                                }
                            } else null
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.level),
                            modifier = Modifier
                                .size(standardPadding)
                                .rotate(rotation),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showLevelDialog,
                    enter = slideInVertically { it } + fadeIn() + expandVertically(),
                    exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
                ) {
                    val listOptions = listOf(
                        stringResource(R.string.amateur),
                        stringResource(R.string.professional)
                    )

                    Column {
                        listOptions.forEach { selectLevel ->
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.1f
                                )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        level = selectLevel
                                        exerciseViewModel.fetchExerciseDetails(
                                            exerciseId,
                                            exerciseDTO.getExerciseGoalInt(context, level),
                                            exerciseDTO.getIntensityInt(context, intensity)
                                        )
                                        showLevelDialog = false
                                    },
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = selectLevel,
                                    modifier = Modifier.padding(standardPadding),
                                )
                            }
                        }
                    }
                }
            }

            ItemCard(
                onClick = {
                    if (title == R.string.do_exercise) {
                        if (exerciseName != initialName ||
                            time != initialTime ||
                            caloriesConsumed != initialCalories
                        ) {
                            if (exerciseGoal == 0 && intensity2 == 0) {
                                null
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.des_no_select_intensity),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                showIntensityDialog = !showIntensityDialog
                                showLevelDialog = false
                                focusManager.clearFocus()
                            }
                        } else {
                            showIntensityDialog = !showIntensityDialog
                            showLevelDialog = false
                            focusManager.clearFocus()
                        }
                    } else null
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
                            if (title == R.string.do_exercise) {
                                if (exerciseName != initialName ||
                                    time != initialTime ||
                                    caloriesConsumed != initialCalories
                                ) {
                                    if (exerciseGoal == 0 && intensity2 == 0) {
                                        null
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.des_no_select_intensity),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        showIntensityDialog = !showIntensityDialog
                                        showLevelDialog = false
                                        focusManager.clearFocus()
                                    }
                                } else {
                                    showIntensityDialog = !showIntensityDialog
                                    showLevelDialog = false
                                    focusManager.clearFocus()
                                }
                            } else null
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.figure_highintensity_intervaltraining),
                            contentDescription = stringResource(R.string.intensity),
                            modifier = Modifier.size(standardPadding * 1.5f),
                            tint = Color(0xFF2962FF)
                        )
                    }

                    Spacer(modifier = Modifier.width(standardPadding / 2))

                    Text(
                        text = stringResource(R.string.intensity),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

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
                        textAlign = TextAlign.End
                    )

                    IconButton(
                        onClick = {
                            if (title == R.string.do_exercise) {
                                if (exerciseName != initialName ||
                                    time != initialTime ||
                                    caloriesConsumed != initialCalories
                                ) {
                                    if (exerciseGoal == 0 && intensity2 == 0) {
                                        null
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.des_no_select_intensity),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        showIntensityDialog = !showIntensityDialog
                                        showLevelDialog = false
                                        focusManager.clearFocus()
                                    }
                                } else {
                                    showIntensityDialog = !showIntensityDialog
                                    showLevelDialog = false
                                    focusManager.clearFocus()
                                }
                            } else null
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.intensity),
                            modifier = Modifier
                                .size(standardPadding)
                                .rotate(rotation2),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showIntensityDialog,
                    enter = slideInVertically { it } + fadeIn() + expandVertically(),
                    exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
                ) {
                    val listOptions = listOf(
                        stringResource(R.string.low),
                        stringResource(R.string.medium),
                        stringResource(R.string.high)
                    )

                    Column {
                        listOptions.forEach { selectIntensity ->
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.1f
                                )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        intensity = selectIntensity
                                        exerciseViewModel.fetchExerciseDetails(
                                            exerciseId,
                                            exerciseDTO.getExerciseGoalInt(context, level),
                                            exerciseDTO.getIntensityInt(context, intensity)
                                        )
                                        showIntensityDialog = false
                                    },
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = selectIntensity,
                                    modifier = Modifier.padding(standardPadding),
                                )
                            }
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            modifier = modifier,
            enabled = if (title == R.string.edit_exercise) {
                (level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low))
            } else false,
            readOnly = !(level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low)),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.timer),
                    contentDescription = stringResource(R.string.time),
                    modifier = Modifier.size(standardPadding * 1.5f),
                    tint = Color(0xFF00C853)
                )
            },
            trailingIcon = if (time != initialTime) {
                if (exerciseGoal == 0 && intensity2 == 0) {
                    {
                        IconButton(onClick = { time = initialTime }) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_trianglehead_counterclockwise),
                                contentDescription = "Recover",
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else null
            } else null,
            prefix = { Text(text = stringResource(R.string.time)) },
            suffix = { Text(text = stringResource(R.string.min)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            interactionSource = interactionSources[1],
            shape = MaterialTheme.shapes.large
        )

        OutlinedTextField(
            value = caloriesConsumed,
            onValueChange = { caloriesConsumed = it },
            modifier = modifier,
            enabled = if (title == R.string.edit_exercise) {
                (level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low))
            } else false,
            readOnly = !(level == stringResource(R.string.amateur) && intensity == stringResource(R.string.low)),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.flame),
                    contentDescription = stringResource(R.string.calories_consumed),
                    modifier = Modifier.size(standardPadding * 1.5f),
                    tint = Color(0xFFDD2C00)
                )
            },
            trailingIcon = if (caloriesConsumed != initialCalories) {
                if (exerciseGoal == 0 && intensity2 == 0) {
                    {
                        IconButton(onClick = { caloriesConsumed = initialCalories }) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_trianglehead_counterclockwise),
                                contentDescription = "Recover",
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else null
            } else null,
            prefix = { Text(text = stringResource(R.string.burned_calories)) },
            suffix = { Text(text = stringResource(R.string.kcal)) },
            supportingText = if (title == R.string.edit_exercise) {
                {
                    Text(text = stringResource(R.string.des_update_exercise))
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = { TODO() }),
            singleLine = true,
            interactionSource = interactionSources[2],
            shape = MaterialTheme.shapes.large
        )

        val exerciseDetailId = exerciseDetailList?.exerciseDetailId
        Log.d("UpdateExerciseContent", "exerciseDTO: $exerciseDTO")
        Log.d("UpdateExerciseContent", "exerciseDetailDTO: $exerciseDetailList")

        Spacer(modifier = Modifier.weight(1f))

        ElevatedButton(
            onClick = {
                if (exerciseDetailId != null) {
                    if (buttonText == doExerciseText) {
                        activity?.let {
                            val intent = Intent(it, ExerciseViewActivity::class.java)
                            intent.putExtra("exerciseDTO", exerciseDTO)
                            intent.putExtra("exerciseDetailDTO", exerciseDetailList)
                            it.startActivity(intent)
                        }
                    } else {
                        if (exerciseGoal == 0 && intensity2 == 0) {
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
                }
            },
            enabled = if (title == R.string.edit_exercise) {
                val nameChanged = exerciseName.isNotEmpty() && exerciseName != initialName
                val timeChanged = time.isNotEmpty() && time != initialTime
                val caloriesChanged =
                    caloriesConsumed.isNotEmpty() && caloriesConsumed != initialCalories
                if (exerciseGoal == 0 && intensity2 == 0) {
                    if (nameChanged || timeChanged || caloriesChanged) {
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } else {
                true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = buttonText,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(
            modifier = Modifier.padding(
                bottom = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateBottomPadding()
            )
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
        UpdateExerciseScreen(
            R.string.edit_exercise,
            ExerciseDTO.default()
        )
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
        UpdateExerciseScreen(
            R.string.edit_exercise,
            ExerciseDTO.default()
        )
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
        UpdateExerciseScreen(
            R.string.edit_exercise,
            ExerciseDTO.default()
        )
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
        UpdateExerciseScreen(
            R.string.edit_exercise,
            ExerciseDTO.default()
        )
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
        UpdateExerciseScreen(
            R.string.edit_exercise,
            ExerciseDTO.default()
        )
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
        UpdateExerciseScreen(
            R.string.edit_exercise,
            ExerciseDTO.default()
        )
    }
}