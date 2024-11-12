package com.runner.ui.activity.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.ruhe.utils.NetworkAlertUtility
import com.runner.R
import com.runner.View.LoginViewNew
import com.runner.databinding.ActivityLoginNewBinding
import com.runner.extras.LogUtils
import com.runner.extras.appUtils
import com.runner.model.LoginModelNew
import com.runner.presenter.LoginePresenterNew
import com.runner.ui.activity.BaseActivity
import okhttp3.RequestBody
import org.json.JSONArray
import java.util.HashMap

class LoginActivityNew :BaseActivity(), View.OnClickListener,LoginViewNew {

    lateinit var binding:ActivityLoginNewBinding

    lateinit var presenter:LoginePresenterNew

    lateinit var prefeMain:SharedPreferences

    var role_id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login_new)

        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)



        presenter = LoginePresenterNew()
        presenter.setView(this)

        binding.btnlogin.setOnClickListener(this)
    }

    override fun selectFileButtonOnClick() {

    }

    override fun printButtonOnClick() {

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btnlogin ->{
                Login()
            }

        }
    }

    private fun Login() {
        if(binding.edtemailmobile.text.isEmpty()){
            binding!!.mobileEmailWarning.visibility = View.VISIBLE
            return
           // binding!!.mobileEmailWarning.visibility = View.GONE
        }else if (binding.edtemailmobile.text.length!=10){
            binding!!.mobileEmailWarning.visibility = View.VISIBLE
            binding!!.mobileEmailWarning.setText("Please fill 10 Digit mobile number")
            return
           // binding!!.mobileEmailWarning.visibility = View.GONE
        }else{

            if (NetworkAlertUtility.isConnectingToInternet(this)) {
                val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()

                var header: HashMap<String, String> = appUtils.Takeheader(this, "")


                presenter!!.LoginNew(this,map,header,true,binding.edtemailmobile.text.toString())

            } else {
                val sweetAlertDialog = SweetAlertDialog(this@LoginActivityNew)
                sweetAlertDialog.setTitleText("Alert!!")
                    .setContentText("please check Internet Connection...")
                    .show()

            }
        }
    }

    override fun onLoginComplete(model: LoginModelNew?, errCode: Int) {
        try {
            if (model!!.status == true) {
                role_id = model.data.use_data.role_id.toString()
                var gson = Gson()




                val editor = prefeMain.edit()
                editor.putString("ROLE_ID",role_id)
                editor.putString("PRINTER_TYPE","SUNMI")
                editor.putInt("SWITCH_VALUE",1)
                editor.putString("Font_TYPE","Roboto Font")
                editor.putString("PRINT_STATUS","1")

                editor.putString("islogged_in", "true")

                LogUtils.debug("@@role",role_id)
               // Toast.makeText(this@LoginActivityNew,model.message.toString(),Toast.LENGTH_SHORT).show()
                if(role_id.equals("3") || role_id.equals("2")){
                    var list = gson.toJson(model.data!!
                        .categories)
                    var jsonArray = JSONArray(list)
                    LogUtils.debug("@@@JsonARRA",jsonArray.toString())
                    editor.putString("FORM",jsonArray.toString())
                }else{

                    var gson  = Gson()
                    var list1 =  gson.toJson(model.data.zapping_locations)
                    LogUtils.debug("@@@list1",list1)
                    editor.putString("Zapping_Location",list1)
                }
                editor.commit()
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else{
                SweetAlertDialog(this@LoginActivityNew)
                    .setTitleText("Alert!")
                    .setContentText(model.message)
                    .show()
            }
        }catch (e:Exception){
            LogUtils.debug("@@Errorrr",e.message.toString())
        }
    }

    override val context: Context?
        get() = this


}