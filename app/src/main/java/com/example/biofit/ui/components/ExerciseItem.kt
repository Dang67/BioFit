package com.example.biofit.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.biofit.R
import com.example.biofit.ui.theme.BioFitTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseItem(
    exercise: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    standardPadding: Dp,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        verticalArrangement = Arrangement.spacedBy(standardPadding / 2),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall
            )

            Image(
                painter = painterResource(R.drawable.btn_back),
                contentDescription = "Add button",
                modifier = Modifier
                    .padding(standardPadding)
                    .rotate(180f)
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun OverviewExerciseCard(
    exerciseName: String,
    time: Int,
    calories: Float,
    onClick: () -> Unit,
    standardPadding: Dp
) {
    ItemCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(standardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(standardPadding / 2)
            ) {
                Text(
                    text = exerciseName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "$time ${stringResource(R.string.min)}, " +
                            "$calories ${stringResource(R.string.cal)}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Image(
                painter = painterResource(R.drawable.btn_back),
                contentDescription = "Extend button",
                modifier = Modifier.rotate(180f)
            )
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
                exercise = "Exercise 1",
                onClick = {},
                onLongClick = {},
                standardPadding = getStandardPadding().first,
                modifier = getStandardPadding().second
            )

            OverviewExerciseCard(
                exerciseName = "Exercise 1",
                time = 30,
                calories = 100f,
                onClick = {},
                standardPadding = getStandardPadding().first
            )
        }
    }
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
                exercise = "Exercise 1",
                onClick = {},
                onLongClick = {},
                standardPadding = getStandardPadding().first,
                modifier = getStandardPadding().second
            )

            OverviewExerciseCard(
                exerciseName = "Exercise 1",
                time = 30,
                calories = 100f,
                onClick = {},
                standardPadding = getStandardPadding().first
            )
        }
    }
}