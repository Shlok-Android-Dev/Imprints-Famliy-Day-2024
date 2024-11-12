package com.ruhe.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.regex.Pattern


object Utils {
    fun DrawableChange(ctx: AppCompatActivity, d: Int, color: Int): Drawable {
        val drawable1 = ctx.resources.getDrawable(d).mutate()
        drawable1.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        return drawable1
    }

    fun isEmailValid(email: String?): Boolean {
        var isValid = false
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        if (matcher.matches()) isValid = true
        return isValid
    }

    fun validateString(str: String): Boolean {
        return stringNotNull(str) && stringNotEmpty(str)
    }

    private fun stringNotNull(str: String?): Boolean {
        return str != null
    }

    private fun stringNotEmpty(str: String): Boolean {
        return !str.isEmpty()
    }

    fun dateFormat(date: String?): String {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateFormat1 = SimpleDateFormat("dd MMMM yyyy_hh:mm aa")
        try {
            val endDate = dateFormat.parse(date)
            val dtes = dateFormat1.format(endDate)
            return dtes.split("_".toRegex()).toTypedArray()[0] + " at " + dtes.split("_".toRegex()).toTypedArray()[1]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun hideSoftKeyboard(activity: AppCompatActivity) {
        val inputManager = activity.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            try {
                assert(inputManager != null)
                inputManager.hideSoftInputFromWindow(focusedView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS)
            } catch (e: AssertionError) {
                e.printStackTrace()
            }
        }
    }
}
