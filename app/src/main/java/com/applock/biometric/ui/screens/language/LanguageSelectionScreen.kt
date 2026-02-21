package com.applock.biometric.ui.screens.language

import android.content.res.Configuration
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.applock.biometric.R
import com.applock.biometric.common.SharedPrefsHelper
import com.applock.biometric.helpers.bounceClick
import com.applock.biometric.ui.theme.AppLockTheme
import com.applock.biometric.navigation.Screen
import com.devsky.videorecorder.screens.language.Language
import flagkit.Flag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    navController: NavHostController,
    onLanguageChange: (String) -> Unit = {}
) {
    var selectedLanguage by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!SharedPrefsHelper.isLanguageSelectionCompleted()) return@LaunchedEffect

        val currentLanguageCode = SharedPrefsHelper.getSelectedLanguage()
        val currentLanguage = Language.Companion.getLanguageByCode(currentLanguageCode) ?: Language.Companion.getDefaultLanguage()
        selectedLanguage = currentLanguage.code

        val selectedIndex = Language.Companion.supportedLanguages.indexOfFirst { it.code == currentLanguage.code }
        if (selectedIndex != -1) {
            lazyListState.scrollToItem(selectedIndex)
        }
    }

    var lockedDirection: LayoutDirection? by remember { mutableStateOf(null) }
    var lockedContext: Context? by remember { mutableStateOf(null) }

    val providedDirection = lockedDirection ?: LocalLayoutDirection.current
    val providedContext = lockedContext ?: context

    CompositionLocalProvider(
        LocalLayoutDirection provides providedDirection,
        LocalContext provides providedContext
    ) {
        val initialContext = remember { context }
        val frozenTitle = remember { initialContext.getString(R.string.language_choose_title) }
        val frozenContinue = remember { initialContext.getString(R.string.language_continue) }
        val frozenCheckmark = remember { initialContext.getString(R.string.language_checkmark) }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = frozenTitle,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    if(selectedLanguage.isNotEmpty()){
                        Box(
                            modifier = Modifier
                                .bounceClick(
                                    onClick = {

                                        // Lock current direction and context for this screen before locale change
                                        lockedDirection = providedDirection
                                        lockedContext = providedContext
                                        onLanguageChange(selectedLanguage)
                                        if (!SharedPrefsHelper.isLanguageSelectionCompleted()) {
                                            SharedPrefsHelper.setLanguageSelectionCompleted(true)
                                            SharedPrefsHelper.isWalkthroughCompleted(
                                                CoroutineScope(
                                                    Dispatchers.Main
                                                )
                                            ) { isCompleted ->
                                                if (isCompleted) {
                                                    navController.navigate(Screen.Home.route) {
                                                        popUpTo(Screen.LanguageSelection.route) {
                                                            inclusive = true
                                                        }
                                                        launchSingleTop = true
                                                    }
                                                } else {
                                                    navController.navigate(Screen.Walkthrough.route) {
                                                        popUpTo(Screen.LanguageSelection.route) {
                                                            inclusive = true
                                                        }
                                                        launchSingleTop = true
                                                    }
                                                }
                                            }
                                        } else {
                                            navController.popBackStack()
                                        }
                                    }
                                )
                                .clip(RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = frozenContinue,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                }
            )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 24.dp , horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(Language.Companion.supportedLanguages) { language ->
                        LanguageItem(
                            language = language,
                            isSelected = selectedLanguage == language.code,
                            checkmarkText = frozenCheckmark,
                            onLanguageSelected = {
                                selectedLanguage = language.code
                            }
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun LanguageItem(
    language: Language,
    isSelected: Boolean,
    checkmarkText: String,
    onLanguageSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { onLanguageSelected() },
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),

        border = if (isSelected)
            BorderStroke(
                width = 1.2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag
            Flag(
                code = language.flagCode,
                shape = RoundedCornerShape(8.dp),
                size = DpSize(40.dp, 30.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Language Name
            Text(
                text = language.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )

            // Selection Indicator
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = checkmarkText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Light Mode")
@Composable
fun LanguagePreviewLight() {
    AppLockTheme {
        LanguageSelectionScreen(navController = rememberNavController())
    }
}

@Preview(
    showBackground = true,
    name = "Dark Mode",
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun LanguagePreviewDark() {
    AppLockTheme {
        LanguageSelectionScreen(navController = rememberNavController())
    }
}
