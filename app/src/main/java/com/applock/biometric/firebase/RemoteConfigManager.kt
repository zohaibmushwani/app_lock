package com.applock.biometric.firebase

import android.content.Context
import android.util.Log
import com.google.common.reflect.TypeToken
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class RemoteConfig(
    private var context: Context,
    private var keysList: Map<String, String>? = null
) {
    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

//    private fun printLogs(msg: String) {
//        Log.d(Logs.REMOTE_LOGS.name, msg)
//    }

    private var configUpdateListener: ConfigUpdateListener? = null
    fun fetchRemoteConfig(callback: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {

            FirebaseApp.initializeApp(context)
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings: FirebaseRemoteConfigSettings =
                FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build()
            mFirebaseRemoteConfig?.setConfigSettingsAsync(configSettings)
            keysList?.let { mFirebaseRemoteConfig?.setDefaultsAsync(it) }

            configUpdateListener = object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    Log.d("RemoteConfig", "Updated")
                    mFirebaseRemoteConfig?.fetchAndActivate()?.addOnCompleteListener {}
                }

                override fun onError(error: FirebaseRemoteConfigException) {
//                    printLogs("Exception")
                }
            }

            fun onComplete() {
                configUpdateListener?.let { mFirebaseRemoteConfig?.addOnConfigUpdateListener(it) }
                callback.invoke()
                return
            }

            when (mFirebaseRemoteConfig?.info?.lastFetchStatus) {
                FirebaseRemoteConfig.LAST_FETCH_STATUS_SUCCESS -> {
                    Log.d("RemoteConfig", "Fetch succeeded.")
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                    return@launch
                }

                FirebaseRemoteConfig.LAST_FETCH_STATUS_FAILURE -> {
                    Log.d("RemoteConfig", "Fetch failed.")
                    val isSuccess = fetchRemoteConfig()
                    withContext(Dispatchers.Main) {
                        if (isSuccess) {
                            onComplete()
                        } else {
                            onComplete()
                        }
                    }
                }

                FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET -> {
                    Log.d("RemoteConfig", "Fetch has not been initiated yet.")
                    val isSuccess = fetchRemoteConfig()
                    withContext(Dispatchers.Main) {
                        if (isSuccess) {
                            onComplete()
                        } else {
                            onComplete()
                        }
                    }
                }

                FirebaseRemoteConfig.LAST_FETCH_STATUS_THROTTLED -> {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                    Log.d("RemoteConfig", "Fetch is throttled.")
                    return@launch
                }
            }
        }
    }

    suspend fun fetchRemoteConfig(): Boolean = suspendCoroutine { continuation ->
        mFirebaseRemoteConfig?.fetchAndActivate()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(true) // Resume coroutine with success
            } else {
                continuation.resume(false) // Resume coroutine with failure
            }
        }
    }

    fun getRemoteValues(key: String, response: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            if (mFirebaseRemoteConfig == null) {
                withContext(Dispatchers.Main) {
                    val value=keysList?.get(key)?:""
                    response(value)
                }
                fetchRemoteConfig {
//                        response(mFirebaseRemoteConfig?.getString(key)?:"")
                }
            } else {
                withContext(Dispatchers.Main) {
                    response(mFirebaseRemoteConfig?.getString(key) ?: "")
                }
            }
        }
    }

    suspend fun getRemoteValuesWithAwait(key: String, response: (String) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            if (mFirebaseRemoteConfig == null) {
                val value = keysList?.get(key) ?: ""
                response(value)
                fetchRemoteConfig {
                }
            } else {
                response(mFirebaseRemoteConfig?.getString(key) ?: "")
            }
        }
    }

    fun getRemoteValuesFromJson(key: String,defaultKeyIfFailed:String, response: (List<Map<String, String>>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            if (mFirebaseRemoteConfig == null) {
                withContext(Dispatchers.Main) {
                    response(listOf(mapOf("ADMOB" to defaultKeyIfFailed)))
                }
                fetchRemoteConfig {

                }
            } else {
                withContext(Dispatchers.Main) {
                    val keyValue=mFirebaseRemoteConfig?.getString(key) ?: ""

                    val adProviders: List<Map<String, String>> = if(keyValue.isEmpty()){
                        listOf(mapOf("ADMOB" to defaultKeyIfFailed))
                    }
                    else {
                        try {
                            val type = object : TypeToken<List<Map<String, String>>>() {}.type
                            Gson().fromJson(keyValue, type)
                        } catch (e: Exception)  {
                            listOf(mapOf("ADMOB" to defaultKeyIfFailed))
                        }
                    }

                    response(adProviders)
                }
            }
        }
    }


    fun getRemoteValuesFromJsonExp(key: String,defaultKeyIfFailed:List<Map<String,String>>, response: (List<Map<String, String>>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            if (mFirebaseRemoteConfig == null) {
                withContext(Dispatchers.Main) {
                    response(defaultKeyIfFailed)
                }
                fetchRemoteConfig {

                }
            } else {
                withContext(Dispatchers.Main) {
                    val keyValue=mFirebaseRemoteConfig?.getString(key) ?: ""

                    val adProviders: List<Map<String, String>> = if(keyValue.isEmpty()){
                        defaultKeyIfFailed
                    }
                    else {
                        try {
                            val type = object : TypeToken<List<Map<String, String>>>() {}.type
                            Gson().fromJson(keyValue, type)
                        } catch (e: Exception) /*TODO default priority in case not found*/ {
                            defaultKeyIfFailed
                        }
                    }

                    response(adProviders)
                }
            }
        }
    }
}
