package com.example.biofit.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.biofit.R
import com.example.biofit.ui.theme.BioFitTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarSelector(
    onDaySelected: (LocalDate) -> Unit,
    standardPadding: Dp,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val startOfMonth = today.withDayOfMonth(1)
    val monthDays = (0 until today.lengthOfMonth()).map { startOfMonth.plusDays(it.toLong()) }

    var selectedDate by rememberSaveable { mutableStateOf(today) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        listState.scrollToItem(monthDays.indexOf(today))
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(standardPadding)
        ) {
            items(monthDays) { date ->
                val isSelected = date == selectedDate
                val isFutureDate = date.isAfter(today)

                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            }
                        )
                        .then(
                            if (date == today) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.medium
                                )
                            } else {
                                Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                    shape = MaterialTheme.shapes.medium
                                )
                            }
                        )
                        .alpha(if (isFutureDate) 0.5f else 1f)
                        .then(
                            if (isFutureDate) {
                                Modifier
                            } else {
                                Modifier.clickable {
                                    selectedDate = date
                                    onDaySelected(date)
                                }
                            }
                        )
                        .padding(standardPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        ),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = selectedDate != today,
            enter = slideInVertically { it } + fadeIn() + expandVertically(),
            exit = slideOutVertically { it } + fadeOut() + shrinkVertically()
        ) {
            TextButton(
                onClick = {
                    selectedDate = today
                    onDaySelected(today)
                    coroutineScope.launch {
                        listState.scrollToItem(monthDays.indexOf(today))
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.back_to_today),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_UNDEFINED)
@Composable
private fun CalendarSelectorDarkModePreview() {
    BioFitTheme {
        CalendarSelector(
            onDaySelected = { TODO() },
            standardPadding = getStandardPadding().first,
            modifier = getStandardPadding().second
        )
    }
}

@Preview
@Composable
private fun CalendarSelectorPreview() {
    BioFitTheme {
        CalendarSelector(
            onDaySelected = { TODO() },
            standardPadding = getStandardPadding().first,
            modifier = getStandardPadding().second
        )
    }
}