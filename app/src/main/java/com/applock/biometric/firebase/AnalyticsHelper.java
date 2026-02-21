package com.applock.biometric.firebase;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsHelper {

    private static AnalyticsHelper analyticsHelper = null;
    private final FirebaseAnalytics instance;

    private AnalyticsHelper(FirebaseAnalytics instance) {
        this.instance = instance;
    }

    public static AnalyticsHelper getAnalyticsHelper(Context context) {
        return analyticsHelper == null ?
                analyticsHelper = new AnalyticsHelper(FirebaseAnalytics.getInstance(context.getApplicationContext())) :
                analyticsHelper;
    }

    public void logTapEvent(String eventName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, "Tapped");
        instance.logEvent(eventName, bundle);
        Log.d("TAG_analytics", "logEvent:java  " + bundle);
    }
}