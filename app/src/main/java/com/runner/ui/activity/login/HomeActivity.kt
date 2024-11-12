package com.runner.ui.activity.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.runner.R
import com.runner.databinding.ActivityHomeIdecBinding
import com.runner.extras.LogUtils
import com.runner.extras.appUtils
import com.runner.model.Constant
import com.runner.ui.activity.BaseActivity


class HomeActivity : BaseActivity(),OnClickListener{

    var binding: ActivityHomeIdecBinding? = null
    lateinit var prefeMain:SharedPreferences
    var role_id = ""
    var BrandName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_idec)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.primary_color)
        }

         prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)

        role_id = prefeMain!!.getString("ROLE_ID","").toString()



        LogUtils.debug("@@RoleId",role_id)

        binding!!.consOnsiteRegistrations!!.setOnClickListener(this)
        binding!!.consOnlineRedemption!!.setOnClickListener(this)
        binding!!.consZapping!!.setOnClickListener(this)
        binding!!.consProfileSetting!!.setOnClickListener(this)
        binding!!.consDiyScanner!!.setOnClickListener(this)
        //binding!!.logout!!.setOnClickListener(this)
        appUtils.checkmanagestorage(this)


        val animation: Animation
        animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up_info)
        binding!!.logo.startAnimation(animation);

        if(role_id.equals("1")){
            binding!!.consOnsiteRegistrations.visibility = View.GONE
            binding!!.consOnlineRedemption.visibility = View.VISIBLE
            binding!!.consZapping.visibility = View.GONE
            binding!!.consDiyScanner.visibility = View.GONE
        }else if(role_id.equals("2")){
            binding?.txtOnlineRedemption?.setText("Online Redemption")

            binding!!.consOnsiteRegistrations.visibility = View.VISIBLE
            binding!!.consZapping.visibility = View.GONE
            binding!!.consDiyScanner.visibility = View.GONE
            binding!!.consOnlineRedemption.visibility = View.VISIBLE

        }else if(role_id.equals("3")){

            binding?.txtOnlineRedemption?.setText("Lunch Allotment")

            binding!!.consOnsiteRegistrations.visibility = View.GONE
            binding!!.consZapping.visibility = View.GONE
            binding!!.consDiyScanner.visibility = View.GONE
            binding!!.consOnlineRedemption.visibility = View.VISIBLE


        /*binding!!.consOnsiteRegistrations.visibility = View.GONE
            binding!!.consZapping.visibility = View.VISIBLE
            binding!!.consDiyScanner.visibility = View.GONE
            binding!!.consOnlineRedemption.visibility = View.GONE*/

        }else if(role_id.equals("5")){
            binding!!.consOnsiteRegistrations.visibility = View.GONE
            binding!!.consZapping.visibility = View.GONE
            binding!!.consOnlineRedemption.visibility = View.GONE
            binding!!.consDiyScanner.visibility = View.VISIBLE
        }else{
            binding!!.consOnsiteRegistrations.visibility = View.GONE
            binding!!.consZapping.visibility = View.VISIBLE
            binding!!.consOnlineRedemption.visibility = View.GONE
            binding!!.consDiyScanner.visibility = View.GONE
            if(prefeMain!!.getString("event_id","").equals("29")){
                binding!!.consDiyScanner.visibility = View.VISIBLE
            }
        }
        if(appUtils.BrandName(this).toString().contains(Constant.SUNMI_K2MINI))
        {
            binding!!.consProfileSetting.setOnLongClickListener(View.OnLongClickListener {
                var prefeMainedit = getSharedPreferences("FRIENDS", AppCompatActivity.MODE_PRIVATE)
                val editor = prefeMainedit.edit()
                editor.putInt("HeightPrint", 0)
                editor.commit()
                Toast.makeText(this,"Printer Setting Reset for K2MINI",Toast.LENGTH_LONG).show()
                return@OnLongClickListener false
            })
        }

    }

    override fun selectFileButtonOnClick() {

    }

    override fun printButtonOnClick() {

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.consOnlineRedemption -> {

                if (role_id.equals("2")) {

                    BrandName = BrandName(this@HomeActivity)


                    if (BrandName.contains(Constant.SUNMI_K2MINI)) {

                        val intent = Intent(this@HomeActivity, EntryActivity_K2_Mini::class.java)
                        intent.putExtra("Camera", -1)
                        startActivity(intent)
                    } else {

                        val intent = Intent(this@HomeActivity, EntryActivity::class.java)
                        intent.putExtra("Camera", -1)
                        startActivity(intent)
                    }
                }
                else if (role_id.equals("3")){

                   // binding?.txtOnlineRedemption?.setText("Lunch Allotment")

                    BrandName = BrandName(this@HomeActivity)


                    if (BrandName.contains(Constant.SUNMI_K2MINI)) {

                        val intent = Intent(this@HomeActivity, EntryActivity_K2_Mini::class.java)
                        intent.putExtra("Camera", -1)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@HomeActivity, LunchEntryActivity::class.java)
                        intent.putExtra("Camera", -1)
                        startActivity(intent)
                    }
                }


            }

            R.id.consOnsiteRegistrations -> {
                val intent = Intent(this@HomeActivity, ChooseCategoryActivity::class.java)
                startActivity(intent)
            }

            R.id.consZapping -> {
                val intent = Intent(this, ChooseZappingLocationActivity::class.java)
                startActivity(intent)
            }

            R.id.consProfileSetting -> {
                val intent = Intent(this@HomeActivity, ProfileSettingActivity::class.java)
                startActivity(intent)
            }

            R.id.consDiyScanner -> {
                val intent = Intent(this@HomeActivity, DiyHomeActivity::class.java)
                startActivity(intent)
            }
            /*R.id.logout -> {
                prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = prefeMain!!.edit()
                editor.clear()
                editor.commit()
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                intent.putExtra("from","")
                intent.putExtra("role_id","")
                intent.putExtra("user_id","")
                startActivity(intent)
                finish()
            }*/
        }
    }

    fun BrandName(context: Context?): String {
        return Build.MODEL
    }
}