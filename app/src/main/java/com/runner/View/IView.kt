package com.runner.View

import android.content.Context

interface IView {
    val context: Context?
    fun enableLoadingBar(context: Context?, enable: Boolean, s: String?)
    fun onError(reason: String?)
}