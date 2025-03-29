package com.example.biofit.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biofit.R
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.utils.DailyLogSharedPrefsHelper
import com.example.biofit.navigation.OverviewActivity
import com.example.biofit.ui.activity.LoginActivity
import com.example.biofit.ui.activity.SettingActivity
import com.example.biofit.ui.activity.TargetActivity
import com.example.biofit.ui.components.DefaultDialog
import com.example.biofit.ui.components.SelectionDialog
import com.example.biofit.ui.components.SubCard
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.LoginViewModel
import com.example.biofit.view_model.UpdateUserViewModel
import com.example.biofit.ui.activity.UpgradeActivity


@Composable
fun ProfileScreen(userData: UserDTO) {
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileContent(
                userData = userData,
                standardPadding = standardPadding,
                modifier = modifier
            )
        }
    }

}

fun base64ToBitmap(base64String: String?): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (_: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ProfileContent(
    userData: UserDTO,
    standardPadding: Dp,
    modifier: Modifier,
    loginViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showSignOutDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDataDialog by rememberSaveable { mutableStateOf(false) }
    var showAvatarDialog by rememberSaveable { mutableStateOf(false) }
    val viewModel: UpdateUserViewModel = viewModel()


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            bitmap?.let {
                viewModel.setAvatar(it)
                viewModel.updateUser(context, userData.userId, loginViewModel) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.update_avatar_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                val bitmap = ImageDecoder.decodeBitmap(source)
                viewModel.setAvatar(bitmap)
                viewModel.updateUser(context, userData.userId, loginViewModel) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.update_avatar_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    )

    val avatarBitmap = viewModel.avatarBitmap.value ?: userData.avatar?.let { base64ToBitmap(it) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(standardPadding * 2),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = if (avatarBitmap != null) {
                        BitmapPainter(avatarBitmap.asImageBitmap()) // Dùng BitmapPainter nếu có avatar
                    } else {
                        painterResource(R.drawable.fake_avatar) // Dùng ảnh mặc định nếu không có avatar
                    },
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(standardPadding * 5)
                        .clip(CircleShape)
                        .clickable { showAvatarDialog = true },
                    contentScale = ContentScale.Crop
                )

                if (showAvatarDialog) {
                    SelectionDialog(
                        selectedOption = null,
                        onOptionSelected = { option ->
                            showAvatarDialog = false
                            when (option) {
                                context.getString(R.string.take_a_photo) -> cameraLauncher.launch(null)
                                context.getString(R.string.choose_from_gallery) -> galleryLauncher.launch("image/*")
                            }
                        },
                        onDismissRequest = { showAvatarDialog = false },
                        title = R.string.choose_how_to_set_avatar,
                        listOptions = listOf(
                            stringResource(R.string.take_a_photo),
                            stringResource(R.string.choose_from_gallery),
                        ),
                        standardPadding = standardPadding
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = standardPadding))
                {
                    Row {
                        Text(
                            text = userData.fullName ?: "N/A",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // cập nhật pro sau khi thanh toán thành công
                        Text(
                            text = userData.getSubscriptionStatus(context),
                            color = if (userData.getSubscriptionStatus(context) == "PRO")
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .background(
                                    color = if (userData.getSubscriptionStatus(context) == "PRO")
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )


                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row {
                        Text(
                            text = userData.getGenderString(context, userData.gender),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = " | ",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = userData.getAge(context, userData.dateOfBirth),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                IconButton(
                    onClick = {
                        activity?.let {
                            val intent = Intent(it, SettingActivity::class.java)
                            it.startActivity(intent)
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.gear),
                        contentDescription = stringResource(R.string.profile),
                        modifier = Modifier.size(standardPadding * 2),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        item {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(standardPadding)
            ) {
                Text(
                    text = stringResource(R.string.target),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )

                SubCard(modifier = Modifier) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(standardPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(standardPadding * 2),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.scalemass),
                                contentDescription = stringResource(R.string.starting_weight),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = stringResource(R.string.starting_weight),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = "${userData.weight} ${stringResource(R.string.kg)}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(standardPadding * 13),
                            color = MaterialTheme.colorScheme.background
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(standardPadding * 2),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.scalemass_fill),
                                contentDescription = stringResource(R.string.target_weight),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = stringResource(R.string.target_weight),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = standardPadding),
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = "${userData.targetWeight} ${stringResource(R.string.kg)}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = standardPadding),
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.background)

                    Column(
                        modifier = Modifier.clickable {
                            activity?.let {
                                val intent = Intent(it, TargetActivity::class.java)
                                it.startActivity(intent)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = standardPadding,
                                    horizontal = standardPadding * 2
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.target),
                                contentDescription = stringResource(R.string.target),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = stringResource(R.string.target),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = "Arrow right icon",
                                modifier = Modifier
                                    .size(standardPadding)
                                    .rotate(180f),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.background)

                    Column(
                        modifier = Modifier.clickable {
                            activity?.let {
                                val intent = Intent(it, OverviewActivity::class.java)
                                it.startActivity(intent)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = standardPadding,
                                    horizontal = standardPadding * 2
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.heart_text_clipboard),
                                contentDescription = stringResource(R.string.nutrition_report),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = Color(0xFFAA00FF)
                            )

                            Text(
                                text = stringResource(R.string.nutrition_report),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = "Arrow right icon",
                                modifier = Modifier
                                    .size(standardPadding)
                                    .rotate(180f),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
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
                    text = stringResource(R.string.term_of_use)
                            + " " + stringResource(R.string.and)
                            + " " + stringResource(R.string.privacy_policy),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )

                SubCard(modifier = Modifier) {
                    Column(
                        modifier = Modifier.clickable {
                            TODO()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = standardPadding,
                                    horizontal = standardPadding * 2
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_term_of_use),
                                contentDescription = stringResource(R.string.term_of_use),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = Color(0xFF2962FF)
                            )

                            Text(
                                text = stringResource(R.string.term_of_use),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = "Arrow right icon",
                                modifier = Modifier
                                    .size(standardPadding)
                                    .rotate(180f),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.background)

                    Column(
                        modifier = Modifier.clickable {
                            TODO()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = standardPadding,
                                    horizontal = standardPadding * 2
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_privacy_policy),
                                contentDescription = stringResource(R.string.privacy_policy),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = Color(0xFFC51162)
                            )

                            Text(
                                text = stringResource(R.string.privacy_policy),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = "Arrow right icon",
                                modifier = Modifier
                                    .size(standardPadding)
                                    .rotate(180f),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
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
                    text = stringResource(R.string.account),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )

                SubCard(modifier = Modifier) {
                    Column(
                        modifier = Modifier.clickable { showDeleteDataDialog = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = standardPadding,
                                    horizontal = standardPadding * 2
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete_data),
                                contentDescription = stringResource(R.string.delete_data),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = Color(0xFFFF6D00)
                            )

                            Text(
                                text = stringResource(R.string.delete_data),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (showDeleteDataDialog) {
                        DefaultDialog(
                            title = R.string.are_you_sure_you_want_to_delete_your_data,
                            description = R.string.des_delete_data,
                            actionTextButton = R.string.delete,
                            actionTextButtonColor = MaterialTheme.colorScheme.error,
                            onClickActionButton = {
                                if (activity != null) {
                                    deleteData(context, activity)
                                }
                            },
                            onCancelClick = { showDeleteDataDialog = false },
                            onDismissRequest = { showDeleteDataDialog = false },
                            standardPadding = standardPadding
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.background)

                    Column(
                        modifier = Modifier.clickable { showDeleteAccountDialog = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = standardPadding,
                                    horizontal = standardPadding * 2
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete_account),
                                contentDescription = stringResource(R.string.delete_account),
                                modifier = Modifier.size(standardPadding * 1.5f),
                                tint = Color(0xFFD50000)
                            )

                            Text(
                                text = stringResource(R.string.delete_account),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.background)

                    Column(
                        modifier = Modifier.clickable {
                            val intent = Intent(context, UpgradeActivity::class.java).apply {
                                putExtra("source", "ProfileScreen")
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = standardPadding,
                                    horizontal = standardPadding * 2
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_backup_24),
                                contentDescription = stringResource(R.string.upgrade_now),
                                tint = Color(0xFF05F6EA)
                            )

                            Text(
                                text = stringResource(R.string.upgrade_now),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = standardPadding),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (showDeleteAccountDialog) {
                        DefaultDialog(
                            title = R.string.are_you_sure_you_want_to_delete_your_account,
                            description = R.string.des_delete_account,
                            actionTextButton = R.string.delete,
                            actionTextButtonColor = MaterialTheme.colorScheme.error,
                            onClickActionButton = {
                                if (activity != null) {
                                    deleteAccount(context, activity)
                                }
                            },
                            onCancelClick = { showDeleteAccountDialog = false },
                            onDismissRequest = { showDeleteAccountDialog = false },
                            standardPadding = standardPadding
                        )
                    }
                }
            }
        }

        item {
            ElevatedButton(
                onClick = { showSignOutDialog = true },
                modifier = Modifier
                    .padding(top = standardPadding)
                    .widthIn(min = standardPadding * 10),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = stringResource(R.string.sign_out),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            if (showSignOutDialog) {
                DefaultDialog(
                    title = R.string.are_you_sure_you_want_to_sign_out,
                    description = null,
                    actionTextButton = R.string.sign_out,
                    actionTextButtonColor = MaterialTheme.colorScheme.secondary,
                    onClickActionButton = {
                        if (activity != null) {
                            DailyLogSharedPrefsHelper.clearDailyLog(context)
                            signOut(context, activity)
                        }
                    },
                    onCancelClick = { showSignOutDialog = false },
                    onDismissRequest = { showSignOutDialog = false },
                    standardPadding = standardPadding
                )
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

fun signOut(
    context: Context,
    activity: Activity
) {
    val sharedPreferences = activity.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Xóa dữ liệu đăng nhập (SharedPreferences)
    sharedPreferences.edit { clear() }

    // Đăng xuất Firebase (nếu dùng Firebase)
    //FirebaseAuth.getInstance().signOut()

    // Đăng xuất OAuth / API backend nếu có
    // AuthManager.logout()

    // Chuyển về màn hình đăng nhập và xóa toàn bộ backstack
    activity.let {
        val intent = Intent(it, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        it.startActivity(intent)
        it.finish() // Kết thúc activity hiện tại
    }

    Toast.makeText(
        activity,
        context.getString(R.string.signed_out_successfully),
        Toast.LENGTH_SHORT
    ).show()
}

fun deleteAccount(
    context: Context,
    activity: Activity
) {
    val sharedPreferences = activity.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit { clear() }
    activity.let {
        val intent = Intent(it, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        it.startActivity(intent)
        it.finish()
    }
    Toast.makeText(
        activity,
        context.getString(R.string.deleted_account_successfully),
        Toast.LENGTH_SHORT
    ).show()
}

fun deleteData(
    context: Context,
    activity: Activity
) {
    val sharedPreferences = activity.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit { clear() }
    activity.let {
        val intent = Intent(it, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        it.startActivity(intent)
        it.finish()
    }
    Toast.makeText(
        activity,
        context.getString(R.string.deleted_data_successfully),
        Toast.LENGTH_SHORT
    ).show()
}

@Preview(
    device = "id:pixel",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    locale = "vi"
)
@Composable
private fun ProfilePortraitScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        ProfileScreen(UserDTO.default())
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ProfilePortraitScreenPreviewInLargePhone() {
    BioFitTheme {
        ProfileScreen(UserDTO.default())
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
private fun ProfilePortraitScreenPreviewInTablet() {
    BioFitTheme {
        ProfileScreen(UserDTO.default())
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
private fun ProfileLandscapeScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        ProfileScreen(UserDTO.default())
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ProfileLandscapeScreenPreviewInLargePhone() {
    BioFitTheme {
        ProfileScreen(UserDTO.default())
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
private fun ProfileLandscapeScreenPreviewInTablet() {
    BioFitTheme {
        ProfileScreen(UserDTO.default())
    }
}