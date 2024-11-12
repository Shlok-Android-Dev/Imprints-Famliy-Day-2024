package com.runner.extras

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.swipe.SwipeIn
import com.mindorks.placeholderview.annotations.swipe.SwipeOut
import com.runner.R
import com.runner.model.InfoModel

@Layout(R.layout.item_info)
class InfoUserView {

    @View(R.id.lblRow1)
    lateinit var txtrow1: TextView


    @View(R.id.lblRow2)
    lateinit var txtrow2: TextView

    @View(R.id.lblRow3)
    lateinit var txtrow3: TextView

    @View(R.id.lblRow4)
    lateinit var txtrow4: TextView

    @View(R.id.lblRow5)
    lateinit var txtrow5: TextView

    var context: Context? = null
    var info: InfoModel? = null
    var swipee: SwipePlaceHolderView? = null
    var swipecardCallback: SwipecardCallback? = null

    constructor(context: Context, model: InfoModel, swipePlaceHolderView: SwipePlaceHolderView) {
        this.context = context
        this.swipecardCallback = context as SwipecardCallback
        this.info = model
        this.swipee = swipePlaceHolderView
        Log.e("@@model",info.toString())
    }

    @Resolve
    private fun onResolve() {
        Log.e("@@model@",info.toString())
        if (!info!!.row1.isEmpty()){
            txtrow1.visibility = android.view.View.VISIBLE
            txtrow1.setText(info!!.row1)
        }else{
            txtrow1.visibility = android.view.View.GONE
        }
        if (!info!!.row2.isEmpty()){
            txtrow2.visibility = android.view.View.VISIBLE
            txtrow2.setText(info!!.row2)

        }else{
            txtrow2.visibility = android.view.View.GONE
        }
        if (!info!!.row3.isEmpty()){
            txtrow3.visibility = android.view.View.VISIBLE
            txtrow3.setText(info!!.row3)
        }else{
            txtrow3.visibility = android.view.View.GONE
        }
        if (!info!!.row4.isEmpty()){
            txtrow4.visibility = android.view.View.VISIBLE
            txtrow4.setText(info!!.row4)
        }else{
            txtrow4.visibility = android.view.View.GONE
        }
        if (!info!!.row5.isEmpty()){
            txtrow5.visibility = android.view.View.VISIBLE
            txtrow5.setText(info!!.row5)
        }else{
            txtrow5.visibility = android.view.View.GONE
        }
    }

    @SwipeIn
    private fun onSwipeIn() {
        swipecardCallback!!.onSwipeIn()
    }

    @SwipeOut
    private fun onSwipeOut() {
        swipecardCallback!!.onSwipeOut()
    }


}