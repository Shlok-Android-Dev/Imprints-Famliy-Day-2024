package com.runner.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.runner.R
import org.apache.commons.lang.WordUtils
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.ViewHolder> {
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
        val listItem = layoutInflater.inflate(R.layout.row_userlist, parent, false)
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
              holder.print_status!!.visibility = View.GONE
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



      /*  if(show_btnList.get(position)==1){
            holder.llprint.visibility = View.VISIBLE
        }else{
            holder.llprint.visibility = View.GONE
        }*/


        if(email_list.get(position).equals("")||email_list.get(position)==null||email_list.get(position).equals("null")){
            holder.llemail.visibility = View.GONE
        }else{
            holder.llemail.visibility = View.VISIBLE
        }

        if(designation_list.get(position).equals("")||designation_list.get(position)==null||designation_list.get(position).equals("null")){
            holder.llDesignation.visibility = View.GONE
        }else{
            holder.llDesignation.visibility = View.VISIBLE
        }

        if(phone_list.get(position).equals("")||phone_list.get(position)==null||phone_list.get(position).equals("null")){
            holder.llmobile.visibility = View.GONE
        }else{
            holder.llmobile.visibility = View.VISIBLE
        }

        if(category!!.get(position).equals("")||category!!.get(position)==null||category!!.get(position).equals("null")){
            holder.llcategory.visibility = View.GONE
        }else{
            holder.llcategory.visibility = View.VISIBLE
        }
        if(company_list!!.get(position).equals("")){
            holder.llcompany.visibility = View.GONE
            Log.e("@@camehere",company_list!!.get(position)+"@@")
        }else{
            holder.llcompany.visibility = View.VISIBLE
        }



        if(!Registration_list!!.get(position).isEmpty() && !Registration_list!!.get(position).equals("null")){
             holder.txt_guest_list.visibility = View.VISIBLE
            var guest_array = JSONArray(Registration_list!!.get(position))

            if(guest_array.length()>=4){
                holder.ll_add_member.visibility = View.GONE
            }else{
                holder.ll_add_member.visibility = View.VISIBLE
            }

            holder.llcontainer_guest.removeAllViews()
            for ( i in 0 until  guest_array.length()){

                val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val inflatedLayout = inflater.inflate(R.layout.layout, null)

                var checkbox : CheckBox = inflatedLayout.findViewById(R.id.checkbox)
                checkbox.text =WordUtils.capitalize(guest_array.getJSONObject(i).getString("name"))
                checkbox.isChecked = true
                checkbox.isEnabled = false

                holder.llcontainer_guest.addView(checkbox)
            }




        }else{
            holder.txt_guest_list.visibility = View.GONE
        }


        var memberArray = JSONArray(temple_List.get(position))

        holder.llcontainer.removeAllViews()
        var count =0

       if(memberArray.length()>0) {

           var list  : ArrayList<String> = ArrayList<String>()
           for ( i in 0 until  memberArray.length()){

               val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
               val inflatedLayout = inflater.inflate(R.layout.layout, null)


               var checkbox : CheckBox = inflatedLayout.findViewById(R.id.checkbox)

               if(memberArray.getJSONObject(i).has("is_printed")){

                   if(memberArray.getJSONObject(i).getInt("is_printed")==1){
                       checkbox.isChecked = true
                       checkbox.isEnabled = false
                       count = count+1
                   }else{
                       checkbox.isChecked = false
                       checkbox.isEnabled = true
                   }
               }
               checkbox.text = memberArray.getJSONObject(i).getString("relation").toUpperCase().replace("_","")+" : "+WordUtils.capitalize(memberArray.getJSONObject(i).getString("name"))

               checkbox.setOnClickListener {


                   if(checkbox.isChecked){
                       list.add(memberArray.getJSONObject(i).getString("relation"))
                   }else{
                       list.remove(memberArray.getJSONObject(i).getString("relation"))
                   }


                   clicklistener.onCheckboxClicked(list.toString())

               }







               holder.llcontainer.addView(inflatedLayout)

           }





           if(count == memberArray.length()){

               holder.print_status!!.text = "Aleady Allotted"
               holder.print_status!!.visibility = View.VISIBLE
               holder.print_status!!.background =context!!.getDrawable(R.drawable.round_corner_10_stroke_dots_line)
               holder.ll_allot!!.isEnabled = false
               holder.allot!!.text = "Allotted"
           }else {
               //holder.print_status!!.text = "You can print"
               holder.print_status!!.visibility = View.GONE
               holder.print_status!!.background =context!!.getDrawable(R.drawable.corner_green)
               holder.ll_allot!!.isEnabled = true
               holder.allot!!.text = "Allot"


           }



       }else{

           if(isPrintList.get(position)==1){

               holder.print_status!!.text = "Aleady Allotted"
               holder.print_status!!.visibility = View.VISIBLE
               holder.print_status!!.background =context!!.getDrawable(R.drawable.round_corner_10_stroke_dots_line)
               holder.ll_allot!!.isEnabled = false
               holder.allot!!.text = "Allotted"
           }else {
               //holder.print_status!!.text = "You can print"
               holder.print_status!!.visibility = View.GONE
               holder.print_status!!.background =context!!.getDrawable(R.drawable.corner_green)
               holder.ll_allot!!.isEnabled = true
               holder.allot!!.text = "Allot"


           }


       }


        if(temple_List!!.get(position).equals("")){
            holder.lltemple.visibility = View.GONE
            holder.temple.text ="NA"
            Log.e("@@camehere",temple_List!!.get(position)+"@@")
        }else{
            holder.lltemple.visibility = View.GONE
        }

        if(attending_on!!.get(position).equals("")||attending_on!!.get(position)==null||attending_on!!.get(position).equals("null")){
            holder.llattending_on.visibility = View.GONE
        }else{
            holder.llattending_on.visibility = View.VISIBLE
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
        holder.txtEdit.visibility = View.GONE

        Log.e("@@camehereevent",prefeMain!!.getString("event_id","").toString())
        Log.e("@@camecategory",category!!.get(position).toString())

      /*  if(prefeMain!!.getString("event_id","").equals("33")) {
            Log.e("@@Inifmain","InMain")
            if(category!!.get(position).contains("cxo",ignoreCase = true)){
                Log.e("@@Inif","Inner")
                holder.upgrad.visibility = View.VISIBLE
            }else{
                Log.e("@@Inelse","Inelse")
                holder.upgrad.visibility = View.GONE
            }
        }else{
            Log.e("@@Inelseout","Inelseouteer")
            holder.upgrad.visibility = View.GONE
        }*/


        if(category!!.get(position).contains("cxo",ignoreCase = true)){
            Log.e("@@Inif","Inner")
            holder.upgrad.visibility = View.GONE
        }else{
            Log.e("@@Inelse","Inelse")
            holder.upgrad.visibility = View.GONE
        }
       // holder.upgrad.visibility = View.VISIBLE






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

        holder.ll_allot.setOnClickListener {

            clicklistener.onAlloteClicked(position)


        }

        holder.ll_add_member.setOnClickListener {
            clicklistener.onUpgradeValue(position)
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


    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var txt_guest_list: TextView
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
        lateinit var ll_add_member: LinearLayout
        lateinit var ll_allot: LinearLayout
        lateinit var llcontainer: LinearLayout
        lateinit var llcontainer_guest: LinearLayout
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
            txt_guest_list = itemView.findViewById<View>(R.id.txt_guest_list) as TextView
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
            allot = itemView.findViewById<View>(R.id.allot_new) as Button

            llemail = itemView.findViewById<View>(R.id.llemail) as LinearLayout
            ll_add_member = itemView.findViewById<View>(R.id.ll_add_member) as LinearLayout
            ll_allot = itemView.findViewById<View>(R.id.ll_allot) as LinearLayout
            llcontainer = itemView.findViewById<View>(R.id.llcontainer) as LinearLayout
            llcontainer_guest = itemView.findViewById<View>(R.id.llcontainer_guest) as LinearLayout
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
        fun onCheckboxClicked(position: String)
        fun onshowTicket(position: Int)
        fun onCashlessRedeem(position: Int)

        fun onEditValue(position: Int)
        fun onUpgradeValue(position: Int)

    }

}