package com.runner.extras

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.Context.USB_SERVICE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.MarginLayoutParamsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.brother.ptouch.sdk.PrinterInfo
import com.brother.sdk.lmprinter.*
import com.brother.sdk.lmprinter.setting.*
import com.example.tscdll.TSCActivity
import com.example.tscdll.TSCUSBActivity
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.qd.testcodeobfuscation.R.id.textView
import com.runner.BuildConfig
import com.runner.R
import com.runner.helper.BluetoothUtil
import com.runner.helper.ESCUtil
import com.runner.helper.SunmiPrintHelper
import com.runner.model.Constant
import com.runner.printdemo.common.Common
import com.runner.printdemo.common.MsgDialog
import com.runner.printdemo.common.MsgHandle
import com.runner.printdemo.printprocess.BasePrint
import com.runner.printdemo.printprocess.ImagePrint
import com.runner.tvs.Manaer.PrintfManager
import com.runner.ui.activity.login.ChooseCategoryActivity

import com.webtopdf.PdfView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class StickerPrinting() {
  companion object{
    val PRINT_HAND_FREEMIUM = "com.dynamixsoftware.printhand"
    val PRINT_HAND_PREMIUM = "com.dynamixsoftware.printhand.premium"
    var mypath: File? = null

    protected var mImageFiles: ArrayList<String?>? = null

    var myPrint: BasePrint? = null
    open var mHandle: MsgHandle? = null
    open var mDialog: MsgDialog? = null

    lateinit var mcontext:Context
    var sharedPreferences: SharedPreferences? = null
    var font:FontUtils? = null

    @SuppressLint("SuspiciousIndentation")
    fun sunmiPrinting(type:String, context : Context, view : RelativeLayout, code : String, r1 : String, r2 : String, r3 : String, r4 : String, r5 : String,
                      model :String,usbManager:UsbManager?,device: UsbDevice?){
      Log.e("Device_key12", "sads12")
      try{
        mcontext =context
        Log.e("Device key", "sads")
        var   prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)

        val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
        val PRINTER_TYPE_TSC = prefeMain.getString("PRINTER_TYPE_TSC", "")
        val deviceAddress = prefeMain.getString("deviceAddress", "")


        if(PRINTER_TYPE.equals("TSC") && model.contains(Constant.SUNMI_K2MINI))
        {
          coommonsunmiTSC1(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
        }
        else{

          if(appUtils.BrandMan(context).toString().equals("sunmi", ignoreCase = true))
          {
            if(model.contains(Constant.SUNMI_K2MINI))
            {
              coommonsunmiTSC(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
            }
            else
            {
              coommonsunmi(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
            }
            Log.e("@@213","2")

          }
          else
          {
            Log.e("@@21312","1")
            coommonsunmimOBILE(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
          }

        }


        Log.e("@@TRack ", "Came Here")
        var rl_badgeprint:RelativeLayout = view
        //                                int fontLarger=42;
//                                int fontSmall=35;
//                                if(name.length()>15){
//                                    fontLarger=35;
//
//                                }
//                                if(category_list.get(position).length()>15){
//                                    fontSmall=25;
//                                }
//                                byte[] send;
//                                send = ESCUtil.alignCenter();
//                                SunmiPrintHelper.getInstance().setAlign(1);
//                                SunmiPrintHelper.getInstance().sendRawData(send);
//                                SunmiPrintHelper.getInstance().print1Line();
//                                SunmiPrintHelper.getInstance().print1Line();
//
//                                SunmiPrintHelper.getInstance().printText(name, fontLarger, true, isUnderLine, testFont);
//                                SunmiPrintHelper.getInstance().print1Line();
//                                SunmiPrintHelper.getInstance().print1Line();
//
//                                SunmiPrintHelper.getInstance().printQr(code_list.get(position), 6, 3);
//                                SunmiPrintHelper.getInstance().printText(code_list.get(position), 25, false, isUnderLine, testFont);
//                                SunmiPrintHelper.getInstance().print1Line();
//                                SunmiPrintHelper.getInstance().print1Line();
//                                SunmiPrintHelper.getInstance().printText(category_list.get(position), fontSmall, true, isUnderLine, testFont);
//                              SunmiPrintHelper.getInstance().print1Line();
//                              SunmiPrintHelper.getInstance().print1Line();
//                              SunmiPrintHelper.getInstance().print1Line();



        rl_badgeprint.isDrawingCacheEnabled = true
        rl_badgeprint.buildDrawingCache()
        //    rl_badgeprint.setDrawingCacheEnabled(true)
        // rl_badgeprint.buildDrawingCache()
        val bitmap_: Bitmap? =
          drawToBitmap(rl_badgeprint,
            rl_badgeprint.getWidth(),
            rl_badgeprint.getHeight()
          )
        rl_badgeprint.isDrawingCacheEnabled = true
        Log.e("@@Bitmap *****", bitmap_.toString())
        Handler().postDelayed({



          if (!BluetoothUtil.isBlueToothPrinter) {

            rl_badgeprint.setDrawingCacheEnabled(true)
            rl_badgeprint.buildDrawingCache()
            val bitmap_: Bitmap? = drawToBitmap(
              rl_badgeprint,
              rl_badgeprint.getWidth(),
              rl_badgeprint.getHeight()
            )
            rl_badgeprint.setDrawingCacheEnabled(true)

            Log.e("@@PRINTER_TYPE_TSC",PRINTER_TYPE_TSC.toString())

            if (PRINTER_TYPE_TSC.toString() .equals( "USB") ){
              if (usbManager != null && device != null && usbManager!!.hasPermission(device)) {
                var TscUSB = TSCUSBActivity()
                var size=  if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)) 82 else if(appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)) 40 else 60

                TscUSB.openport(usbManager, device)

                TscUSB.setup(80, if(appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)) 70 else 63, 4, 15, 1, 1, 1)
                //    TscUSB.sendcommand("TEXT 100,100,\"1\",0,1,1,\"123456\"\r\n");
                TscUSB.sendbitmap(size, 0, bitmap_)
                TscUSB.sendcommand(
                  """
             PUTBMP 20,20,"${bitmap_},SET CUTTER 1"
             
             """.trimIndent()
                )
                TscUSB.printlabel(1, 1)
                //Thread.sleep(1000)

                TscUSB.closeport(1000)
              }

            } else if (PRINTER_TYPE_TSC.toString().equals( "Bluetooth")) {
              var TscDll = TSCActivity()
              Thread {
                Log.e("@@Permision", "Check")
                // Ouverture du socket Bluetooth et connexion à l'appareil
                if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                  ) == PackageManager.PERMISSION_DENIED
                ) {
                  Log.e("@@Permision", "Denied")
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(
                      context!! as Activity,
                      arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                      2
                    )
                    // return;
                  }
                }
                Log.e("@@Permision", "Allow")
                try {

                  var size=  if(model.toString().contains(Constant.SUNMI_V2s_STGL_MODEL)) 82 else if(model.toString().contains(Constant.SUNMI_K2MINI)) 20 else if(appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)) 40 else 60


                  Log.e("##deviceSize",size.toString())
                  TscDll.openport(deviceAddress) //BT
                  TscDll.setup(80, if(appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)) 70 else 63, 4, 15, 1, 1, 1)
                  // Log.e("@@@STATUS",TscDll.printerstatus(300));
                  TscDll.sendbitmap(size,  if(model.toString().contains(Constant.SUNMI_K2MINI)) 65 else 0, bitmap_)


                  TscDll.sendcommand(
                    """
            PUTBMP 20,20,"${bitmap_},SET CUTTER 1"
            
            """.trimIndent()

                  )
                  // TscDll.sendcommand("PRINT 1\r\n\n")

                  TscDll.printlabel(1, 1)
                  //   Thread.sleep(1000)
                  // TscDll.sendcommand("FORMFEED\r\n")
                  // TscDll.sendcommand("CUT\r\n")
                  TscDll.closeport(1000)
                } catch (e: java.lang.Exception) {
                  Log.e("@@EX", e.message!!)
                }
              }.start()
            }

            else  if(model.contains(Constant.SUNMI_K2MINI))
            {
              /* SunmiPrintHelper.instance.setAlign(1)
               SunmiPrintHelper.instance.printBitmapWithOut(bitmap_,0)
               SunmiPrintHelper.instance.feedPaper()
               SunmiPrintHelper.instance.cutpaper()*/

              /*SunmiPrintHelper.instance.setAlign(1)
              SunmiPrintHelper.instance.printBitmapWithOut(bitmap_,0)
              SunmiPrintHelper.instance.feedPaper()
              SunmiPrintHelper.instance.cutpaper()*/
              SunmiPrintHelper.instance.setAlign(1)
              SunmiPrintHelper.instance.sendRawData(ESCUtil.labellocate());
              SunmiPrintHelper.instance.printBitmap(bitmap_,0);
              SunmiPrintHelper.instance.sendRawData(ESCUtil.labelout());
              SunmiPrintHelper.instance.cutpaper();

            }
            else
            {
              /* SunmiPrintHelper.instance.initSunmiPrinterService(mcontext)
               SunmiPrintHelper.instance.initPrinter()*/
              Log.e("@@Myyyy_TRAck","here22222")
              if(SunmiPrintHelper!!.instance!!.isLabelMode && SunmiPrintHelper!!.instance!!.printerPaper.equals(if(model.equals(Constant.SUNMI_V2_PLUS_MODEL))"80mm" else "58mm"))
              {

                SunmiPrintHelper.instance.sendRawData(ESCUtil.labellocate());
                SunmiPrintHelper.instance.printBitmap(bitmap_,0);
                SunmiPrintHelper.instance.sendRawData(ESCUtil.labelout());
                // SunmiPrintHelper.instance.cutpaper();

                // SunmiPrintHelper!!.instance.printOneLabel(bitmap_,context)
              }
              else
              {
                Toast.makeText(context,"Please select label mode and Correct Paper( ${if(model.equals(Constant.SUNMI_V2_PLUS_MODEL))"80mm" else "58mm"}) size",Toast.LENGTH_LONG).show()
              }


            }


            Handler().postDelayed({
              if(type.equals("spot",ignoreCase = true)){
                val intent = Intent(context, ChooseCategoryActivity::class.java)
                context!!.startActivity(intent)
                var activity :Activity = context as Activity
                activity.finish()
              }
            }, 600L)
          }
        }, 500L)

      }
      catch (e: Exception) {
        Log.e("@@ error ", e.message!!)
      }
    }


    @SuppressLint("SuspiciousIndentation")
    fun sunmiPrintingTVS(type:String, context : Context, view : RelativeLayout, code : String, r1 : String, r2 : String, r3 : String, r4 : String, r5 : String,
                         model :String, mPrintfManager: PrintfManager?){
      Log.e("Device_key12", "sads12")
      try{

        mcontext =context
        Log.e("Device key", "sads")
        var   prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)

        val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
        val PRINTER_TYPE_TSC = prefeMain.getString("PRINTER_TYPE_TSC", "")
        val deviceAddress = prefeMain.getString("deviceAddress", "")


        if(PRINTER_TYPE.equals("TVS") && model.contains(Constant.SUNMI_K2MINI))
        {
          coommonsunmiTSC(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
        }
        else{

          if(appUtils.BrandMan(context).toString().equals("sunmi", ignoreCase = true))
          {
            Log.e("@@213","2")
            coommonsunmi(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
          }
          else
          {
            Log.e("@@21312","1")
            coommonsunmimOBILE(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
          }

        }


        var rl_badgeprint:RelativeLayout = view

        rl_badgeprint.isDrawingCacheEnabled = true
        rl_badgeprint.buildDrawingCache()
        //    rl_badgeprint.setDrawingCacheEnabled(true)
        // rl_badgeprint.buildDrawingCache()
        val bitmap_: Bitmap? =
          drawToBitmap(rl_badgeprint,
            rl_badgeprint.getWidth(),
            rl_badgeprint.getHeight()
          )
        rl_badgeprint.isDrawingCacheEnabled = true
        Log.e("@@Bitmap *****", bitmap_.toString())
        Handler().postDelayed({

          if (!BluetoothUtil.isBlueToothPrinter) {

            rl_badgeprint.setDrawingCacheEnabled(true)
            rl_badgeprint.buildDrawingCache()
            val bitmap_: Bitmap? = drawToBitmap(
              rl_badgeprint,
              rl_badgeprint.getWidth(),
              rl_badgeprint.getHeight()
            )
            rl_badgeprint.setDrawingCacheEnabled(true)

            Log.e("@@PRINTER_TYPE_TSC",PRINTER_TYPE_TSC.toString())
            mPrintfManager!!.printf(80, 62, bitmap_, mcontext as Activity)

            /*Handler().postDelayed({
              try{
                var activity=context as Activity
                if(!activity.isFinishing)
                {
                  if(mPrintfManager!=null && mPrintfManager.isConnect)
                  {
                    mPrintfManager!!.disConnect("Disconnect Printer")
                  }
                }

              }
              catch (e:Exception)
              {

              }

            }, 7000L)*/

            Handler().postDelayed({
              if(type.equals("spot",ignoreCase = true)){
                val intent = Intent(context, ChooseCategoryActivity::class.java)
                context!!.startActivity(intent)
                var activity :Activity = context as Activity
                activity.finish()
              }
            }, 600L)
          }
        }, 500L)

      }
      catch (e: Exception) {
        Log.e("@@ error ", e.message!!)
      }
    }


    fun coommonsunmi(type:String, context : Context, view : RelativeLayout, code : String, r1 : String, r2 : String, r3 : String, r4 : String, r5 : String, model :String)
    {
      mcontext =context

      Log.e("Device key", "sads")
      var   prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)

      val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
      val PRINTER_TYPE_TSC = prefeMain.getString("PRINTER_TYPE_TSC", "")
      val deviceAddress = prefeMain.getString("deviceAddress", "")

      font = FontUtils(context)

      var Row1 = prefeMain.getString("ROW1","")
      var Row2 = prefeMain.getString("ROW2","")
      var Row3 = prefeMain.getString("ROW3","")
      var Row4 = prefeMain.getString("ROW4","")
      var Row5 = prefeMain.getString("ROW5","")

      var container_sticker: LinearLayout?= null

      var splitedcode = ""

      if (code.contains("BEGIN:VCARD")) {
        val arrOfStr: Array<String> = code.split("UC:".toRegex()).dropLastWhile { it.isEmpty() }
          .toTypedArray()
        splitedcode = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }
        Log.e("SEarch key", splitedcode)
      }else{
        splitedcode = code
      }

      container_sticker  = view!!.findViewById(R.id.container_sticker);
      if(PRINTER_TYPE.equals("TVS")&&model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
        val param = container_sticker.layoutParams as LinearLayout.LayoutParams
        param.setMargins(12,0,0,0)
        container_sticker.layoutParams = param
      }
      else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
        val param = container_sticker.layoutParams as LinearLayout.LayoutParams
        param.setMargins(-5,0,0,0)
        container_sticker.layoutParams = param
      }
      else  if(PRINTER_TYPE.equals("TVS")&& model.contains(Constant.SUNMI_V2_PLUS_MODEL)){
        val param = container_sticker.layoutParams as LinearLayout.LayoutParams
        param.setMargins(25,0,0,0)
        container_sticker.layoutParams = param
      }

      container_sticker!!.removeAllViews()

      try {
        var bmp: Bitmap? = null
        val writer = QRCodeWriter()
        try {
          val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512)
          val width = bitMatrix.width
          val height = bitMatrix.height
          bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
          for (x in 0 until width) {
            for (y in 0 until height) {
              bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
          }

        } catch (e: WriterException) {
          e.printStackTrace()
          Log.e("@@ nit Error", e.message!!)
        }
        val tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
          .toString() + "/" + "Print" + "/"
        val wallpaperDirectory = File(tempDir)
        if (!wallpaperDirectory.exists()) {
          wallpaperDirectory.mkdirs()
        }
        val outputnomedia = File(wallpaperDirectory, ".nomedia")
        try {
          outputnomedia.createNewFile()
        } catch (e: IOException) {
          e.printStackTrace()
        }
        val uniqueId = "IMGBADGE_" + System.currentTimeMillis().toString() + ".png"


        if(!r1.equals("")){

          if(!Row1.equals("unique_code")) {
            val mInflater = LayoutInflater.from(context)
            val view_h1  =  mInflater.inflate(R.layout.row_h1,null,false)
            val h1 = view_h1.findViewById<TextView>(R.id.lblprintname_sunmi_h1)


            font!!.Fonts(h1,"r1")

            h1.text = r1.toUpperCase()

            if(PRINTER_TYPE.equals("TSC")|| model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI) ){
              h1.minimumWidth = R.dimen.dimen_text_Sunmmi
              view.minimumWidth = R.dimen.dimen_text_Sunmmi

              h1.setTextSize(29F)

              if(r1.length>11){
                h1.setTextSize(19F)
              }

              if(r1.length>15){
                h1.setTextSize(16F)
              }

              if(r1.length>20){
                h1.setTextSize(14F)
              }

              var EventId = prefeMain.getString("event_id","")
              Log.e("@@@EventId",EventId.toString())
              /* if(prefeMain.getString("event_id","")!!.equals("19"))  {

                 h1.setTextSize(19F)

                 if(r1.length>18){
                   h1.setTextSize(17F)
                 }
                 if(r1.length>30){
                   h1.setTextSize(15F)
                 }
               }*/

            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){

              Log.e("@@tuyitutoyi","Here2233")
              h1.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              val param = container_sticker.layoutParams as LinearLayout.LayoutParams

              h1.setTextSize(19F)
              if(r1.length>=12){
                h1.setTextSize(16F)
              }
              if(r1.length>20){
                h1.setTextSize(14F)
              }
            }
            else{
              h1.minimumWidth = R.dimen.dimen_text_Sunmmi
              view.minimumWidth = R.dimen.dimen_text_Sunmmi

              h1.setTextSize(29F)

              if(r1.length>11){
                h1.setTextSize(19F)
              }

              if(r1.length>15){
                h1.setTextSize(16F)
              }

              if(r1.length>20){
                h1.setTextSize(14F)
              }

              var EventId = prefeMain.getString("event_id","")
              Log.e("@@@EventId",EventId.toString())
            }

            container_sticker.addView(view_h1)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL)|| model.contains(Constant.SUNMI_K2MINI) ){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi
            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              /*val param = llqr.layoutParams as RelativeLayout.LayoutParams
              param.setMargins(-15,0,0,0)
              llqr.layoutParams = param*/
            }
            else
            {
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi
            }
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{

          Log.e("@@TRACK1","else.."+r1)
        }


        if(!r2.equals("")){

          if(!Row2.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h2 =mInflater.inflate(R.layout.row_h2,null,false)
            val h2 = view_h2.findViewById<TextView>(R.id.lblprintname_sunmi_h2)

            font!!.Fonts(h2,"r2")

            h2.text = r2.toUpperCase()

            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              h2.minWidth = R.dimen.dimen_text_Sunmmi
              h2.setTextSize(21F)
              if(r2.length>15){
                h2.setTextSize(16F)
              }
              if(r2.length>20){
                h2.setTextSize(14F)
              }
              if(r2.length>23){
                h2.setTextSize(12F)
              }

              if(r1.length>12){
                h2.setTextSize(15F)
              }


              /* if(prefeMain.getString("event_id","")!!.equals("19"))  {
                 h2.setTextSize(17F)

                 if(r2.length>15){
                   h2.setTextSize(15F)
                 }
                 if(r2.length>30){
                   h2.setTextSize(14F)
                 }


               }*/

            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
              h2.minWidth = R.dimen.dimen_text_Sunmmi_v2s
              val param = h2.layoutParams as ConstraintLayout.LayoutParams
              param.setMargins(-1,0,0,0)
              h2.layoutParams = param

              h2.setTextSize(17F)
              if(r2.length>13){
                h2.setTextSize(13F)
              }
              if(r2.length>17){
                h2.setTextSize(12F)
              }

              if(r2.length>22){
                h2.setTextSize(10F)
              }

              if(r1.length>=12){
                h2.setTextSize(14F)
              }
            }
            else
            {
              h2.minWidth = R.dimen.dimen_text_Sunmmi
              h2.setTextSize(21F)
              if(r2.length>15){
                h2.setTextSize(16F)
              }
              if(r2.length>20){
                h2.setTextSize(14F)
              }
              if(r2.length>23){
                h2.setTextSize(12F)
              }

              if(r1.length>12){
                h2.setTextSize(14F)
              }
            }



            container_sticker.addView(view_h2)
          }else {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi
            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              /* val param = llqr.layoutParams as RelativeLayout.LayoutParams
               param.setMargins(-15,0,0,0)
               llqr.layoutParams = param*/
            }
            else
            {
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi
            }
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }


        if(!r3.equals("")){

          if(!Row3.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h3 =mInflater.inflate(R.layout.row_h3,null,false)
            val h3 = view_h3.findViewById<TextView>(R.id.lblPrintDesignation_sunmi)

            font!!.Fonts(h3,"r3")

            h3.text = r3.toUpperCase()


            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL)|| model.contains(Constant.SUNMI_K2MINI) ){
              h3.minWidth = R.dimen.dimen_text_Sunmmi
              h3.setTextSize(16F)
              if(r3.length>15){
                h3.setTextSize(14F)
              }
              if(r3.length>25){
                h3.setTextSize(12F)
              }

              if(r3.length>27){
                h3.setTextSize(10F)
              }

            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
              h3.minWidth = R.dimen.dimen_text_Sunmmi_v2s
              val param = h3.layoutParams as ConstraintLayout.LayoutParams
              param.setMargins(-1,0,0,0)
              h3.layoutParams = param

              h3.setTextSize(14F)
              if(r3.length>15){
                h3.setTextSize(12F)
              }
              if(r3.length>24){
                h3.setTextSize(10F)
              }
              if(r3.length>27){
                h3.setTextSize(9F)
              }

            }
            else
            {
              h3.minWidth = R.dimen.dimen_text_Sunmmi
              h3.setTextSize(16F)
              if(r3.length>15){
                h3.setTextSize(14F)
              }

            }

            container_sticker.addView(view_h3)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi
            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              /* val param = llqr.layoutParams as RelativeLayout.LayoutParams
               param.setMargins(-15,0,0,0)
               llqr.layoutParams = param*/
            }
            else
            {
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi
            }
            unique_code.visibility = View.VISIBLE
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }

        if(!r4.equals("")){

          if(!Row4.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            font!!.Fonts(h4,"r4")
            h4.text = r4.toUpperCase()
            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              h4.minWidth = R.dimen.dimen_text_Sunmmi
              h4.setTextSize(13F);
              if(r4.length>15){
                h4.setTextSize(12F);
              }

            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
              h4.minWidth = R.dimen.dimen_text_Sunmmi_v2s
              val param = h4.layoutParams as ConstraintLayout.LayoutParams
              param.setMargins(-15,0,0,0)
              h4.layoutParams = param

              h4.setTextSize(12F);
              if(r4.length>15){
                h4.setTextSize(10F);
              }
              if(r4.length>20){
                h4.setTextSize(9F);
              }
            }
            else
            {
              h4.minWidth = R.dimen.dimen_text_Sunmmi
              h4.setTextSize(13F);
              if(r4.length>15){
                h4.setTextSize(12F);
              }
            }
            container_sticker.addView(view_h4)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            var EventId = prefeMain.getString("event_id","")
            if(EventId.equals("32")){
              unique_code.setTextSize(10F)
            }
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL)|| model.contains(Constant.SUNMI_K2MINI) ){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi
            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              /*   val param = llqr.layoutParams as RelativeLayout.LayoutParams
                 param.setMargins(-15,0,0,0)
                 llqr.layoutParams = param*/
            }
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }



        if(!r5.equals("")){

          if(!Row5.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            font!!.Fonts(h4,"r2")
            h4.text = r5.toUpperCase()
            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              h4.minWidth = R.dimen.dimen_text_Sunmmi
              h4.setTextSize(21F)
              if(r5.length>15){
                h4.setTextSize(18F)
              }

            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
              h4.minWidth = R.dimen.dimen_text_Sunmmi_v2s
              /* val param = h4.layoutParams as ConstraintLayout.LayoutParams
               param.setMargins(-25,0,0,0)
               h4.layoutParams = param*/
              h4.setTextSize(18F)
              if(r5.length>15){
                h4.setTextSize(16F)
              }
              if(r5.length>20){
                h4.setTextSize(10F)
              }
            }
            else
            {
              h4.minWidth = R.dimen.dimen_text_Sunmmi
              h4.setTextSize(14F)
              if(r5.length>15){
                h4.setTextSize(14F)
              }

            }
            container_sticker.addView(view_h4)
          }else {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5,null,false)

            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi
            }else if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              /* val param = llqr.layoutParams as RelativeLayout.LayoutParams
               param.setMargins(-15,0,0,0)
               llqr.layoutParams = param*/
            }
            else
            {
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi
            }
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }

      }
      catch (e:Exception) {
      }

    }

    fun coommonsunmimOBILE(type:String, context : Context, view : RelativeLayout, code : String, r1 : String, r2 : String, r3 : String, r4 : String, r5 : String, model :String)
    {
      mcontext =context

      Log.e("Device key", "sads")
      var   prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)

      val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
      val PRINTER_TYPE_TSC = prefeMain.getString("PRINTER_TYPE_TSC", "")
      val deviceAddress = prefeMain.getString("deviceAddress", "")

      font = FontUtils(context)

      var Row1 = prefeMain.getString("ROW1","")
      var Row2 = prefeMain.getString("ROW2","")
      var Row3 = prefeMain.getString("ROW3","")
      var Row4 = prefeMain.getString("ROW4","")
      var Row5 = prefeMain.getString("ROW5","")

      var container_sticker: LinearLayout?= null

      var splitedcode = ""

      if (code.contains("BEGIN:VCARD")) {
        val arrOfStr: Array<String> = code.split("UC:".toRegex()).dropLastWhile { it.isEmpty() }
          .toTypedArray()
        splitedcode = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }
        Log.e("SEarch key", splitedcode)
      }else{
        splitedcode = code
      }

      container_sticker  = view!!.findViewById(R.id.container_sticker);


      val param = container_sticker.layoutParams as LinearLayout.LayoutParams
      param.setMargins(0,0,0,0)
      container_sticker.layoutParams = param


      container_sticker!!.removeAllViews()

      try {
        var bmp: Bitmap? = null
        val writer = QRCodeWriter()
        try {
          val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512)
          val width = bitMatrix.width
          val height = bitMatrix.height
          bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
          for (x in 0 until width) {
            for (y in 0 until height) {
              bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
          }

        } catch (e: WriterException) {
          e.printStackTrace()
          Log.e("@@ nit Error", e.message!!)
        }
        val tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
          .toString() + "/" + "Print" + "/"
        val wallpaperDirectory = File(tempDir)
        if (!wallpaperDirectory.exists()) {
          wallpaperDirectory.mkdirs()
        }
        val outputnomedia = File(wallpaperDirectory, ".nomedia")
        try {
          outputnomedia.createNewFile()
        } catch (e: IOException) {
          e.printStackTrace()
        }
        val uniqueId = "IMGBADGE_" + System.currentTimeMillis().toString() + ".png"


        if(!r1.equals("")){

          Log.e("@@","23232")
          if(!Row1.equals("unique_code")) {
            val mInflater = LayoutInflater.from(context)
            val view_h1  =  mInflater.inflate(R.layout.row_h1,null,false)
            val h1 = view_h1.findViewById<TextView>(R.id.lblprintname_sunmi_h1)

            font!!.Fonts(h1,"r1")

            h1.text = r1.toUpperCase()

            if( model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI) ){
              h1.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              view.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s

              h1.setTextSize(22F)

              if(r1.length>11){
                h1.setTextSize(14F)
              }

              if(r1.length>15){
                h1.setTextSize(15F)
              }

              if(r1.length>20){
                h1.setTextSize(11F)
              }

              var EventId = prefeMain.getString("event_id","")
              Log.e("@@@EventId",EventId.toString())
              /* if(prefeMain.getString("event_id","")!!.equals("19"))  {

                 h1.setTextSize(19F)

                 if(r1.length>18){
                   h1.setTextSize(17F)
                 }
                 if(r1.length>30){
                   h1.setTextSize(15F)
                 }
               }*/

            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){

              Log.e("@@","Here2233")
              h1.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              val param = container_sticker.layoutParams as LinearLayout.LayoutParams

              h1.setTextSize(15F)
              if(r1.length>=12){
                h1.setTextSize(13F)
              }
              if(r1.length>20){
                h1.setTextSize(12F)
              }
            }
            else{
              Log.e("@@@length",r1.length.toString())
              h1.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              view.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s

              h1.setTextSize(22F)

              if(r1.length>11){
                h1.setTextSize(15F)
              }

              if(r1.length>15){
                Log.e("@@size14",r1.length.toString())
                h1.setTextSize(13F)
              }

              if(r1.length>20){
                Log.e("@@size145",r1.length.toString())
                h1.setTextSize(11F)
              }
              Log.e("@@size156",r1.length.toString())

              var EventId = prefeMain.getString("event_id","")
              Log.e("@@@EventId",EventId.toString())
            }

            container_sticker.addView(view_h1)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_mobile,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(model.contains(Constant.SUNMI_V2_PLUS_MODEL)|| model.contains(Constant.SUNMI_K2MINI) ){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){
              // llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              /*val param = llqr.layoutParams as RelativeLayout.LayoutParams
              param.setMargins(-15,0,0,0)
              llqr.layoutParams = param*/
            }
            else
            {
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
            }
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{

          Log.e("@@TRACK1","else.."+r1)
        }


        if(!r2.equals("")){

          if(!Row2.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h2 =mInflater.inflate(R.layout.row_h2,null,false)
            val h2 = view_h2.findViewById<TextView>(R.id.lblprintname_sunmi_h2)

            font!!.Fonts(h2,"r2")

            h2.text = r2.toUpperCase()

            if(model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              h2.minWidth = R.dimen.dimen_text_Sunmmi_v2s
              h2.setTextSize(16F)
              if(r2.length>15){
                h2.setTextSize(13F)
              }
              if(r2.length>20){
                h2.setTextSize(12F)
              }
              if(r2.length>23){
                h2.setTextSize(11F)
              }

              if(r1.length>13){
                h2.setTextSize(13F)
              }


              /* if(prefeMain.getString("event_id","")!!.equals("19"))  {
                 h2.setTextSize(17F)

                 if(r2.length>15){
                   h2.setTextSize(15F)
                 }
                 if(r2.length>30){
                   h2.setTextSize(14F)
                 }


               }*/

            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){
              h2.minWidth = R.dimen.dimen_text_Sunmmi_v2s
              val param = h2.layoutParams as ConstraintLayout.LayoutParams
              param.setMargins(-1,0,0,0)
              h2.layoutParams = param

              h2.setTextSize(13F)
              if(r2.length>13){
                h2.setTextSize(12F)
              }
              if(r2.length>17){
                h2.setTextSize(11F)
              }

              if(r2.length>22){
                h2.setTextSize(10F)
              }

              if(r1.length>=12){
                h2.setTextSize(12F)
              }
              if(r1.length>=15){
                h2.setTextSize(11F)
              }
              if(r1.length>=20){
                h2.setTextSize(10F)
              }
            }
            else
            {
              h2.minWidth = R.dimen.dimen_text_Sunmmi_v2s
              h2.setTextSize(16F)
              if(r2.length>15){
                h2.setTextSize(13F)
              }
              if(r2.length>20){
                Log.e("@@size123",r1.length.toString())
                h2.setTextSize(12F)
              }
              if(r2.length>23){
                h2.setTextSize(11F)
              }

              if(r1.length>13){
                Log.e("@@size12",r1.length.toString())
                h2.setTextSize(12F)
              }

              Log.e("@@size1",r1.length.toString())
            }



            container_sticker.addView(view_h2)
          }else {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_mobile,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){
              // llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
              /* val param = llqr.layoutParams as RelativeLayout.LayoutParams
               param.setMargins(-15,0,0,0)
               llqr.layoutParams = param*/
            }
            else
            {
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
            }
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }


        if(!r3.equals("")){

          if(!Row3.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h3 =mInflater.inflate(R.layout.row_h3,null,false)
            val h3 = view_h3.findViewById<TextView>(R.id.lblPrintDesignation_sunmi)

            font!!.Fonts(h3,"r3")

            h3.text = r3.toUpperCase()


            if(model.contains(Constant.SUNMI_V2_PLUS_MODEL)|| model.contains(Constant.SUNMI_K2MINI) ){
              h3.minWidth = R.dimen.dimen_text_Sunmmi_v2s
              h3.setTextSize(14F)
              if(r3.length>15){
                h3.setTextSize(12F)
              }
              if(r3.length>25){
                h3.setTextSize(11F)
              }

            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){
              h3.minWidth = R.dimen.dimen_text_Mobile
              val param = h3.layoutParams as ConstraintLayout.LayoutParams
              //param.setMargins(0,0,0,0)
              h3.layoutParams = param

              Log.e("@@size",r3.length.toString())

              h3.setTextSize(12F)
              if(r3.length>15){
                h3.setTextSize(11F)
              }
              if(r3.length>20){
                h3.setTextSize(9F)
              }
            }
            else
            {
              h3.minWidth = R.dimen.dimen_text_Mobile
              h3.setTextSize(14F)
              if(r3.length>15){
                h3.setTextSize(12F)
              }
              if(r3.length>25){
                h3.setTextSize(12F)
              }
            }

            container_sticker.addView(view_h3)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_mobile,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              llqr.minimumWidth = R.dimen.dimen_text_Mobile
            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){
              //   llqr.minimumWidth = R.dimen.dimen_text_Mobile
              /* val param = llqr.layoutParams as RelativeLayout.LayoutParams
               param.setMargins(-15,0,0,0)
               llqr.layoutParams = param*/
            }
            else
            {
              llqr.minimumWidth = R.dimen.dimen_text_Mobile
            }
            unique_code.visibility = View.VISIBLE
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }

        if(!r4.equals("")){

          if(!Row4.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            font!!.Fonts(h4,"r4")
            h4.text = r4.toUpperCase()
            if(model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              h4.minWidth = R.dimen.dimen_text_Mobile
              h4.setTextSize(13F);
              if(r4.length>15){
                h4.setTextSize(12F);
              }

            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){
              h4.minWidth = R.dimen.dimen_text_Mobile
              val param = h4.layoutParams as ConstraintLayout.LayoutParams
              param.setMargins(-15,0,0,0)
              h4.layoutParams = param

              h4.setTextSize(12F)
              if(r4.length>15){
                h4.setTextSize(10F)
              }
              if(r4.length>20){
                h4.setTextSize(9F)
              }
            }
            else
            {
              h4.minWidth = R.dimen.dimen_text_Mobile
              h4.setTextSize(13F);
              if(r4.length>15){
                h4.setTextSize(12F);
              }
            }
            container_sticker.addView(view_h4)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_mobile,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(PRINTER_TYPE.equals("TSC")||model.contains(Constant.SUNMI_V2_PLUS_MODEL)|| model.contains(Constant.SUNMI_K2MINI) ){
              llqr.minimumWidth = R.dimen.dimen_text_Mobile
            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){
              // llqr.minimumWidth = R.dimen.dimen_text_Mobile
              /*   val param = llqr.layoutParams as RelativeLayout.LayoutParams
                 param.setMargins(-15,0,0,0)
                 llqr.layoutParams = param*/
            }
            else
            {
              llqr.minimumWidth = R.dimen.dimen_text_Mobile
            }
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }



        if(!r5.equals("")){

          if(!Row5.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            font!!.Fonts(h4,"r4")
            h4.text = r5.toUpperCase()
            if(model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              h4.minWidth = R.dimen.dimen_text_Mobile
              h4.setTextSize(12F);
              if(r5.length>15){
                h4.setTextSize(12F);
              }

            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){
              h4.minWidth = R.dimen.dimen_text_Mobile
              /* val param = h4.layoutParams as ConstraintLayout.LayoutParams
               param.setMargins(-25,0,0,0)
               h4.layoutParams = param*/
              h4.setTextSize(11F)
              if(r5.length>15){
                h4.setTextSize(10F);
              }
              if(r5.length>20){
                h4.setTextSize(9F);
              }
            }
            else
            {
              h4.minWidth = R.dimen.dimen_text_Mobile
              h4.setTextSize(12F);
              if(r5.length>15){
                h4.setTextSize(12F);
              }
            }
            container_sticker.addView(view_h4)
          }else {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_mobile,null,false)

            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            if(model.contains(Constant.SUNMI_V2_PLUS_MODEL) || model.contains(Constant.SUNMI_K2MINI)){
              llqr.minimumWidth = R.dimen.dimen_text_Mobile
            }else if(!appUtils.BrandMan(context).toString().contains("SUNMI",ignoreCase = true)){
              llqr.minimumWidth = R.dimen.dimen_text_Mobile
              /* val param = llqr.layoutParams as RelativeLayout.LayoutParams
               param.setMargins(-15,0,0,0)
               llqr.layoutParams = param*/
            }
            else
            {
              llqr.minimumWidth = R.dimen.dimen_text_Sunmmi_v2s
            }
            font!!.Fonts(unique_code,"r5")
            unique_code.text  = splitedcode
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }

      }
      catch (e:Exception) {
      }

    }

    fun coommonsunmiTSC(type:String, context : Context, view : RelativeLayout, code : String, r1 : String, r2 : String, r3 : String, r4 : String, r5 : String, model :String)
    {
      Log.e("WWW","coming")
      try {
        mcontext =context
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var   prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)
        var Row1 = "name"
        var Row2 = "company"
        var Row3 = "unique_code"
        var Row4 = ""
        var Row5 = ""



        var splitedcode = ""

        if (code.contains("BEGIN:VCARD")) {
          val arrOfStr: Array<String> = code.split("UC:".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
          splitedcode = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }
          Log.e("SEarch key", splitedcode)
        }else{
          splitedcode =code
        }
        var container_sticker:LinearLayout?=null

        val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
        if(!PRINTER_TYPE.equals("TSC") && !PRINTER_TYPE.equals("TVS") &&  model.contains(Constant.SUNMI_K2MINI))
        {
          val HeightPrint2 = prefeMain.getInt("HeightPrint", 0)
          if(HeightPrint2==0 || HeightPrint2==1)
          {
            container_sticker = view!!.findViewById(R.id.container_stickek2Mini);

          }
          else
          {

            container_sticker = view!!.findViewById(R.id.container_stickek2Mininew);

          }


          val linearLayoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
          )


          Log.e("HeightPrint2",""+HeightPrint2)
          // 350 pe 410 380/0 pe 350 410 pe 380
          if (HeightPrint2 == 1  || HeightPrint2 == 0) {
            linearLayoutParams.setMargins(27,-30,0,0)

          }
          else if (HeightPrint2 == 2  ) {
            linearLayoutParams.setMargins(27,-30,0,0)
          }



          // container_sticker.setLayoutParams(linearLayoutParams)
        }
        else{
          container_sticker = view!!.findViewById(R.id.container_stickerr);
        }

        if(PRINTER_TYPE.equals("TVS")&&model.contains(Constant.SUNMI_K2MINI)){
          val param = container_sticker.layoutParams as LinearLayout.LayoutParams
          param.setMargins(-25,0,0,0)
          container_sticker.layoutParams = param
        }

        container_sticker.removeAllViews()

        var bmp: Bitmap? = null
        val writer = QRCodeWriter()
        try {
          val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512)
          val width = bitMatrix.width
          val height = bitMatrix.height
          bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
          for (x in 0 until width) {
            for (y in 0 until height) {
              bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
          }

        } catch (e: WriterException) {
          e.printStackTrace()
          Log.e("@@ nit Error", e.message!!)
        }
        val tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
          .toString() + "/" + "Print" + "/"
        val wallpaperDirectory = File(tempDir)
        if (!wallpaperDirectory.exists()) {
          wallpaperDirectory.mkdirs()
        }
        val outputnomedia = File(wallpaperDirectory, ".nomedia")
        try {
          outputnomedia.createNewFile()
        } catch (e: IOException) {
          e.printStackTrace()
        }
        val uniqueId = "IMGBADGE_" + System.currentTimeMillis().toString() + ".png"


        if(!r1.equals("")){

          if(!Row1.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h1,null,false)
            val h1 = view_h1.findViewById<TextView>(R.id.lblprintname_sunmi_h1)
            h1.text = r1.toUpperCase()

            h1.setTextSize(38F);
            if(r1.length>14){
              h1.setTextSize(34F);
            }
            if(r1.length>20){
              h1.setTextSize(33F);
            }
            container_sticker.addView(view_h1)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)


            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK1","else.."+r1)
        }


        if(!r2.equals("")){

          if(!Row2.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h2 =mInflater.inflate(R.layout.row_h2,null,false)
            val h2 = view_h2.findViewById<TextView>(R.id.lblprintname_sunmi_h2)
            h2.text = r2.toString().toUpperCase()
            h2.setTextSize(34F);
            if(r2.length>15){
              h2.setTextSize(31F);
            }
            if(r2.length>20){
              h2.setTextSize(29F);
            }

            if(r1.length>13){
              h2.setTextSize(26F);
            }

            container_sticker.addView(view_h2)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)

            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }


        if(!r3.equals("")){

          if(!Row3.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h3 =mInflater.inflate(R.layout.row_h3,null,false)
            val h3 = view_h3.findViewById<TextView>(R.id.lblPrintDesignation_sunmi)
            h3.text = r3.toUpperCase()
            h3.setTextSize(30F);
            if(r3.length>15){
              h3.setTextSize(27F);
            }
            if(r3.length>20){
              h3.setTextSize(25F);
            }




            container_sticker.addView(view_h3)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)

            unique_code.visibility = View.VISIBLE
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }

        if(!r4.equals("")){

          if(!Row4.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            h4.text = r4.toUpperCase()
            h4.setTextSize(30F);
            if(r4.length>15){
              h4.setTextSize(25F);
            }
            if(r4.length>20){
              h4.setTextSize(24F);
            }
            container_sticker.addView(view_h4)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }



        if(!r5.equals("")){

          if(!Row5.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            h4.text = r5.toUpperCase()

            h4.setTextSize(24F);
            if(r5.length>15){
              h4.setTextSize(22F);
            }
            if(r5.length>20){
              h4.setTextSize(21F);
            }
            container_sticker.addView(view_h4)
          }else {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)

            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            val param = llqr.layoutParams as RelativeLayout.LayoutParams

            unique_code.text  = splitedcode
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }
        Log.e("@@TRack ", "Came Here")
      }
      catch (e:Exception)
      {

      }

    }



    fun coommonsunmiTSC1(type:String, context : Context, view : RelativeLayout, code : String, r1 : String, r2 : String, r3 : String, r4 : String, r5 : String, model :String)
    {
      Log.e("WWW","coming")
      try {
        mcontext =context
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var   prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)

        var Row1 = prefeMain.getString("ROW1","")
        var Row2 = prefeMain.getString("ROW2","")
        var Row3 = prefeMain.getString("ROW3","")
        var Row4 = prefeMain.getString("ROW4","")
        var Row5 = prefeMain.getString("ROW5","")



        var splitedcode = ""

        if (code.contains("BEGIN:VCARD")) {
          val arrOfStr: Array<String> = code.split("UC:".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
          splitedcode = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }
          Log.e("SEarch key", splitedcode)
        }else{
          splitedcode =code
        }
        var container_sticker:LinearLayout?=null

        val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
        if(!PRINTER_TYPE.equals("TSC") && !PRINTER_TYPE.equals("TVS") &&  model.contains(Constant.SUNMI_K2MINI))
        {
          val HeightPrint2 = prefeMain.getInt("HeightPrint", 0)
          if(HeightPrint2==0 || HeightPrint2==1)
          {
            container_sticker = view!!.findViewById(R.id.container_stickek2Mini);

          }
          else
          {

            container_sticker = view!!.findViewById(R.id.container_stickek2Mininew);

          }


          val linearLayoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
          )


          Log.e("HeightPrint2",""+HeightPrint2)
          // 350 pe 410 380/0 pe 350 410 pe 380
          if (HeightPrint2 == 1  || HeightPrint2 == 0) {
            linearLayoutParams.setMargins(27,-30,0,0)

          }
          else if (HeightPrint2 == 2  ) {
            linearLayoutParams.setMargins(27,-30,0,0)
          }



          // container_sticker.setLayoutParams(linearLayoutParams)
        }
        else{
          container_sticker = view!!.findViewById(R.id.container_stickerr);
        }

        if(PRINTER_TYPE.equals("TVS")&&model.contains(Constant.SUNMI_K2MINI)){
          val param = container_sticker.layoutParams as LinearLayout.LayoutParams
          param.setMargins(-25,0,0,0)
          container_sticker.layoutParams = param
        }

        if(PRINTER_TYPE.equals("TSC")&&model.contains(Constant.SUNMI_K2MINI)){
          val param = container_sticker.layoutParams as LinearLayout.LayoutParams
          param.setMargins(-8,0,0,0)
          container_sticker.layoutParams = param
        }





        container_sticker.removeAllViews()

        var bmp: Bitmap? = null
        val writer = QRCodeWriter()
        try {
          val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512)
          val width = bitMatrix.width
          val height = bitMatrix.height
          bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
          for (x in 0 until width) {
            for (y in 0 until height) {
              bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
          }

        } catch (e: WriterException) {
          e.printStackTrace()
          Log.e("@@ nit Error", e.message!!)
        }
        val tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
          .toString() + "/" + "Print" + "/"
        val wallpaperDirectory = File(tempDir)
        if (!wallpaperDirectory.exists()) {
          wallpaperDirectory.mkdirs()
        }
        val outputnomedia = File(wallpaperDirectory, ".nomedia")
        try {
          outputnomedia.createNewFile()
        } catch (e: IOException) {
          e.printStackTrace()
        }
        val uniqueId = "IMGBADGE_" + System.currentTimeMillis().toString() + ".png"


        if(!r1.equals("")){

          if(!Row1.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h1,null,false)
            val h1 = view_h1.findViewById<TextView>(R.id.lblprintname_sunmi_h1)
            h1.text = r1.toUpperCase()

            h1.setTextSize(30F);
            if(r1.length>11){
              h1.setTextSize(28F);
            }
            if(r1.length>20){
              h1.setTextSize(23F);
            }
            if(r1.length>24){
              h1.setTextSize(22F);
            }
            container_sticker.addView(view_h1)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)


            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK1","else.."+r1)
        }


        if(!r2.equals("")){

          if(!Row2.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h2 =mInflater.inflate(R.layout.row_h2,null,false)
            val h2 = view_h2.findViewById<TextView>(R.id.lblprintname_sunmi_h2)
            h2.text = r2.toString().toUpperCase()
            h2.setTextSize(30F);
            if(r2.length>15){
              h2.setTextSize(24F);
            }
            if(r2.length>20){
              h2.setTextSize(23F);
            }

            if(r1.length>13){
              h2.setTextSize(24F);
            }

            if(r1.length>18){
              h2.setTextSize(21F);
            }


            if(r1.length>24){
              h2.setTextSize(15F);
            }
            container_sticker.addView(view_h2)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)

            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }


        if(!r3.equals("")){

          if(!Row3.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h3 =mInflater.inflate(R.layout.row_h3,null,false)
            val h3 = view_h3.findViewById<TextView>(R.id.lblPrintDesignation_sunmi)
            h3.text = r3.toUpperCase()
            h3.setTextSize(23F);
            if(r3.length>15){
              h3.setTextSize(22F);
            }
            if(r3.length>20){
              h3.setTextSize(21F);
            }

            if(r3.length>20){
              h3.setTextSize(19F);
            }


            container_sticker.addView(view_h3)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)

            unique_code.visibility = View.VISIBLE
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }

        if(!r4.equals("")){

          if(!Row4.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            h4.text = r4.toUpperCase()
            h4.setTextSize(20F);
            if(r4.length>15){
              h4.setTextSize(18F);
            }
            if(r4.length>20){
              h4.setTextSize(17F);
            }
            container_sticker.addView(view_h4)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }



        if(!r5.equals("")){

          if(!Row5.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            h4.text = r5.toUpperCase()

            h4.setTextSize(22F);
            if(r5.length>15){
              h4.setTextSize(20F);
            }
            if(r5.length>20){
              h4.setTextSize(21F);
            }
            container_sticker.addView(view_h4)
          }else {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_tsc,null,false)

            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            val param = llqr.layoutParams as RelativeLayout.LayoutParams

            unique_code.text  = splitedcode
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }
        Log.e("@@TRack ", "Came Here")
      }
      catch (e:Exception)
      {

      }

    }




    fun coommonsunmik2Mini(type:String, context : Context, view : RelativeLayout, code : String, r1 : String, r2 : String, r3 : String, r4 : String, r5 : String, model :String)
    {
      Log.e("WWW","coming")
      try {
        mcontext =context
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var   prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)
        var Row1 = prefeMain.getString("ROW1","")
        var Row2 = prefeMain.getString("ROW2","")
        var Row3 = prefeMain.getString("ROW3","")
        var Row4 = prefeMain.getString("ROW4","")
        var Row5 = prefeMain.getString("ROW5","")
        //  var r1="ARJUNVIDYASARadssdsddasd"
        //  var r2="VNVIDYASAGARJUIdsadasd"
        //  var r3="WEbcontxt India Pvt Ltd Jaipurdasd"
        //  var r4="EEMA312312"
        //  var r5="DELAGATE"

        var splitedcode = ""

        if (code.contains("BEGIN:VCARD")) {
          val arrOfStr: Array<String> = code.split("UC:".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
          splitedcode = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }
          Log.e("SEarch key", splitedcode)
        }else{
          splitedcode =code
        }
        var container_sticker:LinearLayout?=null

        val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
        if(!PRINTER_TYPE.equals("TSC") && !PRINTER_TYPE.equals("TVS") &&  model.contains(Constant.SUNMI_K2MINI))
        {
          val HeightPrint2 = prefeMain.getInt("HeightPrint", 0)
          if(HeightPrint2==0 || HeightPrint2==1)
          {
            container_sticker = view!!.findViewById(R.id.container_stickek2Mini);

          }
          else
          {

            container_sticker = view!!.findViewById(R.id.container_stickek2Mininew);

          }


          val linearLayoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
          )


          Log.e("HeightPrint2",""+HeightPrint2)
          // 350 pe 410 380/0 pe 350 410 pe 380
          if (HeightPrint2 == 1  || HeightPrint2 == 0) {
            linearLayoutParams.setMargins(27,-30,0,0)

          }
          else if (HeightPrint2 == 2  ) {
            linearLayoutParams.setMargins(27,-30,0,0)
          }



          // container_sticker.setLayoutParams(linearLayoutParams)
        }
        else{
          container_sticker = view!!.findViewById(R.id.container_stickerr);
        }

        if(PRINTER_TYPE.equals("TVS")&&model.contains(Constant.SUNMI_K2MINI)){
          val param = container_sticker.layoutParams as LinearLayout.LayoutParams
          param.setMargins(-25,0,0,0)
          container_sticker.layoutParams = param
        }

        container_sticker.removeAllViews()

        var bmp: Bitmap? = null
        val writer = QRCodeWriter()
        try {
          val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512)
          val width = bitMatrix.width
          val height = bitMatrix.height
          bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
          for (x in 0 until width) {
            for (y in 0 until height) {
              bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
          }

        } catch (e: WriterException) {
          e.printStackTrace()
          Log.e("@@ nit Error", e.message!!)
        }
        val tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
          .toString() + "/" + "Print" + "/"
        val wallpaperDirectory = File(tempDir)
        if (!wallpaperDirectory.exists()) {
          wallpaperDirectory.mkdirs()
        }
        val outputnomedia = File(wallpaperDirectory, ".nomedia")
        try {
          outputnomedia.createNewFile()
        } catch (e: IOException) {
          e.printStackTrace()
        }
        val uniqueId = "IMGBADGE_" + System.currentTimeMillis().toString() + ".png"

        val mInflater2 = LayoutInflater.from(context)
        val view_h12 =mInflater2.inflate(R.layout.row_h1,null,false)
        val h12 = view_h12.findViewById<TextView>(R.id.lblprintname_sunmi_h1)
        val constraintLayout = view_h12.findViewById<ConstraintLayout>(R.id.constraintLayout)
        h12.text = "ARJUN SA"
        constraintLayout.visibility=View.INVISIBLE
        h12.setTextSize(58F);

        container_sticker.addView(view_h12)



        if(!r1.equals("")){

          if(!Row1.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h1,null,false)
            val h1 = view_h1.findViewById<TextView>(R.id.lblprintname_sunmi_h1)
            h1.text = r1.toUpperCase()
            h1.setTextSize(38F);
            if(r1.length>14){
              h1.setTextSize(34F);
            }
            if(r1.length>20){
              h1.setTextSize(25F);
            }
            //view_h1.layoutParams=layoutParams
            container_sticker.addView(view_h1)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_kmini,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)


            ivQr_sunmi.setImageBitmap(bmp)
            //view_h1.layoutParams=layoutParams
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK1","else.."+r1)
        }


        if(!r2.equals("")){

          if(!Row2.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h2 =mInflater.inflate(R.layout.row_h2,null,false)
            val h2 = view_h2.findViewById<TextView>(R.id.lblprintname_sunmi_h2)
            h2.text = r2.toString().toUpperCase()
            h2.setTextSize(34F);
            if(r2.length>15){
              h2.setTextSize(30F);
            }
            if(r2.length>20){
              h2.setTextSize(23F);
            }
            if(r1.length>13){
              h2.setTextSize(20F)
            }
            // view_h2.layoutParams=layoutParams

            container_sticker.addView(view_h2)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_kmini,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)

            ivQr_sunmi.setImageBitmap(bmp)
            // view_h1.layoutParams=layoutParams
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }


        if(!r3.equals("")){

          if(!Row3.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h3 =mInflater.inflate(R.layout.row_h3_mini,null,false)
            val h3 = view_h3.findViewById<TextView>(R.id.lblPrintDesignation_sunmi)
            h3.text = r3.toUpperCase()
            h3.setTextSize(30F);
            if(r3.length>15){
              h3.setTextSize(26F);
            }
            if(r3.length>20){
              h3.setTextSize(19F);
            }


            // view_h3.layoutParams=layoutParams

            container_sticker.addView(view_h3)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_kmini,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)

            unique_code.visibility = View.VISIBLE
            ivQr_sunmi.setImageBitmap(bmp)
            // view_h1.layoutParams=layoutParams
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }

        if(!r4.equals("")){

          if(!Row4.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            h4.text = r4.toUpperCase()
            h4.setTextSize(30F);
            if(r4.length>15){
              h4.setTextSize(25F);
            }
            if(r4.length>20){
              h4.setTextSize(21F);
            }
            //  view_h4.layoutParams=layoutParams
            container_sticker.addView(view_h4)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_kmini,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            ivQr_sunmi.setImageBitmap(bmp)
            // view_h1.layoutParams=layoutParams
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }



        if(!r5.equals("")){

          if(!Row5.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            h4.text = r5.toUpperCase()

            h4.setTextSize(24F);
            if(r5.length>15){
              h4.setTextSize(22F);
            }
            if(r5.length>20){
              h4.setTextSize(19F);
            }
            /// view_h4.layoutParams=layoutParams
            container_sticker.addView(view_h4)
          }else {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_kmini,null,false)

            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            val param = llqr.layoutParams as RelativeLayout.LayoutParams

            unique_code.text  = splitedcode
            ivQr_sunmi.setImageBitmap(bmp)
            //  view_h1.layoutParams=layoutParams
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }
        Log.e("@@TRack ", "Came Here")
      }
      catch (e:Exception)
      {

      }

    }
    fun sunmiPrinting(type:String, context : Context, view : RelativeLayout, code : String, r1 : String, r2 : String, r3 : String, r4 : String, r5 : String, model :String){

      try{
        var   prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)

        val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
        val PRINTER_TYPE_TSC = prefeMain.getString("PRINTER_TYPE_TSC", "")
        val deviceAddress = prefeMain.getString("deviceAddress", "")
        //    rl_badgeprint.setDrawingCacheEnabled(true)
        // rl_badgeprint.buildDrawingCache()
        Log.e("@@TRack ", "Came Here")
        /*  var r1="Virendrakumar Tanwarsaini"
          var r2="Webcontxt India Private limited"
          var r3="Android developer"
          var r4="EEEMA23123123"
          var r5="EEEMA23123123"*/

        if(PRINTER_TYPE!!.contains("Sunmi",ignoreCase = true) && model.contains(Constant.SUNMI_K2MINI))
        {
          coommonsunmik2Mini(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
        }

        else  if(model.contains(Constant.SUNMI_K2MINI))
        {

          coommonsunmiTSC(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
        }
        else
        {
          coommonsunmi(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
        }
        // coommonsunmi(type, context , view , code , r1 , r2 , r3 , r4 , r5 , model )
        var rl_badgeprint:RelativeLayout = view

        Log.e("rl_badgeprintW: ",""+rl_badgeprint.width)
        Log.e("rl_badgeprintH:",""+rl_badgeprint.height)

        /* if(rl_badgeprint.width==0|| rl_badgeprint.height==0)
         {
           Toast.makeText(context,"Please try again for print ",Toast.LENGTH_LONG).show()
           return
         }*/
        Log.e("@@PRINTER_TYPE ",""+PRINTER_TYPE+" Maon "+appUtils.BrandName(context).toString())
        if(PRINTER_TYPE!!.contains("Sunmi",ignoreCase = true) && appUtils.BrandName(context).toString().contains(Constant.SUNMI_K2MINI)) {
          val scale: Float = context.getResources().getDisplayMetrics().density

          val HeightPrint = prefeMain.getInt("HeightPrint", 0)
          Log.e("@@height ",""+rl_badgeprint.height+"@@widht "+rl_badgeprint.width)
          var height = 1
          // 350 pe 410 380/0 pe 350 410 pe 380
          if (HeightPrint == 1|| HeightPrint==0 ) {
            height = 2
          }
          else if (HeightPrint == 2 ) {
            height = 1
          }

          Log.e("@@HeightPrint ",""+HeightPrint+" height "+height)
          val px = (height * scale + 0.5f).toInt() // replace 100 with your dimensions
          val pxhiehgt = (200 * scale + 0.5f).toInt() // replace 100 with your dimensions

          // rl_badgeprint.layoutParams.height = pxhiehgt
          // rl_badgeprint.layoutParams.width = pxhiehgt
          var prefeMainedit = context.getSharedPreferences("FRIENDS", AppCompatActivity.MODE_PRIVATE)
          val editor = prefeMainedit.edit()
          editor.putInt("HeightPrint", height)
          editor.commit()
        }
        rl_badgeprint.isDrawingCacheEnabled = true
        rl_badgeprint.buildDrawingCache()
        val bitmap_: Bitmap? =
          drawToBitmap(rl_badgeprint,
            rl_badgeprint.getWidth(),
            rl_badgeprint.getHeight()
          )
        rl_badgeprint.isDrawingCacheEnabled = true
        Log.e("@@Bitmap *****", bitmap_.toString())
        Handler().postDelayed({



          if (!BluetoothUtil.isBlueToothPrinter) {

            rl_badgeprint.setDrawingCacheEnabled(true)
            rl_badgeprint.buildDrawingCache()
            val bitmap_: Bitmap? = drawToBitmap(
              rl_badgeprint,
              rl_badgeprint.getWidth(),
              rl_badgeprint.getHeight()
            )
            rl_badgeprint.setDrawingCacheEnabled(true)

            if (PRINTER_TYPE_TSC.equals( "Bluetooth")) {
              var TscDll = TSCActivity()
              Thread {
                Log.e("@@Permision", "Check")
                // Ouverture du socket Bluetooth et connexion à l'appareil
                if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                  ) == PackageManager.PERMISSION_DENIED
                ) {
                  Log.e("@@Permision", "Denied")
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(
                      context!! as Activity,
                      arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                      2
                    )
                    // return;
                  }
                }
                Log.e("@@Permision", "Allow")
                try {

                  TscDll.openport(deviceAddress) //BT
                  TscDll.setup(80, 70, 4, 15, 1, 1, 1)
                  // Log.e("@@@STATUS",TscDll.printerstatus(300));
                  TscDll.sendbitmap(if(model.contains(Constant.SUNMI_V2s_STGL_MODEL)) 82 else 40, 0, bitmap_)

                  TscDll.sendcommand(
                    """
            PUTBMP 20,20,"${bitmap_},SET CUTTER 1"
            
            """.trimIndent()

                  )
                  TscDll.printlabel(1, 1)

                  //   TscDll.sendcommand("CUT\r\n");
                  TscDll.closeport(1000)
                } catch (e: java.lang.Exception) {
                  Log.e("@@EX", e.message!!)
                }
              }.start()
            }


            else if(model.contains(Constant.SUNMI_K2MINI))
            {
              /* SunmiPrintHelper.instance.setAlign(1)
               SunmiPrintHelper.instance.printBitmapWithOut(bitmap_,0)
                SunmiPrintHelper.instance.cutpaper()*/
              SunmiPrintHelper.instance.setAlign(1)
              SunmiPrintHelper.instance.sendRawData(ESCUtil.labellocate());
              SunmiPrintHelper.instance.printBitmap(bitmap_,0);
              SunmiPrintHelper.instance.sendRawData(ESCUtil.labelout());
              SunmiPrintHelper.instance.cutpaper();



            }
            else
            {
              SunmiPrintHelper.instance.sendRawData(ESCUtil.labellocate());
              SunmiPrintHelper.instance.printBitmap(bitmap_,0);
              SunmiPrintHelper.instance.sendRawData(ESCUtil.labelout());
              // SunmiPrintHelper.instance.cutpaper();
              // SunmiPrintHelper.getInstance().cutpaper();

              //   SunmiPrintHelper!!.instance.printOneLabel(bitmap_,context)

              /* SunmiPrintHelper.instance.initSunmiPrinterService(mcontext)
               SunmiPrintHelper.instance.initPrinter()*/
              /*  Log.e("@@Myyyy_TRAck","here22222")
                if(SunmiPrintHelper!!.instance!!.isLabelMode && SunmiPrintHelper!!.instance!!.printerPaper.equals(if(model.equals(Constant.SUNMI_V2_PLUS_MODEL))"80mm" else "58mm"))
                {
                  SunmiPrintHelper!!.instance.printOneLabel(bitmap_,context)
                }
                else
                {
                  Toast.makeText(context,"Please select label mode and Correct Paper( ${if(model.equals(Constant.SUNMI_V2_PLUS_MODEL))"80mm" else "58mm"}) size",Toast.LENGTH_LONG).show()
                }
  */

            }

            var prefeMain = context.getSharedPreferences("FRIENDS", Context.MODE_PRIVATE)
            var eventid=prefeMain.getString("event_id","")

            if(eventid.equals("38"))
            {
              Handler().postDelayed({
                if(type.equals("spot",ignoreCase = true)){
                  val intent = Intent(context, ChooseCategoryActivity::class.java)
                  context!!.startActivity(intent)
                  var activity :Activity = context as Activity
                  activity.finish()
                }
              }, 600L)
            }else{
              Handler().postDelayed({
                if(type.equals("spot",ignoreCase = true)){
                  val intent = Intent(context, ChooseCategoryActivity::class.java)
                  context!!.startActivity(intent)
                  var activity :Activity = context as Activity
                  activity.finish()
                }
              }, 600L)
            }

          }
        }, 500L)

      } catch (e: Exception) {
        Log.e("@@ error ", e.message!!)
      }
    }

    fun BROTHER_PRINTING(type:String, context : Context, view : RelativeLayout, code : String, r1 : String, r2 : String, r3 : String, r4 : String, r5 : String, model :String){
      try {

        mcontext =context
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var   prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)
        var Row1 = prefeMain.getString("ROW1","")
        var Row2 = prefeMain.getString("ROW2","")
        var Row3 = prefeMain.getString("ROW3","")
        var Row4 = prefeMain.getString("ROW4","")
        var Row5 = prefeMain.getString("ROW5","")

        var splitedcode = ""

        if (code.contains("BEGIN:VCARD")) {
          val arrOfStr: Array<String> = code.split("UC:".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
          splitedcode = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }
          Log.e("SEarch key", splitedcode)
        }else{
          splitedcode =code
        }

        val container_sticker: LinearLayout = view!!.findViewById(R.id.container_stickerr);
        container_sticker.removeAllViews()

        var bmp: Bitmap? = null
        val writer = QRCodeWriter()
        try {
          val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512)
          val width = bitMatrix.width
          val height = bitMatrix.height
          bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
          for (x in 0 until width) {
            for (y in 0 until height) {
              bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
          }

        } catch (e: WriterException) {
          e.printStackTrace()
          Log.e("@@ nit Error", e.message!!)
        }
        val tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
          .toString() + "/" + "Print" + "/"
        val wallpaperDirectory = File(tempDir)
        if (!wallpaperDirectory.exists()) {
          wallpaperDirectory.mkdirs()
        }
        val outputnomedia = File(wallpaperDirectory, ".nomedia")
        try {
          outputnomedia.createNewFile()
        } catch (e: IOException) {
          e.printStackTrace()
        }
        val uniqueId = "IMGBADGE_" + System.currentTimeMillis().toString() + ".png"


        if(!r1.equals("")){

          if(!Row1.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h1,null,false)
            val h1 = view_h1.findViewById<TextView>(R.id.lblprintname_sunmi_h1)
            h1.text = r1.toUpperCase()

            h1.setTextSize(48F);
            if(r1.length>14){
              h1.setTextSize(38F);
            }
            if(r1.length>20){
              h1.setTextSize(35F);
            }
            container_sticker.addView(view_h1)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_brother,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)


            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK1","else.."+r1)
        }


        if(!r2.equals("")){

          if(!Row2.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h2 =mInflater.inflate(R.layout.row_h2,null,false)
            val h2 = view_h2.findViewById<TextView>(R.id.lblprintname_sunmi_h2)
            h2.text = r2.toString().toUpperCase()
            h2.setTextSize(38F);
            if(r2.length>15){
              h2.setTextSize(33F);
            }
            if(r2.length>20){
              h2.setTextSize(30F);
            }



            container_sticker.addView(view_h2)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_brother,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)

            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }


        if(!r3.equals("")){

          if(!Row3.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h3 =mInflater.inflate(R.layout.row_h3,null,false)
            val h3 = view_h3.findViewById<TextView>(R.id.lblPrintDesignation_sunmi)
            h3.text = r3.toUpperCase()
            h3.setTextSize(34F);
            if(r3.length>15){
              h3.setTextSize(29F);
            }
            if(r3.length>20){
              h3.setTextSize(25F);
            }




            container_sticker.addView(view_h3)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_brother,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)

            unique_code.visibility = View.VISIBLE
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }

        if(!r4.equals("")){

          if(!Row4.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            h4.text = r4.toUpperCase()
            h4.setTextSize(30F);
            if(r4.length>15){
              h4.setTextSize(25F);
            }
            if(r4.length>20){
              h4.setTextSize(24F);
            }
            container_sticker.addView(view_h4)
          }else
          {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_brother,null,false)
            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            unique_code.text  = splitedcode
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }



        if(!r5.equals("")){

          if(!Row5.equals("unique_code")){
            val mInflater = LayoutInflater.from(context)
            val view_h4 =mInflater.inflate(R.layout.row_h4,null,false)
            val h4 = view_h4.findViewById<TextView>(R.id.lblprintCompany_sunmi)
            h4.text = r5.toUpperCase()

            h4.setTextSize(24F);
            if(r5.length>15){
              h4.setTextSize(22F);
            }
            if(r5.length>20){
              h4.setTextSize(21F);
            }
            container_sticker.addView(view_h4)
          }else {
            val mInflater = LayoutInflater.from(context)
            val view_h1 =mInflater.inflate(R.layout.row_h5_brother,null,false)

            val ivQr_sunmi = view_h1.findViewById<ImageView>(R.id.ivQr_sunmi)
            val unique_code = view_h1.findViewById<TextView>(R.id.qrcode_assigned_sunmi)
            val llqr = view_h1.findViewById<LinearLayout>(R.id.llqr)
            val param = llqr.layoutParams as RelativeLayout.LayoutParams

            unique_code.text  = splitedcode
            ivQr_sunmi.setImageBitmap(bmp)
            container_sticker.addView(view_h1)
          }
        }else{
          Log.e("@@TRACK2","else.."+r2)
        }
        Log.e("@@TRack ", "Came Here")
        var rl_badgeprint:RelativeLayout = view
        rl_badgeprint.setDrawingCacheEnabled(true)
        rl_badgeprint.buildDrawingCache()
        val bitmap_: Bitmap? = drawToBitmap(
          rl_badgeprint,
          rl_badgeprint.getWidth(),
          rl_badgeprint.getHeight()
        )
        rl_badgeprint.setDrawingCacheEnabled(true)
        Log.e("@@Bitmap Brother>>>", bitmap_.toString())
        val sweetAlertDialogPrint = SweetAlertDialog(context)
        sweetAlertDialogPrint.setCancelable(false)
        sweetAlertDialogPrint.setTitleText("Do you want to Print?")
          .setContentText("Click to print")
          .show()
        sweetAlertDialogPrint.setCancelButton("No") { // reset Screen ....
          sweetAlertDialogPrint.cancel()
        }
        sweetAlertDialogPrint.setConfirmClickListener {
          sweetAlertDialogPrint.cancel()
          Handler().postDelayed({

            Printing(view,context)

          }, 500L)
        }

      } catch (e: Exception) {
        Log.e("@@ error ", e.message!!)
      }
    }


    fun PxToDP(px: Float, context: Context): Float {
      val scale = context.resources.displayMetrics.density
      return  (px-0.5f/scale).toFloat()
    }

    fun pxFromDp(context: Context, dp: Float): Float {
      return dp * context.resources.displayMetrics.density
    }



    fun drawToBitmap(viewToDrawFrom: View?, width: Int, height: Int): Bitmap? {
      var width = width
      var height = height
      val wasDrawingCacheEnabled = viewToDrawFrom!!.isDrawingCacheEnabled
      if (!wasDrawingCacheEnabled) viewToDrawFrom.isDrawingCacheEnabled = true
      if (width <= 0 || height <= 0) {
        if (viewToDrawFrom.width <= 0 || viewToDrawFrom.height <= 0) {
          viewToDrawFrom.measure(
            View.MeasureSpec.makeMeasureSpec(
              0,
              View.MeasureSpec.UNSPECIFIED
            ), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
          )
          width = viewToDrawFrom.measuredWidth
          height = viewToDrawFrom.measuredHeight
        }
        if (width <= 0 || height <= 0) {
          val bmp = viewToDrawFrom.drawingCache
          val result = if (bmp == null) null else Bitmap.createBitmap(bmp)
          if (!wasDrawingCacheEnabled) viewToDrawFrom.isDrawingCacheEnabled = false
          return result
        }
        viewToDrawFrom.layout(0, 0, width, height)
      } else {
        viewToDrawFrom.measure(
          View.MeasureSpec.makeMeasureSpec(
            width,
            View.MeasureSpec.EXACTLY
          ), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        viewToDrawFrom.layout(
          0,
          0,
          viewToDrawFrom.measuredWidth,
          viewToDrawFrom.measuredHeight
        )
      }
      val drawingCache = viewToDrawFrom.drawingCache
      val bmp = ThumbnailUtils.extractThumbnail(drawingCache, width, height)
      val result = if (bmp == null || bmp != drawingCache) bmp else Bitmap.createBitmap(bmp)
      if (!wasDrawingCacheEnabled) viewToDrawFrom.isDrawingCacheEnabled = false
      return result
    }








    fun Printing(rl_badgeprint:RelativeLayout,context: Context) {

      val tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        .toString() + "/" + "Print" + "/"
      val wallpaperDirectory = File(tempDir)
      // have the object build the directory structure, if needed.
      if (!wallpaperDirectory.exists()) {
        wallpaperDirectory.mkdirs()
      }
      val outputnomedia = File(wallpaperDirectory, ".nomedia")
      try {
        outputnomedia.createNewFile()
      } catch (e: IOException) {
        e.printStackTrace()
      }
      val uniqueId = "IMGBADGE_" + System.currentTimeMillis().toString() + ".png"
      rl_badgeprint!!.isDrawingCacheEnabled = true
      rl_badgeprint!!.buildDrawingCache()

      // Work here for bitmap......
      val bitmap =
        drawToBitmap(rl_badgeprint, rl_badgeprint!!.width, rl_badgeprint!!.height)
      rl_badgeprint!!.isDrawingCacheEnabled = true
      Log.e("@@bitmap_jj", bitmap.toString() + "")
      mypath = File(tempDir, uniqueId)
      val s = mypath!!.absolutePath
      try {
        mypath!!.createNewFile()
        val bos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()

//write the bytes in file
        val fos = FileOutputStream(mypath)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
      } catch (e: Exception) {
        Log.e("@@@WX", e.message!!) //Nitesh TTrcked Here ...
      }

      // printTemplateSample();
      mDialog = MsgDialog(context)
      mHandle = MsgHandle(context, mDialog)
      myPrint = ImagePrint(context, mHandle, mDialog)

      // when use bluetooth print set the adapter
      val bluetoothAdapter = getBluetoothAdapter()
      myPrint!!.setBluetoothAdapter(bluetoothAdapter)


      // printTemplateSample();
      mImageFiles = ArrayList()
      mImageFiles!!.add(mypath!!.absolutePath)
      Log.e("mImageFiles", mImageFiles.toString())
      Log.e("mImageFiles", mImageFiles.toString())
      (myPrint as ImagePrint).files = mImageFiles
      // when use bluetooth print set the adapter
      if (!checkUSB(context)) return

      val printTread = V4PrinterThread(context)
      printTread.start()
    }

    private class V4PrinterThread(val context: Context) : Thread() {
      private fun waitForUSBAuthorizationRequest(port: PrinterInfo.Port) {
        if (port == PrinterInfo.Port.USB) {
          while (true) {
            if (Common.mUsbAuthorizationState != Common.UsbAuthorizationState.NOT_DETERMINED) {
              break
            }
            try {
              sleep(50)
            } catch (e: InterruptedException) {
            }
          }
        }
      }

      private fun currentPrintSettings(currentModel: PrinterModel): PrintSettings {
        val currentModelString = currentModel.name
        val gson = Gson()

        if (currentModelString.startsWith("QL")) {


          val qlPrintSettingsJson = sharedPreferences!!.getString("qlV4PrintSettings", "")
          return if (qlPrintSettingsJson === "") {
            val setting = QLPrintSettings(currentModel)
            setting.isAutoCut = true
            setting
          } else {
            gson.fromJson(qlPrintSettingsJson, QLPrintSettings::class.java)
              .copyPrintSettings(currentModel)
          }
        } else if (currentModelString.startsWith("PT")) {
          val ptPrintSettingsJson = sharedPreferences!!.getString("ptV4PrintSettings", "")
          return if (ptPrintSettingsJson === "") {
            PTPrintSettings(currentModel)
          } else {
            gson.fromJson(ptPrintSettingsJson, PTPrintSettings::class.java)
              .copyPrintSettings(currentModel)
          }
        } else if (currentModelString.startsWith("PJ")) {
          val pjPrintSettingsJson = sharedPreferences!!.getString("pjV4PrintSettings", "")
          return if (pjPrintSettingsJson === "") {
            PJPrintSettings(currentModel)
          } else {
            gson.fromJson(pjPrintSettingsJson, PJPrintSettings::class.java)
              .copyPrintSettings(currentModel)
          }
        } else if (currentModelString.startsWith("RJ")) {
          val rjPrintSettingsJson = sharedPreferences!!.getString("rjV4PrintSettings", "")
          return if (rjPrintSettingsJson === "") {
            RJPrintSettings(currentModel)
          } else {
            gson.fromJson(rjPrintSettingsJson, RJPrintSettings::class.java)
              .copyPrintSettings(currentModel)
          }
        } else if (currentModelString.startsWith("TD")) {
          val tdPrintSettingsJson = sharedPreferences!!.getString("tdV4PrintSettings", "")
          return if (tdPrintSettingsJson === "") {
            TDPrintSettings(currentModel)
          } else {
            gson.fromJson(tdPrintSettingsJson, TDPrintSettings::class.java)
              .copyPrintSettings(currentModel)
          }
        } else if (currentModelString.startsWith("MW")) {
          val mwPrintSettingsJson = sharedPreferences!!.getString("mwV4PrintSettings", "")
          return if (mwPrintSettingsJson === "") {
            MWPrintSettings(currentModel)
          } else {
            gson.fromJson(mwPrintSettingsJson, MWPrintSettings::class.java)
              .copyPrintSettings(currentModel)
          }
        }
        return QLPrintSettings(currentModel)
      }

      override fun run() {
        val model = PrinterInfo.Model.valueOf(
          sharedPreferences!!.getString("printerModel", "")!!
        )
        val port = PrinterInfo.Port.valueOf(
          sharedPreferences!!.getString("port", "")!!
        )
        val ipAddress = sharedPreferences!!.getString("address", "")
        val macAddress = sharedPreferences!!.getString("macAddress", "")
        val localName = sharedPreferences!!.getString("localName", "")
        waitForUSBAuthorizationRequest(port)
        val channel: Channel
        channel = when (port) {
          PrinterInfo.Port.BLUETOOTH -> Channel.newBluetoothChannel(
            macAddress,
            BluetoothAdapter.getDefaultAdapter()
          )
          PrinterInfo.Port.BLE -> Channel.newBluetoothLowEnergyChannel(
            localName,
            context,
            BluetoothAdapter.getDefaultAdapter()
          )
          PrinterInfo.Port.USB -> Channel.newUsbChannel(context.getSystemService(USB_SERVICE) as UsbManager)
          PrinterInfo.Port.NET -> Channel.newWifiChannel(ipAddress)
          else -> return
        }
        // start message
        var msg = mHandle!!.obtainMessage(Common.MSG_PRINT_START)
        mHandle!!.sendMessage(msg)

        // Create a `PrinterDriver` instance
        val result = PrinterDriverGenerator.openChannel(channel)
        if (result.error.code != OpenChannelError.ErrorCode.NoError) {
          mHandle!!.setResult(result.error.code.toString())
          mHandle!!.sendMessage(mHandle!!.obtainMessage(Common.MSG_PRINT_END))
          return
        }
        val printerDriver = result.driver
        val gson = Gson()
        val v4model = PrinterModel.valueOf(model.toString())

        // Initialize `PrintSettings`
        val printSettings = currentPrintSettings(v4model)
        val FilePaths = mImageFiles!!.toTypedArray()
        // Print the image
        val printError = printerDriver.printImage(FilePaths, printSettings)
        if (printError.code != PrintError.ErrorCode.NoError) {
          printerDriver.closeChannel()
          mHandle!!.setResult(printError.code.toString())
          mHandle!!.sendMessage(mHandle!!.obtainMessage(Common.MSG_PRINT_END))
          return
        }
        printerDriver.closeChannel()

        // end message
        mHandle!!.setResult("Success")
        msg = mHandle!!.obtainMessage(Common.MSG_PRINT_END)
        mHandle!!.sendMessage(msg)
      }
    }

    val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
      @TargetApi(12)
      override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (ACTION_USB_PERMISSION == action) {
          synchronized(this) {
            if (intent.getBooleanExtra(
                UsbManager.EXTRA_PERMISSION_GRANTED, false
              )
            ) Common.mUsbAuthorizationState =
              Common.UsbAuthorizationState.APPROVED else Common.mUsbAuthorizationState =
              Common.UsbAuthorizationState.DENIED
          }
        }
      }
    }

    @SuppressLint("MissingPermission")
    open fun getBluetoothAdapter(): BluetoothAdapter? {
      val bluetoothAdapter = BluetoothAdapter
        .getDefaultAdapter()
      if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
        val enableBtIntent = Intent(
          BluetoothAdapter.ACTION_REQUEST_ENABLE
        )
        enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mcontext.startActivity(enableBtIntent)
      }
      return bluetoothAdapter
    }

    @TargetApi(12)
    open fun checkUSB(context: Context): Boolean {
      if (myPrint!!.printerInfo.port != PrinterInfo.Port.USB) {
        return true
      }
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
        val msg = mHandle!!.obtainMessage(Common.MSG_WRONG_OS)
        //  mHandle.sendMessage(msg);
        return false
      }
      val usbManager = context.getSystemService(USB_SERVICE) as UsbManager
      val usbDevice = myPrint!!.getUsbDevice(usbManager)
      if (usbDevice == null) {

        //  Log.e("@@mHandle",mHandle==null?"n":"u");
        if (mHandle == null) {
          return false
        }
        val msg = mHandle!!.obtainMessage(Common.MSG_NO_USB)
        //  mHandle.sendMessage(msg);
        return false
      }
      val permissionIntent = PendingIntent.getBroadcast(
        context, 0, Intent(ACTION_USB_PERMISSION), 0)
      context.registerReceiver(mUsbReceiver, IntentFilter(ACTION_USB_PERMISSION))
      if (!usbManager.hasPermission(usbDevice)) {
        Common.mUsbAuthorizationState = Common.UsbAuthorizationState.NOT_DETERMINED
        usbManager.requestPermission(usbDevice, permissionIntent)
      } else {
        Common.mUsbAuthorizationState = Common.UsbAuthorizationState.APPROVED
      }
      return true
    }




    fun PrintShare_Printhand_Printing(webView: WebView,context: Activity,url:String,type:String){

      webView.settings.javaScriptEnabled = true
      webView.loadUrl(url)
      webView.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
          view: WebView,
          urlNewString: String
        ): Boolean {
          view.loadUrl(urlNewString)
          return true
        }

        override fun onPageStarted(view: WebView, url: String, facIcon: Bitmap?) {

          //SHOW LOADING IF IT ISNT ALREADY VISIBLE
        }

        override fun onPageFinished(view: WebView, url: String) {
          Log.e("@@loaded url", url)
          val sweetAlertDialog = SweetAlertDialog(context)
          sweetAlertDialog.setCancelable(false)
          sweetAlertDialog.setTitleText("Do you want to Print?")
            .setContentText("Click to print")
            .show()
          sweetAlertDialog.setCancelButton(
            "No"
          ) { sweetAlertDialog -> // reset Screen ....
            /*   Intent intent = new Intent(EntryActivity.this, OnspotRegistrationActivity.class);
                                     intent.putExtra("type","free");
                                     startActivity(intent);
                                     finish();*/
            sweetAlertDialog.cancel()
          }
          sweetAlertDialog.setConfirmClickListener {
            val path =
              Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/PDFTest/")
            //  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "sample-take-image")
            if (!path.exists()) {
              path.mkdir()
            }
            val mRealpath =
              path.toString() + "Test" + System.currentTimeMillis() + ".pdf"
            try {
              File(mRealpath).createNewFile()
            } catch (e: IOException) {
              e.printStackTrace()
            }
            PdfView.printToPdf(
              context,
              webView,
              mRealpath,
              object :  PdfView.Callback {
                override fun success(path: String?) {

                  //   PdfView.openPdfFile(OnspotRegistrationActivity.this, getString(R.string.app_name), "Do you want to open the pdf file?" + path, path);

                  // File path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/PDFTest/Test.pdf");
                  var data_uri: Uri? = null
                  data_uri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                      FileProvider.getUriForFile(
                        Objects.requireNonNull(
                          context
                        ),
                        BuildConfig.APPLICATION_ID + ".provider",
                        File(mRealpath)
                      )
                    } else {
                      Uri.fromFile(File(mRealpath))
                    }
                  val prefeMain = context!!.getSharedPreferences(
                    "FRIENDS",
                    Activity.MODE_PRIVATE
                  )
                  val PRINTAPP = prefeMain.getString("PRINTAPP", "")
                  if (PRINTAPP == Constant.PRINTSHARE) {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.setPackage("com.dynamixsoftware.printershare")
                    i.flags =
                      Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    i.setDataAndType(data_uri, "application/pdf")
                    context!!.startActivity(i)
                  } else if (PRINTAPP == Constant.PRINTHAND) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(
                      Intent.EXTRA_TEXT,
                      "pi_3M52yTKK4rC5cN2g0D8utE3O"
                    )
                    intent.putExtra(Intent.EXTRA_STREAM, data_uri)
                    intent.type = "application/pdf"
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    if (startPrintHandActivityFailed(intent,context))
                      showStartPrintHandActivityErrorDialog(context)
                  } else if (PRINTAPP == Constant.SELFPRINT) {

                  }
                }

                override fun failure() {}
              })
          }
        }
      }
    }


    fun startPrintHandActivityFailed(intent: Intent,context: Activity): Boolean {
      return (startActivityFailed(intent, PRINT_HAND_FREEMIUM,context)
              && startActivityFailed(intent, PRINT_HAND_PREMIUM,context))
    }


    fun showStartPrintHandActivityErrorDialog(context: Context) {
      Toast.makeText(context,context.getString(R.string.message_error_start_intent),Toast.LENGTH_LONG).show()
    }

    fun startActivityFailed(intent: Intent, packageName: String,context: Activity): Boolean {
      return startActivityForResultFailed(intent, packageName, -1,context)
    }

    fun startActivityForResultFailed(
      intent: Intent,
      packageName: String,
      requestCode: Int,
      context: Activity
    ): Boolean {
      return try {
        intent.setPackage(packageName)
        context.startActivityForResult(intent, requestCode)
        false
      } catch (e: ActivityNotFoundException) {
        true
      }
    }
  }

}