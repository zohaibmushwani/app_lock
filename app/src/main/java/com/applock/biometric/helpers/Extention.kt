package com.applock.biometric.helpers

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.util.lerp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.absoluteValue


fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
fun Modifier.singleClick(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    delayMillis: Long = 450L,
    onClick: () -> Unit
): Modifier = composed(inspectorInfo = debugInspectorInfo {
    name = "singleClick"
    properties["enabled"] = enabled
    properties["onClickLabel"] = onClickLabel
    properties["onClick"] = onClick
}) {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    Modifier
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime >= delayMillis) {
                        lastClickTime = currentTime
                        onClick()
                    }
                }
            }
        }
        .semantics {
            this.onClick(label = onClickLabel, action = { onClick(); true })
        }
}

fun Modifier.boxShadow(
    blurRadius: Dp,
    color: Color = Color.Black.copy(alpha = .15f),
    spreadRadius: Dp = 0.dp,
    offset: DpOffset = DpOffset.Zero,
    shape: Shape = RectangleShape,
    clip: Boolean = true,
    inset: Boolean = false,
): Modifier {
    require(blurRadius.isSpecified) { "blurRadius must be specified." }
    require(blurRadius.value >= 0f) { "blurRadius can't be negative." }
    require(color.isSpecified) { "color must be specified." }
    require(spreadRadius.isSpecified) { "spreadRadius must be specified." }
    require(offset.isSpecified) { "offset must be specified." }

    val shadowModifier = drawWithCache {
        onDrawWithContent {
            if (inset) drawContent()
            drawIntoCanvas { canvas ->
                val spreadSize =
                    spreadRadius.toPx()
                        .times(other = 2)
                        .let { if (inset) -it else it }
                        .let(::Size)

                canvas.withSave {
                    val scope = this@drawWithCache
                    if (inset) {
                        canvas.inset(
                            outline = shape.createOutline(scope = scope, size = size),
                            color = color,
                        )
                    }

                    val density = Density(density = density, fontScale = fontScale)
                    canvas.translate((offset - spreadRadius).toOffset(density = density))

                    val shadowOutline = shape.createOutline(scope = scope, size = size + spreadSize)
                    canvas.drawShadow(
                        density = density,
                        outline = shadowOutline,
                        blurRadius = blurRadius,
                        color = color,
                    )
                }
            }
            if (!inset) drawContent()
        }
    }
    return if (clip) shadowModifier.clip(shape) else shadowModifier
}


@Composable
fun Modifier.lazyRowSmoothScaleAnimation(
    lazyListState: LazyListState,
    thisItemIndex: Int,
    itemSize: Dp,
    scaleDownFactor : Float = 0.8f
): Modifier {
    val density = LocalDensity.current

    // Calculate the center position of the viewport in real-time
    val viewportCenter by remember {
        derivedStateOf {
            (lazyListState.layoutInfo.viewportStartOffset + lazyListState.layoutInfo.viewportEndOffset) / 2
        }
    }

    // Calculate the item's position in real-time
    val itemCenter by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo
                .find { it.index == thisItemIndex }
                ?.let { it.offset + it.size / 2 }
                ?: Float.MAX_VALUE
        }
    }

    // Calculate the offset from the viewport center in pixels
    val pageOffset by remember {
        derivedStateOf {
            if (itemCenter != Float.MAX_VALUE) {
                (itemCenter.toFloat() - viewportCenter) / with(density) { itemSize.toPx() }
            } else {
                Float.MAX_VALUE
            }
        }
    }

    return this.then(
        Modifier.graphicsLayer {
            // Smooth scale animation based on the offset
            lerp(
                start = scaleDownFactor, // Minimum scale
                stop = 1f,    // Maximum scale (center item)
                fraction = (1f - pageOffset.absoluteValue).coerceIn(0f, 1f)
            ).also { scale ->
                scaleX = scale
                scaleY = scale
            }
        }
    )
}


enum class ButtonState { Pressed, Idle }


fun Modifier.bounceClick(
    scaleOutFactor: Float = 0.90f,
    throttleDuration: Long = 1000L,
    onClick: () -> Unit = {}
) = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val scale by animateFloatAsState(
        targetValue = if (buttonState == ButtonState.Pressed) scaleOutFactor else 1f,
        label = ""
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                val now = System.currentTimeMillis()
                if (now - lastClickTime > throttleDuration) {
                    lastClickTime = now
                    onClick()
                }
            }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Modifier.click(onClick: () -> Unit = {}) = composed {
    this
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                onClick()
            }
        )

}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

//fun Modifier.dropShadow(
//    shape: Shape,
//    color: Color = Color.Black.copy(0.25f),
//    blur: Dp = 4.dp,
//    offsetY: Dp = 0.dp,
//    offsetX: Dp = 0.dp,
//    spread: Dp = 0.dp
//) = this.drawBehind {
//
//    val shadowSize = Size(size.width + spread.toPx(), size.height + spread.toPx())
//    val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)
//
//    val paint = Paint()
//    paint.color = color
//    paint.style = PaintingStyle.Stroke
//    paint.strokeWidth = spread.toPx()
//
//    if (blur.toPx() > 0) {
//        paint.asFrameworkPaint().apply {
//            this.maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
//        }
//    }
//
//    drawIntoCanvas { canvas ->
//        canvas.save()
//        canvas.translate(offsetX.toPx(), offsetY.toPx())
//        canvas.drawOutline(shadowOutline, paint)
//        canvas.restore()
//    }
//}


fun String.formatTime(): String {
    return try {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(this)
        outputFormat.format(date ?: return this)
    } catch (e: ParseException) {
        this
    }
}




fun String.toFormattedDate(): String {
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
    val outputFormat = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())
    val date = inputFormat.parse(this)
    return outputFormat.format(date)
}




fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(this))
    } else {
        this
    }
}


/**
 * Adds a dashed border around a Composable component.
 *
 * @param color The color of the dashed border.
 * @param shape The shape of the dashed border.
 * @param strokeWidth The width of the dashed border stroke.
 * @param dashLength The length of each dash in the border.
 * @param gapLength The length of the gap between each dash.
 * @param cap The style of the stroke caps at the ends of dashes.
 *
 * @return A Modifier with the dashed border applied.
 */
fun Modifier.dashedBorder(
    color: Color,
    shape: Shape,
    strokeWidth: Dp = 2.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Round
) = dashedBorder(brush = SolidColor(color), shape, strokeWidth, dashLength, gapLength, cap)

/**
 * Adds a dashed border around a Composable component.
 *
 * @param brush The brush of the dashed border.
 * @param shape The shape of the dashed border.
 * @param strokeWidth The width of the dashed border stroke.
 * @param dashLength The length of each dash in the border.
 * @param gapLength The length of the gap between each dash.
 * @param cap The style of the stroke caps at the ends of dashes.
 *
 * @return A Modifier with the dashed border applied.
 */
fun Modifier.dashedBorder(
    brush: Brush,
    shape: Shape,
    strokeWidth: Dp = 2.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Round
) = this.drawWithContent {

    val outline = shape.createOutline(size, layoutDirection, density = this)

    val dashedStroke = Stroke(
        cap = cap,
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx())
        )
    )

    drawContent()

    drawOutline(
        outline = outline,
        style = dashedStroke,
        brush = brush
    )
}




fun Activity.enableFullscreen(enable: Boolean) {
    if (enable) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        actionBar?.hide()
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_VISIBLE
                )
        actionBar?.show()
    }
}

var toast: Toast? = null
fun <T> Context.showToast(value: T) {
    cancelToast()
    toast = Toast.makeText(this, value.toString(), Toast.LENGTH_SHORT)
    toast?.show()
}
private fun cancelToast() {
    toast?.cancel()
    toast = null
}


//fun Activity.shouldShowAd(): Boolean {
//    return when (this) {
//
//        is AdActivity -> {
//            false
//        }
//
//
////        is SplashActivity -> {
////            false
////        }
//
//        else -> {
//            !(this.localClassName.contains("AdActivity") ||
//                    this.localClassName.contains("InterstitialAdActivity") ||
//                    this.localClassName.contains("FullScreenAdActivity") ||
//                    this.localClassName.contains("RewardedVideoActivity") ||
//                    this.localClassName.contains("MTGAdSplashActivity") ||
//                    this.localClassName.contains("MBBaseActivity") ||
//                    this.localClassName.contains("MBCommonActivity") ||
//                    this.localClassName.contains("MBRewardVideoActivity") ||
//                    this.localClassName.contains("MBInterstitialActivity") ||
//                    this.localClassName.contains("InteractiveShowActivity") ||
//                    this.localClassName.contains("MTGADActivity") ||
//                    this.localClassName.contains("InMobiAdActivity") ||
//                    this.localClassName.contains("InMobiInterstitialActivity") ||
//                    this.localClassName.contains("AppLovinInterstitialActivity") ||
//                    this.localClassName.contains("AppLovinRewardedVideoActivity") ||
//                    this.localClassName.contains("AppLovinFullscreenActivity") ||
//                    this.localClassName.contains("AudienceNetworkActivity") ||
//                    this.localClassName.contains("MaxInterstitialAd") ||
//                    this.localClassName.contains("VungleActivity") ||
//                    this.localClassName.contains("AdUnitActivity") ||
//                    this.localClassName.contains("UnityAdsFullscreenActivity") ||
//                    this.localClassName.contains("TTInterstitialActivity") ||
//                    this.localClassName.contains("TTFullScreenExpressVideoActivity") ||
//                    this.localClassName.contains("TTLandingPageActivity") ||
//                    this.localClassName.contains("TTRewardVideoActivity")
//                    )
//        }
//    }
//}