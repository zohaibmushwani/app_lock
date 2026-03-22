package com.applock.biometric.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.applock.biometric.R
import com.applock.biometric.data.PreferencesManager
import com.applock.biometric.helpers.AppInfo
import com.applock.biometric.helpers.AppUtils
import com.applock.biometric.navigation.Screen
import androidx.compose.foundation.Image
import ir.kaaveh.sdpcompose.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val ctx = LocalContext.current
    var apps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            apps = AppUtils.getInstalledApps(ctx)
        }
    }

    val filteredApps = remember(apps, searchQuery) {
        if (searchQuery.isBlank()) apps
        else {
            val query = searchQuery.trim().lowercase()
            apps.filter { app ->
                app.appName.lowercase().contains(query) ||
                        app.packageName.lowercase().contains(query)
            }
        }
    }

    val (lockedApps, unlockedApps) = remember(filteredApps, refreshTrigger) {
        filteredApps.partition { PreferencesManager.isAppLocked(it.packageName) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopBarTitle()
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                com.applock.biometric.ui.components.SecurityDashboard(
                    modifier = Modifier.padding(16.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = {
                        Text(stringResource(R.string.search_apps))
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    if (lockedApps.isNotEmpty()) {
                        item(key = "locked_header") {
                            SectionHeader(stringResource(R.string.locked_apps))
                        }
                        items(
                            items = lockedApps,
                            key = { it.packageName }
                        ) { app ->
                            AppListItem(
                                app = app,
                                onLockChanged = { refreshTrigger++ }
                            )
                        }
                    }

                    if (unlockedApps.isNotEmpty()) {
                        item(key = "unlocked_header") {
                            SectionHeader(stringResource(R.string.other_apps))
                        }
                        items(
                            items = unlockedApps,
                            key = { it.packageName }
                        ) { app ->
                            AppListItem(
                                app = app,
                                onLockChanged = { refreshTrigger++ }
                            )
                        }
                    }

                    if (lockedApps.isEmpty() && unlockedApps.isEmpty()) {
                        item(key = "empty") {
                            Text(
                                text = stringResource(R.string.no_apps_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun AppListItem(
    app: AppInfo,
    onLockChanged: () -> Unit
) {
    var isLocked by remember(app.packageName) {
        mutableStateOf(PreferencesManager.isAppLocked(app.packageName))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = app.icon.toBitmap().asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = app.appName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = isLocked,
            onCheckedChange = { checked ->
                isLocked = checked
                PreferencesManager.setAppLocked(app.packageName, checked)
                onLockChanged()
            }
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

@Composable
fun TopBarTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.str_app),
            modifier = Modifier.padding(end = 8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.W900
        )
        Text(
            text = stringResource(R.string.str_lock),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier,
            fontWeight = FontWeight.Bold
        )
    }
}