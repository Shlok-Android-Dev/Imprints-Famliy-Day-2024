package com.runner.extra

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*


@SuppressLint("ValidFragment")
class DatePicker : DialogFragment(), OnDateSetListener {
    var fetcher: DateFetcher? = null
    var Year = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        //Log.e("year ",year+"month "+month+"day "+day);
        c[year, month] = day
        val datePickerDialog = DatePickerDialog(activity!!, AlertDialog.THEME_HOLO_LIGHT, this, year, month, day)
        /*AlertDialog.THEME_HOLO_LIGHT//Light mode
        custom my dialogtheme
        https://stackoverflow.com/questions/30239627/how-to-change-the-style-of-a-datepicker-in-android
        AlertDialog.THEME_HOLO_Dark/?Dark mode*/if (Year != 0) {
            c.add(Calendar.YEAR, -Year)
            datePickerDialog.datePicker.maxDate = c.timeInMillis
            // Subtract 6 days from Calendar updated date
            c.add(Calendar.YEAR, -100)
            // Set the Calendar new date as minimum date of date picker
            datePickerDialog.datePicker.minDate = c.timeInMillis
        }
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        var monthString = (month + 1).toString()
        var dayOfMonthString = dayOfMonth.toString()
        if (monthString.length == 1) {
            monthString = "0$monthString"
        }
        if (dayOfMonthString.length == 1) {
            dayOfMonthString = "0$dayOfMonthString"
        }
        fetcher!!.dateset("$year-$monthString-$dayOfMonthString")
    }

    fun setCustomListener(fetcher: DateFetcher?, year: Int) {
        this.fetcher = fetcher
        Year = year
    }
}