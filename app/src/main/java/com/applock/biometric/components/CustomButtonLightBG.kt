 package com.devsky.videorecorder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.applock.biometric.common.ThemePreviews
import com.applock.biometric.helpers.bounceClick
import com.applock.biometric.ui.theme.AppLockTheme
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun CustomButtonLightBG(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    label: String = "Example Button",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .bounceClick { onClick() }
            .clip(RoundedCornerShape(12.sdp))
            .height(48.sdp)
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.25f else 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.ssp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.sdp)
        )
    }
}

@Composable
fun PrimaryButtonFullWidth(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .bounceClick { onClick() },
        shape = RoundedCornerShape(12.sdp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                fontSize = 14.ssp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
fun RoundedCornerButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    ),
    useGradient: Boolean = true,
    onClick: () -> Unit
) {
    val backgroundBrush = if (useGradient && colors.size > 1) {
        Brush.horizontalGradient(colors)
    } else {
        Brush.linearGradient(listOf(colors.first()))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(35.sdp)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier),
        shape = RoundedCornerShape(50),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(brush = backgroundBrush)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 15.ssp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@ThemePreviews
@Composable
fun CustomButtonLightBGPreview() {
    AppLockTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomButtonLightBG(
                    label = "Example Button",
                    onClick = {},
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth(0.8f)
                )
                Spacer(modifier = Modifier.height(16.sdp))
                PrimaryButtonFullWidth(
                    text = "Example Button",
                    onClick = {},
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth(0.8f)
                )
            }
        }
    }
}