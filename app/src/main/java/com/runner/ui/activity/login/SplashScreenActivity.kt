package visitor.wbcntx.arjs.visitormanagement

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager

import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.runner.R
import com.brother.ptouch.sdk.Printer

import com.brother.ptouch.sdk.PrinterInfo
import com.runner.extras.appUtils
import com.runner.model.Constant
import com.runner.printdemo.printprocess.PrinterModelInfo
import com.runner.ui.activity.login.*


class SplashScreen : AppCompatActivity() {
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar!!.hide()
        sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this@SplashScreen)
        //val tabletSize = resources.getBoolean(R.bool.isTablet)
        val address = sharedPreferences!!.getString("address", "")
        // System.loadLibrary("libcreatedata");
        if (address.equals("", ignoreCase = true)) {
            setPrefereces()
        }

        if (Build.VERSION.SDK_INT >= 23) {
            // for external Run time permissionManifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SEND_SMS
            val PERMISSION_ALL = 1
            var PERMISSIONS: Array<String>? =null

            if(appUtils.BrandName(this).toString().contains("V1s-G"))
            {
                 PERMISSIONS = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }
            else{
                PERMISSIONS = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.NEARBY_WIFI_DEVICES,
                )
            }


            if (!hasPermissions(this@SplashScreen, *PERMISSIONS)) {
                ActivityCompat.requestPermissions(this@SplashScreen, PERMISSIONS, PERMISSION_ALL)
            } else {
                openNext()
            }
        }else
        {
            openNext()
        }
        // statusbar color
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.white)
        }
    }

    private fun setPrefereces() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this)
        // initialization for print
        var printerInfo: PrinterInfo? = PrinterInfo()
        val printer = Printer()
        printerInfo = printer.getPrinterInfo()
        if (printerInfo == null) {
            printerInfo = PrinterInfo()
            printer.setPrinterInfo(printerInfo)
        }
        if (sharedPreferences.getString("printerModel", "") == "") {
            val printerModel: String = printerInfo.printerModel.toString()
            val model: PrinterModelInfo.Model = PrinterModelInfo.Model.QL_720NW
            val editor = sharedPreferences.edit()
            editor.putString("printerModel", "QL_720NW")
            editor.putString("port", "NET")
            editor.putString("address", "192.168.137.64")
            editor.putString("macAddress", "00:80:92:e3:ed:32")
            editor.putString("PCIP", getString(R.string.weburl))
            editor.putString("paperSize", "CUSTOM")
            editor.putString("orientation", "PORTRAIT")
            editor.putString(
                "numberOfCopies",
                Integer.toString(1)
            )
            editor.putString("halftone", "PATTERNDITHER")
            editor.putString("printMode", "FIT_TO_PAGE")
            editor.putString("pjCarbon", "false")
            editor.putString(
                "pjDensity",
                Integer.toString(5)
            )
            editor.putString("pjFeedMode", "PJ_FEED_MODE_FIXEDPAGE")
            editor.putString("align", "LEFT")
            editor.putString(
                "leftMargin",
                Integer.toString(0)
            )
            editor.putString("valign", "TOP")
            editor.putString(
                "topMargin",
                Integer.toString(printerInfo.margin.top)
            )
            editor.putString(
                "customPaperWidth",
                Integer.toString(printerInfo.customPaperWidth)
            )
            editor.putString(
                "customPaperLength",
                Integer.toString(printerInfo.customPaperLength)
            )
            editor.putString(
                "customFeed",
                Integer.toString(printerInfo.customFeed)
            )
            editor.putString(
                "paperPostion",
                printerInfo.paperPosition.toString()
            )
            editor.putString(
                "customSetting",
                sharedPreferences.getString("customSetting", "")
            )
            editor.putString(
                "rjDensity",
                Integer.toString(printerInfo.rjDensity)
            )
            editor.putString(
                "rotate180",
                java.lang.Boolean.toString(printerInfo.rotate180)
            )
            editor.putString("dashLine", java.lang.Boolean.toString(printerInfo.dashLine))
            editor.putString("peelMode", java.lang.Boolean.toString(printerInfo.peelMode))
            editor.putString("mode9", java.lang.Boolean.toString(printerInfo.mode9))
            editor.putString("pjSpeed", Integer.toString(printerInfo.pjSpeed))
            editor.putString(
                "printerCase",
                printerInfo.rollPrinterCase.toString()
            )
            editor.putString(
                "skipStatusCheck",
                java.lang.Boolean.toString(printerInfo.skipStatusCheck)
            )
            editor.putString(
                "imageThresholding",
                Integer.toString(printerInfo.thresholdingValue)
            )
            editor.putString(
                "scaleValue",
                java.lang.Double.toString(printerInfo.scaleValue)
            )
            editor.commit()
        }
    }

    private fun openNext() {
        Handler().postDelayed({
            Log.e("@@HEre","MMMMMMMMMM")
            val prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            val ISLOGIN = prefeMain.getString("islogged_in", "")
            Log.e("@@ISLOGIN", "$ISLOGIN n")

            if ( ISLOGIN != "") {
               /* val intent = Intent(this@SplashScreen, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()*/
                val Role_id = prefeMain.getString("ROLE_ID", "")
                Log.e("@@Role", "$Role_id n")
                if (Role_id.equals("3")) {
                    val intent = Intent(this@SplashScreen, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (Role_id.equals("1") || Role_id.equals("2")) {
                    val intent = Intent(this@SplashScreen, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
              else {
                    val intent = Intent(this@SplashScreen, LoginActivityNew::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("from","")
                    intent.putExtra("role_id","")
                    intent.putExtra("user_id","")
                    startActivity(intent)
                    finish()
                }
            }
            else {
                val intent = Intent(this@SplashScreen, LoginActivityNew::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("from","")
                intent.putExtra("role_id","")
                intent.putExtra("user_id","")
                startActivity(intent)
                finish()
            }


        }, 1500)
/*
        Handler().postDelayed({

        },1000)*/


        /* (new Handler()).postDelayed(new Runnable()
        {
            public void run() {

            }
        }, 1500);*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openNext()
            }
        }
    }

    companion object {
        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission!!
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }
    }
}