package com.example.biofit.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.biofit.R
import com.example.biofit.data.model.dto.ExerciseDTO
import com.example.biofit.ui.theme.BioFitTheme
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseItem(
    onDoClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    exercise: ExerciseDTO,
    standardPadding: Dp,
    modifier: Modifier,
    isExpanded: Boolean, // Nhận trạng thái mở từ bên ngoài
    onExpandChange: (Boolean) -> Unit // Callback để thay đổi trạng thái
) {
    val rotation = animatedRotation(
        targetRotation = if (isExpanded) 90f else 270f
    )

    Column(
        modifier = modifier.clickable { onExpandChange(!isExpanded) },
        verticalArrangement = Arrangement.spacedBy(standardPadding / 2),
    ) {
        Row(
            modifier = Modifier.padding(top = standardPadding),
            horizontalArrangement = Arrangement.spacedBy(standardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise.exerciseName,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )

            AnimatedVisibility(
                visible = !isExpanded,
                enter = slideInVertically { it } + fadeIn() + expandVertically(),
                exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(standardPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.timer),
                            contentDescription = stringResource(R.string.time),
                            modifier = Modifier.size(standardPadding),
                            tint = Color(0xFF00C853)
                        )

                        Text(
                            text = exercise.detailList[0].time.toString(),
                            color = Color(0xFF00C853),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.flame),
                            contentDescription = stringResource(R.string.burned_calories),
                            modifier = Modifier.size(standardPadding),
                            tint = Color(0xFFDD2C00)
                        )

                        Text(
                            text = exercise.detailList[0].burnedCalories.toString(),
                            color = Color(0xFFDD2C00),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "Add button",
                modifier = Modifier
                    .padding(vertical = standardPadding)
                    .size(standardPadding)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)

    AnimatedVisibility(
        visible = isExpanded,
        modifier = Modifier.padding(vertical = standardPadding / 2),
        enter = slideInVertically { it } + fadeIn() + expandVertically(),
        exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(standardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubCard(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(standardPadding),
                    verticalArrangement = Arrangement.spacedBy(standardPadding)
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) { }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = stringResource(R.string.low),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = stringResource(R.string.medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = stringResource(R.string.high),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.amateur),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.timer),
                                    contentDescription = stringResource(R.string.time),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFF00C853)
                                )

                                Text(
                                    text = exercise.detailList[0].time.toString(),
                                    color = Color(0xFF00C853),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.flame),
                                    contentDescription = stringResource(R.string.burned_calories),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFFDD2C00)
                                )

                                Text(
                                    text = exercise.detailList[0].burnedCalories.toString(),
                                    color = Color(0xFFDD2C00),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.timer),
                                    contentDescription = stringResource(R.string.time),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFF00C853)
                                )

                                Text(
                                    text = exercise.detailList[1].time.toString(),
                                    color = Color(0xFF00C853),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.flame),
                                    contentDescription = stringResource(R.string.burned_calories),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFFDD2C00)
                                )

                                Text(
                                    text = exercise.detailList[1].burnedCalories.toString(),
                                    color = Color(0xFFDD2C00),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.timer),
                                    contentDescription = stringResource(R.string.time),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFF00C853)
                                )

                                Text(
                                    text = exercise.detailList[2].time.toString(),
                                    color = Color(0xFF00C853),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.flame),
                                    contentDescription = stringResource(R.string.burned_calories),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFFDD2C00)
                                )

                                Text(
                                    text = exercise.detailList[2].burnedCalories.toString(),
                                    color = Color(0xFFDD2C00),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.professional),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.timer),
                                    contentDescription = stringResource(R.string.time),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFF00C853)
                                )

                                Text(
                                    text = exercise.detailList[3].time.toString(),
                                    color = Color(0xFF00C853),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.flame),
                                    contentDescription = stringResource(R.string.burned_calories),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFFDD2C00)
                                )

                                Text(
                                    text = exercise.detailList[3].burnedCalories.toString(),
                                    color = Color(0xFFDD2C00),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.timer),
                                    contentDescription = stringResource(R.string.time),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFF00C853)
                                )

                                Text(
                                    text = exercise.detailList[4].time.toString(),
                                    color = Color(0xFF00C853),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.flame),
                                    contentDescription = stringResource(R.string.burned_calories),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFFDD2C00)
                                )

                                Text(
                                    text = exercise.detailList[4].burnedCalories.toString(),
                                    color = Color(0xFFDD2C00),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.timer),
                                    contentDescription = stringResource(R.string.time),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFF00C853)
                                )

                                Text(
                                    text = exercise.detailList[5].time.toString(),
                                    color = Color(0xFF00C853),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.flame),
                                    contentDescription = stringResource(R.string.burned_calories),
                                    modifier = Modifier.size(standardPadding),
                                    tint = Color(0xFFDD2C00)
                                )

                                Text(
                                    text = exercise.detailList[5].burnedCalories.toString(),
                                    color = Color(0xFFDD2C00),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ElevatedButton(
                    onClick = { onDoClick() },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.figure_highintensity_intervaltraining),
                            contentDescription = stringResource(R.string.do_exercise),
                            modifier = Modifier.size(standardPadding * 1.5f),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                ElevatedButton(
                    onClick = { onEditClick() },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xFFFF6D00)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = stringResource(R.string.edit_exercise),
                            modifier = Modifier.size(standardPadding * 1.5f),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                ElevatedButton(
                    onClick = { onDeleteClick() },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xFFDD2C00)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
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

@Composable
fun OverviewExerciseCard(
    onDeleteClick: () -> Unit,
    startOfWeek: LocalDate,
    exerciseName: String,
    level: Int,
    intensity: Int,
    time: Float,
    calories: Float,
    session: Int,
    standardPadding: Dp,
    isExpanded: Boolean, // Nhận trạng thái mở từ bên ngoài
    onExpandChange: (Boolean) -> Unit // Callback để thay đổi trạng thái
) {
    val rotation = animatedRotation(
        targetRotation = if (isExpanded) 90f else 270f
    )

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

    ItemCard(
        onClick = { onExpandChange(!isExpanded) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(standardPadding),
            horizontalArrangement = Arrangement.spacedBy(standardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    when (session) {
                        0 -> R.drawable.cloud_sun_fill
                        1 -> R.drawable.sun_max_fill
                        else -> R.drawable.cloud_moon_fill
                    }
                ),
                contentDescription = stringResource(R.string.session),
                modifier = Modifier.size(standardPadding * 2f),
                tint = when (session) {
                    0 -> Color(0xFFFFAB00)
                    1 -> Color(0xFFDD2C00)
                    else -> Color(0xFF2962FF)
                }
            )

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
                AnimatedVisibility(
                    visible = !isExpanded,
                    enter = slideInVertically { it } + fadeIn() + expandVertically(),
                    exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                        verticalAlignment = Alignment.CenterVertically
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
                                text = "$time",
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
                                contentDescription = stringResource(R.string.calories),
                                modifier = Modifier.size(standardPadding),
                                tint = Color(0xFFDD2C00)
                            )

                            Text(
                                text = "$calories",
                                color = Color(0xFFDD2C00),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "Add button",
                modifier = Modifier
                    .size(standardPadding)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically { it } + fadeIn() + expandVertically(),
            exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
        ) {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(standardPadding),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(standardPadding / 4),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.decrease_quotelevel),
                                contentDescription = stringResource(R.string.level),
                                modifier = Modifier.size(standardPadding),
                                tint = Color(0xFFFFAB00)
                            )

                            Text(
                                text = levelString,
                                color = Color(0xFFFFAB00),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(standardPadding / 4),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.figure_highintensity_intervaltraining),
                                contentDescription = stringResource(R.string.intensity),
                                modifier = Modifier.size(standardPadding),
                                tint = Color(0xFF2962FF)
                            )

                            Text(
                                text = intensityString,
                                color = Color(0xFF2962FF),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

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
                                text = "$time",
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
                                contentDescription = stringResource(R.string.calories),
                                modifier = Modifier.size(standardPadding),
                                tint = Color(0xFFDD2C00)
                            )

                            Text(
                                text = "$calories",
                                color = Color(0xFFDD2C00),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                /*HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDeleteClick() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.trash),
                        contentDescription = stringResource(R.string.delete_exercise),
                        modifier = Modifier
                            .padding(standardPadding)
                            .size(standardPadding * 1.5f),
                        tint = Color(0xFFDD2C00)
                    )
                }*/
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_UNDEFINED,
    showBackground = true
)
@Composable
private fun ExerciseItemDarkModePreview() {
    BioFitTheme {
        Column(
            modifier = Modifier.padding(getStandardPadding().first),
            verticalArrangement = Arrangement.spacedBy(getStandardPadding().first)
        ) {
            ExerciseItem(
                onDoClick = {},
                onEditClick = {},
                onDeleteClick = {},
                exercise = ExerciseDTO.default(),
                standardPadding = getStandardPadding().first,
                modifier = getStandardPadding().second,
                isExpanded = false,
                onExpandChange = {}
            )

            OverviewExerciseCard(
                onDeleteClick = {},
                startOfWeek = LocalDate.now(),
                exerciseName = "Exercise 1",
                level = 0,
                intensity = 0,
                time = 30f,
                calories = 100f,
                session = 1,
                standardPadding = getStandardPadding().first,
                isExpanded = false,
                onExpandChange = {}
            )
        }
    }
}

@Composable
fun animatedRotation(
    targetRotation: Float,
    duration: Int = 500,
    easing: Easing = FastOutSlowInEasing
): Float {
    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = duration, easing = easing),
        label = "AnimatedRotation"
    )
    return rotation
}

@Preview(showBackground = true)
@Composable
private fun ExerciseItemPreview() {
    BioFitTheme {
        Column(
            modifier = Modifier.padding(getStandardPadding().first),
            verticalArrangement = Arrangement.spacedBy(getStandardPadding().first)
        ) {
            ExerciseItem(
                onDoClick = {},
                onEditClick = {},
                onDeleteClick = {},
                exercise = ExerciseDTO.default(),
                standardPadding = getStandardPadding().first,
                modifier = getStandardPadding().second,
                isExpanded = false,
                onExpandChange = {}
            )

            OverviewExerciseCard(
                onDeleteClick = {},
                startOfWeek = LocalDate.now(),
                exerciseName = "Exercise 1",
                level = 0,
                intensity = 0,
                time = 30f,
                calories = 100f,
                session = 1,
                standardPadding = getStandardPadding().first,
                isExpanded = false,
                onExpandChange = {}
            )
        }
    }
}