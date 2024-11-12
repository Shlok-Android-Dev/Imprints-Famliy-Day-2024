package com.runner.extras

import android.app.Dialog
import android.content.Context
import com.runner.R
import android.view.WindowManager
import android.view.Gravity
import android.view.LayoutInflater

class ProgressDialogClass(context: Context?) : Dialog(
    context!!, R.style.my_dialog_theme
) {
    init {
        //appUtils.colorStatusBar(getWindow(),context,false);
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val wlmp = window!!.attributes
        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = wlmp
        setTitle(null)
        val view = LayoutInflater.from(context).inflate(R.layout.progress_bar, null)
        setContentView(view)
    }
}