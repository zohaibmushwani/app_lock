package com.applock.biometric.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.applock.biometric.helpers.AuthSession
import androidx.fragment.app.FragmentActivity

// Must be FragmentActivity for BiometricPrompt
class LockActivity : FragmentActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val targetPackage = intent.getStringExtra("TARGET_PACKAGE") ?: run {
            finish()
            return
        }

        setupBiometric(targetPackage)

        setContent {
            LockScreenContent(
                targetPackage = targetPackage,
                onUnlockClick = { showBiometricPrompt() }
            )
        }
    }

    private fun setupBiometric(targetPackage: String) {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    AuthSession.setAuthenticated(targetPackage)
                    finish()
                    // Disable animation for instant unlock feel
                    overridePendingTransition(0, 0)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && 
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                         Toast.makeText(applicationContext, "Auth Error: $errString", Toast.LENGTH_SHORT).show()
                    }
                    // Implement Fallback to PIN here
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock App")
            .setSubtitle("Authenticate to access")
            .setNegativeButtonText("Use PIN")
            .build()
    }

    private fun showBiometricPrompt() {
        biometricPrompt.authenticate(promptInfo)
    }

    override fun onResume() {
        super.onResume()
        // Auto-show prompt on resume
        showBiometricPrompt()
    }
    
    // Disable Back button to prevent bypassing
    override fun onBackPressed() {
        // Move task to back instead of finishing, effectively minimizing the app loop
        moveTaskToBack(true)
    }
}

@Composable
fun LockScreenContent(
    targetPackage: String,
    onUnlockClick: () -> Unit
) {
    val context = LocalContext.current
    var appIcon by remember { mutableStateOf<Drawable?>(null) }
    var appName by remember { mutableStateOf("") }

    LaunchedEffect(targetPackage) {
        try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(targetPackage, 0)
            appIcon = pm.getApplicationIcon(appInfo)
            appName = pm.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            appName = "Unknown App"
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f) // Slightly transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (appIcon != null) {
                Image(
                    bitmap = appIcon!!.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "$appName is Locked",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onUnlockClick,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Tap to Unlock", fontSize = 18.sp)
            }
        }
    }
}
