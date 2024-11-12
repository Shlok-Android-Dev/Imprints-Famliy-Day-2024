package com.runner.ui.activity.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.ruhe.utils.NetworkAlertUtility
import com.runner.R
import com.runner.View.ChooseFormView
import com.runner.databinding.ActivityChooseCategory2Binding
import com.runner.extras.LogUtils
import com.runner.extras.appUtils
import com.runner.model.ChooseCategoryModel
import com.runner.presenter.ChooseFormPresenter
import com.runner.ui.activity.BaseActivity
import okhttp3.RequestBody
import org.json.JSONArray
import java.util.HashMap

class ChooseCategoryActivity :BaseActivity(),ChooseFormView {

    lateinit var binding:ActivityChooseCategory2Binding

    lateinit var prefeMain:SharedPreferences
    var FormArray: JSONArray? = JSONArray()
    var valueList: ArrayList<String>? = ArrayList()
    var typeList: ArrayList<String>? = ArrayList()
    var id_list: ArrayList<String>? = ArrayList()
    var listSpinner1: ArrayList<String> = ArrayList<String>()

    var type = ""
    var selected_id= ""
    var presenter:ChooseFormPresenter?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_choose_category2)

        presenter = ChooseFormPresenter()
        presenter!!.setView(this)

        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        if (!prefeMain!!.getString("FORM", "")!!.isEmpty()) {
            FormArray = JSONArray(prefeMain!!.getString("FORM", ""))
        }
        valueList!!.add("--select--")
        id_list!!.add("0")

        LogUtils.debug("@@JsonCAtegoryvalue",FormArray.toString())
        for (i in 0 until FormArray!!.length()) {
            valueList!!.add(FormArray!!.getJSONObject(i).getString("category"))
            id_list!!.add(FormArray!!.getJSONObject(i).getString("id"))
        }
        LogUtils.debug("@@deWE",valueList.toString())

        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            valueList as List<Any?>
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerType!!.setAdapter(adapter)


        binding.spinnerType.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (valueList!!.size > 0) {
                    type =valueList!!.get(position)
                    selected_id = id_list!!.get(position)

                    LogUtils.debug("@@de",type)
                    LogUtils.debug("@@de",selected_id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        binding.btnSubmit.setOnClickListener(View.OnClickListener {
            if (type.equals("--select--", ignoreCase = true)) {
               binding.warningCategory.setVisibility(View.VISIBLE)
                return@OnClickListener
            }
            binding.warningCategory.setVisibility(View.GONE)
            CallNextApi("2",selected_id)
        })

    }

    override fun selectFileButtonOnClick() {
        TODO("Not yet implemented")
    }

    override fun printButtonOnClick() {
        TODO("Not yet implemented")
    }


    private fun CallNextApi(id: String,type: Any) {

        if (NetworkAlertUtility.isConnectingToInternet(this)) {
            val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()

            var header: HashMap<String, String> = appUtils.Takeheader(this, "")

            var api_name : String? = "get_form_details"

            presenter!!.ChooseFormFields(this, map, header, true, id,type.toString(),api_name!!)
        } else {
            val sweetAlertDialog = SweetAlertDialog(this@ChooseCategoryActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()

        }
    }

    override fun OnFormSuccess(model: ChooseCategoryModel?, errCode: Int) {
        try {
            val editor = prefeMain.edit()
            if (model!!.status == true) {
                var gson: Gson = Gson()
                val listString = gson.toJson(model!!.form_fields!!)
                val jsonArray = JSONArray(listString)
                editor.putString("FORM1",jsonArray.toString())
                editor.putString("CategoryChooseForm",type)
                editor.commit()
               Handler().postDelayed({
                   val intent = Intent(this@ChooseCategoryActivity, DiyOnspotActicvity::class.java)
                   startActivity(intent)
                   finish()
               },200)
            }else{
                SweetAlertDialog(this@ChooseCategoryActivity)
                    .setTitleText("Alert!")
                    .setContentText(model.message)
                    .show()
            }
        }catch (e:Exception){
            LogUtils.debug("@@Errorrr",e.message.toString())
        }
    }

    override val context: Context?
        get() = TODO("Not yet implemented")

}