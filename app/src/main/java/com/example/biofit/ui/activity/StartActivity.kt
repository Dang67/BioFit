package com.example.biofit.ui.activity

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.biofit.R
import com.example.biofit.ui.components.getStandardPadding
import com.example.biofit.ui.theme.BioFitTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BioFitTheme {
                StartScreen()
            }
        }
    }

    // thay đổi chế độ hiển thị khi thiết bị xoay màn hình
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}

@Composable
fun StartScreen() {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val standardPadding = getStandardPadding().first

    Surface(
        modifier = Modifier.fillMaxSize(), // tuỳ chỉnh kích thước của Surface
        // shape = , // tuỳ chỉnh hình dạng của Surface
        color = MaterialTheme.colorScheme.background, // tuỳ chỉnh màu nền của Surface
        // contentColor = , // tuỳ chỉnh màu của nội dung bên trong Surface
        // tonalElevation = , // tuỳ chỉnh độ nổi của Surface
        // shadowElevation = , // tuỳ chỉnh độ getJSONShadow của Surface
        // border = , // tuỳ chỉnh viền của Surface
    ) {
        Box/*(
            modifier = , // tuỳ chỉnh kích thước và vị trí của Box
            contentAlignment = , // tuỳ chỉnh vị trí của nội dung bên trong Box
            propagateMinConstraints = , // tuỳ chỉnh xem Box có kế thừa các ràng buộc min hay không
        )*/ {
            StartScreenBackgroundImage(standardPadding)
            StartScreenContent(
                screenWidth,
                screenHeight,
                standardPadding
            )
        } // nội dung bên trong Box
    } // nội dung bên trong Surface
}

@Composable
fun StartScreenBackgroundImage(standardPadding: Dp) {
    val images = listOf(
        R.drawable.bg_start_screen1,
        R.drawable.bg_start_screen2,
        R.drawable.bg_start_screen3
    )

    val pagerState = rememberPagerState(
        initialPage = 500,
        initialPageOffsetFraction = 0f,
        pageCount = { Int.MAX_VALUE }
    ) // Bắt đầu từ giữa danh sách
    val coroutineScope = rememberCoroutineScope()

    var isBlurred by remember { mutableStateOf(false) }

    val blurRadius by animateDpAsState(
        targetValue = if (isBlurred) standardPadding * 4 else 0.dp,
        animationSpec = tween(durationMillis = 600), label = "blurAnimation" // Mượt mà
    )

    LaunchedEffect(Unit) {
        delay(2000)
        isBlurred = true
    }

    // Auto-scroll effect
    LaunchedEffect(Unit) {
        delay(2000)
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        val index = page % images.size
        Image(
            painter = painterResource(id = images[index]),
            contentDescription = "Background Image $index",
            modifier = Modifier
                .fillMaxSize()
                .blur(
                    radius = blurRadius,
                    edgeTreatment = BlurredEdgeTreatment.Rectangle
                ),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun StartScreenContent(
    screenWidth: Int,
    screenHeight: Int,
    standardPadding: Dp
) {
    val images = listOf(
        R.drawable.bg_start_screen1,
        R.drawable.bg_start_screen2,
        R.drawable.bg_start_screen3
    )

    val pagerState = rememberPagerState(
        initialPage = 500,
        initialPageOffsetFraction = 0f,
        pageCount = { Int.MAX_VALUE }
    ) // Bắt đầu từ giữa danh sách
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll effect
    LaunchedEffect(Unit) {
        delay(2000)
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }
    /*
    ************************************************************************************************
    */
    // Animation hiệu ứng
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Delay một chút để bắt đầu hiệu ứng sau khi vào màn
        visible = true
    }
    /*
    ************************************************************************************************
    */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
                start = standardPadding,
                end = standardPadding,
                bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding(),
            ), // tuỳ chỉnh kích thước và vị trí của Column
        verticalArrangement = Arrangement.SpaceBetween, // sắp xếp các phần tử theo chiều dọc
        horizontalAlignment = Alignment.CenterHorizontally, // sắp xếp các phần tử theo chiều ngang
    ) {
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            enter = fadeIn(animationSpec = tween(1500)) + slideInVertically(
                initialOffsetY = { -it / 4 },
                animationSpec = tween(1500)
            )
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                pageSpacing = standardPadding * 2,
                userScrollEnabled = false,
                pageSize = PageSize.Fill,
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                beyondViewportPageCount = 100
            ) { page ->
                val index = page % images.size
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = standardPadding,
                            horizontal =  standardPadding * 4
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = images[index]),
                        contentDescription = "Background Image $index",
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(
                                elevation = standardPadding,
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }
        WelcomeSection(standardPadding)
        Spacer(modifier = Modifier.height(standardPadding))
        ActionButtons(standardPadding)
    } // nội dung bên trong Column
}

@Composable
fun WelcomeSection(standardPadding: Dp) {
    val charAppName = listOf<String>("B", "I", "O", "F", "I", "T")
    /*
    ************************************************************************************************
    */
    // Animation hiệu ứng
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }
    /*
    ************************************************************************************************
    */
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(standardPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(1000)
            )
        ) {
            Text(
                text = stringResource(R.string.welcome_to_app),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }


        Row/*(
            modifier = , // tuỳ chỉnh kích thước và vị trí của Row
            horizontalArrangement = , // tuỳ chỉnh cách sắp xếp các phần tử theo chiều ngang
            verticalAlignment = , // tuỳ chỉnh cách sắp xếp các phần tử theo chiều dọc
        )*/ {
            charAppName.forEachIndexed { index, char ->
                val delay = 1000 + index * 500

                val isPrimary = index < 3
                val color = if (isPrimary) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onPrimary
                val style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = if (!isPrimary) Shadow(
                        color = MaterialTheme.colorScheme.onPrimary,
                        blurRadius = 10f
                    ) else null
                )

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(delay)) + slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(delay)
                    )
                ) {
                    Text(
                        text = char,
                        // modifier = , // tuỳ chỉnh kích thước và vị trí của Text
                        color = color,
                        // fontSize = , // tuỳ chỉnh kích thước chữ
                        // fontStyle = , // tuỳ chỉnh font chữ
                        // fontWeight = , // tuỳ chỉnh độ đậm của chữ
                        // fontFamily = , // tuỳ chỉnh font chữ
                        // letterSpacing = , // tuỳ chỉnh khoảng cách chữ
                        // textDecoration = , // tuỳ chỉnh kiểu chữ
                        // textAlign = , // tuỳ chỉnh căn giữa chữ
                        // lineHeight = , // tuỳ chỉnh chiều cao chữ
                        // overflow = , // tuỳ chỉnh cách xử lý khi chữ vượt quá giới hạn
                        // softWrap = , // tuỳ chỉnh cách xử lý khi chữ vượt quá giới hạn
                        // maxLines = , // tuỳ chỉnh số lượng dòng tối đa
                        // minLines = , // tuỳ chỉnh số lượng dòng tối thiểu
                        // onTextLayout = , // tuỳ chỉnh hành vi khi layout chữ
                        style = style // tuỳ chỉnh kiểu chữ
                    )
                }
            }
        } // nội dung bên trong Row

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1500)) + slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(1500)
            )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(standardPadding / 2),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.start_title_1),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = stringResource(R.string.start_title_2),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
fun ActionButtons(standardPadding: Dp) {
    val context = LocalContext.current
    val activity = context as? Activity
    /*
    ************************************************************************************************
    */
    // Animation hiệu ứng
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Delay một chút để bắt đầu hiệu ứng sau khi vào màn
        delay(500)
        visible = true
    }
    /*
    ************************************************************************************************
    */

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(standardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(2000)) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(2000)
            )
        ) {
            GetStartedButton(
                onCLick = {
                    activity?.let {
                        val intent = Intent(it, LoginActivity::class.java)
                        it.startActivity(intent)
                        it.finish()
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(standardPadding))
    }
}

@Composable
fun GetStartedButton(
    onCLick: () -> Unit = {}
) {
    // GET STARTED button
    ElevatedButton(
        onClick = onCLick, // xử lý sự kiện khi nút được nhấn
        // modifier = Modifier, // tuỳ chỉnh kích thước và vị trí của nút
        // enabled = true, // trạng thái kích hoạt của nút (mặc định true)
        // shape = MaterialTheme.shapes.extraLarge, // hình dạng của nút
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary, // màu nền của nút (mặc định màu primary)
            // contentColor = MaterialTheme.colorScheme.onPrimary, // màu của nội dung bên trong nút (mặc định màu onPrimary)
            // disabledContainerColor: màu nền của nút khi vô hiệu hóa
            // disabledContentColor: màu của nội dung bên trong nút khi vô hiệu hóa
        ), // cấu hình màu của nút
        /* elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 16.dp, // độ cao của nút
            pressedElevation = 8.dp, // độ cao khi nút được nhấn
            disabledElevation = 0.dp // độ cao khi nút vô hiệu hóa
        ), // cấu hình độ cao của nút */
        /* border = BorderStroke(
            // width: độ dày của viền
            // color: màu viền
        ), // cấu hình viền của nút */
        // contentPadding = PaddingValues(), // quy định khoảng cách giữa nội dung và viền của nút
        // interactionSource = MutableInteractionSource(), // đối tượng lưu trữ trạng thái tương tác của nút
    ) {
        Text(text = stringResource(id = R.string.get_started))
    } // nội dung bên trong nút thường là Text hoặc Icon
}

// xem trước layout mà không cần chạy ứng dụng
@Preview(
    device = "id:pixel",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    locale = "vi"
)
@Composable
private fun StartPortraitScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        StartScreen()
    }
}

@Preview(
    device = "id:pixel_9_pro_xl",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun StartPortraitScreenPreviewInLargePhone() {
    BioFitTheme {
        StartScreen()
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
private fun StartPortraitScreenPreviewInTablet() {
    BioFitTheme {
        StartScreen()
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
private fun StartLandscapeScreenDarkModePreviewInSmallPhone() {
    BioFitTheme {
        StartScreen()
    }
}

@Preview(
    device = "spec:parent=pixel_9_pro_xl,orientation=landscape",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun StartLandscapeScreenPreviewInLargePhone() {
    BioFitTheme {
        StartScreen()
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
private fun StartLandscapeScreenPreviewInTablet() {
    BioFitTheme {
        StartScreen()
    }
}