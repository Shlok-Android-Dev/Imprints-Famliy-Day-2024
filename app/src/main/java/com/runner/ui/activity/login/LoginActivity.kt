package com.runner.ui.activity.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.ruhe.utils.NetworkAlertUtility
import com.runner.R
import com.runner.RuheApp
import com.runner.databinding.ActivityLoginBinding
import com.runner.extras.LogUtils
import com.runner.extras.appUtils
import com.runner.model.LoginModel
import com.runner.model.VerifyPinModel
import com.runner.presenter.LoginPresenter
import com.runner.ui.activity.BaseActivity
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject


class LoginActivity : BaseActivity(), com.runner.View.LoginView,OnClickListener {

    var binding: ActivityLoginBinding? = null
    var presenter: LoginPresenter? = null
    var User_id =""
    var Role_id =""
    var Event_id =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.primary_color)
        }
        presenter = LoginPresenter()
        presenter!!.setView(this)

        binding!!.infoLayoyt.visibility = VISIBLE
        binding!!.llpin.visibility = GONE

        if(intent.getStringExtra("from").equals("")){
            binding!!.llpin!!.visibility = GONE
            Role_id = ""
            User_id =""
        }else{
            binding!!.llpin!!.visibility = VISIBLE
            Role_id = intent.getStringExtra("role_id").toString()
            User_id = intent.getStringExtra("user_id").toString()
        }

        appUtils.setupUI(binding!!.rootLayout, this)
        binding!!.btnLogin!!.setOnClickListener(this)
        binding!!.btnPin!!.setOnClickListener(this)
        binding!!.llBack!!.setOnClickListener(this)

        binding!!.edtEmailMobile.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length>0){
                    binding!!.view.setBackgroundColor(resources.getColor(R.color.primary_color))
                }else{
                    binding!!.view.setBackgroundColor(resources.getColor(R.color.color_grey))
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    override fun selectFileButtonOnClick() {
        TODO("Not yet implemented")
    }

    override fun printButtonOnClick() {
        TODO("Not yet implemented")
    }
    private fun ApiLogin() {
        RuheApp.instance!!.createApiService()
        if (NetworkAlertUtility.isConnectingToInternet(this)) {
            val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
            var header: HashMap<String, String> = appUtils.Takeheader(this, "")
            map["phone"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edtEmailMobile.text.toString().trim())
            presenter!!.LoginUser(this, map, header, true, binding!!.edtEmailMobile.text.toString().trim())
        } else {
            val sweetAlertDialog = SweetAlertDialog(this@LoginActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
        }
    }

    private fun VerifyPin() {
        val prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        val editor = prefeMain.edit()
        editor.putString("islogged_in", "")
        editor.commit()
        RuheApp.instance!!.createApiService()
        if (NetworkAlertUtility.isConnectingToInternet(this)) {
            val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
            var header: HashMap<String, String> = appUtils.Takeheader(this, "")
            map["user_id"] = RequestBody.create(MediaType.parse("text/plain"), User_id)
            map["role_id"] = RequestBody.create(MediaType.parse("text/plain"), Role_id)
            map["pin"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.squareField.text.toString().trim())
            presenter!!.verifyPin(this, map, header, true, binding!!.edtEmailMobile.text.toString().trim())
        } else {
            val sweetAlertDialog = SweetAlertDialog(this@LoginActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
        }
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onLoginComplete(model: LoginModel?, errCode: Int) {
        try {
            if (errCode == 500 || errCode == 401) {
            } else if (model!!.getStatus()!!) {
                val prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
                val editor = prefeMain.edit()
                var role_id: Int = model.getData()!!.getRoleId()!!
                Role_id = role_id.toString()
                User_id = model.getData()!!.getId()!!.toString()
                Event_id = model.getData()!!.getEventId()!!.toString()

                editor!!.commit()
                binding!!.infoLayoyt.visibility = View.GONE
                binding!!.llpin.visibility = View.VISIBLE
            } else {
                SweetAlertDialog(this@LoginActivity)
                    .setTitleText("Alert!")
                    .setContentText(model!!.getMessage())
                    .show()
            }

        }catch (ex:Exception){
            LogUtils.debug("@@Exception",ex.message.toString())
        }
    }

    override fun OnVerifryPin(model: VerifyPinModel?, errCode: Int) {
        try {
            if (model!!.getStatus() == true) {
                val prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
                val editor = prefeMain.edit()
                editor.putString("PREFIX", model!!.getEventData()!!.prefixUniquecode)
                editor.putString("BASE_URL", model!!.getEventData()!!.baseUrl)
                editor.putInt("IS_CASHLESS", model!!.getEventData()!!.is_cashless!!)
                editor.putString("AppName", model!!.getEventData()!!.name)
                editor.putString("LOGO", model!!.getEventData()!!.logo)
                editor.putString("USER_ID", User_id)
                editor.putString("ROLE_ID",Role_id)
                editor.putString("islogged_in", "true")
                editor.putString("event_id", Event_id)
                LogUtils.debug("@@@Userid",User_id)

                editor.commit()
                RuheApp.instance!!.createApiService()

                if (Role_id.equals("1")) {
                    var gson: Gson = Gson()
                    var jsonobj : JSONObject = JSONObject( gson.toJson(model!!.settingsData!!))
                    editor.putString("Location", jsonobj. getString("locations"))
                    if(jsonobj.has("zapping_data_show")){
                        editor.putString("zapping_data_show", jsonobj. getString("zapping_data_show"))
                    }
                    if(jsonobj.has("zapping_user_search")){
                        editor.putString("zapping_user_search", jsonobj. getString("zapping_user_search"))
                    }

                    if(jsonobj.has("zapping_user_update")){
                        editor.putString("zapping_user_update", jsonobj. getString("zapping_user_update"))
                    }

                    if(jsonobj.has("zapping_info_row1")){
                        editor.putString("zapping_info_row1", jsonobj. getString("zapping_info_row1"))
                    }

                    if(jsonobj.has("zapping_info_row2")){
                        editor.putString("zapping_info_row2", jsonobj. getString("zapping_info_row2"))
                    }

                    if(jsonobj.has("zapping_info_row3")){
                        editor.putString("zapping_info_row3", jsonobj. getString("zapping_info_row3"))
                    }

                    if(jsonobj.has("zapping_info_row4")){
                        editor.putString("zapping_info_row4", jsonobj. getString("zapping_info_row4"))
                    }

                    if(jsonobj.has("zapping_info_row5")){
                        editor.putString("zapping_info_row5", jsonobj. getString("zapping_info_row5"))
                    }

                }
                if (Role_id.equals("2")) {
                    val gson = Gson()
                    val listString = gson.toJson(model!!.formSettings!!)
                    val jsonArray = JSONArray(listString)
                    editor.putString("FORM",jsonArray.toString())

                    //  uncomment this if priter Added
                    var jsonobj : JSONObject = JSONObject( gson.toJson(model!!.settingsData!!))

                    editor.putString("PRINTER_TYPE", jsonobj. getString("onground_registration_printer_type"))
                    editor.putString("PRINT_STATUS", jsonobj. getString("onground_registration_print"))

                    if(jsonobj.isNull("onground_registration_print_row1")){
                        editor.putString("ROW1", "")
                    }else{
                        editor.putString("ROW1", jsonobj. getString("onground_registration_print_row1"))
                    }

                    if(jsonobj.isNull("onground_registration_print_row2")){
                        editor.putString("ROW2", "")
                    }else{
                        editor.putString("ROW2", jsonobj. getString("onground_registration_print_row2"))
                    }

                    if(jsonobj.isNull("onground_registration_print_row3")){
                        editor.putString("ROW3", "")
                    }else{
                        editor.putString("ROW3", jsonobj. getString("onground_registration_print_row3"))
                    }

                    if(jsonobj.isNull("onground_registration_print_row4")){
                        editor.putString("ROW4", "")
                    }else{
                        editor.putString("ROW4", jsonobj. getString("onground_registration_print_row4"))
                    }

                    if(jsonobj.isNull("onground_registration_print_row5")){
                        editor.putString("ROW5", "")
                    }else{
                        editor.putString("ROW5", jsonobj. getString("onground_registration_print_row5"))
                    }

                    if(!jsonobj.isNull("onground_registration_default_contact_country")){
                        editor.putString("default_country_code", jsonobj. getString("onground_registration_default_contact_country"))
                    }

                    editor.putString("ONSPOT_API", jsonobj. getString("onground_registration_api"))
                }

                if (Role_id.equals("3")) {
                    var gson: Gson = Gson()
                    var jsonobj : JSONObject = JSONObject( gson.toJson(model!!.settingsData!!))
                    editor.putString("searchfield", jsonobj. getString("search_fields"))

                    editor.putString("PRINTER_TYPE", jsonobj. getString("printer_type"))

                    if(jsonobj.isNull("print_row1")){
                        editor.putString("ROW1", "")
                    }else{
                        if(jsonobj.getString("print_row1")==null){
                            editor.putString("ROW1", "")
                        }else{
                            editor.putString("ROW1", jsonobj.getString("print_row1"))
                        }
                    }


                    if(jsonobj.isNull("print_row2")){
                        editor.putString("ROW2", "")
                    }else{
                        if(jsonobj.getString("print_row2")==null){
                            editor.putString("ROW2", "")
                        }else{
                            editor.putString("ROW2", jsonobj.getString("print_row2"))
                        }
                    }


                    if(jsonobj.isNull("print_row3")){
                        editor.putString("ROW3", "")
                    }else{
                        if(jsonobj.getString("print_row3")==null){
                            editor.putString("ROW3", "")
                        }else{
                            editor.putString("ROW3", jsonobj.getString("print_row3"))
                        }
                    }


                    if(jsonobj.isNull("print_row4")){
                        editor.putString("ROW4", "")
                    }else{
                        if(jsonobj.getString("print_row4")==null){
                            editor.putString("ROW4", "")
                        }else{
                            editor.putString("ROW4", jsonobj.getString("print_row4"))
                        }
                    }

                    if(jsonobj.isNull("print_row5")){
                        editor.putString("ROW5", "")
                    }else{
                        if(jsonobj.getString("print_row5")==null){
                            editor.putString("ROW5", "")
                        }else{
                            editor.putString("ROW5", jsonobj.getString("print_row5"))
                        }
                    }

                    editor.putString("Search_api", jsonobj.getString("redemption_user_search"))
                    editor.putString("Update_api", jsonobj.getString("redemption_user_update"))

                    if(jsonobj.has("redemption_user_edit")){
                        editor.putString("editvalue", jsonobj.getString("redemption_user_edit"))
                    }

                    if(jsonobj.isNull("redemption_print")){
                        editor.putString("PRINT_STATUS", "")
                    }else{
                        if(jsonobj.getString("redemption_print")==null){
                            editor.putString("PRINT_STATUS", "")
                        }else{
                            editor.putString("PRINT_STATUS", jsonobj.getString("redemption_print"))
                        }
                    }
                }


                if (Role_id.equals("4")) {
                    var gson: Gson = Gson()
                    var jsonobj : JSONObject = JSONObject( gson.toJson(model!!.settingsData!!))
                    editor.putString("searchfield", jsonobj. getString("search_fields"))
                    val listString = gson.toJson(model!!.formSettings!!)
                    val jsonArray = JSONArray(listString)
                    editor.putString("FORM",jsonArray.toString())


                    if(jsonobj.has("locations")){
                        editor.putString("Location", jsonobj. getString("locations"))
                    }

                    if(jsonobj.has("s3_access_key")){
                        editor.putString("S3_ACCESS_KEY", jsonobj. getString("s3_access_key"))
                    }
                    if(jsonobj.has("s3_secret_key")){
                        editor.putString("S3_SECRATE_KEY", jsonobj. getString("s3_secret_key"))
                    }

                    if(jsonobj.has("s3_default_region")){
                        editor.putString("S3_REGION", jsonobj. getString("s3_default_region"))
                    }

                    if(jsonobj.has("s3_bucket")){
                        editor.putString("S3_BUCKET", jsonobj. getString("s3_bucket"))
                    }

                    editor.putString("PRINTER_TYPE", jsonobj. getString("printer_type"))
                    editor.putString("PRINT_STATUS", jsonobj. getString("onground_registration_print"))
                    editor.putString("REDEMPTION_PRINT_STATUS", jsonobj. getString("redemption_print"))

                    if(jsonobj.has("onground_registration_api")){
                        editor.putString("ONSPOT_API", jsonobj. getString("onground_registration_api"))
                    }

                    if(jsonobj.has("redemption_user_search")){
                        editor.putString("Search_api", jsonobj.getString("redemption_user_search"))
                    }

                    if(jsonobj.has("redemption_user_update")){
                        editor.putString("Update_api", jsonobj.getString("redemption_user_update"))
                    }



                    if(jsonobj.has("redemption_user_edit")){
                        editor.putString("editvalue", jsonobj.getString("redemption_user_edit"))
                    }else{
                        editor.putString("editvalue", "")
                    }


                    if(!jsonobj.isNull("onground_registration_default_contact_country")){
                        editor.putString("default_country_code", jsonobj. getString("onground_registration_default_contact_country"))
                    }

                    if(jsonobj.has("zapping_user_search")){
                        editor.putString("zapping_user_search", jsonobj. getString("zapping_user_search"))
                    }

                    if(jsonobj.has("zapping_user_update")){
                        editor.putString("zapping_user_update", jsonobj. getString("zapping_user_update"))
                    }


                    if(jsonobj.isNull("print_row1")){
                        editor.putString("ROW1", "")
                    }else{
                        if(jsonobj.getString("print_row1")==null){
                            editor.putString("ROW1", "")
                        }else{
                            editor.putString("ROW1", jsonobj.getString("print_row1"))
                        }
                    }


                    if(jsonobj.isNull("print_row2")){
                        editor.putString("ROW2", "")
                    }else{
                        if(jsonobj.getString("print_row2")==null){
                            editor.putString("ROW2", "")
                        }else{
                            editor.putString("ROW2", jsonobj.getString("print_row2"))
                        }
                    }

                    if(jsonobj.isNull("print_row3")){
                        editor.putString("ROW3", "")
                    }else{
                        if(jsonobj.getString("print_row3")==null){
                            editor.putString("ROW3", "")
                        }else{
                            editor.putString("ROW3", jsonobj.getString("print_row3"))
                        }
                    }


                    if(jsonobj.isNull("print_row4")){
                        editor.putString("ROW4", "")
                    }else{
                        if(jsonobj.getString("print_row4")==null){
                            editor.putString("ROW4", "")
                        }else{
                            editor.putString("ROW4", jsonobj.getString("print_row4")   )
                        }
                    }

                    if(jsonobj.isNull("print_row5")){
                        editor.putString("ROW5", "")
                    }else{
                        if(jsonobj.getString("print_row5")==null){
                            editor.putString("ROW5", "")
                        }else{
                            editor.putString("ROW5", jsonobj.getString("print_row5"))
                        }
                    }


                    if(jsonobj.has("zapping_data_show")){
                        editor.putString("zapping_data_show", jsonobj. getString("zapping_data_show"))
                    }
                    if(jsonobj.has("zapping_user_search")){
                        editor.putString("zapping_user_search", jsonobj. getString("zapping_user_search"))
                    }

                    if(jsonobj.has("zapping_user_update")){
                        editor.putString("zapping_user_update", jsonobj. getString("zapping_user_update"))
                    }

                    if(jsonobj.has("zapping_info_row1")){
                        editor.putString("zapping_info_row1", jsonobj. getString("zapping_info_row1"))
                    }

                    if(jsonobj.has("zapping_info_row2")){
                        editor.putString("zapping_info_row2", jsonobj. getString("zapping_info_row2"))
                    }

                    if(jsonobj.has("zapping_info_row3")){
                        editor.putString("zapping_info_row3", jsonobj. getString("zapping_info_row3"))
                    }

                    if(jsonobj.has("zapping_info_row4")){
                        editor.putString("zapping_info_row4", jsonobj. getString("zapping_info_row4"))
                    }

                    if(jsonobj.has("zapping_info_row5")){
                        editor.putString("zapping_info_row5", jsonobj. getString("zapping_info_row5"))
                    }
                }
                editor.putInt("SWITCH_VALUE", 1)

                editor.commit()

                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                /*if (Role_id.equals("1")) {
                    val intent = Intent(this@LoginActivity, ChooseZappingLocationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                }

                if (Role_id.equals("2")) {

                    val intent = Intent(this@LoginActivity, DiyOnspotActicvity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                }

                if (Role_id.equals("3")) {
                    val intent = Intent(this, EntryActivity::class.java)
                    intent.putExtra("Camera", -1)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }

                if (Role_id.equals("4")) {

                }*/


            } else {
                SweetAlertDialog(this@LoginActivity)
                    .setTitleText("Alert!")
                    .setContentText(model!!.getMessage())
                    .show()
            }
        }catch (ex : Exception){
            LogUtils.debug("@@Error",ex.message.toString())
        }
    }

    override val context: Context?
        get() = this

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_login -> {

                if(binding!!.edtEmailMobile.text.toString().isEmpty()){
                    binding!!.mobileEmailWarning.visibility = VISIBLE
                    return
                }
                binding!!.mobileEmailWarning.visibility = GONE
                ApiLogin()
            }

            R.id.btn_pin -> {

                if(binding!!.squareField.text.toString().isEmpty()){
                    binding!!.pinWarning.visibility = VISIBLE
                    binding!!.pinWarning.text =getString(R.string.warning_pin_emplty)
                    return
                }
                binding!!.pinWarning.visibility = GONE


                if(binding!!.squareField.text.toString().length<4){
                    binding!!.pinWarning.visibility = VISIBLE
                    binding!!.pinWarning.text =getString(R.string.warning_pin_length)

                    return
                }
                binding!!.pinWarning.visibility = GONE

                VerifyPin()
            }
            R.id.llBack -> {
                binding!!.infoLayoyt.visibility = VISIBLE
                binding!!.edtEmailMobile.setText("")
                binding!!.squareField.setText("")
                binding!!.llpin.visibility = GONE
            }
        }
    }
}