package com.applock.biometric.common

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.jakewharton.processphoenix.ProcessPhoenix;

class InAppUpdate(private val parentActivity: Activity) : InstallStateUpdatedListener {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(parentActivity)
    private var currentType = AppUpdateType.FLEXIBLE

    private companion object {
        const val MY_REQUEST_CODE = 500
    }

    init {
        // Initialize the app update manager and check for updates
        Log.d("TAG_update", "init:")
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            Log.d("TAG_update", "appUpdateManager: $appUpdateInfo")
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    // If an update is available, start the update flow
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
                    }
                    Log.d("TAG_update", "UPDATE_AVAILABLE: $appUpdateInfo")

                }

                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    Log.d("TAG_update", "DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS: $appUpdateInfo")
                }

                UpdateAvailability.UNKNOWN -> {
                    Log.d("TAG_update", "UNKNOWN: $appUpdateInfo")
                }

                UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                    Log.d("TAG_update", "UPDATE_NOT_AVAILABLE: $appUpdateInfo")
                }
            }
        }
        appUpdateManager.registerListener(this)
    }

    private fun startUpdate(info: AppUpdateInfo, type: Int) {
        // Start the update flow with the given type
        try {
            appUpdateManager.startUpdateFlowForResult(info, type, parentActivity, MY_REQUEST_CODE)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
        currentType = type
    }

    fun onResume() {
        // Resume the update flow after returning from the background
        Log.d("TAG_update", "Resume:")
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            Log.d("TAG_update", "Resume:$appUpdateInfo")

            when (currentType) {
                AppUpdateType.FLEXIBLE -> {
                    Log.d("TAG_update", "Resume FLEXIBLE:$appUpdateInfo")
                    // If the update is downloaded but not installed, notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        flexibleUpdateDownloadCompleted()
                    }
                }

                AppUpdateType.IMMEDIATE -> {
                    Log.d("TAG_update", "Resume IMMEDIATE:$appUpdateInfo")
                    // If the update is in progress, resume the update flow
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
                    }
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Handle the result of the update flow
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                Log.e("ERROR", "Update flow failed! Result code: $resultCode")
            }
        }
    }

    private fun flexibleUpdateDownloadCompleted() {
        ProcessPhoenix.triggerRebirth(parentActivity);
    }

    fun onDestroy() {
        appUpdateManager.unregisterListener(this)
    }

    override fun onStateUpdate(installState: InstallState) {
        // Listen for state updates and handle them accordingly
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            flexibleUpdateDownloadCompleted()
        }
    }
}
