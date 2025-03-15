package com.example.biofit.ui.activity

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.service.autofill.UserData
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.ExerciseViewModel

class CreateExerciseActivity : ComponentActivity() {
    private var userData: UserDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userData = UserSharedPrefsHelper.getUserData(this)
        setContent {
            BioFitTheme {
                CreateExerciseScreen(userData = userData ?: UserDTO.default())
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun CreateExerciseScreen(
    userData: UserDTO,
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val standardPadding = getStandardPadding().first
    val modifier = getStandardPadding().second

    val userId = userData.userId
    var exerciseName by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var burnedCalories by remember { mutableStateOf("") }

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
                title = stringResource(R.string.new_exercise),
                middleButton = null,
                rightButton = {
                    TextButton(
                        onClick = {
                            val exerciseDTO = ExerciseDTO(
                                exerciseId = 0L,
                                userId = userId,
                                exerciseName = exerciseName,
                                detailList = listOf(
                                    ExerciseDetailDTO(
                                        exerciseDetailId = 0L,
                                        exerciseId = 0L,
                                        exerciseGoal = 0,
                                        intensity = 0,
                                        time = time.toFloatOrNull() ?: 0f,
                                        burnedCalories = burnedCalories.toFloatOrNull() ?: 0f
                                    )
                                )
                            )
                            exerciseViewModel.createExercise(exerciseDTO)
                            activity?.finish()
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

            CreateExerciseContent(
                exerciseName = exerciseName,
                onExerciseNameChange = { exerciseName = it },
                time = time,
                onTimeChange = { time = it },
                burnedCalories = burnedCalories,
                onBurnedCaloriesChange = { burnedCalories = it },
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun CreateExerciseContent(
    exerciseName: String,
    onExerciseNameChange: (String) -> Unit,
    time: String,
    onTimeChange: (String) -> Unit,
    burnedCalories: String,
    onBurnedCaloriesChange: (String) -> Unit,
    standardPadding: Dp,
    modifier: Modifier
) {

    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(standardPadding),
        horizontalAlignment = Alignment.CenterHorizontally
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
                    text = stringResource(R.string.des_create_exercise),
                    textAlign = TextAlign.Justify
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

        OutlinedTextField(
            value = time,
            onValueChange = onTimeChange,
            modifier = modifier,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.End
            ),
            prefix = {
                Text(
                    text = stringResource(R.string.time),
                    style = MaterialTheme.typography.bodySmall
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
            value = burnedCalories,
            onValueChange = onBurnedCaloriesChange,
            modifier = modifier,
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
private fun CreateExerciseScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        CreateExerciseScreen(UserDTO.default())
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun CreateExerciseScreenPreviewInLargePhone() {
    BioFitTheme {
        CreateExerciseScreen(UserDTO.default())
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
private fun CreateExerciseScreenPreviewInTablet() {
    BioFitTheme {
        CreateExerciseScreen(UserDTO.default())
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
private fun CreateExerciseScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        CreateExerciseScreen(UserDTO.default())
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun CreateExerciseScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        CreateExerciseScreen(UserDTO.default())
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
private fun CreateExerciseScreenLandscapePreviewInTablet() {
    BioFitTheme {
        CreateExerciseScreen(UserDTO.default())
    }
}