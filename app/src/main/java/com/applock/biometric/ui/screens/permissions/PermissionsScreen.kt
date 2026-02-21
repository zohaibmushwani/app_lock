package com.applock.biometric.ui.screens.permissions

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.applock.biometric.R
import com.applock.biometric.helpers.dashedBorder
import com.applock.biometric.ui.theme.AppLockTheme
import com.applock.biometric.navigation.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ir.kaaveh.sdpcompose.sdp

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(navController: NavHostController) {
    val context = LocalContext.current

    val permissions = remember {
        buildList {
            add(Manifest.permission.CAMERA)
            add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_VIDEO)
                add(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    val permissionsState = rememberMultiplePermissionsState(permissions)

    val allGranted = permissionsState.allPermissionsGranted


    PermissionsScreen(
        navController = navController,
        permissionsState = permissionsState,
        context = context,
        allGranted = allGranted
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    navController: NavController,
    permissionsState: MultiplePermissionsState,
    context: Context,
    allGranted: Boolean
) {
    Scaffold(
        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        text = stringResource(id = R.string.permissions_required),
//                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
//                    )
//                }
//            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Image(
               imageVector = Icons.Default.BatterySaver,
                contentDescription = null,
                modifier = Modifier
                    .size(160.sdp)
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.height(25.sdp))
            Text(
                text = stringResource(id = R.string.permissions_required),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(10.sdp))
            Text(
                text = stringResource(id = R.string.permissions_description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            Spacer(modifier = Modifier.height(25.sdp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .dashedBorder(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        dashLength = 10.dp,
                        gapLength = 8.dp,
                        strokeWidth = 2.dp
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                }
            }
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (allGranted) {
                        navController.navigate(Screen.BatteryOptimization.route) {
                            popUpTo(Screen.Permissions.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = if (allGranted) Icons.Default.Check else Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (allGranted)
                        stringResource(id = R.string.continue_text)
                    else stringResource(id = R.string.grant_permissions),
                    style = MaterialTheme.typography.bodyLarge
                )
            }



            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = if (allGranted)
                    stringResource(id = R.string.all_permissions_granted)
                else stringResource(id = R.string.permissions_to_proceed),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PermissionRow(title: String, icon: ImageVector, granted: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.sdp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        }

        Card(
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = if (granted) stringResource(id = R.string.granted) else stringResource(id = R.string.required),
                color = if (granted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PermissionScrPreview() {
    AppLockTheme {
        Box(Modifier.fillMaxSize()){ PermissionsScreen(navController = rememberNavController()) }
    }
}