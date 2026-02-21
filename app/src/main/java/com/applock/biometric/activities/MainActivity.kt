package com.applock.biometric.activities

import android.app.ComponentCaller
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.applock.biometric.R
import com.applock.biometric.firebase.AnalyticsHelper
import com.applock.biometric.helpers.LocaleHelper
import com.applock.biometric.ui.theme.AppLockTheme
import com.devsky.videorecorder.common.IntentConstants
import com.applock.biometric.common.SharedPrefsHelper
import com.applock.biometric.navigation.NavGraph
import com.applock.biometric.navigation.Screen
import com.applock.biometric.ui.screens.splash.shouldShowAppOpen


class MainActivity : FragmentActivity() {

//    private lateinit var inAppUpdate: InAppUpdate
    private lateinit var analyticsHelper: AnalyticsHelper


    private var backPressedTime: Long = 0
    private val BACK_PRESS_INTERVAL = 2000L



    override fun attachBaseContext(base: Context) {
        val context = if (SharedPrefsHelper.isInitialized()) {
            val languageCode = SharedPrefsHelper.getSelectedLanguage()
            LocaleHelper.changeLang(base, languageCode)
        } else {
            base
        }
        super.attachBaseContext(context)
    }


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        analyticsHelper = AnalyticsHelper.getAnalyticsHelper(this)
        analyticsHelper.logTapEvent("app_launched")
//        inAppUpdate = InAppUpdate(this)


        setupBackPressHandling()

        setContent {
            val navController = rememberNavController()

            var currentLayoutDirection by remember {
                mutableStateOf(
                    LocaleHelper.getLayoutDirectionForLanguage(
                        SharedPrefsHelper.getSelectedLanguage()
                    )
                )
            }

            LaunchedEffect(intent) {
                intent?.let { launchIntent ->
                    when (launchIntent.action) {
                        IntentConstants.ACTION_REQUIRE_AUTH -> {
                            if (SharedPrefsHelper.isPinSet() && !SharedPrefsHelper.isAuthenticated()) {
                                navController.navigate(Screen.PinEntry.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                        IntentConstants.ACTION_SETUP_PIN -> {
                            navController.navigate(Screen.PinSetup.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                    
                    // Handle navigation route from floating actions menu
                    launchIntent.getStringExtra("NAV_ROUTE")?.let { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                }
            }

            AppLockTheme {
                CompositionLocalProvider(
                    LocalLayoutDirection provides currentLayoutDirection
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                    ) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            onLanguageChange = { language ->
                                SharedPrefsHelper.saveSelectedLanguage(language)
                                LocaleHelper.changeLang(
                                    context = this,
                                    languageCode = language
                                )
                                currentLayoutDirection = LocaleHelper.getLayoutDirectionForLanguage(language)
                            }
                        )
                    }
                }

            }
        }
    }





    private fun setupBackPressHandling() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + BACK_PRESS_INTERVAL > System.currentTimeMillis()) {
                    shouldShowAppOpen = false
                    finish()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.press_back_to_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                    backPressedTime = System.currentTimeMillis()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
//        inAppUpdate.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        setIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        inAppUpdate.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
//        inAppUpdate.onDestroy()
    }

}

@Composable
fun TopBarTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.str_app),
            modifier = Modifier.padding(end = 8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
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
