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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat.startActivity
import com.example.biofit.R
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.navigation.MainActivity
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.LoginViewModel
import com.example.biofit.view_model.UpdateUserViewModel

class InfoUserTargetActivity : ComponentActivity() {
    private var userData: UserDTO? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userData = UserSharedPrefsHelper.getUserData(this)
        setContent {
            BioFitTheme {
                InfoUserTargetScreen(userData ?: UserDTO.default())
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun InfoUserTargetScreen(
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
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary
                    ),
                    screenWidth,
                    screenHeight,
                    standardPadding
                )
                InfoUserTargetContent(
                    standardPadding,
                    modifier,
                    viewModel
                )
            }

            val userId = userData.userId
            NextButtonInfoScreen(
                onClick = {
                    viewModel.updateUser(context, userId, loginViewModel) {
//                        val intent = Intent(context, UpgradeActivity::class.java)
//                        context.startActivity(intent)

                        val intent = Intent(context, UpgradeActivity::class.java)
                        intent.putExtra("source", "InfoUserTargetActivity")
                        context.startActivity(intent)
                    }
                },
                standardPadding = standardPadding
            )
        }
    }
}

@Composable
fun InfoUserTargetContent(
    standardPadding: Dp,
    modifier: Modifier,
    viewModel: UpdateUserViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val targetWeight = viewModel.targetWeight.value?.toString() ?: ""
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = stringResource(R.string.what_is_your_weight_goal),
                modifier = Modifier.padding(top = standardPadding * 3),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall
            )
        }

        item {
            Text(
                text = stringResource(R.string.description_target),
                modifier = Modifier.padding(bottom = standardPadding),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }

        item {
            OutlinedTextField(
                value = targetWeight,
                onValueChange = {
                    viewModel.targetWeight.value = it.toFloatOrNull()
                }, // xử lý thay đổi giá trị và cập nhật trạng thái tương ứng
                modifier = modifier, // kích thước và vị trí của trường nhập liệu
                // enabled = true, // trạng thái kích hoạt của trường nhập liệu (mặc định true)
                // readOnly = false, // trạng thái chỉ đọc của trường nhập liệu (mặc định false)
                textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.End), // kiểu chữ và kích thước của văn bản trong trường nhập liệu
                //label = { Text(text = stringResource(R.string.target_weight)) }, // nhãn cho trường nhập liệu
                // placeholder = null, // văn bản gợi ý bên trong trường nhập liệu (mặc định null)
                // leadingIcon = null, // biểu tượng trước văn bản (mặc định null)
                // trailingIcon = null, // biểu tượng sau văn bản (mặc định null)
                prefix = {
                    Text(
                        text = stringResource(R.string.target),
                        style = MaterialTheme.typography.bodySmall
                    )
                }, // tiền tố văn bản (mặc định null)
                suffix = {
                    Text(
                        text = stringResource(R.string.kg),
                        style = MaterialTheme.typography.bodySmall
                    )
                }, // hậu tố văn bản
                // supportingText = null, // văn bản trợ giúp dưới trường nhập liệu (mặc định null)
                //isError = false, // trạng thái lỗi
                // visualTransformation = VisualTransformation.None, // biến đổi hiển thị của văn bản (mặc định VisualTransformation.None)
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Go
                ), // kiểu bàn phím
                keyboardActions = KeyboardActions(onGo = { /*TODO*/ }), // hành động khi nhấn phím
                singleLine = true, // chỉ cho phép nhập một dòng văn bản
                //maxLines = 1, // số lượng dòng tối đa cho văn bản
                // minLines = 1, // số lượng dòng tối thiểu cho văn bản (mặc định 1)
                shape = MaterialTheme.shapes.large // hình dạng của trường nhập liệu
                /*colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer
                )*/ // màu sắc của trường nhập liệu
            )
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
private fun InfoUserTargetPortraitScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        InfoUserTargetScreen(UserDTO.default())
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun InfoUserTargetPortraitScreenPreviewInLargePhone() {
    BioFitTheme {
        InfoUserTargetScreen(UserDTO.default())
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
private fun InfoUserTargetPortraitScreenPreviewInTablet() {
    BioFitTheme {
        InfoUserTargetScreen(UserDTO.default())
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
private fun InfoUserTargetLandscapeScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        InfoUserTargetScreen(UserDTO.default())
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun InfoUserTargetLandscapeScreenPreviewInLargePhone() {
    BioFitTheme {
        InfoUserTargetScreen(UserDTO.default())
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
private fun InfoUserTargetLandscapeScreenPreviewInTablet() {
    BioFitTheme {
        InfoUserTargetScreen(UserDTO.default())
    }
}