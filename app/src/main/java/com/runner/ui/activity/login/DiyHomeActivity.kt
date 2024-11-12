package com.runner.ui.activity.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.View.OnLongClickListener
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.runner.R
import com.runner.ui.activity.BaseActivity

class DiyHomeActivity : BaseActivity() {

    var lottiemain: LottieAnimationView? = null
    var btnScan: Button? = null
    var txtScanQr: TextView? = null
    var prefeMain: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diy_home)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        lottiemain = findViewById(R.id.lottiemain)
        btnScan = findViewById(R.id.btnScan)
        txtScanQr = findViewById(R.id.txtScanQr)
        lottiemain!!.playAnimation()


        /* if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }*/txtScanQr!!.setOnLongClickListener(OnLongClickListener {
            val editor = prefeMain!!.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(this@DiyHomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            false
        })
        btnScan!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@DiyHomeActivity, DiyScannerActivity::class.java)
            intent.putExtra("Camera", 1)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })
    }

    override fun selectFileButtonOnClick() {

    }

    override fun printButtonOnClick() {

    }
}