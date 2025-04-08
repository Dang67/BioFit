package com.example.biofit.ui.screen

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Paint
import android.widget.NumberPicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.biofit.R
import com.example.biofit.ui.activity.CalorieTodayActivity
import com.example.biofit.ui.activity.CreatePlanningActivity
import com.example.biofit.ui.activity.EditExerciseActivity
import com.example.biofit.ui.activity.ExerciseViewActivity
import com.example.biofit.ui.activity.MealsListActivity
import com.example.biofit.ui.components.CalendarSelector
import com.example.biofit.ui.components.ItemCard
import com.example.biofit.ui.components.SubCard
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberTopAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.VerticalPosition
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun PlanningScreen() {
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlanningHeaderBar(
                rightButton = {
                    IconButton(
                        onClick = { TODO() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar),
                            contentDescription = stringResource(R.string.calendar),
                            modifier = Modifier.size(standardPadding * 2f),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                standardPadding = standardPadding
            )

            PlanningScreenContent(
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun PlanningScreenContent(
    standardPadding: Dp,
    modifier: Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

//    val userPlanning = databaseHelper.getUserPlanById(1, 0)
    val userPlanning = 0

    val selectedIntensityOption = rememberSaveable { mutableIntStateOf(R.string.low) }
    var expandedIntensity by rememberSaveable { mutableStateOf(false) }
    val optionsIntensity = listOf(
        R.string.low,
        R.string.medium,
        R.string.high
    )
    val filteredOptionsIntensity =
        optionsIntensity.filter { it != selectedIntensityOption.intValue }


    val selectedDietPlanOption = rememberSaveable { mutableIntStateOf(R.string.weight_loss) }
    var expandedDietPlan by rememberSaveable { mutableStateOf(false) }
    val optionsDietPlan = listOf(
        R.string.weight_loss,
        R.string.muscle_gain,
        R.string.healthy_diet
    )
    val filteredOptionsDietPlan = optionsDietPlan.filter { it != selectedDietPlanOption.intValue }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(standardPadding * 2)
    ) {
        if (userPlanning == null) {
            item {
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.make_a_plan_for_yourself),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Text(
                        text = stringResource(R.string.des_planning),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )

                    TextButton(
                        onClick = {
                            activity?.let {
                                val intent = Intent(it, CreatePlanningActivity::class.java)
                                it.startActivity(intent)
                            }
                        },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_plus),
                                contentDescription = "Create new plan",
                                modifier = Modifier.size(standardPadding * 2f),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = stringResource(R.string.create_plan),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        } else {
            item {
                CalendarSelector(
                    onDaySelected = { },
                    standardPadding = standardPadding,
                    modifier = modifier
                )
            }

            item {
                val caloriesData = listOf(
                    "12AM" to 250f,
                    "6AM" to 500f,
                    "12PM" to 300f,
                    "6PM" to 450f,
                    "12PM" to 200f,
                )

                CaloriesLineChart(
                    weightData = caloriesData,
                    standardPadding = standardPadding,
                    modifier = modifier
                )
            }

            item {
                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(standardPadding)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding)
                    ) {
                        Column(
                            modifier = Modifier.weight(0.5f)
                        ) {
                            WellnessTrackerCard(
                                onClick = { TODO() },
                                title = stringResource(R.string.workout_sessions),
                                details = "0/7",
                                standardPadding = standardPadding,
                                modifier = modifier
                            )
                        }

                        Column(
                            modifier = Modifier.weight(0.5f)
                        ) {
                            var showDialog by rememberSaveable { mutableStateOf(false) }
                            var selectedMinutes by rememberSaveable { mutableIntStateOf(0) }

                            WellnessTrackerCard(
                                onClick = { showDialog = true },
                                title = stringResource(R.string.duration),
                                details = "$selectedMinutes ${stringResource(R.string.min)}",
                                standardPadding = standardPadding,
                                modifier = modifier
                            )

                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    title = {
                                        Text(
                                            text = "Select Duration",
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                    },
                                    text = {
                                        NumberPicker(
                                            value = selectedMinutes,
                                            range = 0..120,
                                            onValueChange = { selectedMinutes = it }
                                        )
                                    },
                                    shape = MaterialTheme.shapes.extraLarge,
                                    confirmButton = {
                                        TextButton(
                                            onClick = { showDialog = false }
                                        ) {
                                            Text(
                                                text = "OK",
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding)
                    ) {
                        Column(
                            modifier = Modifier.weight(0.5f)
                        ) {
                            WellnessTrackerCard(
                                onClick = { expandedIntensity = true },
                                title = stringResource(R.string.intensity),
                                details = stringResource(selectedIntensityOption.intValue),
                                standardPadding = standardPadding,
                                modifier = modifier
                            )

                            DropdownMenu(
                                expanded = expandedIntensity,
                                onDismissRequest = { expandedIntensity = false }
                            ) {
                                filteredOptionsIntensity.forEach { selection ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(selection)) },
                                        onClick = {
                                            selectedIntensityOption.intValue = selection
                                            expandedIntensity = false
                                        }
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.weight(0.5f)
                        ) {
                            WellnessTrackerCard(
                                onClick = { expandedDietPlan = true },
                                title = stringResource(R.string.diet_plan),
                                details = stringResource(selectedDietPlanOption.intValue),
                                standardPadding = standardPadding,
                                modifier = modifier
                            )

                            DropdownMenu(
                                expanded = expandedDietPlan,
                                onDismissRequest = { expandedDietPlan = false }
                            ) {
                                filteredOptionsDietPlan.forEach { selection ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(selection)) },
                                        onClick = {
                                            selectedDietPlanOption.intValue = selection
                                            expandedDietPlan = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(standardPadding)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.fork_knife),
                            contentDescription = "Food",
                            modifier = Modifier.size(standardPadding * 2f),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = stringResource(R.string.suggested_meals),
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge
                        )

                        IconButton(
                            onClick = { TODO() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = "Edit",
                                modifier = Modifier.size(standardPadding * 2f),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    val morningMeals = listOf(
                        "Bakery",
                        "Milk"
                    )
                    val afternoonMeals = listOf(
                        "Brown rice",
                        "Boiled eggs"
                    )
                    val eveningMeals = listOf(
                        "Brown rice",
                        "Meat"
                    )
                    val snackMeals = listOf(
                        "Salad",
                        "Fruit"
                    )

                    Column(
                        modifier = modifier,
                        verticalArrangement = Arrangement.spacedBy(standardPadding)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(standardPadding)
                        ) {
                            SuggestedMeals(
                                onClick = {
                                    activity?.let {
                                        val intent = Intent(it, MealsListActivity::class.java)
                                        intent.putExtra("SESSION_TOGGLE", R.string.morning)
                                        it.startActivity(intent)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                suggestedMealsSession = R.string.morning,
                                suggestedMealsSessionColor = Color(0xFFFFAB00),
                                suggestedMealsSessionIcon = R.drawable.cloud_sun_fill,
                                suggestedMealsImg = R.drawable.img_food_default,
                                suggestedMeals = morningMeals,
                                standardPadding = standardPadding,
                            )

                            SuggestedMeals(
                                onClick = {
                                    activity?.let {
                                        val intent = Intent(it, MealsListActivity::class.java)
                                        intent.putExtra("SESSION_TOGGLE", R.string.afternoon)
                                        it.startActivity(intent)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                suggestedMealsSession = R.string.afternoon,
                                suggestedMealsSessionColor = Color(0xFFDD2C00),
                                suggestedMealsSessionIcon = R.drawable.sun_max_fill,
                                suggestedMealsImg = R.drawable.img_food_default,
                                suggestedMeals = afternoonMeals,
                                standardPadding = standardPadding
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(standardPadding)
                        ) {
                            SuggestedMeals(
                                onClick = {
                                    activity?.let {
                                        val intent = Intent(it, MealsListActivity::class.java)
                                        intent.putExtra("SESSION_TOGGLE", R.string.evening)
                                        it.startActivity(intent)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                suggestedMealsSession = R.string.evening,
                                suggestedMealsSessionColor = Color(0xFF2962FF),
                                suggestedMealsSessionIcon = R.drawable.cloud_moon_fill,
                                suggestedMealsImg = R.drawable.img_food_default,
                                suggestedMeals = eveningMeals,
                                standardPadding = standardPadding
                            )

                            SuggestedMeals(
                                onClick = {
                                    activity?.let {
                                        val intent = Intent(it, MealsListActivity::class.java)
                                        intent.putExtra("SESSION_TOGGLE", R.string.snack)
                                        it.startActivity(intent)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                suggestedMealsSession = R.string.snack,
                                suggestedMealsSessionColor = Color(0xFF00BFA5),
                                suggestedMealsSessionIcon = R.drawable.circle_hexagongrid_fill,
                                suggestedMealsImg = R.drawable.img_food_default,
                                suggestedMeals = snackMeals,
                                standardPadding = standardPadding
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(standardPadding)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.figure_core_training),
                            contentDescription = stringResource(R.string.workout_suggestion),
                            modifier = Modifier.size(standardPadding * 2f),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = stringResource(R.string.workout_suggestion),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    val workoutSuggestion = listOf(
                        Triple(Pair(0, "Exercise 1"), Triple(15, 120f, 0), 0),
                        Triple(Pair(1, "Exercise 2"), Triple(30, 150f, 1), 1),
                        Triple(Pair(2, "Exercise 3"), Triple(45, 200f, 0), 2)
                    )

                    workoutSuggestion.forEach { aiExercise ->
                        WorkoutSuggestion(
                            session = when (aiExercise.first.first) {
                                0 -> R.string.morning
                                1 -> R.string.afternoon
                                else -> R.string.evening
                            },
                            exerciseName = aiExercise.first.second,
                            level = aiExercise.second.third,
                            intensity = aiExercise.third,
                            time = aiExercise.second.first,
                            calories = aiExercise.second.second,
                            onClickButton = {
                                activity?.let {
                                    val intent = Intent(it, ExerciseViewActivity::class.java)
                                    it.startActivity(intent)
                                }
                            },
                            standardPadding = standardPadding
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

@Composable
fun PlanningHeaderBar(
    rightButton: (@Composable () -> Unit)? = null,
    standardPadding: Dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(standardPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.leaf),
            contentDescription = "Planning",
            modifier = Modifier.size(standardPadding * 2f),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.nutrition_planning),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall
        )

        rightButton?.invoke()
    }
}

@Composable
fun CaloriesLineChart(
    weightData: List<Pair<String, Float>>,
    standardPadding: Dp,
    modifier: Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val min = weightData.minOf { it.second }
    val max = weightData.maxOf { it.second }

    val chartEntryModel = remember {
        ChartEntryModelProducer(
            weightData.mapIndexed { index, data ->
                entryOf(index.toFloat(), data.second)
            }
        )
    }

    val lineChart = lineChart(
        lines = listOf(
            LineChart.LineSpec(
                lineColor = MaterialTheme.colorScheme.outline.toArgb(),
                lineBackgroundShader = DynamicShaders.fromBrush(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                ),
                lineThicknessDp = 3f,
                lineCap = Paint.Cap.ROUND,
                dataLabel = textComponent(
                    color = MaterialTheme.colorScheme.onBackground,
                    background = ShapeComponent(
                        shape = Shapes.pillShape,
                        color = Color.Transparent.toArgb()
                    )
                ),
                dataLabelVerticalPosition = VerticalPosition.Top,
                point = ShapeComponent(
                    shape = Shapes.pillShape,
                    color = MaterialTheme.colorScheme.onBackground.toArgb()
                ),
                pointSizeDp = 10f,
            )
        ),
        axisValuesOverrider = AxisValuesOverrider.fixed(
            minY = min - (max - min),
            maxY = max + (max - min)
        )
    )

    val marker = rememberMarkerComponent()

    val chartScrollState = rememberChartScrollState()

    Column(
        modifier = modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.extraLarge
            )
            .clickable {
                activity?.let {
                    val intent = Intent(it, CalorieTodayActivity::class.java)
                    it.startActivity(intent)
                }
            },
        verticalArrangement = Arrangement.spacedBy(standardPadding),
    ) {
        Row(
            modifier = Modifier.padding(standardPadding * 2),
            horizontalArrangement = Arrangement.spacedBy(standardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.atom),
                contentDescription = stringResource(R.string.calories),
                modifier = Modifier.size(standardPadding * 2f),
                tint = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = stringResource(R.string.calories),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Text(
            text = "1200kcal",
            modifier = Modifier.padding(horizontal = standardPadding * 2),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displaySmall
        )

        Row(
            modifier = Modifier.padding(horizontal = standardPadding * 2)
        ) {
            Text(
                text = stringResource(R.string.today) + " ",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "+10%",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Chart(
            chart = lineChart,
            model = chartEntryModel.getModel(),
            modifier = Modifier.padding(standardPadding * 2),
            bottomAxis = rememberBottomAxis(
                label = textComponent(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                valueFormatter = { value, _ ->
                    weightData.getOrNull(value.toInt())?.first ?: ""
                },
                guideline = null
            ),
            marker = marker,
            isZoomEnabled = true,
            chartScrollState = chartScrollState
        )
    }
}

@Composable
fun WellnessTrackerCard(
    onClick: () -> Unit,
    title: String,
    details: String,
    standardPadding: Dp,
    modifier: Modifier
) {
    SubCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .height(standardPadding * 14)
                .padding(standardPadding * 2),
            verticalArrangement = Arrangement.spacedBy(standardPadding)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = details,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun NumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                minValue = range.first
                maxValue = range.last
                setOnValueChangedListener { _, _, newVal -> onValueChange(newVal) }
            }
        },
        update = { it.value = value }
    )
}

@Composable
fun SuggestedMeals(
    onClick: () -> Unit,
    modifier: Modifier,
    suggestedMealsSession: Int,
    suggestedMealsSessionColor: Color,
    suggestedMealsSessionIcon: Int,
    suggestedMealsImg: Int,
    suggestedMeals: List<String>,
    standardPadding: Dp
) {
    SubCard(
        onClick = { onClick() },
        modifier = modifier
    ) {
        Image(
            painter = painterResource(suggestedMealsImg),
            contentDescription = "Suggested meals",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier.padding(standardPadding),
            horizontalArrangement = Arrangement.spacedBy(standardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(suggestedMealsSessionIcon),
                contentDescription = stringResource(R.string.session),
                modifier = Modifier.size(standardPadding * 1.5f),
                tint = suggestedMealsSessionColor
            )

            Text(
                text = stringResource(suggestedMealsSession),
                color = suggestedMealsSessionColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            text = suggestedMeals.joinToString(),
            modifier = Modifier.padding(standardPadding),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun WorkoutSuggestion(
    session: Int,
    exerciseName: String,
    level: Int,
    intensity: Int,
    time: Int,
    calories: Float,
    onClickButton: () -> Unit,
    standardPadding: Dp
) {
    val levelString = when (level) {
        0 -> stringResource(R.string.amateur)
        1 -> stringResource(R.string.professional)
        else -> stringResource(R.string.unknown)
    }
    val intensityString = when (intensity) {
        0 -> stringResource(R.string.low)
        1 -> stringResource(R.string.medium)
        2 -> stringResource(R.string.high)
        else -> stringResource(R.string.unknown)
    }

    var showExerciseDetail = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(standardPadding)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(standardPadding / 4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemCard(
                onClick = { showExerciseDetail.value = !showExerciseDetail.value },
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(start = standardPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            when (session) {
                                R.string.morning -> R.drawable.cloud_sun_fill
                                R.string.afternoon -> R.drawable.sun_max_fill
                                else -> R.drawable.cloud_moon_fill
                            }
                        ),
                        contentDescription = stringResource(R.string.session),
                        modifier = Modifier.size(standardPadding * 2f),
                        tint = when (session) {
                            R.string.morning -> Color(0xFFFFAB00)
                            R.string.afternoon -> Color(0xFFDD2C00)
                            else -> Color(0xFF2962FF)
                        }
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(standardPadding),
                        verticalArrangement = Arrangement.spacedBy(standardPadding / 2)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = exerciseName,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(standardPadding / 4),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.timer),
                                        contentDescription = stringResource(R.string.time),
                                        modifier = Modifier.size(standardPadding),
                                        tint = Color(0xFF00C853)
                                    )

                                    Text(
                                        text = "$time ${stringResource(R.string.min)}",
                                        color = Color(0xFF00C853),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(standardPadding / 4),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.flame),
                                        contentDescription = stringResource(R.string.loaded),
                                        modifier = Modifier.size(standardPadding),
                                        tint = Color(0xFFDD2C00)
                                    )

                                    Text(
                                        text = "$calories ${stringResource(R.string.kcal)}",
                                        color = Color(0xFFDD2C00),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    ElevatedButton(
                        onClick = onClickButton,
                        shape = MaterialTheme.shapes.large.copy(
                            topStart = CornerSize(0f),
                            bottomStart = CornerSize(0f)
                        ),
                        modifier = Modifier.fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.start),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
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
private fun PlanningScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        PlanningScreen()
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun PlanningScreenPreviewInLargePhone() {
    BioFitTheme {
        PlanningScreen()
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
private fun PlanningScreenPreviewInTablet() {
    BioFitTheme {
        PlanningScreen()
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
private fun PlanningScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        PlanningScreen()
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun PlanningScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        PlanningScreen()
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
private fun PlanningScreenLandscapePreviewInTablet() {
    BioFitTheme {
        PlanningScreen()
    }
}