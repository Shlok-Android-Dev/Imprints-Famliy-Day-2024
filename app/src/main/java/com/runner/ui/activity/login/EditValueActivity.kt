package com.runner.ui.activity.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ruhe.utils.NetworkAlertUtility
import com.runner.R
import com.runner.View.EditValueView
import com.runner.databinding.ActivityEditValueBinding
import com.runner.extras.StickerPrinting
import com.runner.extras.appUtils
import com.runner.model.OnspotModel
import com.runner.presenter.OnEditValuePresenter
import com.runner.ui.activity.BaseActivity
import okhttp3.MediaType
import okhttp3.RequestBody

class EditValueActivity : BaseActivity(),EditValueView {

     var binding: ActivityEditValueBinding?= null
    var prefeMain: SharedPreferences? = null
     var presenter:OnEditValuePresenter? = null
    var mValuePhoneCOde:String?= "+91"
    var apiname:String? =""
    var received_id = ""
    var name = ""
    var email = ""
    var phone = ""
    var company = ""
    var designation = ""
    var linkedIn = ""
    var code_recv = ""
    var website = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@EditValueActivity,R.layout.activity_edit_value)

        received_id = intent.getStringExtra("id").toString()
        code_recv = intent.getStringExtra("uniquecode").toString()
        name = intent.getStringExtra("name").toString()
        email = intent.getStringExtra("email").toString()
        phone = intent.getStringExtra("phone").toString()
        company = intent.getStringExtra("company").toString()
        designation = intent.getStringExtra("designation").toString()
        linkedIn = intent.getStringExtra("linkedIn").toString()
        website = intent.getStringExtra("website").toString()

        Log.e("@@received_id",received_id)
        Log.e("@@name",name)
        Log.e("@@email",email)
        Log.e("@@phone",phone)
        Log.e("@@company",company)
        Log.e("@@designation",designation)


        binding!!.edName.setText(name)
        binding!!.edEmail.setText(email)
        binding!!.edPhone.setText(phone)
        binding!!.edCompanyname.setText(company)
        binding!!.edDesignation.setText(designation)
        binding!!.edWebsite.setText(website)

        if(linkedIn.equals("")||linkedIn.equals("null")){
            binding!!.edLinkedIn.setText("https://www.linkedin.com/")
        }else{
            binding!!.edLinkedIn.setText(linkedIn)
        }



        presenter = OnEditValuePresenter()
        presenter!!.setView(this@EditValueActivity)
        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)

        if(!prefeMain!!.getString("editvalue","")!!.isEmpty()){
            apiname = prefeMain!!.getString("editvalue","")
            Log.e("@@EditAPi",apiname.toString())
        }

        binding!!.ccp!!.setOnCountryChangeListener { selectedCountry ->
            mValuePhoneCOde = "+" + selectedCountry.phoneCode
            Log.e("@@CCP", "+" + mValuePhoneCOde)
        }

        binding!!.btnOnsiteSubmit.setOnClickListener {

            if (NetworkAlertUtility.isConnectingToInternet(this)) {
                val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
                var header: HashMap<String, String> = appUtils.Takeheader(this, "")
                map["name"] = RequestBody.create(
                    MediaType.parse("text/plain"),
                    binding!!.edName.text.toString().trim()
                )
                map["company"] = RequestBody.create(
                    MediaType.parse("text/plain"),
                    binding!!.edCompanyname.text.toString().trim()
                )
                map["unique_code"] =
                    RequestBody.create(MediaType.parse("text/plain"), code_recv.trim())
                /* map["email"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edEmail.text.toString().trim())
                 map["phone"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edPhone.text.toString().trim())

                 map["designation"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edDesignation.text.toString().trim())
                 map["website"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edWebsite.text.toString().trim())
                 map["country_code"] = RequestBody.create(MediaType.parse("text/plain"), mValuePhoneCOde)*/


                if (binding!!.edLinkedIn.text.toString()
                        .equals("https://www.linkedin.com/", ignoreCase = true)
                ) {
                    map["linkedin_profile"] = RequestBody.create(MediaType.parse("text/plain"), "")
                } else {
                    map["linkedin_profile"] = RequestBody.create(
                        MediaType.parse("text/plain"),
                        binding!!.edLinkedIn.text.toString()
                    )
                }


                presenter!!.onEditValue(this, map, header, true, "edituser")

            } else {
                val sweetAlertDialog = SweetAlertDialog(this@EditValueActivity)
                sweetAlertDialog.setTitleText("Alert!!")
                    .setContentText("please check Internet Connection...")
                    .show()
            }

        }

        binding!!.ivBack.setOnClickListener({
            finish()
        })

    }


    override fun OnEditUser(model: OnspotModel?, errCode: Int) {

        if(model!!.status==true){
            val sweetAlertDialogPrint = SweetAlertDialog(context)
            sweetAlertDialogPrint.setCancelable(false)
            sweetAlertDialogPrint.setTitleText("Success")
                .setContentText(model.message)
                .show()
            sweetAlertDialogPrint.setConfirmClickListener {
                sweetAlertDialogPrint.cancel()
                finish()
            }
        }else{
            val sweetAlertDialog = SweetAlertDialog(this@EditValueActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText(model.message)
                .show()
        }
    }

    override val context: Context?
        get() = this


    override fun onError(reason: String?) {

    }

    override fun selectFileButtonOnClick() {
        TODO("Not yet implemented")
    }

    override fun printButtonOnClick() {
        TODO("Not yet implemented")
    }



}