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
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.Ref
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
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.ui.components.DefaultDialog
import com.example.biofit.ui.components.ExerciseItem
import com.example.biofit.ui.components.SubCard
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.ExerciseViewModel

class ExerciseActivity : ComponentActivity() {
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BioFitTheme {
                ExerciseScreen()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

    override fun onResume() {
        super.onResume()
        val userId = UserSharedPrefsHelper.getUserData(this)?.userId ?: 0L
        exerciseViewModel.fetchExercises(userId) // Cập nhật danh sách bài tập từ server khi quay lại
    }
}

@Composable
fun ExerciseScreen() {
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
                                val intent = Intent(it, CreateExerciseActivity::class.java)
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

            ExerciseContent(
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ExerciseContent(
    standardPadding: Dp,
    modifier: Modifier,
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var search by rememberSaveable { mutableStateOf("") }

    var deleteExerciseDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(standardPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = { newValue ->
                search = newValue
            },
            modifier = modifier.shadow(
                elevation = 6.dp,
                shape = MaterialTheme.shapes.large
            ),
            placeholder = { Text(text = stringResource(R.string.search)) },
            trailingIcon = {
                if (search.isEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    IconButton(
                        onClick = { search = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedBorderColor = Color.Transparent
            )
        )

        val exerciseList by exerciseViewModel.exerciseList.collectAsState()

        val userId = UserSharedPrefsHelper.getUserData(context)?.userId ?: 0L
        LaunchedEffect(exerciseViewModel.fetchExercises(userId)) {
            exerciseList
        }

        val viewModel: ExerciseViewModel = viewModel()

        LaunchedEffect(Unit) {
            val savedUserId = userId
            savedUserId.let { viewModel.setUserId(it) }
        }

        // Lọc danh sách dựa trên từ khóa tìm kiếm; tìm kiếm tự động khi 'search' thay đổi
        val filteredList = if (search.isEmpty()) {
            exerciseList
        } else {
            exerciseList.filter { it.exerciseName.contains(search, ignoreCase = true) }
        }

        // Nhóm danh sách đã lọc theo chữ cái đầu của tên bài tập
        val groupedExercises = filteredList
            .sortedBy { it.exerciseName }
            .groupBy { it.exerciseName.first().uppercaseChar() }

        val expandedState = remember { mutableStateOf<Pair<String?, Int?>>(null to null) }
        val expanded = remember { mutableStateOf<Pair<String?, Int?>>(null to null) }
        LazyColumn {
            Log.d("ExerciseListScreen", "exerciseList size: ${exerciseList.size}")

            groupedExercises.forEach { (letter, exercises) ->
                item {
                    Text(
                        text = letter.toString(),
                        modifier = modifier.padding(top = standardPadding * 2),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.displaySmall,
                    )
                }

                itemsIndexed(exercises) { index, exercise ->

                    val menuAnchor = remember { Ref<Rect>() }

                    Column(modifier = modifier) {
                        ExerciseItem(
                            onDoClick = {
                                activity?.let {
                                    val intent =
                                        Intent(it, UpdateExerciseActivity::class.java).apply {
                                            putExtra("exerciseId", exercise.exerciseId)
                                            putExtra("exerciseDTO", exercise)
                                            putExtra("title", R.string.do_exercise)
                                        }
                                    it.startActivity(intent)
                                }
                                expandedState.value = (null to null)
                                expanded.value = (null to null)
                            },
                            onEditClick = {
                                activity?.let {
                                    val intent =
                                        Intent(it, UpdateExerciseActivity::class.java).apply {
                                            putExtra("exerciseId", exercise.exerciseId)
                                            putExtra("exerciseDTO", exercise)
                                            putExtra("title", R.string.edit_exercise)
                                        }
                                    it.startActivity(intent)
                                }
                                expandedState.value = (null to null)
                                expanded.value = (null to null)
                            },
                            onDeleteClick = {
                                expanded.value = (letter.toString() to index)
                                expandedState.value = (null to null)
                            },
                            exercise = exercise,
                            standardPadding = standardPadding,
                            modifier = modifier
                                .onGloballyPositioned { coordinates ->
                                    menuAnchor.value =
                                        coordinates.boundsInRoot() // Lưu vị trí của ExerciseItem
                                },
                            isExpanded = expandedState.value == (letter.toString() to index), // So sánh cặp (date, index)
                            onExpandChange = { isExpanded ->
                                expandedState.value =
                                    if (isExpanded) (letter.toString() to index) else (null to null)
                                expanded.value = (null to null)
                            }
                        )

                        AnimatedVisibility(
                            visible = expanded.value == (letter.toString() to index),
                            enter = slideInVertically { it } + fadeIn() + expandVertically(),
                            exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
                        ) {
                            Row (
                                horizontalArrangement = Arrangement.spacedBy(standardPadding),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SubCard(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.des_delete_exercise),
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.padding(standardPadding),
                                        textAlign = TextAlign.Justify,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ElevatedButton(
                                        onClick = {
                                            expanded.value = (null to null)
                                            expandedState.value = (letter.toString() to index)
                                        },
                                        colors = ButtonDefaults.elevatedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(
                                                standardPadding / 2
                                            ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_plus),
                                                contentDescription = stringResource(R.string.delete_exercise),
                                                modifier = Modifier
                                                    .size(standardPadding * 1.5f)
                                                    .rotate(45f),
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }

                                    ElevatedButton(
                                        onClick = {
                                            Log.d(
                                                "ExerciseListScreen",
                                                "Deleting exercise: ${exercise.exerciseId}"
                                            )
                                            exerciseViewModel.deleteExercise(exercise.exerciseId)
                                            expanded.value = (null to null)
                                            expandedState.value = (null to null)
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.exercise_deleted_successfully),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        colors = ButtonDefaults.elevatedButtonColors(
                                            containerColor = Color(0xFFDD2C00)
                                        )
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(
                                                standardPadding / 2
                                            ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.trash),
                                                contentDescription = stringResource(R.string.delete_exercise),
                                                modifier = Modifier.size(standardPadding * 1.5f),
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
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
private fun ExerciseScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        ExerciseScreen()
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun ExerciseScreenPreviewInLargePhone() {
    BioFitTheme {
        ExerciseScreen()
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
private fun ExerciseScreenPreviewInTablet() {
    BioFitTheme {
        ExerciseScreen()
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
private fun ExerciseScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        ExerciseScreen()
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ExerciseScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        ExerciseScreen()
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
private fun ExerciseScreenLandscapePreviewInTablet() {
    BioFitTheme {
        ExerciseScreen()
    }
}