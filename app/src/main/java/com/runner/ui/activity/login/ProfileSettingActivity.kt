package com.runner.ui.activity.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.runner.R
import com.runner.bluetooth.SelectDeviceActivity
import com.runner.databinding.ActivityProfileSettingBinding
import com.runner.extras.appUtils
import com.runner.model.Constant
import com.runner.printdemo.Activity_Settings
import com.runner.printdemo.BaseActivity
import com.runner.tvs.Activity.PrintfBlueListActivity
import kotlinx.android.synthetic.main.choosefont_layout.*
import kotlinx.android.synthetic.main.chooseprinter_layout.*
import kotlinx.android.synthetic.main.chooseprinter_layout.btnSave
import kotlinx.android.synthetic.main.chooseprinter_layout.consRoot
import kotlinx.android.synthetic.main.chooseprinter_layout.consRootMain
import kotlinx.android.synthetic.main.chooseprinter_layout.cvCross
import kotlinx.android.synthetic.main.chooseprinter_layout.forK2MiniFirebase
import kotlinx.android.synthetic.main.logout_dialog_layout.*
import kotlinx.android.synthetic.main.save_kmini_dialog.*

class ProfileSettingActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityProfileSettingBinding
    var prefeMain: SharedPreferences? = null
    var selectprinter:String = ""
    var selectfont:String = ""
    var switchvalue = 0
    var slide:Slide?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_setting)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.primary_color)
        }


        slide = Slide()

            Handler().postDelayed({
                slide!!.slideEdge = Gravity.END
                TransitionManager.beginDelayedTransition(binding.consSelectContactCard,slide)
                binding.customSwitch!!.visibility=  View.VISIBLE

                Handler().postDelayed({
                    slide!!.slideEdge = Gravity.BOTTOM
                    TransitionManager.beginDelayedTransition(binding.consRoot,slide)
                    binding.imgRefresh!!.visibility=  View.GONE
                    binding.imgLogout!!.visibility=  View.VISIBLE
                },0)
            },200)


        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        val editor = prefeMain!!.edit()
        selectprinter = prefeMain!!.getString("PRINTER_TYPE","")!!
        switchvalue = prefeMain!!.getInt("SWITCH_VALUE",0)!!
        selectfont = prefeMain!!.getString("Font_TYPE","Roboto Font")!!

        val appname = prefeMain!!.getString("AppName","")!!
        binding.txtAppName.text = "EventName : "+getString(R.string.app_name)
        binding.txtSelectedPriter.text = "Currently Selected: ${selectprinter}"
        binding.txtSelectedFont.text = "Currently Selected: ${selectfont}"


        binding.imgBack.setOnClickListener(this)
        binding.imgLogout.setOnClickListener(this)
        binding.consSelectPrinter.setOnClickListener(this)
        binding.imgRefresh.setOnClickListener(this)
        binding.consSelectFont.setOnClickListener(this)

        if(switchvalue==1){
            binding.customSwitch.isChecked = true
        }else{
            binding.customSwitch.isChecked = false
        }

        binding.customSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchvalue = 1
                editor.putInt("SWITCH_VALUE",switchvalue)
                Toast.makeText(applicationContext, "Enable Contact Card", Toast.LENGTH_SHORT).show()
            }else {
                switchvalue = 0
                editor.putInt("SWITCH_VALUE",switchvalue)
                Toast.makeText(applicationContext, "Disable Contact Card", Toast.LENGTH_SHORT).show()
            }
            editor.commit()
        }

        var android_id = Settings.Secure.getString(getContentResolver(),
            Settings.Secure.ANDROID_ID)
        binding!!.txtAppDeviceID.setText("Device ID: $android_id")



    }

    override fun selectFileButtonOnClick() {

    }

    override fun printButtonOnClick() {

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgBack -> {
                finish()
            }
            R.id.imgLogout -> {
                OpenLogoutDialog()
            }
            R.id.consSelectPrinter -> {
                OpenDialog()

            }
            R.id.imgRefresh -> {
               Refresh()
            }
            R.id.consSelectFont->{
                OpenDialogFont()
            }
        }
    }

    private fun OpenDialogFont() {
        val dialog = Dialog(this, R.style.my_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.choosefont_layout)
        dialog.setCancelable(false)

        val cvCross = dialog.cvCross.findViewById<CardView>(R.id.cvCross)
        val InterFont = dialog.InterFont.findViewById<RadioButton>(R.id.InterFont)
        val PoppinsFont = dialog.PoppinsFont.findViewById<RadioButton>(R.id.PoppinsFont)
        val PlayFair = dialog.PlayFair.findViewById<RadioButton>(R.id.PlayFair)
        val Roboto = dialog.Roboto.findViewById<RadioButton>(R.id.Roboto)
        val btnsave = dialog.btnSave.findViewById<Button>(R.id.btnSave)

        InterFont.isChecked = false
        PoppinsFont.isChecked = false
        PlayFair.isChecked = false
        Roboto.isChecked = false

        if(selectfont.contains("Poppins Font")){
            PoppinsFont.isChecked = true
        }else if(selectfont.contains("PlayFair Font")){
            PlayFair.isChecked = true
        }else if(selectfont.contains("Roboto Font")){
            Roboto.isChecked = true
        }else{
            InterFont.isChecked = true
        }


        InterFont.setOnClickListener({

            if(InterFont.isChecked){
                selectfont = Constant.INTER_FONT
            }else{
                selectfont = ""
            }
            Log.e("Selectfont23",selectfont)
        })

        PoppinsFont.setOnClickListener({

            if(PoppinsFont.isChecked){
                selectfont = Constant.POPPINS_FONT
            }else{
                selectfont = ""
            }
            Log.e("Selectfont22",selectfont)
        })

        PlayFair.setOnClickListener({

            if(PlayFair.isChecked){
                selectfont = Constant.PLAY_FAIR_FONT
            }else{
                selectfont = ""
            }

            Log.e("Selectfont21",selectfont)

        })

        Roboto.setOnClickListener({

            if(Roboto.isChecked){
                selectfont = Constant.ROBOTO_FONT
            }else{
                selectfont = ""
            }
            Log.e("Selectfont11",selectfont)

        })

        Log.e("Selectfont34",selectfont)




        cvCross.setOnClickListener({
            dialog.dismiss()
        })

        btnsave.setOnClickListener({
            binding.txtSelectedFont.text = "Currently Selected: "+selectfont
            prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = prefeMain!!.edit()
            editor.putString("Font_TYPE", selectfont)
            //editor.putString("PRINTER_TYPE_TSC", "")
            editor.commit()
            dialog.dismiss()
        })


        dialog.show()
    }

    private fun Refresh(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("from","reg")
        intent.putExtra("role_id",prefeMain!!.getString("ROLE_ID",""))
        intent.putExtra("user_id",prefeMain!!.getString("USER_ID",""))
        startActivity(intent)
        finish()
    }

    private fun   OpenLogoutDialog() {
        val dialog = Dialog(this, R.style.my_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.logout_dialog_layout)
        dialog.setCancelable(false)
        val btnNo = dialog.btnNo.findViewById<Button>(R.id.btnNo)
        val consRootLay = dialog.consRootLay.findViewById<ConstraintLayout>(R.id.consRootLay)
        val btnYes = dialog.btnYes.findViewById<Button>(R.id.btnYes)

        val animation: Animation
        animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up_info)
        consRootLay.startAnimation(animation);

        btnNo.setOnClickListener({
            dialog.dismiss()
        })

        btnYes.setOnClickListener({
            Logout()
        })

        dialog.show()
    }
    private fun Logout() {
        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefeMain!!.edit()
        editor.clear()
        editor.commit()
        val intent = Intent(this@ProfileSettingActivity, LoginActivityNew::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("from", "")
        intent.putExtra("role_id", "")
        intent.putExtra("user_id", "")
        startActivity(intent)
        finish()
    }

    fun BrandName(context: Context?): String {
        return Build.MODEL
    }

    private fun OpenSaveDialog() {
        val dialog = Dialog(this, R.style.my_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.save_kmini_dialog)
        dialog.setCancelable(false)
        dialog.show()

      var  prefeMainget = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        val btnSearch = dialog.btnSearch.findViewById<Button>(R.id.btnSearch)
        val cvCross = dialog.cvCrossMain.findViewById<CardView>(R.id.cvCrossMain)
        val ed_name = dialog.ed_name.findViewById<EditText>(R.id.ed_name)
        ed_name.setText(prefeMainget.getString("PRINTER_TYPE_device",""))
        ed_name.setSelection(ed_name.text.toString().trim().length)
        val warning_name = dialog.warning_name.findViewById<TextView>(R.id.warning_name)
        btnSearch.setOnClickListener(View.OnClickListener {
            if(ed_name.text.toString().trim().length>0 && ed_name.text.trim().length>8)
            {
                warning_name.visibility=View.GONE

                val prefeMain: SharedPreferences =
                    getSharedPreferences("FRIENDS", MODE_PRIVATE)
                val editor = prefeMain.edit()

                editor.putString("PRINTER_TYPE_device",ed_name.text.toString().trim())
                editor.putString("PRINTER_TYPE", "K2_MINI")
                editor.putString("PRINTER_TYPE_TSC", "")
                editor.commit()
                Toast.makeText(this, "K2MINI over Wifi  is selected for Print", Toast.LENGTH_LONG)
                    .show()
                dialog.dismiss()
                binding!!.txtSelectedPriter.text="K2MINI"
            }
            else{
                warning_name.visibility=View.VISIBLE
                warning_name.text="Please fill valid Device ID"

            }



        })
        cvCross.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })


    }
    private fun OpenDialog(){
        val dialog = Dialog(this, R.style.my_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.chooseprinter_layout)
        dialog.setCancelable(false)

        val cvCross = dialog.cvCross.findViewById<CardView>(R.id.cvCross)
        val radiobtnbrother = dialog.BrotherPrinter.findViewById<RadioButton>(R.id.BrotherPrinter)
        val radioforK2Mini = dialog.forK2Mini.findViewById<RadioButton>(R.id.forK2Mini)
        val radioforK2MiniFirebase = dialog.forK2MiniFirebase.findViewById<RadioButton>(R.id.forK2MiniFirebase)
        val radiobtntxtSunmiV2sPrinter = dialog.sunmi_v2_s_plusPrinter.findViewById<RadioButton>(R.id.sunmi_v2_s_plusPrinter)
        val radiobtnOther = dialog.OtherPrinter.findViewById<RadioButton>(R.id.OtherPrinter)
        val rdTVS = dialog.rdTVS.findViewById<RadioButton>(R.id.rdTVS)
        val btnsave = dialog.btnSave.findViewById<Button>(R.id.btnSave)
        val consRoot = dialog.consRoot.findViewById<ConstraintLayout>(R.id.consRoot)
        val consRootMain = dialog.consRootMain.findViewById<ConstraintLayout>(R.id.consRootMain)
        if(appUtils.BrandName(this).equals(Constant.SUNMI_K2MINI))
        {
            radioforK2MiniFirebase.visibility=View.VISIBLE
        }

      /*  val animation: Animation
        animation = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_up)
        consRootMain.startAnimation(animation);*/

        radiobtnbrother.isChecked = false
        radiobtntxtSunmiV2sPrinter.isChecked = false
        radiobtnOther.isChecked = false

        if(selectprinter.contains(Constant.BROTHER,ignoreCase = true)){
            radiobtnbrother.isChecked = true
        }else if(selectprinter.contains(Constant.SUNMI,ignoreCase = true)){
            radiobtntxtSunmiV2sPrinter.isChecked = true
        }
        else if(selectprinter.contains(Constant.TSC,ignoreCase = true)){
            radioforK2Mini.isChecked = true
        }else if(selectprinter.contains(Constant.TVS,ignoreCase = true)){
            rdTVS.isChecked = true
        }
        else if(selectprinter.contains("K2_MINI",ignoreCase = true)){
            radioforK2MiniFirebase.isChecked = true
        }
        else{
            radiobtnOther.isChecked = true
        }



        radiobtnbrother.setOnClickListener({

            if(radiobtnbrother.isChecked){
                selectprinter = Constant.BROTHER
                val intent = Intent(this, Activity_Settings::class.java)
                this.startActivity(intent)
            }else{
                selectprinter = ""
            }


        })

        radioforK2Mini.setOnClickListener({

            if(radioforK2Mini.isChecked){
                selectprinter = Constant.TSC
                val intent = Intent(this, SelectDeviceActivity::class.java)
                this.startActivity(intent)
                dialog.dismiss()
            }else{
                selectprinter = ""
            }


        })

        rdTVS.setOnClickListener({

            if(rdTVS.isChecked){
                selectprinter = Constant.TVS
                val intent = Intent(this, PrintfBlueListActivity::class.java)
                this.startActivity(intent)
                dialog.dismiss()
            }else{
                selectprinter = ""
            }


        })
        radioforK2MiniFirebase.setOnClickListener({

            if(radioforK2MiniFirebase.isChecked){
                dialog.dismiss()
                OpenSaveDialog()
            }else{
                selectprinter = ""
            }


        })


        radiobtntxtSunmiV2sPrinter.setOnClickListener({

            if(radiobtntxtSunmiV2sPrinter.isChecked){
                selectprinter = Constant.SUNMI
            }else{
                selectprinter = ""

            }



        })

        radiobtnOther.setOnClickListener({

            if(radiobtnOther.isChecked){
                selectprinter = Constant.OTHER
            }else{
                selectprinter = ""
            }


        })



        cvCross.setOnClickListener({
            dialog.dismiss()
        })

        btnsave.setOnClickListener({

            binding.txtSelectedPriter.text = "Currently Selected: "+selectprinter

            prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = prefeMain!!.edit()
            editor.putString("PRINTER_TYPE", selectprinter)
            editor.putString("PRINTER_TYPE_TSC", "")
            editor.commit()

            dialog.dismiss()
        })

        dialog.show()
    }


}


