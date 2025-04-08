package com.example.biofit.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
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
import com.example.biofit.view_model.RegisterViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BioFitTheme {
                RegisterScreen()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun RegisterScreen() {
    val standardPadding = getStandardPadding().first
    val modifier = getStandardPadding().second

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box {
            SignInAndSignUpBackground()
            RegisterContent(
                standardPadding,
                modifier
            )
        }
    }
}

@Composable
fun RegisterContent(
    standardPadding: Dp,
    modifier: Modifier
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
                start = standardPadding,
                end = standardPadding,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            AppLogo(standardPadding)
            RegisterForm(
                modifier = Modifier.padding(top = standardPadding),
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
fun RegisterForm(
    modifier: Modifier = Modifier,
    standardPadding: Dp,
    modifier2: Modifier,
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var emailError by remember { mutableStateOf<Int?>(null) }
    var passwordError by remember { mutableStateOf<Int?>(null) }
    var confirmPasswordError by remember { mutableStateOf<Int?>(null) }

    val passwordStrength = remember(viewModel.password.value) {
        when {
            viewModel.password.value.isEmpty() -> 0
            viewModel.password.value.length < 6 -> 1
            viewModel.password.value.length >= 8 &&
                    viewModel.password.value.any { it.isDigit() } &&
                    viewModel.password.value.any { it.isUpperCase() } -> 3

            else -> 2
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

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
            label = { Text(text = stringResource(R.string.email)) },
            placeholder = { Text(text = stringResource(R.string.biofit_example_com)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.person_crop_square_filled_and_at_rectangle_fill),
                    contentDescription = stringResource(R.string.email),
                    modifier = Modifier.size(standardPadding * 1.5f)
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
            onValueChange = {
                viewModel.password.value = it
                passwordError = if (it.length < 6) {
                    R.string.min_8_chars_upper_lower_numbers
                } else {
                    null
                }
                confirmPasswordError =
                    if (
                        viewModel.confirmPassword.value.isNotEmpty() &&
                        viewModel.confirmPassword.value != it
                    ) {
                        R.string.passwords_do_not_match
                    } else {
                        null
                    }
            },
            modifier = modifier2.padding(top = standardPadding),
            label = { Text(text = stringResource(R.string.password)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ellipsis_rectangle),
                    contentDescription = stringResource(R.string.password),
                    modifier = Modifier.size(standardPadding * 1.5f)
                )
            },
            trailingIcon = {
                Checkbox(
                    checked = passwordVisible,
                    onCheckedChange = { passwordVisible = it },
                    modifier = Modifier.padding(end = standardPadding / 2)
                )
            },
            isError = passwordError != null,
            supportingText = if (passwordError != null || viewModel.password.value.isNotEmpty()) {
                {
                    Column {
                        passwordError?.let { Text(stringResource(it)) }
                        if (viewModel.password.value.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            if (passwordStrength >= 1)
                                                when (passwordStrength) {
                                                    1 -> Color(0xFFDD2C00)
                                                    2 -> Color(0xFFFFAB00)
                                                    3 -> MaterialTheme.colorScheme.primary
                                                    else -> Color.Transparent
                                                }
                                            else Color.Gray.copy(alpha = 0.3f)
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            if (passwordStrength >= 2)
                                                when (passwordStrength) {
                                                    2 -> Color(0xFFFFAB00)
                                                    3 -> MaterialTheme.colorScheme.primary
                                                    else -> Color.Transparent
                                                }
                                            else Color.Gray.copy(alpha = 0.3f)
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            if (passwordStrength >= 3) MaterialTheme.colorScheme.primary
                                            else Color.Gray.copy(alpha = 0.3f)
                                        )
                                )
                            }
                            Text(
                                text = when (passwordStrength) {
                                    1 -> stringResource(R.string.weak_password)
                                    2 -> stringResource(R.string.medium_password)
                                    3 -> stringResource(R.string.strong_password)
                                    else -> ""
                                },
                                color = when (passwordStrength) {
                                    1 -> Color(0xFFDD2C00)
                                    2 -> Color(0xFFFFAB00)
                                    3 -> MaterialTheme.colorScheme.primary
                                    else -> Color.Transparent
                                },
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else null,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
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
            value = viewModel.confirmPassword.value,
            onValueChange = {
                viewModel.confirmPassword.value = it
                confirmPasswordError = if (it != viewModel.password.value) {
                    R.string.passwords_do_not_match
                } else {
                    null
                }
            },
            modifier = modifier2.padding(top = standardPadding),
            label = { Text(text = stringResource(R.string.confirm_password)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ellipsis_rectangle_fill),
                    contentDescription = stringResource(R.string.confirm_password),
                    modifier = Modifier.size(standardPadding * 1.5f)
                )
            },
            trailingIcon = {
                Checkbox(
                    checked = confirmPasswordVisible,
                    onCheckedChange = { confirmPasswordVisible = it },
                    modifier = Modifier.padding(end = standardPadding / 2)
                )
            },
            isError = confirmPasswordError != null,
            supportingText = { confirmPasswordError?.let { Text(stringResource(it)) } },
            visualTransformation = if (confirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                errorLabelColor = Color(0xFFDD2C00),
                errorSupportingTextColor = Color(0xFFDD2C00),
                errorBorderColor = Color(0xFFDD2C00)
            ),
            shape = MaterialTheme.shapes.large
        )

        val context = LocalContext.current

        ElevatedButton(
            onClick = { viewModel.registerUser(context, loginViewModel) },
            modifier = Modifier.padding(vertical = standardPadding),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.sign_up_uppercase),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        val registerMessage = viewModel.registerMessage.value
        LaunchedEffect(registerMessage) {
            registerMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.registerMessage.value = null
            }
        }

        SignInPrompt()
        SocialLoginButtons(
            standardPadding,
            modifier2
        )
    }
}

@Composable
fun SignInPrompt() {
    val context = LocalContext.current
    val activity = context as? Activity

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.have_an_account),
            style = MaterialTheme.typography.titleSmall
        )
        TextButton(
            onClick = {
                activity?.let {
                    val intent = Intent(it, LoginActivity::class.java)
                    it.startActivity(intent)
                    it.finish()
                }
            },
        ) {
            Text(
                text = stringResource(R.string.login_account),
                color = MaterialTheme.colorScheme.inversePrimary
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
private fun RegisterPortraitScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        RegisterScreen()
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun RegisterPortraitScreenPreviewInLargePhone() {
    BioFitTheme {
        RegisterScreen()
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
private fun RegisterPortraitScreenPreviewInTablet() {
    BioFitTheme {
        RegisterScreen()
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
private fun RegisterLandscapeScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        RegisterScreen()
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun RegisterLandscapeScreenPreviewInLargePhone() {
    BioFitTheme {
        RegisterScreen()
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
private fun RegisterLandscapeScreenPreviewInTablet() {
    BioFitTheme {
        RegisterScreen()
    }
}