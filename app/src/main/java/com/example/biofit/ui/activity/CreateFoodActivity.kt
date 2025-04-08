package com.example.biofit.ui.activity

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.ui.components.SelectionDialog
import com.example.biofit.ui.components.TopBar
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.FoodViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreateFoodActivity : ComponentActivity() {
    private var userData: UserDTO? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userData = UserSharedPrefsHelper.getUserData(this)
        enableEdgeToEdge()
        val session = intent.getIntExtra("SESSION", R.string.morning)
        setContent {
            BioFitTheme {
                CreateFoodScreen(userData = userData ?: UserDTO.default(), session = session)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun CreateFoodScreen(
    userData: UserDTO,
    FoodViewModel : FoodViewModel = viewModel(),
    session: Int
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val sessionString = stringResource(session)
    var foodName by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var mass by remember { mutableStateOf("") }
    val defaultMass = stringResource(R.string.gram)
    var selectedUnitMeasure by remember { mutableStateOf(defaultMass) }
    var showUnitDialog by remember { mutableStateOf(false) }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbohydrate by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var sodium by remember { mutableStateOf("") }

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
                title = stringResource(R.string.create_new_food),
                middleButton = null,
                rightButton = null,
                standardPadding = standardPadding
            )

            CreateFoodContent(
                session = sessionString,
                FoodViewModel = FoodViewModel,
                userData = userData,
                standardPadding = standardPadding,
                modifier = modifier,
                foodName = foodName,
                servingSize = servingSize,
                mass = mass,
                selectedUnitMeasure = selectedUnitMeasure,
                showUnitDialog = showUnitDialog,
                calories = calories,
                protein = protein,
                carbohydrate = carbohydrate,
                fat = fat,
                sodium = sodium,
                onFoodNameChange = { foodName = it },
                onServingSizeChange = { servingSize = it },
                onMassChange = { mass = it },
                onSelectedUnitMeasureChange = { selectedUnitMeasure = it },
                onShowUnitDialogChange = { showUnitDialog = it },
                onCaloriesChange = { calories = it },
                onProteinChange = { protein = it },
                onCarbohydrateChange = { carbohydrate = it },
                onFatChange = { fat = it },
                onSodiumChange = { sodium = it }

            )
        }
    }
}

@Composable
fun CreateFoodContent(
    session: String,
    FoodViewModel: FoodViewModel,
    userData: UserDTO,
    standardPadding: Dp,
    modifier: Modifier,
    foodName: String,
    servingSize: String,
    mass: String,
    selectedUnitMeasure: String,
    showUnitDialog: Boolean,
    calories: String,
    protein: String,
    carbohydrate: String,
    fat: String,
    sodium: String,
    onFoodNameChange: (String) -> Unit,
    onServingSizeChange: (String) -> Unit,
    onMassChange: (String) -> Unit,
    onSelectedUnitMeasureChange: (String) -> Unit,
    onShowUnitDialogChange: (Boolean) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onCarbohydrateChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onSodiumChange: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val currentDate = LocalDate.now().format(formatter)

    val userId = userData.userId

    val focusManager = LocalFocusManager.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(standardPadding * 2),
    ) {
        item {
            Column(
                modifier = modifier.padding(top = standardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.img_food_default),
                    contentDescription = "Food image",
                    modifier = Modifier
                        .size(standardPadding * 15)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                )

                TextButton(
                    onClick = { TODO() }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(standardPadding / 2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_plus),
                            contentDescription = "Add button",
                            modifier = Modifier.size(standardPadding),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = stringResource(R.string.upload_new_photo),
                            color = MaterialTheme.colorScheme.primary
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
                Text(
                    text = stringResource(R.string.nutrition_information),
                    style = MaterialTheme.typography.titleMedium
                )

                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(standardPadding)
                ) {
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = onFoodNameChange,
                        modifier = modifier,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.enter_food_name),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        },
                        prefix = { Text(text = stringResource(R.string.food_name) + "*") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )

                    OutlinedTextField(
                        value = servingSize,
                        onValueChange = onServingSizeChange,
                        modifier = modifier,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.enter_serving_size),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        },
                        prefix = { Text(text = stringResource(R.string.serving_size)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )

                    OutlinedTextField(
                        value = mass,
                        onValueChange =  onMassChange,
                        modifier = modifier,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        prefix = { Text(text = stringResource(R.string.mass)) },
                        trailingIcon = {
                            IconButton(onClick = { onShowUnitDialogChange(true) })  {
                                Icon(
                                    painter = painterResource(R.drawable.ic_back),
                                    contentDescription = stringResource(R.string.unit_of_measure),
                                    modifier = Modifier
                                        .rotate(270f)
                                        .size(standardPadding),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        suffix = { Text(text = selectedUnitMeasure) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )

                    if (showUnitDialog) {
                        SelectionDialog(
                            onOptionSelected = { selectedUnit ->
                                onSelectedUnitMeasureChange(selectedUnit) // Cập nhật đơn vị đo lường
                                onShowUnitDialogChange(false) // Đóng dialog đúng cách
                            },
                            onDismissRequest = { onShowUnitDialogChange(false) },
                            title = R.string.select_unit_of_measure,
                            listOptions = listOf(
                                stringResource(R.string.gram),
                                stringResource(R.string.milligram)
                            ),
                            standardPadding = standardPadding
                        )
                    }

                    OutlinedTextField(
                        value = calories,
                        onValueChange = onCaloriesChange,
                        modifier = modifier,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        prefix = { Text(text = stringResource(R.string.calories) + "*") },
                        suffix = { Text(text = stringResource(R.string.kcal)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )

                    OutlinedTextField(
                        value = protein,
                        onValueChange =  onProteinChange,
                        modifier = modifier,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        prefix = { Text(text = stringResource(R.string.protein)) },
                        suffix = { Text(text = stringResource(R.string.gram)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )

                    OutlinedTextField(
                        value = carbohydrate,
                        onValueChange = onCarbohydrateChange,
                        modifier = modifier,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        prefix = { Text(text = stringResource(R.string.carbohydrate)) },
                        suffix = { Text(text = stringResource(R.string.gram)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )

                    OutlinedTextField(
                        value = fat,
                        onValueChange =   onFatChange,
                        modifier = modifier,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        prefix = { Text(text = stringResource(R.string.fat)) },
                        suffix = { Text(text = stringResource(R.string.gram)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )

                    OutlinedTextField(
                        value = sodium,
                        onValueChange =   onSodiumChange,
                        modifier = modifier,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        prefix = { Text(text = stringResource(R.string.sodium)) },
                        suffix = { Text(text = stringResource(R.string.milligram)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(onGo = { TODO() }),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )
                }
            }
        }

        item {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ElevatedButton(
                    onClick = {

                        // Kiểm tra tên món ăn
                        if (foodName.isBlank()) {
                            Toast.makeText(context, "Tên món ăn không thể để trống", Toast.LENGTH_SHORT).show()
                            return@ElevatedButton
                        }

                        // Kiểm tra kích thước phục vụ
                        if (servingSize.isBlank() || servingSize.toFloatOrNull() == null || servingSize.toFloatOrNull()!! <= 0f) {
                            Toast.makeText(context, "Kích thước khẩu phần không hợp lệ", Toast.LENGTH_SHORT).show()
                            return@ElevatedButton
                        }

                        // Kiểm tra khối lượng
                        if (mass.isBlank() || mass.toFloatOrNull() == null || mass.toFloatOrNull()!! <= 0f) {
                            Toast.makeText(context, "Khối lượng không hợp lệ", Toast.LENGTH_SHORT).show()
                            return@ElevatedButton
                        }

                        // Kiểm tra calo
                        if (calories.isBlank() || calories.toFloatOrNull() == null || calories.toFloatOrNull()!! <= 0f) {
                            Toast.makeText(context, "Giá trị calo không hợp lệ", Toast.LENGTH_SHORT).show()
                            return@ElevatedButton
                        }

                        // Kiểm tra protein
                        if (protein.isBlank() || protein.toFloatOrNull() == null || protein.toFloatOrNull()!! <= 0f) {
                            Toast.makeText(context, "Giá trị protein không hợp lệ", Toast.LENGTH_SHORT).show()
                            return@ElevatedButton
                        }

                        // Kiểm tra carbohydrate
                        if (carbohydrate.isBlank() || carbohydrate.toFloatOrNull() == null || carbohydrate.toFloatOrNull()!! <= 0f) {
                            Toast.makeText(context, "Giá trị carbohydrate không hợp lệ", Toast.LENGTH_SHORT).show()
                            return@ElevatedButton
                        }

                        // Kiểm tra fat
                        if (fat.isBlank() || fat.toFloatOrNull() == null || fat.toFloatOrNull()!! <= 0f) {
                            Toast.makeText(context, "Giá trị chất béo không hợp lệ", Toast.LENGTH_SHORT).show()
                            return@ElevatedButton
                        }

                        // Kiểm tra sodium
                        if (sodium.isBlank() || sodium.toFloatOrNull() == null || sodium.toFloatOrNull()!! <= 0f) {
                            Toast.makeText(context, "Giá trị sodium không hợp lệ", Toast.LENGTH_SHORT).show()
                            return@ElevatedButton
                        }

                        val foodDTO = FoodDTO(
                            userId = userId,
                            foodId = 0, // ID có thể do backend tạo
                            foodName = foodName,
                            session = session,
                            date = currentDate,
                            foodImage = "",
                            servingSize = servingSize.toFloatOrNull() ?: 0f,
                            servingSizeUnit = selectedUnitMeasure,
                            mass = mass.toFloatOrNull() ?: 0f,
                            calories = calories.toFloatOrNull() ?: 0f,
                            protein = protein.toFloatOrNull() ?: 0f,
                            carbohydrate = carbohydrate.toFloatOrNull() ?: 0f,
                            fat = fat.toFloatOrNull() ?: 0f,
                            sodium = sodium.toFloatOrNull() ?: 0f
                        )
                        FoodViewModel.createFood(foodDTO)
                        activity?.finish() // Đóng màn hình sau khi lưu
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.save_food),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
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

@Preview(
    device = "id:pixel",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    locale = "vi"
)
@Composable
private fun CreateFoodScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        CreateFoodScreen(UserDTO.default(), session = R.string.morning)
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun CreateFoodScreenPreviewInLargePhone() {
    BioFitTheme {
        CreateFoodScreen(UserDTO.default(), session = R.string.morning)
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
private fun CreateFoodScreenPreviewInTablet() {
    BioFitTheme {
        CreateFoodScreen(UserDTO.default(), session = R.string.morning)
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
private fun CreateFoodScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        CreateFoodScreen(UserDTO.default(), session = R.string.morning)
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun CreateFoodScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        CreateFoodScreen(UserDTO.default(), session = R.string.morning)
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
private fun CreateFoodScreenLandscapePreviewInTablet() {
    BioFitTheme {
        CreateFoodScreen(UserDTO.default(), session = R.string.morning)
    }
}