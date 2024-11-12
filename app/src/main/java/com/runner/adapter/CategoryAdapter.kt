package com.runner.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.runner.R
import android.widget.TextView
import java.util.ArrayList

class CategoryAdapter(context: Context, textViewResourceId: Int, category: ArrayList<String>) :
    ArrayAdapter<Any?>(context, textViewResourceId, category.toTypedArray()) {
    var inflater: LayoutInflater
    var category1: ArrayList<String>

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        category1 = category
    }

    fun getCustomView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View {

       // Inflating the layout for the custom Spinner
        val layout = inflater.inflate(R.layout.custom_spinner_layout, parent, false)
        val row_date = layout.findViewById<TextView>(R.id.date_row)
        row_date.text = category1[position]
        return layout
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }
    // It gets a View that displays the data at the specified position
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }
}