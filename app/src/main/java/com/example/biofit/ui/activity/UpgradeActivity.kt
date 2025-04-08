package com.example.biofit.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.biofit.R
import com.example.biofit.navigation.MainActivity
import com.example.biofit.ui.components.PaymentMethodDialog
import com.example.biofit.ui.theme.BioFitTheme

class UpgradeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val source = intent.getStringExtra("source")
        enableEdgeToEdge()
        setContent {
            BioFitTheme {
                UpgradeScreen(source = source)
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun UpgradeScreen(source: String?) {

    var selectedPlan by remember { mutableStateOf("YEARLY") }
    var showPaymentMethodDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Background food images
        Image(
            painter = painterResource(id = R.drawable.healthy_food),
            contentDescription = stringResource(R.string.des_app_name),
            modifier = Modifier
                .align(Alignment.TopEnd) // Căn ảnh về góc phải trên
                .size(170.dp),
            contentScale = ContentScale.Crop
        )

        // Close button
        IconButton(
            onClick = {
                val activity = context as? Activity
                if (source == "ProfileScreen") {
                    activity?.finish() // Quay lại ProfileScreen
                } else {
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    activity?.finish() // Đóng UpgradeActivity
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .padding(top = 25.dp)
                .size(36.dp)
                .zIndex(10f) // Đảm bảo nút hiển thị phía trên các thành phần khác
        ) {
            Icon(
                painter = painterResource(R.drawable.xmark_circle_fill),
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.primary,
            )
        }


        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Header
            Text(
                text = "${stringResource(R.string.hello_upgrade_1)} \n${stringResource(R.string.hello_upgrade_2)}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "${stringResource(R.string.upgrade_1)} ${stringResource(R.string.upgrade_2)}",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Premium features
            PremiumFeatureItem(
                title = "Chatbot AI:",
                description = stringResource(R.string.chatbot_ai_description)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PremiumFeatureItem(
                title = stringResource(R.string.calo_macro_tracking),
                description = stringResource(R.string.calo_macro_tracking_description)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PremiumFeatureItem(
                title = stringResource(R.string.personalized_plans),
                description = stringResource(R.string.personalized_plans_description)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Plan selection
            Text(
                text = stringResource(R.string.plan_selection),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Pricing plans
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Container for Yearly plan with badge
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp) // Fixed height for both containers
                ) {
                    PlanCard(
                        title = stringResource(R.string.yearly),
                        price = "300.00 ",
                        perDuration = "/YR",
                        originalPrice = "150.00 đ",
                        billingInfo = stringResource(R.string.billed_yearly_after_free_trial),
                        isSelected = selectedPlan == "YEARLY",
                        savings = stringResource(R.string._30_savings),
                        onClick = { selectedPlan = "YEARLY" }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Monthly plan with same dimensions
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp) // Fixed height to match
                        .padding(top = 20.dp)
                ) {
                    PlanCard(
                        title = stringResource(R.string.monthly),
                        price = "100.00 ",
                        perDuration = "/MO",
                        originalPrice = null,
                        billingInfo = stringResource(R.string.billed_month_after_free_trial),
                        isSelected = selectedPlan == "MONTHLY",
                        savings = null,
                        onClick = { selectedPlan = "MONTHLY" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.title_experence),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            // upgrade now
            ElevatedButton(
                onClick = { showPaymentMethodDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    stringResource(R.string.upgrade_now),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    if (showPaymentMethodDialog) {
        PaymentMethodDialog(
            onDismiss = { showPaymentMethodDialog = false },
            onSelectPayment = { method ->
                // Handle payment method selection
                showPaymentMethodDialog = false

                when (method) {
                    "MoMo" -> {
//                        Toast.makeText(context, "MoMo will be supported soon", Toast.LENGTH_SHORT).show()
                    }

                    "VNPAY" -> {
                        // VNPay handling is done inside the PaymentMethodDialog

                    }
                }
            },
            selectedPlan = selectedPlan
        )
    }
}

@Composable
fun PremiumFeatureItem(title: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Crown icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE3DEDE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Row {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = " $description",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    perDuration: String,
    originalPrice: String?,
    billingInfo: String,
    isSelected: Boolean,
    savings: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Savings
    Box(
        modifier = modifier
    ) {
        // Savings badge positioned above the card
        if (savings != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-1).dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .zIndex(1f) // Ensure it appears above the card
            ) {
                Text(
                    text = savings,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Main card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (savings != null) 16.dp else 0.dp)
                .border(
                    width = 2.dp,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = MaterialTheme.shapes.extraLarge
                )
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.background)
                .clickable(onClick = onClick)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = price.substringBefore(" "),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = " đ",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = perDuration,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (originalPrice != null) {
                    Text(
                        text = originalPrice,
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodySmall.copy(
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = billingInfo,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall
                )

                // Box Savings
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .size(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.checkmark_circle_fill),
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .size(12.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = CircleShape),
                    )
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
private fun AddScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        UpgradeScreen(
            source = "ProfileScreen"
        )
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun AddScreenPreviewInLargePhone() {
    BioFitTheme {
        UpgradeScreen(
            source = "ProfileScreen"
        )
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
private fun AddScreenPreviewInTablet() {
    BioFitTheme {
        UpgradeScreen(
            source = "ProfileScreen"
        )
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
private fun AddScreenLandscapeDarkModePreviewInSmallPhone() {
    BioFitTheme {
        UpgradeScreen(
            source = "ProfileScreen"
        )
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AddScreenLandscapePreviewInLargePhone() {
    BioFitTheme {
        UpgradeScreen(
            source = "ProfileScreen"
        )
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
private fun AddScreenLandscapePreviewInTablet() {
    BioFitTheme {
        UpgradeScreen(
            source = "ProfileScreen"
        )
    }
}