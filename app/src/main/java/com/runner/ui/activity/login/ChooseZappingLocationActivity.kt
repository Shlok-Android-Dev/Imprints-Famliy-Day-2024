package com.runner.ui.activity.login

import android.R.string
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.runner.R
import com.runner.adapter.CategoryAdapter11
import com.runner.databinding.ActivityChooseCategoryBinding
import com.runner.extras.LogUtils
import com.runner.model.Constant
import com.runner.ui.activity.BaseActivity
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList


class ChooseZappingLocationActivity : BaseActivity() {
    var prefeMain: SharedPreferences? = null
    var binding : ActivityChooseCategoryBinding? = null
    var role_id: String? = ""
    var category_list:ArrayList<String>? = null
    var category_Id_list: ArrayList<String>? = null
    var jsonArray :JSONArray?= null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_category)
        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        role_id = prefeMain!!.getString("ROLE_ID", "")
        category_list = ArrayList()
        category_Id_list = ArrayList()

        jsonArray = JSONArray()

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.primary_color)
        }

        /*binding!!.logo.setOnLongClickListener {
             val editor = prefeMain!!.edit()
             editor.clear()
             editor.commit()
             val intent = Intent(this@ChooseZappingLocationActivity, LoginActivity::class.java)
            intent.putExtra("from","")
            intent.putExtra("role_id","")
            intent.putExtra("user_id","")
             startActivity(intent)
             finish()
             false
         }*/
        try {
            var location :String   = prefeMain!!.getString("Zapping_Location", "").toString()



            if(location.isEmpty()){

                val dialog = Dialog(this, R.style.my_dialog)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_no_loc)
                val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
                val height = (resources.displayMetrics.heightPixels * 0.45).toInt()
                dialog.window!!.setLayout(width, height)
                dialog.setCancelable(false)
                val tv_msg = dialog.findViewById<TextView>(R.id.msg)
                val btn_ok = dialog.findViewById<Button>(R.id.btn_yes)
                val btn_no = dialog.findViewById<Button>(R.id.btn_no)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                btn_ok.setOnClickListener {
                    dialog.dismiss()
                    finish()
                }
                btn_no.setOnClickListener { dialog.dismiss()
                    finish()
                }
                dialog.show()




                return
            }



            var zapping_Location = location.replace("[","").replace("]","")

            if(zapping_Location.contains(",")){
                var JSONArray = JSONArray(location)

                for(i in 0 until  JSONArray.length()){
                    category_list!!.add(JSONArray.getString(i))
                }


            }else {
                //Log.e("@@Location",zapping_Location)
                category_list!!.add(zapping_Location.toString().replace("\"", ""))
                Log.e("@@Location",zapping_Location)
            }

            val adapter = CategoryAdapter11(applicationContext, R.layout.child_activity, category_list!!)
            binding!!.listCategory!!.setAdapter(adapter)

            binding!!.listCategory!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                var  BrandName = EntryActivity_Zapping_Newmini.BrandName(this)
                if (BrandName.contains(Constant.SUNMI_K2MINI)) {
                    val intent = Intent(this@ChooseZappingLocationActivity, EntryActivity_Zapping_Newmini::class.java)
                    intent.putExtra("Camera", -1)
                    val editor = prefeMain!!.edit()
                    editor.putString("location", category_list!![position].trim())
                    editor.commit()
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                else
                {
                    val intent = Intent(this@ChooseZappingLocationActivity, EntryActivity_Zapping_New::class.java)
                    intent.putExtra("Camera", -1)
                    val editor = prefeMain!!.edit()
                    editor.putString("location", category_list!![position].trim())
                    editor.commit()
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }


            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun selectFileButtonOnClick() {

    }

    override fun printButtonOnClick() {

    }
}