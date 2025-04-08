package com.example.biofit.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.biofit.R
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.LoginViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BioFitTheme {
                LoginScreen()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun LoginScreen() {
    val screenWidth = LocalConfiguration.current.screenWidthDp // lấy chiều rộng màn hình thiết bị
    val screenHeight = LocalConfiguration.current.screenHeightDp // lấy chiều cao màn hình thiết bị

    val standardPadding = getStandardPadding().first
    val modifier = getStandardPadding().second

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            content = {
                SignInAndSignUpBackground()
                LoginContent(
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    standardPadding = standardPadding,
                    modifier = modifier
                )
            }
        )
    }
}

@Composable
fun SignInAndSignUpBackground() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painter = if (isSystemInDarkTheme()) {
                painterResource(id = R.drawable.bg_sign_in_up_top_screen_dark_mode)
            } else {
                painterResource(id = R.drawable.bg_sign_in_up_top_screen)
            },
            contentDescription = "Login Screen Background Top",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillBounds
        )

        Image(
            painter = if (isSystemInDarkTheme()) {
                painterResource(id = R.drawable.bg_sign_in_up_bot_screen_dark_mode)
            } else {
                painterResource(id = R.drawable.bg_sign_in_up_bot_screen)
            },
            contentDescription = "Login Screen Background Bottom",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun LoginContent(
    screenWidth: Int,
    screenHeight: Int,
    standardPadding: Dp,
    modifier: Modifier
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                if (screenWidth > screenHeight) {
                    PaddingValues(
                        top = standardPadding,
                        start = standardPadding,
                        end = standardPadding,
                    )
                } else {
                    PaddingValues(
                        top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
                        start = standardPadding,
                        end = standardPadding,
                    )
                }
            ), // tuỳ chỉnh kích thước và vị trí của LazyColumn
        // state = , // tuỳ chỉnh trạng thái của LazyColumn
        // contentPadding = , // tuỳ chỉnh padding bên trong LazyColumn
        // reverseLayout = , // tuỳ chỉnh layout ngược lại
        verticalArrangement = Arrangement.Center, // sắp xếp các phần tử theo chiều dọc
        horizontalAlignment = Alignment.CenterHorizontally, // sắp xếp các phần tử theo chiều ngang
        // flingBehavior = , // tuỳ chỉnh hành vi cuộn
        // userScrollEnabled = , // tuỳ chỉnh xem có cho phép cuộn hay không
    ) {
        item {
            AppLogo(standardPadding)
            LoginForm(
                modifier = Modifier.padding(top = standardPadding),
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                standardPadding = standardPadding,
                modifier2 = modifier
            )
            TermsAndPrivacy(standardPadding)
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
fun AppLogo(standardPadding: Dp) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo",
        modifier = Modifier
            .fillMaxWidth()
            .size(standardPadding * 10f)
    )
}

@Composable
fun LoginForm(
    modifier: Modifier,
    screenWidth: Int,
    screenHeight: Int,
    standardPadding: Dp,
    modifier2: Modifier,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val context = LocalContext.current
        val loginMessage = viewModel.loginMessage.value

        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

        var emailError by remember { mutableStateOf<Int?>(null) }

        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = {
                viewModel.email.value = it
                emailError = if (!it.isValidEmail() && it.isNotEmpty()) {
                    R.string.enter_valid_email
                } else {
                    null
                }
            },
            modifier = modifier2.padding(top = standardPadding),
            label = { Text(text = stringResource(id = R.string.email)) },
            placeholder = { Text(text = stringResource(id = R.string.biofit_example_com)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.person_crop_square_filled_and_at_rectangle_fill),
                    contentDescription = stringResource(id = R.string.email),
                    modifier = Modifier.size(size = standardPadding * 1.5f)
                )
            },
            isError = emailError != null,
            supportingText = emailError?.let { { Text(stringResource(it)) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = { focusManager.clearFocus() },
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                errorLabelColor = Color(0xFFDD2C00),
                errorSupportingTextColor = Color(0xFFDD2C00),
                errorBorderColor = Color(0xFFDD2C00)
            ),
            shape = MaterialTheme.shapes.large
        )

        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            modifier = modifier2.padding(top = standardPadding),
            label = { Text(text = stringResource(id = R.string.password)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ellipsis_rectangle),
                    contentDescription = stringResource(id = R.string.password),
                    modifier = Modifier.size(size = standardPadding * 1.5f),
                )
            },
            trailingIcon = {
                Checkbox(
                    checked = passwordVisible,
                    onCheckedChange = { passwordVisible = it },
                    modifier = Modifier.padding(end = standardPadding / 2)
                )
            },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = { viewModel.loginUser(context) }),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (screenWidth > screenHeight) {
                Arrangement.Center
            } else {
                Arrangement.Start
            },
        ) {
            TextButton(
                onClick = {
                    val intent = Intent(context, ForgotPasswordActivity::class.java)
                    context.startActivity(intent)
                },
            ) {
                Text(
                    text = stringResource(id = R.string.forgot_password),
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }

        ElevatedButton(
            onClick = { viewModel.loginUser(context) },
            modifier = Modifier.padding(vertical = standardPadding),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(id = R.string.sign_in_uppercase),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        LaunchedEffect(loginMessage) {
            loginMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.loginMessage.value = null
            }
        }

        SignUpPrompt()
        SocialLoginButtons(
            standardPadding,
            modifier2
        )
    }
}

@Composable
fun SignUpPrompt() {
    val context = LocalContext.current
    val activity = context as? Activity

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.don_t_have_an_account),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall
        )
        TextButton(
            onClick = {
                activity?.let {
                    val intent = Intent(it, RegisterActivity::class.java)
                    it.startActivity(intent)
                    it.finish()
                }
            },
        ) {
            Text(
                text = stringResource(id = R.string.create_account),
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}

@Composable
fun SocialLoginButtons(
    standardPadding: Dp,
    modifier: Modifier
) {
    // Sign in with Google button
    ElevatedButton(
        onClick = { /* TODO */ },
        modifier = modifier
    ) {
        SocialLoginContent(
            iconId = R.drawable.ic_google,
            text = stringResource(id = R.string.sign_in_with_google)
        )
    }

    // Sign in with Facebook button
    ElevatedButton(
        onClick = { /* TODO */ },
        modifier = modifier.padding(top = standardPadding / 2)
    ) {
        SocialLoginContent(
            iconId = R.drawable.ic_facebook,
            text = stringResource(id = R.string.sign_in_with_facebook)
        )
    }
}

@Composable
fun SocialLoginContent(
    iconId: Int,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = iconId), // tuỳ chỉnh nguồn ảnh
            contentDescription = null, // tuỳ chỉnh mô tả nội dung của ảnh
            modifier = Modifier.size(size = getStandardPadding().first * 1.5f), // tuỳ chỉnh kích thước và vị trí của Icon
            tint = Color.Unspecified // tuỳ chỉnh màu sắc của Icon
        )

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TermsAndPrivacy(standardPadding: Dp) {
    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = standardPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = {
                    activity?.let {
                        val intent = Intent(it, TOUAndPPActivity::class.java)
                        intent.putExtra("title", context.getString(R.string.term_of_use))
                        intent.putExtra("from", "login_screen")
                        it.startActivity(intent)
                    }
                },
            ) {
                Text(
                    text = stringResource(id = R.string.term_of_use),
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }

            Text(
                text = stringResource(id = R.string.and),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall
            )

            TextButton(
                onClick = {
                    activity?.let {
                        val intent = Intent(it, TOUAndPPActivity::class.java)
                        intent.putExtra("title", context.getString(R.string.privacy_policy))
                        intent.putExtra("from", "login_screen")
                        it.startActivity(intent)
                    }
                },
            ) {
                Text(
                    text = stringResource(id = R.string.privacy_policy),
                    color = MaterialTheme.colorScheme.inversePrimary
                )
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
private fun LoginPortraitScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        LoginScreen()
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun LoginPortraitScreenPreviewInLargePhone() {
    BioFitTheme {
        LoginScreen()
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
private fun LoginPortraitScreenPreviewInTablet() {
    BioFitTheme {
        LoginScreen()
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
private fun LoginLandscapeScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        LoginScreen()
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun LoginLandscapeScreenPreviewInLargePhone() {
    BioFitTheme {
        LoginScreen()
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
private fun LoginLandscapeScreenPreviewInTablet() {
    BioFitTheme {
        LoginScreen()
    }
}