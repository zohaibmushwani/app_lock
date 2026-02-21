package com.applock.biometric.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.applock.biometric.helpers.PermissionUtils

@Composable
fun SecurityDashboard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // State to trigger recomposition when app resumes (checking permission changes)
    var refreshTrigger by remember { mutableStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Check Permissions
    val securityState = remember(refreshTrigger, lifecycleOwner) {
        val isAccessibilityEnabled = PermissionUtils.isAccessibilityServiceEnabled(context)
        val isOverlayEnabled = PermissionUtils.canDrawOverlays(context)
        val isBatteryOptimized = !PermissionUtils.isIgnoringBatteryOptimizations(context)
        Triple(isAccessibilityEnabled, isOverlayEnabled, isBatteryOptimized)
        val isIgnoringBatteryOptimizations = PermissionUtils.isIgnoringBatteryOptimizations(context)
        val isAdminActive = PermissionUtils.isAdminActive(context)
        Triple(isAccessibilityEnabled, isOverlayEnabled, isIgnoringBatteryOptimizations) to isAdminActive
    }

    val (permissionsTriple, isAdminActive) = securityState
    val (isAccessibilityEnabled, isOverlayEnabled, isIgnoringBatteryOptimizations) = permissionsTriple

    // Determine Status
    val (statusTitle, statusColor, statusIcon, actionLabel, actionIntent) = when {
        !isAccessibilityEnabled -> {
            SecurityStatus(
                "Service Disabled",
                MaterialTheme.colorScheme.error,
                Icons.Default.Security,
                "Enable Service",
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            )
        }
        !isOverlayEnabled -> {
            SecurityStatus(
                "Overlay Missing",
                MaterialTheme.colorScheme.error,
                Icons.Default.Warning,
                "Allow Overlay",
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
            )
        }
        isIgnoringBatteryOptimizations -> {
            SecurityStatus(
                "Optimization Active",
                Color(0xFFFFA000), // Amber
                Icons.Default.BatteryAlert,
                "Boost Stability",
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}"))
            )
        }
        else -> {
            SecurityStatus(
                "Reference Secure", // "System Secure"?
                Color(0xFF4CAF50), // Green
                Icons.Default.CheckCircle,
                null,
                null
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (actionLabel == null) "All Systems Go" else "Action Required",
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )
                Text(
                    text = statusTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (actionLabel != null && actionIntent != null) {
                Button(
                    onClick = { context.startActivity(actionIntent) },
                    colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

data class SecurityStatus(
    val title: String,
    val color: Color,
    val icon: ImageVector,
    val actionLabel: String?,
    val intent: Intent?
)
