package com.applock.biometric.ui.screens.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.applock.biometric.R
import com.applock.biometric.navigation.Screen
import ir.kaaveh.sdpcompose.sdp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(
    navController: NavController,
    onBack: () -> Unit = {},
    isRTL: Boolean = LocalLayoutDirection.current == LayoutDirection.Rtl,
) {
    val context = LocalContext.current
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrDefault("1.0")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_app)) },
                navigationIcon = {
                    IconButton(
                        onBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.back),
                            modifier = Modifier
                                .padding(8.dp)
                                .rotate(if (isRTL) 180f else 0f)

                        )
                    }
                }
            )
        }
    ) { paddout ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddout),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Image(
                painter = painterResource(R.mipmap.ic_launcher_round),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.sdp).clip(CircleShape)
            )

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )

            // Version
            Text(
                text = stringResource(R.string.version_format, version as Any),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )

            // Last Updated (empty for now)
            Text(
                text = "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = stringResource(R.string.nav_home),
                        modifier = Modifier
                            .size(35.dp)
                            .clickable {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.AboutApp.route) { inclusive = true }
                                }
                            }
                    )
                    Text(
                        text = stringResource(R.string.nav_home),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                // Rate Us Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                       imageVector = Icons.Outlined.StarRate,
                        contentDescription = stringResource(R.string.rate_us),
                        modifier = Modifier
                            .size(35.dp)
                            .clickable {
                                val appPackageName = context.packageName
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    "market://details?id=$appPackageName".toUri()
                                )
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
                    )
                    Text(
                        text = stringResource(R.string.rate_us),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Share Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = stringResource(R.string.share),
                        modifier = Modifier
                            .size(35.dp)
                            .clickable {
                                val appPackageName = context.packageName
                                val shareText = context.getString(
                                    R.string.share_message,
                                    "https://play.google.com/store/apps/details?id=$appPackageName"
                                )
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                shareIntent.type = "text/plain"
                                val chooser = Intent.createChooser(
                                    shareIntent,
                                    context.getString(R.string.share_app)
                                )
                                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(chooser)
                            }
                    )
                    Text(
                        text = stringResource(R.string.share),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = {
                    try {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                ("market://details?id=" + context.packageName).toUri()
                            )
                        )
                    } catch (e: ActivityNotFoundException) {
                        context
                            .startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    ("https://play.google.com/store/apps/details?id=" + context.packageName).toUri()
                                )
                            )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 32.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    stringResource(R.string.check_for_updates),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

}
