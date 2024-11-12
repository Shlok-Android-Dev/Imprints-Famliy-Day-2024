package com.runner.extras

import android.util.Log
import com.runner.BuildConfig

object LogUtils {
    fun debug(tag: String?, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message!!)
        }
    }
}