package com.example.biofit.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.biofit.R
import com.example.biofit.ui.components.FoodItem
import com.example.biofit.ui.components.ToggleButtonBar
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme

class AddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialSelectedOption = intent.getIntExtra("SESSION_TITLE", R.string.morning)
        setContent {
            BioFitTheme {
                AddScreen(initialSelectedOption)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun AddScreen(initialSelectedOption: Int) {
    val context = LocalContext.current
    val activity = context as? Activity

    val standardPadding = getStandardPadding().first
    val modifier = getStandardPadding().second

    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedOption by rememberSaveable { mutableIntStateOf(initialSelectedOption) }

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
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(standardPadding * 3),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            filteredOptions.forEach { selection ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(selection)) },
                                    onClick = {
                                        selectedOption = selection
                                        expanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = when (selection) {
                                                R.string.morning -> painterResource(R.drawable.ic_morning_2)
                                                R.string.afternoon -> painterResource(R.drawable.ic_afternoon_2)
                                                R.string.evening -> painterResource(R.drawable.ic_evening_2)
                                                else -> painterResource(R.drawable.ic_snack_2)
                                            },
                                            contentDescription = stringResource(R.string.morning),
                                            tint = MaterialTheme.colorScheme.onSurface
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
                                val intent = Intent(it, CreateFoodActivity::class.java)
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
    modifier: Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var search by rememberSaveable { mutableStateOf("") }

    var selectedToggle by rememberSaveable { mutableIntStateOf(R.string.create) }

    OutlinedTextField(
        value = search,
        onValueChange = { search = it },
        modifier = modifier
            .padding(vertical = standardPadding)
            .shadow(
                elevation = 6.dp,
                shape = MaterialTheme.shapes.large
            ),
        textStyle = MaterialTheme.typography.bodySmall,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.bodySmall
            )
        },
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

    val foodListCreateMorning = listOf<FoodInfo>()
    val foodListCreateAfternoon = listOf(food1, food2)
    val foodListCreateEvening = listOf(food1)
    val foodListCreateSnack = listOf(food3)

    val foodListRecent = when (selectedOption) {
        R.string.morning -> foodListMorning
        R.string.afternoon -> foodListAfternoon
        R.string.evening -> foodListEvening
        else -> foodListSnack
    }

    val foodListCreate = when (selectedOption) {
        R.string.morning -> foodListCreateMorning
        R.string.afternoon -> foodListCreateAfternoon
        R.string.evening -> foodListCreateEvening
        else -> foodListCreateSnack
    }

    LazyColumn(
        modifier = Modifier.padding(top = standardPadding * 2),
        verticalArrangement = Arrangement.spacedBy(standardPadding)
    ) {
        item {
            when (selectedToggle) {
                R.string.recently ->
                    when (foodListRecent.isNotEmpty()) {
                        true ->
                            Column(
                                modifier = modifier,
                                verticalArrangement = Arrangement.spacedBy(standardPadding)
                            ) {
                                foodListRecent.forEachIndexed { index, _ ->
                                    FoodItem(
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
                                                val intent =
                                                    Intent(it, FoodDetailActivity::class.java)
                                                it.startActivity(intent)
                                            }
                                        },
                                        standardPadding = standardPadding
                                    )
                                }
                            }

                        else -> EmptyAddScreen(
                            standardPadding = standardPadding,
                            modifier = modifier
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
                                    FoodItem(
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
                                                val intent =
                                                    Intent(it, FoodDetailActivity::class.java)
                                                it.startActivity(intent)
                                            }
                                        },
                                        standardPadding = standardPadding
                                    )
                                }
                            }

                        else -> EmptyAddScreen(
                            standardPadding = standardPadding,
                            modifier = modifier
                        )

                    }

            }
        }
    }
}

@Composable
fun EmptyAddScreen(
    standardPadding: Dp,
    modifier: Modifier
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
                        val intent = Intent(it, CreateFoodActivity::class.java)
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
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(R.string.create_new),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

val food2 = FoodInfo(
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
        AddScreen(
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
private fun AddScreenPreviewInLargePhone() {
    BioFitTheme {
        AddScreen(
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
private fun AddScreenPreviewInTablet() {
    BioFitTheme {
        AddScreen(
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
private fun AddScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        AddScreen(
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
private fun AddScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        AddScreen(
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
private fun AddScreenLandscapePreviewInTablet() {
    BioFitTheme {
        AddScreen(
            initialSelectedOption = R.string.morning
        )
    }
}