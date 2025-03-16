package com.example.biofit.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biofit.R
import com.example.biofit.ui.theme.BioFitTheme

@Composable
fun PaymentMethodDialogScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PaymentMethodDialog()
    }
}
// Dialog chọn phương thức thanh toán hiển thị từ dưới lên
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodDialog(
    onDismiss: () -> Unit = {},
    onSelectPayment: (String) -> Unit = {}
) {
    // Sử dụng ModalBottomSheet để tạo dialog từ dưới lên
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Select a payment method",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Phương thức thanh toán MoMo
            PaymentMethodItem(
                name = "Pay with Momo",
                imageResId = R.drawable.momo_logo,
                onClick = { /*onSelectPayment("MoMo")*/ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phương thức thanh toán ZaloPay
            PaymentMethodItem(
                name = "Pay with ZaloPay",
                imageResId = R.drawable.zalopay_logo,
                onClick = { /*onSelectPayment("ZaloPay")*/ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Nút close
            Button(
                onClick = { onDismiss() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Close",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Item hiển thị phương thức thanh toán
@Composable
fun PaymentMethodItem(
    name: String,
    imageResId: Int,
    onClick: () -> Unit

) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Gray,
                spotColor = Color.DarkGray
            )
            .background(Color(0xFFF6F6F6))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Logo hình ảnh
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = name,
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Tên phương thức thanh toán
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Icon mũi tên
            Icon(
                painter = painterResource(id = R.drawable.right_arrow), // Thay thế bằng icon mũi tên phù hợp
                contentDescription = "Select",
                tint = Color.Gray,
                modifier = Modifier.size(15.dp)
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
private fun DefaultDialogDarkModePreviewInSmallPhone() {
    BioFitTheme {
        PaymentMethodDialogScreen()
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
        PaymentMethodDialogScreen()
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
        PaymentMethodDialogScreen()
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
        PaymentMethodDialogScreen()
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
        PaymentMethodDialogScreen()
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
        PaymentMethodDialogScreen()
    }
}