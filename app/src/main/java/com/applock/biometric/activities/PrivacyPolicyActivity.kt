package com.applock.biometric.activities

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.applock.biometric.R


class PrivacyPolicyActivity : AppCompatActivity() {
    private var webView: WebView? = null
    private var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_privacy_policy)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                systemBarsInsets.bottom
            )
            insets
        }

        findViewById<View?>(R.id.imgBack)?.setOnClickListener { finish() }

        try {
            if (isInternetAvailable(this)) {
                webView = findViewById(R.id.webView)
                loadUrl()
            } else findViewById<View?>(R.id.tvInternet)?.visibility = View.VISIBLE
        } catch (ignored: Exception) {
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        if (network == null) return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network)
        if (activeNetwork == null) return false
        if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return true
        } else if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return true
        } else return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun loadUrl(url: String = "https://backgroundvideorecorder1.blogspot.com/2025/10/background-video-recorder.html") {
        val settings = webView!!.settings

        settings.setJavaScriptEnabled(true)
        webView!!.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY

        webView!!.settings.builtInZoomControls = true
        webView!!.settings.useWideViewPort = true
        webView!!.settings.loadWithOverviewMode = true

        progressDialog = AlertDialog.Builder(this@PrivacyPolicyActivity)
            .setMessage("Loading...")
            .setCancelable(false)
            .create()
        progressDialog?.show()

        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                Toast.makeText(
                    this@PrivacyPolicyActivity,
                    "Error:$description",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        webView!!.loadUrl(url)
    }
}
