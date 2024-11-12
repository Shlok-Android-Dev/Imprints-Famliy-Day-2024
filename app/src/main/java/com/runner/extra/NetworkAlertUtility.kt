package com.ruhe.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
object NetworkAlertUtility {
    fun isConnectingToInternet(context: Context?): Boolean {
        if (context != null) {
            val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null) for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }
}