package com.applock.biometric.helpers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Extension function to create a darker variant of a color.
 * Steps: convert ARGB → HSL, reduce lightness by `factor`, clamp, then HSL → ARGB.
 */
fun Int.toDarkVariant(factor: Float = 0.8f): Int {
    // Extract ARGB components
    val alpha = (this shr 24) and 0xFF
    val red = (this shr 16) and 0xFF
    val green = (this shr 8) and 0xFF
    val blue = this and 0xFF

    // Convert RGB (0-255) to HSL (0-1, 0-1, 0-1)
    val hsl = rgbToHsl(red / 255f, green / 255f, blue / 255f)

    // Reduce lightness by factor, ensuring it doesn't go below 0
    hsl[2] = (hsl[2] * (1f - factor)).coerceIn(0f, 1f)

    // Convert HSL back to RGB (0-1)
    val rgb = hslToRgb(hsl[0], hsl[1], hsl[2])

    // Convert RGB (0-1) back to (0-255) and combine with alpha
    val newRed = (rgb[0] * 255).toInt().coerceIn(0, 255)
    val newGreen = (rgb[1] * 255).toInt().coerceIn(0, 255)
    val newBlue = (rgb[2] * 255).toInt().coerceIn(0, 255)

    return (alpha shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
}

/**
 * Extension function to create a lighter variant of a color.
 * Steps: convert ARGB → HSL, increase lightness by `factor`, clamp, then HSL → ARGB.
 */
fun Int.toLightVariant(factor: Float = 0.3f): Int {
    // Extract ARGB components
    val alpha = (this shr 24) and 0xFF
    val red = (this shr 16) and 0xFF
    val green = (this shr 8) and 0xFF
    val blue = this and 0xFF

    // Convert RGB (0-255) to HSL (0-1, 0-1, 0-1)
    val hsl = rgbToHsl(red / 255f, green / 255f, blue / 255f)

    // Increase lightness by factor, ensuring it doesn't go above 1
    hsl[2] = (hsl[2] + (1f - hsl[2]) * factor).coerceIn(0f, 1f)

    // Convert HSL back to RGB (0-1)
    val rgb = hslToRgb(hsl[0], hsl[1], hsl[2])

    // Convert RGB (0-1) back to (0-255) and combine with alpha
    val newRed = (rgb[0] * 255).toInt().coerceIn(0, 255)
    val newGreen = (rgb[1] * 255).toInt().coerceIn(0, 255)
    val newBlue = (rgb[2] * 255).toInt().coerceIn(0, 255)

    return (alpha shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
}

/**
 * Extension function to create a darker variant of a Color.
 * @param factor How much to darken the color (0.0 = no change, 1.0 = black)
 */
fun Color.darker(factor: Float = 0.3f): Color {
    return Color(this.toArgb().toDarkVariant(factor))
}

/**
 * Extension function to create a lighter variant of a Color.
 * @param factor How much to lighten the color (0.0 = no change, 1.0 = white)
 */
fun Color.lighter(factor: Float = 0.3f): Color {
    return Color(this.toArgb().toLightVariant(factor))
}

/**
 * Converts RGB values (0-1 range) to HSL values (0-1 range).
 * @return FloatArray of [hue, saturation, lightness]
 */
private fun rgbToHsl(r: Float, g: Float, b: Float): FloatArray {
    val max = max(r, max(g, b))
    val min = min(r, min(g, b))
    val delta = max - min

    // Lightness
    val lightness = (max + min) / 2f

    // Saturation
    val saturation = when {
        delta == 0f -> 0f
        lightness < 0.5f -> delta / (max + min)
        else -> delta / (2f - max - min)
    }

    // Hue
    val hue = when {
        delta == 0f -> 0f
        max == r -> ((g - b) / delta + (if (g < b) 6 else 0)) / 6f
        max == g -> ((b - r) / delta + 2) / 6f
        else -> ((r - g) / delta + 4) / 6f
    }

    return floatArrayOf(hue, saturation, lightness)
}

/**
 * Converts HSL values (0-1 range) to RGB values (0-1 range).
 * @return FloatArray of [red, green, blue]
 */
private fun hslToRgb(h: Float, s: Float, l: Float): FloatArray {
    return if (s == 0f) {
        // Achromatic (gray)
        floatArrayOf(l, l, l)
    } else {
        val c = (1f - abs(2f * l - 1f)) * s
        val x = c * (1f - abs((h * 6f) % 2f - 1f))
        val m = l - c / 2f

        val (r, g, b) = when ((h * 6f).toInt()) {
            0 -> Triple(c, x, 0f)
            1 -> Triple(x, c, 0f)
            2 -> Triple(0f, c, x)
            3 -> Triple(0f, x, c)
            4 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        floatArrayOf(r + m, g + m, b + m)
    }
}