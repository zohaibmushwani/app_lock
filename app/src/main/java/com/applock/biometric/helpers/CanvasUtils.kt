package com.applock.biometric.helpers

import android.graphics.BlurMaskFilter
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset

fun Canvas.drawShadow(
    density: Density,
    outline: Outline,
    blurRadius: Dp,
    color: Color,
) {
    drawOutline(outline = outline, paint = createBlurPaint(density, blurRadius, color))
}


fun createBlurPaint(
    density: Density,
    blurRadius: Dp,
    color: Color,
): Paint {
    return Paint().asFrameworkPaint().apply {
        if (blurRadius.value > 0f) {
            val style = BlurMaskFilter.Blur.NORMAL
            val radiusPx = with(density) { blurRadius.toPx() }
            this.maskFilter = BlurMaskFilter(radiusPx, style)
        }
        this.color = color.toArgb()
    }.asComposePaint()
}
fun Shape.createOutline(scope: CacheDrawScope, size: Size): Outline {
    val density = Density(scope.density, scope.fontScale)
    return createOutline(size = size, layoutDirection = scope.layoutDirection, density = density)
}

fun Canvas.inset(
    outline: Outline,
    color: Color,
) {
    clip(outline)

    val colorMatrix =
        ColorMatrix().apply {
            setScale(alphaScale = -1f)
            shiftAlpha(255f * color.alpha)
        }
    val colorFilter = ColorFilter.colorMatrix(colorMatrix)
    val filterPaint = Paint().apply { this.colorFilter = colorFilter }

    saveLayer(outline.bounds, filterPaint)
}

operator fun Size.plus(size: Size): Size = Size(width + size.width, height + size.height)

fun Size(all: Float): Size = Size(width = all, height = all)

fun ColorMatrix.shiftAlpha(value: Float): Unit = set(row = 3, column = 4, v = value)

fun ColorMatrix.setScale(
    redScale: Float = get(row = 0, column = 0),
    greenScale: Float = get(row = 1, column = 1),
    blueScale: Float = get(row = 2, column = 2),
    alphaScale: Float = get(row = 3, column = 3),
): Unit = setToScale(redScale, greenScale, blueScale, alphaScale)

fun Canvas.clip(outline: Outline) {
    when (outline) {
        is Outline.Generic -> clipPath(path = outline.path)
        is Outline.Rectangle -> clipRect(rect = outline.rect)
        is Outline.Rounded -> clipPath(path = Path().apply { addRoundRect(outline.roundRect) })
    }
}

 fun DpOffset.toOffset(density: Density): Offset = with(density) {
    Offset(x.toPx(), y.toPx())
}

 fun Canvas.translate(offset: Offset) {
    translate(dx = offset.x, dy = offset.y)
}

 operator fun DpOffset.minus(value: Dp): DpOffset = DpOffset(x = x - value, y = y - value)

