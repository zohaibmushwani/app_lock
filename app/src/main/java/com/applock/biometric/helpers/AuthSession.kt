package com.applock.biometric.helpers

object AuthSession {
    private var lastAuthenticatedPackage: String? = null
    private var lastAuthenticatedTime: Long = 0
    private const val TIMEOUT_MS = 60000L // 1 minute default timeout

    fun isSessionValid(packageName: String): Boolean {
        if (lastAuthenticatedPackage != packageName) return false
        val currentTime = System.currentTimeMillis()
        // Simple Logic: Valid if authenticated recently.
        // Can be extended with Relock Policy rules.
        return (currentTime - lastAuthenticatedTime) < TIMEOUT_MS
    }

    fun setAuthenticated(packageName: String) {
        lastAuthenticatedPackage = packageName
        lastAuthenticatedTime = System.currentTimeMillis()
    }
    
    fun clear() {
        lastAuthenticatedPackage = null
        lastAuthenticatedTime = 0
    }
}
