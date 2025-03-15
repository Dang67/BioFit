package com.example.biofit.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.biofit.R
import com.example.biofit.ui.theme.BioFitTheme

@Composable
fun SelectionDialogScreen() {
    val defaultLevel = stringResource(R.string.amateur)
    var level by rememberSaveable { mutableStateOf(defaultLevel) }
    var showLevelDialog by rememberSaveable { mutableStateOf(value = false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SelectionDialog(
            selectedOption = level,
            onOptionSelected = { selectedLevel ->
                level = selectedLevel
                showLevelDialog = false
            },
            onDismissRequest = { showLevelDialog = false },
            title = R.string.select_level,
            listOptions = listOf(
                stringResource(R.string.amateur),
                stringResource(R.string.professional)
            ),
            standardPadding = getStandardPadding().first
        )
    }
}

@Composable
fun SelectionDialog(
    selectedOption: String? = null,
    onOptionSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    title: Int,
    listOptions: List<String>,
    standardPadding: Dp
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        SubCard(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = screenHeight / 1.5f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(title),
                    modifier = Modifier.padding(standardPadding * 2),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(standardPadding * 2)
                ) {
                    items(listOptions) { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOptionSelected(option)
                                    onDismissRequest()
                                }
                                .padding(vertical = standardPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.fillMaxWidth(),
                                color = if (option == selectedOption) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground
                                },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
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
private fun DefaultDialogDarkModePreviewInSmallPhone() {
    BioFitTheme {
        SelectionDialogScreen()
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun DefaultDialogPreviewInLargePhone() {
    BioFitTheme {
        SelectionDialogScreen()
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
private fun DefaultDialogPreviewInTablet() {
    BioFitTheme {
        SelectionDialogScreen()
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
private fun DefaultDialogLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        SelectionDialogScreen()
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun DefaultDialogLandscapePreviewInLargePhone() {
    BioFitTheme {
        SelectionDialogScreen()
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
private fun DefaultDialogLandscapePreviewInTablet() {
    BioFitTheme {
        SelectionDialogScreen()
    }
}