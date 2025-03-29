package com.example.biofit.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.biofit.R
import com.example.biofit.data.utils.UserSharedPrefsHelper
import com.example.biofit.navigation.MainActivity
import com.example.biofit.ui.theme.BioFitTheme
import com.example.biofit.view_model.PaymentViewModel
import kotlinx.coroutines.delay


class PaymentWebViewActivity : ComponentActivity() {
    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = intent?.data
        if (uri != null && uri.toString().startsWith("biofit://payment/callback")) {
            // Trích xuất các tham số truy vấn
            val queryParameters = uri.queryParameterNames
            val paramMap = mutableMapOf<String, String>()

            for (name in queryParameters) {
                uri.getQueryParameter(name)?.let { value ->
                    paramMap[name] = value
                }
            }

            val responseCode = paramMap["vnp_ResponseCode"]
            val orderId = paramMap["vnp_TxnRef"]

            // Nhận ID người dùng từ tùy chọn đăng kí
            val userId = UserSharedPrefsHelper.getUserId(this)

            // Kiểm tra trạng thái đăng ký nếu thanh toán thành công
            if (responseCode == "00" && userId > 0) {
                viewModel.checkSubscriptionStatus(userId, this)
                UserSharedPrefsHelper.setPremiumStatus(this, true)

                // Lưu trạng thái để hiển thị dialog trong MainActivity
                UserSharedPrefsHelper.setShowCongratulationsDialog(this, true)
            }

            setContent {
                BioFitTheme {
                    val loading = viewModel.loading.observeAsState(false).value
                    val subscriptionActive = viewModel.subscriptionActive.observeAsState(false).value

                    PaymentResultScreen(
                        responseCode = responseCode,
                        orderId = orderId,
                        isLoading = loading,
                        isPremium = subscriptionActive,
                        onClose = {
                            // Chuyển hướng tới trang Home
                            val intent = Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                            finish()

                        }
                    )
                }
            }
        } else {
            // URI không hợp lệ, chuyển hướng đến trang Home
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

@Composable
fun PaymentResultScreen(
    responseCode: String?,
    orderId: String?,
    isLoading: Boolean = false,
    isPremium: Boolean = false,
    onClose: () -> Unit
) {
    val success = responseCode == "00"
    val animatedProgress = remember { Animatable(0f) }

// Cách 1: Sử dụng Int trong mutableStateOf
    val countdownState = remember { mutableStateOf(3) }
    val countdown = countdownState.value

    // Tự động đếm ngược và chuyển về trang chính
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )

        // Bắt đầu đếm ngược
        for (i in 3 downTo 1) {
            countdownState.value = i
            delay(1000) // Chờ 1 giây
        }
        countdownState.value = 0
        delay(200) // Chờ thêm 200ms để người dùng thấy số 0
        onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .graphicsLayer {
                    alpha = animatedProgress.value
                    translationY = (1 - animatedProgress.value) * 50.dp.toPx()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                // Loading State
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 6.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.verify_subscription),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                // Result State
                AnimatedIcon(success)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (success)
                        stringResource(R.string.payment_successful)
                    else stringResource(R.string.payment_failed),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (success)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when {
                        success && isPremium ->
                            stringResource(R.string.congratulations)
                        success ->
                            stringResource(R.string.processing)
                        else ->
                            stringResource(R.string.unsuccess)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (orderId != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.order_id) + " "+ orderId,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }



                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (success)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    // Sử dụng Box và Row để sắp xếp text và hình tròn đếm ngược
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.return_to_home),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            // Hình tròn chứa số đếm ngược
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = countdown.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedIcon(success: Boolean) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .background(
                color = if (success)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = if (success)
                painterResource(id = R.drawable.ic_check_circle)
            else painterResource(id = R.drawable.ic_error),
            contentDescription = if (success) "Success" else "Error",
            tint = if (success)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(80.dp)
                .scale(animatedProgress.value)
        )
    }
}

