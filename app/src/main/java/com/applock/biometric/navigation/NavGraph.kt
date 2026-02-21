package com.applock.biometric.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.applock.biometric.common.SharedPrefsHelper
import com.applock.biometric.ui.screens.home.HomeScreen
import com.applock.biometric.ui.screens.language.LanguageSelectionScreen
import com.applock.biometric.ui.screens.permissions.BatteryOptimizationScreen
import com.applock.biometric.ui.screens.permissions.PermissionsScreen
import com.devsky.videorecorder.screens.security.PinEntryScreen
import com.applock.biometric.ui.screens.security.PinSetupScreen
import com.applock.biometric.ui.screens.settings.AboutAppScreen
import com.applock.biometric.ui.screens.settings.SecuritySettingsScreen
import com.applock.biometric.ui.screens.settings.SettingsScreen
import com.applock.biometric.ui.screens.splash.SplashScreen
import com.applock.biometric.ui.screens.walkthrough.WalkthroughScreen
import com.applock.biometric.ui.screens.security.SecurityQuestionScreen
import com.applock.biometric.ui.screens.security.PasswordRecoveryScreen


@Composable
fun NavGraph(
    navController: NavHostController,
    onLanguageChange: (String) -> Unit = {}
) {
    val startDestination = when {
        !SharedPrefsHelper.isPinSet() -> Screen.Splash.route
        !SharedPrefsHelper.isAuthenticated() -> Screen.PinEntry.route
        else -> Screen.Home.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Screen.Splash.route,
        ) {
            SplashScreen(navController = navController)
        }

        composable(
            route = Screen.Walkthrough.route,
        ) {
            WalkthroughScreen(navController = navController)
        }

        composable(
            route = Screen.Home.route,
        ) {
            HomeScreen(navController = navController)
        }

        composable(
            route = Screen.Permissions.route,
        ) {
            PermissionsScreen(navController = navController)
        }

        composable(
            route = Screen.BatteryOptimization.route,
        ) {
            BatteryOptimizationScreen(
                navController = navController,
                onContinue = {
                    navController.navigate(Screen.PinSetup.route) {
                        popUpTo(Screen.BatteryOptimization.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Settings.route,
        ) {
            SettingsScreen(navController, onBack = {
                navController.popBackStack()
            })
        }

        composable(
            route = Screen.AboutApp.route,
        ) {
            AboutAppScreen(navController = navController, onBack = {
                navController.popBackStack()
            })
        }

        composable(
            route = Screen.LanguageSelection.route,
        ) {
            LanguageSelectionScreen(
                navController = navController,
                onLanguageChange = onLanguageChange
            )
        }

        composable(
            route = Screen.PinEntry.route,
        ) {
            PinEntryScreen(
                onPinValidated = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.PinEntry.route) { inclusive = true }
                    }
                },
                onForgotPin = {
                    navController.navigate(Screen.PasswordRecovery.route)
                }
            )
        }

        composable(
            route = Screen.PinSetup.route,
        ) {
            PinSetupScreen(
                onPinSet = {
                    navController.navigate(Screen.SecurityQuestion.route) {
                        popUpTo(Screen.PinSetup.route) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PinSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.SecurityQuestion.route
        ) {
            SecurityQuestionScreen(
                navController = navController,
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SecurityQuestion.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.SecuritySettings.route,
        ) {
            SecuritySettingsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToPinSetup = {
                    navController.navigate(Screen.PinSetup.route)
                },
                onNavigateToPinChange = {
                    navController.navigate(Screen.PinEntry.route)
                },
                onNavigateToSecurityQuestion = {
                    navController.navigate(Screen.SecurityQuestion.route)
                }
            )
        }

        composable(
            route = Screen.PasswordRecovery.route
        ) {
            PasswordRecoveryScreen(
                navController = navController,
                onSuccess = {
                    navController.navigate(Screen.PinSetup.route) {
                        popUpTo(Screen.PinEntry.route) { inclusive = true }
                    }
                }
            )
        }

    }
}