package com.applock.biometric.ui.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.applock.biometric.R
import com.applock.biometric.activities.PrivacyPolicyActivity
import com.applock.biometric.components.BackButton
import com.applock.biometric.components.CustomDialog
import com.applock.biometric.helpers.bounceClick
import com.applock.biometric.ui.theme.AppLockTheme
import com.applock.biometric.navigation.Screen
import com.devsky.videorecorder.screens.settings.SettingsViewModel
import com.applock.biometric.ui.screens.splash.shouldShowAppOpen
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import ir.kaaveh.sdpcompose.sdp



@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onBack: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel(),
) {
    val context = LocalContext.current
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    BackButton(onClick = onBack)
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding()
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SettingRow(
                title = stringResource(R.string.settings_change_language),
                subtitle = stringResource(R.string.settings_select_language),
                icon = Icons.Default.Language,
                action = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )


            SettingRow(
                title = stringResource(R.string.share_app),
                subtitle = stringResource(R.string.share_app_desc),
                icon = Icons.Default.Share,
                action = {
                    val appPackageName = context.packageName
                    val shareText = context.getString(
                        R.string.share_message,
                        "https://play.google.com/store/apps/details?id=$appPackageName"
                    )
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    shareIntent.type = "text/plain"
                    val chooser =
                        Intent.createChooser(shareIntent, context.getString(R.string.share_app))
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    shouldShowAppOpen = false
                    context.startActivity(chooser)
                }
            )

            SettingRow(
                title = stringResource(R.string.privacy_policy),
                subtitle = stringResource(R.string.privacy_policy_subtitle),
                icon = Icons.Default.PrivacyTip,
                action = {
                    context.startActivity(
                        Intent(context, PrivacyPolicyActivity::class.java)
                    )
                }
            )
            SettingRow(
                title = stringResource(R.string.rate_us),
                subtitle = stringResource(R.string.rate_us_desc),
                icon = Icons.Default.StarRate,
                action = { viewModel.setShowRatingDialog(true) }
            )
        }

        if (ui.showRatingDialog) {
            RatingDialog(
                rating = ui.rating,
                onRatingChange = { viewModel.setRating(it) },
                onDismiss = {
                    viewModel.setShowRatingDialog(false)
                    viewModel.setRating(0f)
                },
                onConfirm = {
                    viewModel.setShowRatingDialog(false)
                    handleRatingAction(context, ui.rating)
                    viewModel.setRating(0f)
                }
            )
        }

    }
}

@Composable
fun RatingDialog(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    CustomDialog(
        showDialog = true,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.StarRate,
                    contentDescription = stringResource(id = R.string.rate_us),
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = stringResource(R.string.rate_app_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.rate_app_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            RatingBar(
                value = rating,
                style = RatingBarStyle.Fill(
                    activeColor = MaterialTheme.colorScheme.primary,
                    inActiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                onValueChange = { onRatingChange(it) },
                onRatingChanged = { /* Rating changed */ },
                modifier = Modifier.padding(vertical = 16.dp),
                size = 36.dp,
                spaceBetween = 8.dp,
                numOfStars = 5,
                isIndicator = false
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.rate_app_cancel),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = onConfirm,
                    enabled = rating > 0f,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.rate_app_submit),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

private fun handleRatingAction(context: Context, rating: Float) {
    if (rating <= 3f) {
        // Send feedback email
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {

            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email)))
            putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(R.string.rate_app_feedback_email_subject)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.rate_app_feedback_email_body, rating.toInt().toString())
            )
        }
        emailIntent.data = "mailto:".toUri()
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(emailIntent)
        } catch (e: Exception) {
            // Handle case where no email app is available
        }
    } else {
        // Go to Play Store
        val appPackageName = context.packageName
        val intent = Intent(Intent.ACTION_VIEW, "market://details?id=$appPackageName".toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                )
            )
        }
    }
}


@Composable
fun SettingRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isRTL: Boolean = LocalLayoutDirection.current == LayoutDirection.Rtl,
    action: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { action() }
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.width(16.sdp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(4.sdp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray.copy(alpha = 0.8f)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.go),
            modifier = Modifier
                .size(20.dp)
                .rotate(if (isRTL) 180f else 0f),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun HomePreviewLight() {
    AppLockTheme {
        SettingsScreen(rememberNavController())
    }
}

@Preview(
    showBackground = true,
    name = "Dark Mode",
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomePreviewDark() {
    AppLockTheme {
        SettingsScreen(rememberNavController())
    }
}