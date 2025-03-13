package com.example.biofit.ui.activity

import android.app.Activity
import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.biofit.R
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.ui.components.ItemCard
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.LoginViewModel
import com.example.biofit.view_model.UpdateUserViewModel
import java.util.Calendar

class InfoUserBirthdayActivity : ComponentActivity() {
    private var userData: UserDTO? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userData = UserSharedPrefsHelper.getUserData(this)
        setContent {
            BioFitTheme {
                InfoUserBirthdayScreen(userData ?: UserDTO.default())
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun InfoUserBirthdayScreen(
    userData: UserDTO,
    viewModel: UpdateUserViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val standardPadding = getStandardPadding().first
    val modifier = getStandardPadding().second

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box {
            BackgroundInfoScreen()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
                        start = standardPadding,
                        end = standardPadding,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TopBarInfoScreen(
                    onBackClick = { activity?.finish() },
                    stepColors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.secondary
                    ),
                    screenWidth,
                    screenHeight,
                    standardPadding
                )
                InfoUserBirthdayContent(
                    standardPadding,
                    modifier
                )
            }

            val userId = userData.userId
            NextButtonInfoScreen(
                onClick = {
                    viewModel.updateUser(context, userId, loginViewModel) {
                        val intent = Intent(context, InfoUserHeightAndWeightActivity::class.java)
                        context.startActivity(intent)
                    }
                },
                standardPadding = standardPadding
            )
        }
    }
}

@Composable
fun InfoUserBirthdayContent(
    standardPadding: Dp,
    modifier: Modifier,
    viewModel: UpdateUserViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = stringResource(id = R.string.what_is_your_date_of_birth),
                modifier = Modifier.padding(top = standardPadding * 3),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall
            )
        }

        item {
            Text(
                text = stringResource(R.string.description_date_of_birth),
                modifier = Modifier.padding(bottom = standardPadding),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }

        item {

            var showDatePicker by rememberSaveable { mutableStateOf(false) }
            val dateOfBirth = viewModel.dateOfBirth.value ?: ""
            ItemCard(
                onClick = { showDatePicker = true },
                modifier = modifier
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = standardPadding, vertical = standardPadding / 4),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateOfBirth.ifEmpty {
                            stringResource(R.string.select_date_of_birth)
                        },
                        modifier = Modifier.weight(1f),
                        color = if (dateOfBirth.isEmpty()) {
                            MaterialTheme.colorScheme.outline
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )

                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.date_of_birth),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (showDatePicker) {
                val context = LocalContext.current
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val minYear = 1920 // Giới hạn năm tối thiểu

                DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        if (selectedYear > currentYear) {
                            // Không cho phép chọn năm sinh trong tương lai
                            Toast.makeText(context, "Bạn không thể chọn năm sinh trong tương lai!", Toast.LENGTH_SHORT).show()
                        } else if (selectedYear < minYear) {
                            // Không cho phép chọn năm sinh quá xa trong quá khứ
                            Toast.makeText(context, "Năm sinh không hợp lệ! Vui lòng chọn từ $minYear trở đi.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Định dạng ngày và tháng thành 2 chữ số
                            val formattedMonth = String.format("%02d", selectedMonth + 1) // Tháng bắt đầu từ 0 nên +1
                            val formattedDay = String.format("%02d", selectedDay)
                            val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"

                            // Lưu ngày sinh vào ViewModel
                            viewModel.dateOfBirth.value = selectedDate
                        }

                        showDatePicker = false
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    // Giới hạn ngày được chọn trong khoảng hợp lệ
                    datePicker.minDate = Calendar.getInstance().apply { set(minYear, 0, 1) }.timeInMillis // Từ 01/01/1920
                    datePicker.maxDate = calendar.timeInMillis // Đến ngày hiện tại
                }.show()

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

@Preview(
    device = "id:pixel",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    locale = "vi"
)
@Composable
private fun InfoUserBirthdayPortraitScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        InfoUserBirthdayScreen(UserDTO.default())
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun InfoUserBirthdayPortraitScreenPreviewInLargePhone() {
    BioFitTheme {
        InfoUserBirthdayScreen(UserDTO.default())
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
private fun InfoUserBirthdayPortraitScreenPreviewInTablet() {
    BioFitTheme {
        InfoUserBirthdayScreen(UserDTO.default())
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
private fun InfoUserBirthdayLandscapeScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        InfoUserBirthdayScreen(UserDTO.default())
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun InfoUserBirthdayLandscapeScreenPreviewInLargePhone() {
    BioFitTheme {
        InfoUserBirthdayScreen(UserDTO.default())
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
private fun InfoUserBirthdayLandscapeScreenPreviewInTablet() {
    BioFitTheme {
        InfoUserBirthdayScreen(UserDTO.default())
    }
}