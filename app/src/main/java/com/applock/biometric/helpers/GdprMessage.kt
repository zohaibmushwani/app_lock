package com.applock.biometric.helpers

//import com.google.android.ump.ConsentDebugSettings
//import com.google.android.ump.ConsentInformation
//import com.google.android.ump.ConsentRequestParameters
//import com.google.android.ump.UserMessagingPlatform
//import kotlin.text.isNotEmpty
//
//class GdprMessage {
//
//    private val TAG = "GdprMessage"
//    private var consentInfo: ConsentInformation? = null
//    private var gdprTimer: CountDownTimer? = null
//
//    fun interface OnConsentGatheringCompleteListener {
//        fun consentGatheringComplete()
//    }
//
//    /**
//     * requestConsentForm:
//     * - context: Activity (required by UMP UI)
//     * - gdprReset: whether to call `consentInfo.reset()` (dev use only)
//     * - testDeviceHashedId: optional hashed device id to enable debug geography
//     */
//    fun requestConsentForm(
//        context: Activity,
//        gdprReset: Boolean = false,
//        testDeviceHashedId: String = "",
//        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener
//    ) {
//        // Cancel any previous timer
//        gdprTimer?.cancel()
//        gdprTimer = object : CountDownTimer(7_000L, 1_000L) {
//            override fun onTick(millisUntilFinished: Long) {}
//            override fun onFinish() {
//                Log.d(TAG, "GDPR timer finished; calling completion callback")
//                onConsentGatheringCompleteListener.consentGatheringComplete()
//            }
//        }.apply { start() }
//
//        // Build debug params (only during testing)
//        val debugSettings = if (testDeviceHashedId.isNotEmpty()) {
//            ConsentDebugSettings.Builder(context)
//                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//                .addTestDeviceHashedId(testDeviceHashedId)
//                .build()
//        } else null
//
//        val params = if (debugSettings != null) {
//            ConsentRequestParameters.Builder()
//                .setConsentDebugSettings(debugSettings)
//                .setTagForUnderAgeOfConsent(false)
//                .build()
//        } else {
//            ConsentRequestParameters.Builder()
//                .setTagForUnderAgeOfConsent(false)
//                .build()
//        }
//
//        // Get consent information
//        consentInfo = UserMessagingPlatform.getConsentInformation(context)
//        if (gdprReset) {
//            try { consentInfo?.reset() } catch (t: Throwable) { Log.w(TAG, "reset consent failed", t) }
//        }
//
//        // Request update
//        consentInfo?.requestConsentInfoUpdate(
//            context,
//            params,
//            {
//                // success -> load and show form if required
//                Log.d(TAG, "requestConsentInfoUpdate success. status=${consentInfo?.consentStatus}")
//                UserMessagingPlatform.loadAndShowConsentFormIfRequired(context) { formError ->
//                    // formError == null -> finished without error
//                    Log.d(TAG, "loadAndShowConsentFormIfRequired finished. formError=$formError")
//                    // stop timer and notify caller
//                    gdprTimer?.cancel()
//                    gdprTimer = null
//                    onConsentGatheringCompleteListener.consentGatheringComplete()
//                }
//            },
//            { loadAndShowError ->
//                // failure retrieving consent info — stop timer and notify caller
//                Log.w(TAG, "requestConsentInfoUpdate failed: $loadAndShowError")
//                gdprTimer?.cancel()
//                gdprTimer = null
//                onConsentGatheringCompleteListener.consentGatheringComplete()
//            }
//        )
//    }
//
//    // Helper: check whether we can request ads (based on consent info)
//    fun canRequestAds(): Boolean {
//        val status = consentInfo?.consentStatus ?: ConsentInformation.ConsentStatus.UNKNOWN
//        return (status == ConsentInformation.ConsentStatus.OBTAINED ||
//                status == ConsentInformation.ConsentStatus.NOT_REQUIRED)
//    }
//
//    // Helper: determine if consent form was required
//    fun isConsentRequired(): Boolean {
//        return (consentInfo?.consentStatus == ConsentInformation.ConsentStatus.REQUIRED)
//    }
//}

