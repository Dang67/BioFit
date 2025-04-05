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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biofit.R
import com.example.biofit.data.model.dto.FoodDTO
import com.example.biofit.data.model.dto.FoodInfoDTO
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.navigation.MainActivity
import com.example.biofit.ui.components.FoodItem
import com.example.biofit.ui.components.ToggleButtonBar
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.FoodViewModel

class AddActivity : ComponentActivity() {
    private val foodViewModel: FoodViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val session = intent.getIntExtra("SESSION", -1)
        setContent {
            BioFitTheme {
                AddScreen(session)
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
        foodViewModel.fetchFood(userId)
    }
}

@Composable
fun AddScreen(
    session: Int?,
    foodViewModel: FoodViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val standardPadding = getStandardPadding().first
    val modifier = getStandardPadding().second

    var expanded by remember { mutableStateOf(false) }

    val foodList by foodViewModel.foodList.collectAsState()
    Log.d("Session", "Default option: ${foodList.firstOrNull()?.session}")
    /*foodList.firstOrNull()?.session?.toIntOrNull() ?: R.string.morning*/
    val defaultOption = session
    var selectedOption by remember { mutableIntStateOf(defaultOption ?: -1) }

    val options = listOf(
        R.string.morning,
        R.string.afternoon,
        R.string.evening,
        R.string.snack
    )
    val filteredOptions = options.filter { it != selectedOption }


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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                onBackClick = { activity?.finish() },
                title = stringResource(selectedOption),
                middleButton = {
                    Box {
                        IconButton(
                            onClick = { expanded = !expanded }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(standardPadding)
                                    .rotate(270f),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            filteredOptions.forEach { selection ->
                                DropdownMenuItem(
                                    text = {  Text(
                                        text = stringResource(id = selection),
                                        color = when (selection) {
                                            R.string.morning -> Color(0xFFFFAB00)
                                            R.string.afternoon -> Color(0xFFDD2C00)
                                            R.string.evening -> Color(0xFF2962FF)
                                            else -> Color(0xFF00BFA5)
                                        }
                                    )},
                                    onClick = {
                                        selectedOption = selection
                                        expanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = when (selection) {
                                                R.string.morning -> painterResource(R.drawable.cloud_sun_fill)
                                                R.string.afternoon -> painterResource(R.drawable.sun_max_fill)
                                                R.string.evening -> painterResource(R.drawable.cloud_moon_fill)
                                                else -> painterResource(R.drawable.circle_hexagongrid_fill)
                                            },
                                            contentDescription = stringResource(R.string.morning),
                                            modifier = Modifier.size(standardPadding * 1.5f),
                                            tint = when (selection) {
                                                R.string.morning -> Color(0xFFFFAB00)
                                                R.string.afternoon -> Color(0xFFDD2C00)
                                                R.string.evening -> Color(0xFF2962FF)
                                                else -> Color(0xFF00BFA5)
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                rightButton = {
                    IconButton(
                        onClick = {
                            activity?.let {
                                val intent = Intent(it, CreateFoodActivity::class.java).apply {
                                    putExtra("SESSION", selectedOption) // ✅ Truyền buổi ăn
                                }
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

            AddContent(
                selectedOption = selectedOption,
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun AddContent(
    selectedOption: Int,
    standardPadding: Dp,
    modifier: Modifier,
    foodViewModel: FoodViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val foodListDTO by foodViewModel.foodList.collectAsState()
    // Chuyển đổi danh sách FoodDTO thành FoodInfoDTO
    val foodListInfoDTO = foodListDTO.map { it.toFoodInfoDTO() }

    var search by remember { mutableStateOf("") }

    var selectedToggle by remember { mutableIntStateOf(R.string.create) }

    OutlinedTextField(
        value = search,
        onValueChange = { search = it },
        modifier = modifier
            .padding(vertical = standardPadding)
            .shadow(
                elevation = 6.dp,
                shape = MaterialTheme.shapes.large
            ),
        placeholder = { Text(text = stringResource(id = R.string.search)) },
        trailingIcon = {
            IconButton(
                onClick = { TODO() }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { /*TODO*/ }
        ),
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            unfocusedBorderColor = Color.Transparent
        )
    )

    Column(
        modifier = modifier
    ) {
        ToggleButtonBar(
            options = listOf(
                R.string.create,
                R.string.recently
            ),
            selectedOption = selectedToggle,
            onOptionSelected = { selectedToggle = it },
            standardPadding = standardPadding
        )
    }

    val foodListCreateMorning = foodListInfoDTO.filter { it.session.equals(stringResource(R.string.morning), ignoreCase = true) }
    val foodListCreateAfternoon = foodListInfoDTO.filter { it.session.equals(stringResource(R.string.afternoon), ignoreCase = true) }
    val foodListCreateEvening = foodListInfoDTO.filter { it.session.equals(stringResource(R.string.evening), ignoreCase = true) }
    val foodListCreateSnack = foodListInfoDTO.filter { it.session.equals(stringResource(R.string.snack), ignoreCase = true) }

    val foodListRecentMorning = foodListInfoDTO.filter { it.session.equals(stringResource(R.string.morning), ignoreCase = true) }
    val foodListRecentAfternoon = foodListInfoDTO.filter { it.session.equals(stringResource(R.string.afternoon), ignoreCase = true) }
    val foodListRecentEvening = foodListInfoDTO.filter { it.session.equals(stringResource(R.string.evening), ignoreCase = true) }
    val foodListRecentSnack = foodListInfoDTO.filter { it.session.equals(stringResource(R.string.snack), ignoreCase = true) }


    val foodListCreate = when (selectedOption) {
        R.string.morning -> foodListCreateMorning
        R.string.afternoon -> foodListCreateAfternoon
        R.string.evening -> foodListCreateEvening
        else -> foodListCreateSnack
    }

    val foodListRecent = when (selectedOption) {
        R.string.morning -> foodListRecentMorning
        R.string.afternoon -> foodListRecentAfternoon
        R.string.evening -> foodListRecentEvening
        else -> foodListRecentSnack
    }

    LazyColumn(
        modifier = Modifier.padding(top = standardPadding * 2),
        verticalArrangement = Arrangement.spacedBy(standardPadding)
    ) {
        item {
            Log.d("FoodList", "foodListCreate: $foodListCreate")
            when (selectedToggle) {
                R.string.recently ->
                    when (foodListRecent.isNotEmpty()) {
                        true ->
                            Column(
                                modifier = modifier,
                                verticalArrangement = Arrangement.spacedBy(standardPadding)
                            ) {
                                foodListRecent.forEachIndexed { index, _ ->
                                    var expanded by remember { mutableStateOf(false) }

                                    Box {
                                        FoodItem(
                                            foodId = foodListRecent[index].foodId,
                                            session = foodListRecent[index].session,
                                            foodImg = foodListRecent[index].foodImage,
                                            foodName = foodListRecent[index].foodName,
                                            servingSize = Pair(
                                                foodListRecent[index].servingSize.first,
                                                foodListRecent[index].servingSize.second
                                            ),
                                            mass = foodListRecent[index].mass,
                                            calories = foodListRecent[index].calories,
                                            macros = listOf(
                                                Pair(
                                                    foodListRecent[index].protein.first,
                                                    foodListRecent[index].protein.third
                                                ),
                                                Pair(
                                                    foodListRecent[index].carbohydrate.first,
                                                    foodListRecent[index].carbohydrate.third
                                                ),
                                                Pair(
                                                    foodListRecent[index].fat.first,
                                                    foodListRecent[index].fat.third
                                                )
                                            ),
                                            onClick = {
                                                activity?.let {
                                                    val intent = Intent(it, FoodDetailActivity::class.java)
                                                    val foodId = foodListRecent[index].foodId
                                                    intent.putExtra("FOOD_ID", foodId)
                                                    it.startActivity(intent)
                                                }
                                            },
                                            onEatClick = { foodId, session ->  // Truyền foodId và session vào đây
                                                activity?.let {
                                                    val intent = Intent(it, TrackActivity::class.java)
                                                    intent.putExtra("FOOD_ID", foodId)
                                                    intent.putExtra("SESSION", session)
                                                    it.startActivity(intent)
                                                }
                                            },
                                            onLongClick = { expanded = true },
                                            standardPadding = standardPadding,
                                        )

                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = stringResource(R.string.edit_food),
                                                        color = Color(0xFFFF6D00)
                                                    )
                                                },
                                                onClick = {
                                                    activity?.let {
                                                        val intent =
                                                            Intent(it, EditFoodActivity::class.java).apply {
                                                                /*putExtra("exerciseId", exercise.exerciseId)
                                                                putExtra("exerciseDTO", exercise)*/
                                                            }
                                                        it.startActivity(intent)
                                                    }
                                                    expanded = false
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_edit),
                                                        contentDescription = stringResource(R.string.edit_food),
                                                        modifier = Modifier.size(standardPadding * 1.5f),
                                                        tint = Color(0xFFFF6D00)
                                                    )
                                                }
                                            )

                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = stringResource(R.string.delete_food),
                                                        color = Color(0xFFDD2C00)
                                                    )
                                                },
                                                onClick = {
                                                    /*Log.d(
                                                        "ExerciseListScreen",
                                                        "Deleting exercise: ${exercise.exerciseId}"
                                                    )
                                                    exerciseViewModel.deleteExercise(exercise.exerciseId)*/
                                                    expanded = false
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.food_deleted_successfully),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(R.drawable.trash),
                                                        contentDescription = stringResource(R.string.delete_food),
                                                        modifier = Modifier.size(standardPadding * 1.5f),
                                                        tint = Color(0xFFDD2C00)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                        else -> EmptyAddScreen(
                            standardPadding = standardPadding,
                            modifier = modifier,
                            selectedOption = selectedOption
                        )
                    }
                else ->
                    when (foodListCreate.isNotEmpty()) {
                        true ->
                            Column(
                                modifier = modifier,
                                verticalArrangement = Arrangement.spacedBy(standardPadding)
                            ) {
                                foodListCreate.forEachIndexed { index, _ ->
                                    var expanded by remember { mutableStateOf(false) }

                                    Box {
                                        FoodItem(
                                            foodId = foodListCreate[index].foodId,
                                            session = foodListCreate[index].session,
                                            foodImg = foodListCreate[index].foodImage,
                                            foodName = foodListCreate[index].foodName,
                                            servingSize = Pair(
                                                foodListCreate[index].servingSize.first,
                                                foodListCreate[index].servingSize.second
                                            ),
                                            mass = foodListCreate[index].mass,
                                            calories = foodListCreate[index].calories,
                                            macros = listOf(
                                                Pair(
                                                    foodListCreate[index].protein.first,
                                                    foodListCreate[index].protein.third
                                                ),
                                                Pair(
                                                    foodListCreate[index].carbohydrate.first,
                                                    foodListCreate[index].carbohydrate.third
                                                ),
                                                Pair(
                                                    foodListCreate[index].fat.first,
                                                    foodListCreate[index].fat.third
                                                )
                                            ),
                                            onClick = {
                                                activity?.let {
                                                    val intent = Intent(it, FoodDetailActivity::class.java)
                                                    val foodId = foodListRecent[index].foodId
                                                    Log.d("FoodItem", "Clicked foodId: $foodId")
                                                    intent.putExtra("FOOD_ID", foodId)
                                                    it.startActivity(intent)
                                                }
                                            },
                                            onEatClick = { foodId, session ->  // Truyền foodId và session vào đây
                                                activity?.let {
                                                    val intent = Intent(it, TrackActivity::class.java)
                                                    intent.putExtra("FOOD_ID", foodId)
                                                    intent.putExtra("SESSION", session)
                                                    it.startActivity(intent)
                                                }
                                            },
                                            onLongClick = { expanded = true },
                                            standardPadding = standardPadding
                                        )

                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = stringResource(R.string.edit_food),
                                                        color = Color(0xFFFF6D00)
                                                    )
                                                },
                                                onClick = {
                                                    activity?.let {
                                                        val intent =
                                                            Intent(it, EditFoodActivity::class.java).apply {
                                                                /*putExtra("exerciseId", exercise.exerciseId)
                                                                putExtra("exerciseDTO", exercise)*/
                                                            }
                                                        it.startActivity(intent)
                                                    }
                                                    expanded = false
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_edit),
                                                        contentDescription = stringResource(R.string.edit_food),
                                                        modifier = Modifier.size(standardPadding * 1.5f),
                                                        tint = Color(0xFFFF6D00)
                                                    )
                                                }
                                            )

                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = stringResource(R.string.delete_food),
                                                        color = Color(0xFFDD2C00)
                                                    )
                                                },
                                                onClick = {
                                                    /*Log.d(
                                                        "ExerciseListScreen",
                                                        "Deleting exercise: ${exercise.exerciseId}"
                                                    )
                                                    exerciseViewModel.deleteExercise(exercise.exerciseId)*/
                                                    expanded = false
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.food_deleted_successfully),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(R.drawable.trash),
                                                        contentDescription = stringResource(R.string.delete_food),
                                                        modifier = Modifier.size(standardPadding * 1.5f),
                                                        tint = Color(0xFFDD2C00)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                        else -> EmptyAddScreen(
                            standardPadding = standardPadding,
                            modifier = modifier,
                            selectedOption = selectedOption
                        )

                    }

            }
        }
    }
}

@Composable
fun EmptyAddScreen(
    standardPadding: Dp,
    modifier: Modifier,
    selectedOption: Int
) {
    val context = LocalContext.current
    val activity = context as? Activity

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.create_new_food),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = stringResource(R.string.des_create_new_food),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )

            TextButton(
                onClick = {
                    activity?.let {
                        val intent = Intent(it, CreateFoodActivity::class.java).apply {
                            putExtra("SESSION", selectedOption) // Truyền buổi ăn
                        }
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
                        contentDescription = "Add button",
                        modifier = Modifier.size(standardPadding),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(R.string.create_new),
                        color = MaterialTheme.colorScheme.primary
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
private fun AddScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        AddScreen(R.string.morning)
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun AddScreenPreviewInLargePhone() {
    BioFitTheme {
        AddScreen(R.string.morning)
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
private fun AddScreenPreviewInTablet() {
    BioFitTheme {
        AddScreen(R.string.morning)
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
private fun AddScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        AddScreen(R.string.morning)
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AddScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        AddScreen(R.string.morning)
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
private fun AddScreenLandscapePreviewInTablet() {
    BioFitTheme {
        AddScreen(R.string.morning)
    }
}