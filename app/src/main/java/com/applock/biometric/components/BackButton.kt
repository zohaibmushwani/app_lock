package com.applock.biometric.components

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.kaaveh.sdpcompose.sdp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.applock.biometric.R

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    isRTL: Boolean = LocalLayoutDirection.current == LayoutDirection.Rtl,
    onClick: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    IconButton(singleClick{
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            onClick()
        }else{
            Log.e("BackButton", "BackButton: ${lifecycleOwner.lifecycle.currentState}")
        }
    }) {
        Icon(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = stringResource(R.string.back),
            modifier = Modifier
                .padding(10.dp)
                .size(16.sdp)
                .rotate(if (isRTL) 180f else 0f)
        )
    }
}