package com.example.biofit.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.biofit.R
import com.example.biofit.ui.components.FoodItem
import com.example.biofit.ui.components.ToggleButtonBar
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme

class MealsListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialSelectedOption = intent.getIntExtra("SESSION_TOGGLE", R.string.morning)
        setContent {
            BioFitTheme {
                MealsListScreen(initialSelectedOption)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun MealsListScreen(initialSelectedOption: Int) {
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
                title = stringResource(R.string.meals_list),
                middleButton = null,
                rightButton = null,
                standardPadding = standardPadding
            )

            MealsListContent(
                initialSelectedOption = initialSelectedOption,
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun MealsListContent(
    initialSelectedOption: Int,
    standardPadding: Dp,
    modifier: Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedOption by rememberSaveable { mutableIntStateOf(initialSelectedOption) }
    val foodList = when (selectedOption) {
        R.string.morning -> foodListMorning
        R.string.afternoon -> foodListAfternoon
        R.string.evening -> foodListEvening
        else -> foodListSnack
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(standardPadding * 2)
    ) {
        item {
            Column(
                modifier = modifier.padding(top = standardPadding)
            ) {
                ToggleButtonBar(
                    options = listOf(
                        R.string.morning,
                        R.string.afternoon,
                        R.string.evening,
                        R.string.snack
                    ),
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                    standardPadding = standardPadding
                )
            }
        }

        itemsIndexed(foodList) { index, _ ->
            var expanded by remember { mutableStateOf(false) }

            Column(
                modifier = modifier
            ) {
                Box {
                    FoodItem(
                        foodId = foodList[index].foodId,
                        session = foodList[index].session,
                        foodImg = foodList[index].foodImage,
                        foodName = foodList[index].foodName,
                        servingSize = foodList[index].servingSize,
                        mass = foodList[index].mass,
                        calories = foodList[index].calories,
                        macros = listOf(
                            Pair(foodList[index].protein.first, foodList[index].protein.third),
                            Pair(
                                foodList[index].carbohydrate.first,
                                foodList[index].carbohydrate.third
                            ),
                            Pair(foodList[index].fat.first, foodList[index].fat.third)
                        ),
                        onClick = {
                            activity?.let {
                                val intent = Intent(it, MealsDetailActivity::class.java)
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

        item {
            Spacer(
                modifier = Modifier.padding(
                    bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding() * 2
                            + standardPadding
                )
            )
        }
    }
}


data class FoodInfo(
    val foodId: Long,
    val session: String,
    val foodImage: Int,
    val foodName: String,
    val servingSize: Pair<Float, String>,
    val mass: Float,
    val calories: Float,
    val protein: Triple<Int, Int, Float>,
    val carbohydrate: Triple<Int, Int, Float>,
    val fat: Triple<Int, Int, Float>,
    val sodium: Float? = null,
)

//Dữ liệu giả
val food1 = FoodInfo(
    foodId = 1,
    session = "Morning",
    foodImage = R.drawable.img_food_default,
    foodName = "Pizza",
    servingSize = Pair(1f, "slice"),
    mass = 107f,
    calories = 285f,
    protein = Triple(R.drawable.ic_protein, R.string.protein, 12f),
    carbohydrate = Triple(R.drawable.ic_carbohydrate, R.string.carbohydrate, 36f),
    fat = Triple(R.drawable.ic_fat, R.string.fat, 10f),
    sodium = 640f
)

val food2 = FoodInfo(
    foodId = 2,
    session = "Morning",
    foodImage = R.drawable.img_food_default,
    foodName = "Hamburger",
    servingSize = Pair(1f, "sandwich"),
    mass = 226f,
    calories = 540f,
    protein = Triple(R.drawable.ic_protein, R.string.protein, 34f),
    carbohydrate = Triple(R.drawable.ic_carbohydrate, R.string.carbohydrate, 40f),
    fat = Triple(R.drawable.ic_fat, R.string.fat, 27f),
    sodium = 791f
)

val food3 = FoodInfo(
    foodId = 3,
    session = "Afternoon",
    foodImage = R.drawable.img_food_default,
    foodName = "Beefsteaks",
    servingSize = Pair(1f, "steak"),
    mass = 221f,
    calories = 614f,
    protein = Triple(R.drawable.ic_protein, R.string.protein, 58f),
    carbohydrate = Triple(R.drawable.ic_carbohydrate, R.string.carbohydrate, 0f),
    fat = Triple(R.drawable.ic_fat, R.string.fat, 41f),
    sodium = 115f
)

val foodListMorning = listOf<FoodInfo>()
val foodListAfternoon = listOf(
    food2, food3
)
val foodListEvening = listOf(
    food3, food1
)
val foodListSnack = listOf(
    food1
)

@Preview(
    device = "id:pixel",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    locale = "vi"
)
@Composable
private fun MealsListScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        MealsListScreen(
            initialSelectedOption = R.string.morning
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
private fun MealsListScreenPreviewInLargePhone() {
    BioFitTheme {
        MealsListScreen(
            initialSelectedOption = R.string.morning
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
private fun MealsListScreenPreviewInTablet() {
    BioFitTheme {
        MealsListScreen(
            initialSelectedOption = R.string.morning
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
private fun MealsListScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        MealsListScreen(
            initialSelectedOption = R.string.morning
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
private fun MealsListScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        MealsListScreen(
            initialSelectedOption = R.string.morning
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
private fun MealsListScreenLandscapePreviewInTablet() {
    BioFitTheme {
        MealsListScreen(
            initialSelectedOption = R.string.morning
        )
    }
}