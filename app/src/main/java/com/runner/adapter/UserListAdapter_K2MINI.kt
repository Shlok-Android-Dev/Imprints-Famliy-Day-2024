package com.runner.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.runner.R
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class UserListAdapter_K2MINI : RecyclerView.Adapter<UserListAdapter_K2MINI.ViewHolder> {
    var context: Context
    var name_list: List<String>
    lateinit var designation_list: List<String>
    var email_list: List<String>
    var temple_List: List<String>
    var phone_list: List<String>
    var id_list: List<String>
    var company_list: List<String>
    var code_list: List<String>
    var isPrintList: List<Int>
    var show_btnList: List<Int>
    var isAllotList: List<Int>
    var status_list: List<String>
    var clicklistener: Clicklistener
    var attending_on: List<String>
    var category: List<String>? = null
    var Registration_list: List<String>? = null


    var prefeMain: SharedPreferences? = null

    constructor(activity: Context,
        name_list: List<String>,
        email_list: List<String>,
        phone_list: List<String>,
        id_list: List<String>,
        company_list: List<String>,
        code_list: List<String>,
        isPrintList: List<Int>,
        status_list: List<String>,
        isAllotList: List<Int>,
        attending_on: List<String>,
        categoryList: List<String>?,
        designation_list: List<String>? ,
        temple_List: List<String>,
        Registration_list: List<String>,
        show_btnList: List<Int>) {
        context = activity
        clicklistener = activity as Clicklistener
        this.name_list = name_list
        this.email_list = email_list
        this.phone_list = phone_list
        this.id_list = id_list
        this.code_list = code_list
        this.company_list = company_list
        this.isPrintList = isPrintList
        this.isAllotList = isAllotList
        this.status_list = status_list
        this.attending_on = attending_on
        this.category = categoryList
        this.designation_list = designation_list!!
        this.temple_List = temple_List!!
        this.Registration_list = Registration_list!!
        this.show_btnList = show_btnList!!
        prefeMain = context.getSharedPreferences("FRIENDS", AppCompatActivity.MODE_PRIVATE)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_userlist_k2_list, parent, false)
        return ViewHolder(listItem)
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = name_list[position]
        holder.email.text = email_list[position]
        holder.mobile.text = phone_list[position]
        holder.qrcode.text = code_list[position]
        holder.category.text = category!![position]
        holder.company_name.text = company_list[position]
        holder.temple.text = temple_List[position]
        holder.attending_on.text = attending_on[position]
        holder.designation.text = designation_list[position]
       // holder.de.text = designation_list[position]

          if(isPrintList.get(position)==1){
              holder.print_status!!.text = "Aleady Printed"
              holder.print_status!!.visibility = View.VISIBLE
              holder.print_status!!.background =context!!.getDrawable(R.drawable.round_corner_10_stroke_dots_line)
              holder.print!!.isEnabled = true
              holder.print!!.text = "Reprint"
          }else {
              //holder.print_status!!.text = "You can print"
              holder.print_status!!.visibility = View.GONE
              holder.print_status!!.background =context!!.getDrawable(R.drawable.corner_green)
              holder.print!!.isEnabled = true
              holder.print!!.text = "Print"
          }



        if(show_btnList.get(position)==1){
            holder.llprint.visibility = View.VISIBLE
        }else{
            holder.llprint.visibility = View.GONE
        }




       if(prefeMain!!.getInt("IS_CASHLESS",0)==1) {
               holder.ll_cashless_reddem2.visibility = View.VISIBLE

           if(prefeMain!!.getString("event_id","").equals("32"))
           {
               holder.ll_cashless_reddem2.setText("VCARD Tag")
           }
           else{
               holder.ll_cashless_reddem2.setText("Redeem For Cashless")
           }
       }else{
           holder.ll_cashless_reddem2.visibility = View.GONE
       }

        if(!prefeMain!!.getString("editvalue","")!!.isEmpty()){
            holder.txtEdit.visibility = View.VISIBLE
        }else{
            holder.txtEdit.visibility = View.GONE
        }

        Log.e("@@camehereevent",prefeMain!!.getString("event_id","").toString())
        Log.e("@@camecategory",category!!.get(position).toString())

        if(prefeMain!!.getString("event_id","").equals("33")) {
            Log.e("@@Inifmain","InMain")
            if(category!!.get(position).contains("Visitor",ignoreCase = true)){
                Log.e("@@Inif","Inner")
                holder.upgrad.visibility = View.VISIBLE
            }else{
                Log.e("@@Inelse","Inelse")
                holder.upgrad.visibility = View.GONE
            }
        }else{
            Log.e("@@Inelseout","Inelseouteer")
            holder.upgrad.visibility = View.GONE
        }


        holder.llprint!!.setOnClickListener {
            Log.e("@@","InAdapter")


            val pattern = "ddMM"
            val dateInString = SimpleDateFormat(pattern).format(Date())

            Log.e("@@new date", dateInString)

            if (isPrintList[position] == 1) {
                val dialog = Dialog(context, R.style.my_dialog)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_pin)
                val ed_name = dialog.findViewById<EditText>(R.id.ed_name)
                val btn_yes = dialog.findViewById<Button>(R.id.btn_yes)
                val btn_no = dialog.findViewById<Button>(R.id.btn_no)
                btn_no.setOnClickListener { dialog.dismiss() }
                btn_yes.setOnClickListener {
                    if (ed_name.text.toString().equals(dateInString, ignoreCase = true)) {
                        dialog.findViewById<View>(R.id.warning_name).visibility =
                            View.GONE
                        clicklistener.onPrintClicked(position)
                        dialog.dismiss()
                    } else {
                        dialog.findViewById<View>(R.id.warning_name).visibility =
                            View.VISIBLE
                    }
                }
                dialog.setCancelable(true)
                dialog.show()
            } else {
                clicklistener.onPrintClicked(position)
                holder.print.isEnabled = false
                Handler().postDelayed({
                    holder.print.isEnabled = true
                    holder.print.text = "Re Print"
                    holder.print_status.text = "Already Printed"
                    holder.print_status.background =
                        context.resources.getDrawable(R.drawable.corner_red)
                }, 1500)
            }


        }

        holder.ll_cashless_reddem2!!.setOnClickListener {
            Log.e("@@","InAdapter")
            clicklistener!!.onCashlessRedeem(position)
            holder.ll_cashless_reddem2!!.isEnabled =false

            Handler().postDelayed({
                holder.ll_cashless_reddem2!!.isEnabled =true
            },2500)
        }

        holder.txtEdit.setOnClickListener({
            clicklistener.onEditValue(position)
        })

        holder.upgrad.setOnClickListener({
            clicklistener.onUpgradeValue(position)
        })


    }

    override fun getItemCount(): Int {
        return name_list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var email: TextView
        var mobile: TextView
        var qrcode: TextView
        var company_name: TextView
        var temple: TextView
        var txtEdit: TextView
        var upgrad: TextView

        var attending_on: TextView
        var category: TextView
        var date: TextView
        var designation: TextView
        var print: Button
        var llprint: LinearLayout
        var ll_cashless_reddem: LinearLayout
        var ll_cashless_reddem2: TextView
        var print_status: TextView
        var allot: Button
        var show_badge: Button

        lateinit var llemail: LinearLayout
        lateinit var llcode: LinearLayout
        lateinit var llcompany: LinearLayout
        lateinit var llDesignation: LinearLayout
        lateinit var llmobile: LinearLayout
        lateinit var llattending_on: LinearLayout
        lateinit var llcategory: LinearLayout
        lateinit var lltemple: LinearLayout
        lateinit var llUserType: LinearLayout


        init {
            name = itemView.findViewById<View>(R.id.name) as TextView
            ll_cashless_reddem2 = itemView.findViewById<View>(R.id.ll_cashless_reddem2) as TextView
            email = itemView.findViewById<View>(R.id.email) as TextView
            designation = itemView.findViewById<View>(R.id.designation) as TextView
            mobile = itemView.findViewById<View>(R.id.mobile) as TextView
            company_name = itemView.findViewById<View>(R.id.company_name) as TextView
            temple = itemView.findViewById<View>(R.id.temple) as TextView
            txtEdit = itemView.findViewById<View>(R.id.txtEdit) as TextView
            upgrad = itemView.findViewById<View>(R.id.upgrad) as TextView
            date = itemView.findViewById<View>(R.id.date) as TextView
            this.attending_on = itemView.findViewById<View>(R.id.attending_on) as TextView
            qrcode = itemView.findViewById<View>(R.id.qrcode) as TextView
            print = itemView.findViewById<View>(R.id.print) as Button
            llprint = itemView.findViewById<View>(R.id.ll_print) as LinearLayout
            ll_cashless_reddem = itemView.findViewById<View>(R.id.ll_cashless_reddem) as LinearLayout
            show_badge = itemView.findViewById<View>(R.id.show_badge) as Button
            this.category = itemView.findViewById<View>(R.id.category) as TextView
            print_status = itemView.findViewById<View>(R.id.print_status) as TextView
            allot = itemView.findViewById<View>(R.id.allot) as Button

            llemail = itemView.findViewById<View>(R.id.llemail) as LinearLayout
            llcode = itemView.findViewById<View>(R.id.llcode) as LinearLayout
            llcompany = itemView.findViewById<View>(R.id.llcompany) as LinearLayout
            llDesignation = itemView.findViewById<View>(R.id.llDesignation) as LinearLayout
            llmobile = itemView.findViewById<View>(R.id.llmobile) as LinearLayout
            llattending_on = itemView.findViewById<View>(R.id.llattending_on) as LinearLayout
            llcategory = itemView.findViewById<View>(R.id.llcategory) as LinearLayout
            lltemple = itemView.findViewById<View>(R.id.lltemple) as LinearLayout
            llUserType = itemView.findViewById<View>(R.id.llUserType) as LinearLayout

        }
    }

    interface Clicklistener {
       fun onPrintClicked(position: Int)
        fun onAlloteClicked(position: Int)
        fun onshowTicket(position: Int)
        fun onCashlessRedeem(position: Int)

        fun onEditValue(position: Int)
        fun onUpgradeValue(position: Int)

    }

}