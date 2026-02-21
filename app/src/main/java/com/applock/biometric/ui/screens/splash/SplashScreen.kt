package com.applock.biometric.ui.screens.splash

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import  com.applock.biometric.R
import com.applock.biometric.common.SharedPrefsHelper
import com.applock.biometric.helpers.isNetworkAvailable
import com.applock.biometric.ui.theme.AppLockTheme
import com.applock.biometric.navigation.Screen
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val AD_WAIT_NO_NETWORK_MS: Long = 5_000L
private const val AD_WAIT_WITH_NETWORK_MS: Long = 22_000L

var shouldShowAppOpen = false

@Composable
fun SplashScreen(navController: NavHostController) {

    val visibleProgress = remember { Animatable(0f) }
    val progress = remember { derivedStateOf { visibleProgress.value.coerceIn(0f, 1f) } }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            visibleProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = AD_WAIT_NO_NETWORK_MS.toInt())
            )
            // This block will execute after the animation completes
            if (SharedPrefsHelper.isLanguageSelectionCompleted()) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            } else {
                navController.navigate(Screen.LanguageSelection.route) {
                    popUpTo(Screen.Splash.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
        // You can still have a delay here if needed for other purposes,
        // but navigation will happen upon progress completion.
        // delay(AD_WAIT_WITH_NETWORK_MS) // Removed or adjusted as per logic

    }

    Scaffold { innerPadding ->
        SplashContents(modifier = Modifier.padding(innerPadding), progress)
    }

}

@Composable
fun SplashContents(modifier: Modifier = Modifier, progress: State<Float>) {
//    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_splash))
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
//            LottieAnimation(
//                composition = lottieComposition,
//                modifier = Modifier
//                    .clip(RoundedCornerShape(16.sdp))
//                    .size(250.sdp),
//                contentScale = ContentScale.Crop
//            )
        }
        Spacer(modifier = Modifier.height(12.sdp))

        Text(
            text = stringResource(R.string.str_recoder),
            fontSize = 30.ssp,
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF23F22), Color(0xFFFC7F1F))
                ),
                lineHeight = 40.ssp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(1f))

        ProgressIndicatorUI(progress = progress)

        Spacer(modifier = Modifier.height(12.sdp))

//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 48.sdp)
//        ) {
//            Spacer(modifier = Modifier.height(8.sdp))
//
//            Text(
//                text = stringResource(R.string.splash_ads_notice),
//                fontSize = 12.ssp,
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onBackground,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 24.sdp)
//            )
//        }

    }
}

@Composable
fun ProgressIndicatorUI(progress: State<Float>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.6f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "${(progress.value.coerceIn(0f, 1f) * 100).toInt()}%",
                fontSize = 14.ssp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(12.sdp))
//        LinearProgressIndicator(
//            progress = { progress.value },
//            modifier = Modifier
//                .fillMaxWidth(0.6f)
//                .height(20.sdp),
//            color = MaterialTheme.colorScheme.primary,
//            trackColor = MaterialTheme.colorScheme.primary.copy(0.1f),
//            strokeCap = StrokeCap.Round,
//            gapSize = (-15).dp,
//            drawStopIndicator = {},
//        )


        GradientLinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth(0.6f),
            progress = progress.value,
            colors = listOf(
                Color(0xFFF23F22), // vibrant red
                Color(0xFFFF5F1F), // warm orange-red midpoint
                Color(0xFFFC7F1F), // bright orange
                Color(0xFFFFA04B), // soft light orange end
            ),
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            strokeWidth = 12.sdp,
            glowRadius = 6.dp
        )


        Spacer(modifier = Modifier.height(12.sdp))
    }
}

fun safeNavigate(
    navigated: Boolean,
    navController: NavHostController,
    onNavigated: (Boolean) -> Unit = {},
    toLanguage: Boolean
) {
    if (navigated) return
    onNavigated(true)

    if (toLanguage) {
        navController.navigate(Screen.LanguageSelection.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
            launchSingleTop = true
        }
    } else {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
            launchSingleTop = true
        }
    }
}


@Composable
fun AnimatedProgressBarOnce() {
    val context = LocalContext.current
    val animatable = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Initial planned duration
    val animDuration = remember {
        if (context.isNetworkAvailable()) 27_000 else 5_000
    }
    val startTime = remember { System.currentTimeMillis() }

    LaunchedEffect(Unit) {
        val animJob = scope.launch {
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = animDuration)
            )
        }

        // Wait checkpoint (7s)
        delay(7_000)

        if (animJob.isActive) {
            animJob.cancel()

            val elapsed = System.currentTimeMillis() - startTime
            val current = animatable.value.coerceIn(0f, 1f)
            val remaining = 1f - current

            // 🔑 Compute duration for remaining portion only
            val quickDuration = when {
                elapsed >= animDuration -> 2_000
                else -> {
                    // scale remaining fraction into a compressed time
                    val scaled = ((animDuration - elapsed) * remaining).toInt()
                    scaled.coerceAtLeast(2_000)
                }
            }

            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = quickDuration)
            )

            val totalTime = System.currentTimeMillis() - startTime
            Log.d("ProgressBar", "Animation completed in $totalTime ms")
        } else {
            animJob.join()
            val totalTime = System.currentTimeMillis() - startTime
            Log.d("ProgressBar", "Animation completed in $totalTime ms")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val progress = animatable.value
        Text("Progress: ${"%.0f".format(progress * 100)}%")
        Text("Planned duration: $animDuration ms")
        Text("Started: $startTime")
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = ProgressIndicatorDefaults.linearColor,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            drawStopIndicator = {},
            gapSize = -(15).dp
        )
    }
}


@Preview(showBackground = true, name = "Light Mode")
@Composable
fun SplashPreviewLight() {
    AppLockTheme {
        SplashScreen(navController = rememberNavController())
    }
}

@Composable
fun GradientLinearProgressIndicator(
    progress: Float,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
    strokeWidth: Dp = 4.dp,
    strokeCap: StrokeCap = StrokeCap.Round,
    glowRadius: Dp? = 2.dp,
    gradientAnimationSpeed: Int = 2000,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // Animate gradient movement horizontally
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        label = "",
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = gradientAnimationSpeed,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    // Build the animated gradient brush
    val brush = remember(offset, colors) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val step = 1f / colors.size
                val start = step / 2

                val originalSpots = List(colors.size) { start + (step * it) }

                val transformedSpots = originalSpots.map { spot ->
                    val shifted = (spot + offset)
                    if (shifted > 1f) shifted - 1f else shifted
                }

                val pairs = colors.zip(transformedSpots).sortedBy { it.second }

                val margin = size.width / 2

                return LinearGradientShader(
                    colors = pairs.map { it.first },
                    colorStops = pairs.map { it.second },
                    from = Offset(-margin, 0f),
                    to = Offset(size.width + margin, 0f)
                )
            }
        }
    }

    Canvas(modifier) {
        val width = size.width
        val height = size.height
        val strokePx = strokeWidth.toPx()

        // --- Track (background) ---
        drawLine(
            color = trackColor,
            start = Offset(0f, height / 2f),
            end = Offset(width, height / 2f),
            strokeWidth = strokePx,
            cap = strokeCap
        )

        // --- Progress (foreground gradient) ---
        if (progress > 0f) {
            val paint = Paint().apply {
                isAntiAlias = true
                style = PaintingStyle.Stroke
                this.strokeWidth = strokePx
                this.strokeCap = strokeCap
                shader = brush.createShader(size)
            }

            glowRadius?.let { radius ->
                paint.asFrameworkPaint().apply {
                    setShadowLayer(radius.toPx(), 0f, 0f, "#FF6F2D".toColorInt())
                }
            }

            drawIntoCanvas { canvas ->
                canvas.drawLine(
                    p1 = Offset(0f, height / 2f),
                    p2 = Offset(width * progress.coerceIn(0f, 1f), height / 2f),
                    paint = paint
                )
            }
        }
    }
}
