package com.example.biofit.ui.activity

import android.app.Activity
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.value = it },
            modifier = modifier2.padding(top = standardPadding),
            textStyle = MaterialTheme.typography.bodySmall,
            label = {
                Text(
                    text = stringResource(R.string.email),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.biofit_example_com),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.person_crop_square_filled_and_at_rectangle_fill),
                    contentDescription = stringResource(R.string.email),
                    modifier = Modifier.size(standardPadding * 1.5f)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = { focusManager.clearFocus() },
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            modifier = modifier2.padding(top = standardPadding),
            label = {
                Text(
                    text = stringResource(R.string.password),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            textStyle = MaterialTheme.typography.bodySmall,
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
            supportingText = {
                Text(
                    text = stringResource(R.string.min_8_chars_upper_lower_numbers),
                    style = MaterialTheme.typography.bodySmall
                )
            },
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
            shape = MaterialTheme.shapes.large
        )

        OutlinedTextField(
            value = viewModel.confirmPassword.value,
            onValueChange = { viewModel.confirmPassword.value = it },
            modifier = modifier2.padding(top = standardPadding),
            textStyle = MaterialTheme.typography.bodySmall,
            label = {
                Text(
                    text = stringResource(R.string.confirm_password),
                    style = MaterialTheme.typography.bodySmall
                )
            },
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
            supportingText = {
                Text(
                    stringResource(R.string.re_enter_password),
                    style = MaterialTheme.typography.bodySmall
                )
            },
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
            style = MaterialTheme.typography.bodySmall
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