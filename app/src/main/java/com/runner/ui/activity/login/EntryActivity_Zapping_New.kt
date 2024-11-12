package com.runner.ui.activity.login


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.brother.ptouch.sdk.LabelInfo
import com.brother.ptouch.sdk.Printer
import com.brother.ptouch.sdk.PrinterInfo
import com.brother.ptouch.sdk.PrinterStatus
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.camera.CameraSettings
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder
import com.ruhe.utils.NetworkAlertUtility
import com.runner.R
import com.runner.View.ZappingView
import com.runner.adapter.UserListAdapter
import com.runner.cn.guanmai.scanner.IScannerManager
import com.runner.cn.guanmai.scanner.SupporterManager
import com.runner.databinding.ActivityEntryBinding
import com.runner.extra.SunmiScanner
import com.runner.extras.*
import com.runner.manager.CheckInternetConection
import com.runner.model.*
import com.runner.presenter.ZappingPresenter
import com.runner.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.chooseprinter_layout.*
import kotlinx.android.synthetic.main.chooseprinter_layout.cvCross
import kotlinx.android.synthetic.main.search_manually_dialog_layout.*
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*


class EntryActivity_Zapping_New : BaseActivity(), UserListAdapter.Clicklistener, ZappingView,
    SwipecardCallback {
    var binding: ActivityEntryBinding? = null
    protected var mPrintResult: PrinterStatus? = null
    protected var mPrinterInfo: PrinterInfo? = null
    var presenter: ZappingPresenter? = null
    var sharedPreferences: SharedPreferences? = null
    protected var mImageFiles: ArrayList<String?>? = null
    var is_front = 1
    var mBtnReset = false
    private var mScannerManager: SupporterManager<*>? = null
    var qrcode_scanned = ""
    var prefeMain: SharedPreferences? = null
    var BrandName = ""
    var zapping_data_show = ""
    var role_id = ""
    var ticketmodel: TicketModel? = null
    private var swipePlaceHolderView: SwipePlaceHolderView? = null
    var mypath: File? = null
    var edsearchvalue = "";
    var informodel: InfoModel? = null
    private var sunmiScanner: SunmiScanner? = null
    var   dialog:Dialog?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_entry)


        presenter = ZappingPresenter()
        presenter!!.setView(this)
        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        binding!!.title1!!.text = "QR Scan\n[ ${prefeMain!!.getString("location", "")} ]"


        if(prefeMain!!.getString("session", "")!!.isEmpty()){
            binding!!.txtWelcome!!.text = "[ ${prefeMain!!.getString("location", "")} ]"
        }  else{

            var loc = prefeMain!!.getString("location", "")
            var session = prefeMain!!.getString("session", "")

            binding!!.txtWelcome!!.text = "["+ loc+" - "+session+"]"

        }


        zapping_data_show = prefeMain!!.getString("zapping_data_show", "").toString()
        role_id = prefeMain!!.getString("ROLE_ID", "").toString()
        setupUI(binding!!.flMain, this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        is_front = intent.getSerializableExtra("Camera") as Int
       // val txtsearchtext = findViewById<TextView>(R.id.txtsearchtext)
       // txtsearchtext.text = getString(R.string.mannual_txt)
        mPrinterInfo = PrinterInfo()
        mPrinter = Printer()
        mPrinterInfo = mPrinter!!.printerInfo
        sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this@EntryActivity_Zapping_New)
        sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this@EntryActivity_Zapping_New)

        binding!!.btnreset!!.setOnClickListener {
            Log.e("@@CHECJK0","2")

        }
        binding!!.ImageFake!!.setOnClickListener {
            Log.e("@@CHECJK0","4")

        }
        binding!!.btnScan!!.setOnClickListener {
            Log.e("@@CHECJK0","3")
            OpenSearchDialog()

        }
        if (Build.VERSION.SDK_INT >= 23) {
            val PERMISSION_ALL = 1
            val PERMISSIONS = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS
            )
            if (!hasPermissions(this@EntryActivity_Zapping_New, *PERMISSIONS)) {
                ActivityCompat.requestPermissions(
                    this@EntryActivity_Zapping_New,
                    PERMISSIONS,
                    PERMISSION_ALL
                )
            }
        }

        swipePlaceHolderView = findViewById(R.id.swipePlaceHolder)
        swipePlaceHolderView!!.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
            .setDisplayViewCount(1)
            .setSwipeDecor(
                SwipeDecor().setPaddingTop(20)
                    .setRelativeScale(0.01f).setSwipeInMsgLayoutId(R.layout.item_koloda)
                    .setSwipeOutMsgLayoutId(R.layout.item_koloda)
            )

      //  prefeMain = getSharedPreferences("Auth", MODE_PRIVATE)
        BrandName = BrandName(this)
        if (!BrandName.contains("L2") && !BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) && !BrandName.contains(
                Constant.SUNMI_V2s_STGL_MODEL
            ) && !BrandName.contains(Constant.SUNMI_K2MINI)
        ) {
            resetscreen()
            binding!!.barcodeScanner!!.decodeContinuous(callback)
            if (is_front != -1) {
                val cameraSettings = CameraSettings()
                cameraSettings.requestedCameraId = is_front
                binding!!.barcodeScanner!!.setCameraSettings(cameraSettings)
            }
            binding!!.layoutTool!!.visibility = View.VISIBLE
           // findViewById<View>(R.id.llGif).visibility = View.VISIBLE
            binding!!.barcodeScanner!!.setVisibility(View.VISIBLE)
            binding!!.flMain!!.visibility = View.VISIBLE
            binding!!.Constraitlay!!.visibility = View.GONE
        } else {
            binding!!.barcodeScanner!!.setVisibility(View.GONE)
            initScanner()
           // findViewById<View>(R.id.llGif).visibility = View.GONE
            binding!!.barcodeScanner!!.setVisibility(View.GONE)
            binding!!.layoutTool!!.visibility = View.GONE
            binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.greenallowed))
        }

        binding!!.searchManualy!!.setOnClickListener {
            // slideUpDown()
            Log.e("@@CHECJK0","1")
            OpenSearchDialog()
        }


        binding!!.ivcross!!.setOnClickListener { openBackDialog() }
        // binding!!.flMain!!.setOnClickListener { slideDown() }

        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        //LogUtils.debug("@@imagepath",prefeMain!!.getString("USER_ID","").toString())

        Glide.with(this)
            .load(prefeMain!!.getString("LOGO",""))
            .placeholder(R.drawable.ic_logo)
            .into(binding!!.imgLogo)
    }


    private fun OpenSearchDialog() {
        dialog = Dialog(this, R.style.my_dialog)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog!!.setContentView(R.layout.search_manually_dialog_layout)
        dialog!!.setCancelable(false)
        dialog!!.show()
        val cvCross = dialog!!.cvCross.findViewById<CardView>(R.id.cvCross)
        val txtTittle = dialog!!.txtTittle.findViewById<TextView>(R.id.txtTittle)
        txtTittle.setText("Please enter code to\nsearch manually")
        val ed_name = dialog!!.ed_name.findViewById<EditText>(R.id.ed_name)
        val warning_name = dialog!!.warning_name.findViewById<TextView>(R.id.warning_name)
        val btnSearch = dialog!!.btnSearch.findViewById<Button>(R.id.btnSearch)

        cvCross.setOnClickListener({
            dialog!!.dismiss()
        })

        btnSearch.setOnClickListener({
            if (ed_name.text.toString().isEmpty()) {
                warning_name.visibility = View.VISIBLE
                return@setOnClickListener
            }
            warning_name.visibility = View.GONE
            edsearchvalue = ed_name.text.toString().trim()
            LogUtils.debug("@#WW3434",edsearchvalue)

            getUserList(edsearchvalue)

            dialog!!.dismiss()
        })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getUserList(searchKeyword: String) {

        LogUtils.debug("@#WW",searchKeyword)
        var searchKeyword = searchKeyword
        if (!CheckInternetConection.isInternetConnection(this@EntryActivity_Zapping_New)) {
            val sweetAlertDialog = SweetAlertDialog(this@EntryActivity_Zapping_New)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
            return
        }
        if (searchKeyword.contains("[")) {
            try {
                val array = JSONArray(searchKeyword)
                searchKeyword = array.getString(0)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        if (searchKeyword.contains("BEGIN:VCARD")) {
            val arrOfStr = searchKeyword.split("UC:").toTypedArray()
            searchKeyword = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }

        }

        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        var prefix: String = prefeMain!!.getString("PREFIX", "")!!


        /*if (!prefix.isNullOrEmpty() && !searchKeyword.contains(prefix)) {
            binding!!.llMessage!!.visibility = View.VISIBLE
            binding!!.llCamera.visibility = View.GONE
            binding!!.llMessage!!.setBackgroundColor(resources.getColor(R.color.solid_red))
            binding!!.txtMessage!!.text = "*Invalid Code"
            binding!!.lottieMain!!.visibility = View.GONE
            binding!!.lottieFail!!.visibility = View.VISIBLE
            binding!!.lottieFail!!.playAnimation()
            resetscreen()
            return
        }*/

        if (NetworkAlertUtility.isConnectingToInternet(this)) {


     /* if(prefeMain!!.getString("event_id","").equals("18")) {

          var model_local: TicketModel = TicketModel()
          model_local.e_ticket = "https://s3.ap-south-1.amazonaws.com/test.bucket.in/Smart-Reg/regDemo/eticket/" + searchKeyword + ".png"

          Log.e("@@ETICKET11", model_local.e_ticket)
          swipePlaceHolderView!!.addView(
              ETIcketcardView(
                  this@EntryActivity_Zapping_New,
                  swipePlaceHolderView,
                  model_local
              )
          )

      }*/


            val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
            var header: HashMap<String, String> = appUtils.Takeheader(this, "")
            prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            var api_name: String? = "zapping"
            var location: String? = prefeMain!!.getString("location", "")
            var session: String? = prefeMain!!.getString("session", "")

            if(session!!.isEmpty()){
                api_name = "zapping"
                presenter!!.Zapping(this, map, header, true, api_name!!, searchKeyword, location!!)
            }else{
                api_name = "zapping"

                presenter!!.ZappingwithSession(this, map, header, true, api_name!!, searchKeyword, location!!,session!!)

            }



        } else {
            val sweetAlertDialog = SweetAlertDialog(this@EntryActivity_Zapping_New)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
        }
    }


    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text != null) {
                if(dialog!=null){
                    dialog!!.dismiss()


                }
                qrcode_scanned = ""
                binding!!.barcodeScanner!!.pause()
                val qrcode = result.text

                qrcode_scanned = result.text
                try {
                    getUserList(qrcode_scanned)
                } catch (e: Exception) {
                    binding!!.llCamera.visibility = View.VISIBLE
                    binding!!.llMessage!!.visibility = View.GONE
                    resetscreen()
                }
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    /*  private fun initScanner() {
          if (mScannerManager != null) {
              mScannerManager!!.scannerEnable(true)
          }
          mScannerManager = SupporterManager<IScannerManager>(this, object :
              SupporterManager.IScanListener {
              override fun onScannerResultChange(result: String) {

                  if(dialog!=null){
                      dialog!!.dismiss()


                  }

                  binding!!.edUnique.setText("")
                  qrcode_scanned = ""

                  Handler().postDelayed({
                      binding!!.edUnique.setText(result)
                      qrcode_scanned = result
                      //@@...
                      getUserList(qrcode_scanned)
                  }, 400L)
              }

              override fun onScannerServiceConnected() {}
              override fun onScannerServiceDisconnected() {}
              override fun onScannerInitFail() {}
          })
      }*/
    fun String.toNumericString() = this.filter { it.isLetterOrDigit() }

    private fun initScanner() {
        Log.e("BrandName: ",BrandName+"Model: "+Constant.SUNMI_K2MINI)
        if (BrandName.contains(Constant.SUNMI_K2MINI)) {
            Log.e("@@data","3213")
            sunmiScanner = SunmiScanner(this)
            sunmiScanner!!.analysisBroadcast()
            sunmiScanner!!.setScannerListener(object : SunmiScanner.OnScannerListener {
                override fun onScanData(data: String, type: SunmiScanner.DATA_DISCRIBUTE_TYPE) {
                    Log.e("@@data",data)

                    binding!!.tvNote.setText(data.replace("?",""))

                    val result = binding!!.tvNote.text.toString().trim().toNumericString()
                    //   Log.e("@@text",binding!!.tvNote.text.toString().trim().toNumericString())

                    qrcode_scanned = ""
                    Handler().postDelayed({

                        qrcode_scanned = result
                        //@@...
                        Log.e("@@Inner Track", qrcode_scanned)
                        if (qrcode_scanned.contains("?q=")) {
                            val array = qrcode_scanned.split("q=").toTypedArray()
                            Log.e("@@keyword Breaked", array[1])
                            qrcode_scanned = array[1]
                        }

                        getUserList(qrcode_scanned)
                    }, 400L)

                    //用十六进制可以看后缀是啥
                }

                override fun onResponseData(data: String, type: SunmiScanner.DATA_DISCRIBUTE_TYPE) {

                    Log.e("@@data",data)
                    /*  Log.i(
                          com.sunmi.sunmiscannerdemo.MainActivity.TAG,
                          """
                      数据类型:${type}串口指令返回值[hex]:$data[${ByteUtils.str2HexString(data)}]

                      """.trimIndent()
                      )*/
                }

                override fun onResponseTimeout() {
                    Log.e("@@data","Error")
                    //Log.i(com.sunmi.sunmiscannerdemo.MainActivity.TAG, "指令响应超时\n")
                }
            })
        }
        else {
            mScannerManager =
                SupporterManager<IScannerManager>(this, object : SupporterManager.IScanListener {
                    override fun onScannerResultChange(result: String) {

                        if(dialog!=null){
                            dialog!!.dismiss()


                        }

                        qrcode_scanned = ""
                        Handler().postDelayed({

                            qrcode_scanned = result

                            if (qrcode_scanned.contains("?q=")) {
                                val array = qrcode_scanned.split("q=").toTypedArray()

                                qrcode_scanned = array[1]
                            }


                            getUserList(qrcode_scanned)
                        }, 400L)
                    }

                    override fun onScannerServiceConnected() {}
                    override fun onScannerServiceDisconnected() {}
                    override fun onScannerInitFail() {}
                })
        }
    }

    override fun onBackPressed() {
        LogUtils.debug("@@IsOpen","Dismiss")
        openBackDialog()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun resetscreen() {
        Handler().postDelayed({
            binding!!.llCamera.visibility = View.VISIBLE
            binding!!.llMessage!!.visibility = View.GONE
            BrandName = BrandName(applicationContext)
            if (!BrandName.contains("L2") && !BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) && !BrandName.contains(
                    Constant.SUNMI_V2s_STGL_MODEL
                ) && !BrandName.contains(Constant.SUNMI_K2MINI)
            ) {
                findViewById<View>(R.id.scan_msg).visibility = View.GONE

                binding!!.flMain!!.visibility = View.VISIBLE
                binding!!.Constraitlay!!.visibility = View.GONE
                binding!!.layoutTool!!.visibility = View.VISIBLE
                binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.transparent))
                binding!!.barcodeScanner!!.resume()
                binding!!.barcodeScanner.refreshDrawableState()
            } else {
                binding!!.scanMsg.visibility = View.VISIBLE
                binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.greenallowed))
                binding!!.barcodeScanner!!.visibility = View.GONE
                if(!BrandName.contains(
                        Constant.SUNMI_K2MINI))
                {
                    mScannerManager!!.scannerEnable(true)
                }
                binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.greenallowed))
                binding!!.flMain!!.visibility = View.GONE
                binding!!.Constraitlay!!.visibility = View.VISIBLE
                binding!!.layoutTool!!!!.visibility = View.GONE
            }
        }, 1600)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun resetscreenfalse() {
        Handler().postDelayed({
            binding!!.llCamera.visibility = View.VISIBLE
            binding!!.llMessage!!.visibility = View.GONE
            BrandName = BrandName(applicationContext)
            if (!BrandName.contains("L2") && !BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) && !BrandName.contains(
                    Constant.SUNMI_V2s_STGL_MODEL
                ) && !BrandName.contains(Constant.SUNMI_K2MINI)
            ) {
                findViewById<View>(R.id.scan_msg).visibility = View.GONE

                binding!!.flMain!!.visibility = View.VISIBLE
                binding!!.Constraitlay!!.visibility = View.GONE
                binding!!.layoutTool!!.visibility = View.VISIBLE
                binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.transparent))
                binding!!.barcodeScanner!!.resume()
                binding!!.barcodeScanner.refreshDrawableState()
            } else {
                binding!!.scanMsg.visibility = View.VISIBLE
                binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.greenallowed))
                binding!!.barcodeScanner!!.visibility = View.GONE
                if(!BrandName.contains(
                        Constant.SUNMI_K2MINI))
                {
                    mScannerManager!!.scannerEnable(true)
                }
                binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.greenallowed))
                binding!!.flMain!!.visibility = View.GONE
                binding!!.Constraitlay!!.visibility = View.VISIBLE
                binding!!.layoutTool!!!!.visibility = View.GONE
            }
        }, 2400)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                resetscreen()
            }
        }
    }

    override fun onPrintClicked(position: Int) {


    }

    override fun onAlloteClicked(position: Int) {}
    override fun onCheckboxClicked(position: String) {
        TODO("Not yet implemented")
    }

    override fun onshowTicket(position: Int) {}
    override fun onCashlessRedeem(position: Int) {
        TODO("Not yet implemented")
    }

    override fun selectFileButtonOnClick() {}
    override fun printButtonOnClick() {}

    override fun onDestroy() {
        super.onDestroy()
        if (mScannerManager != null) {
            mScannerManager!!.recycle()
        }

        try {
            if (sunmiScanner != null)
                sunmiScanner!!.destory()
        } catch (e: Exception) {

        }
    }

    private fun openBackDialog() {
        val dialog = Dialog(this, R.style.my_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_back)
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.45).toInt()
        dialog.window!!.setLayout(width, height)
        dialog.setCancelable(false)
        val tv_msg = dialog.findViewById<TextView>(R.id.msg)
        tv_msg.text = getString(R.string.back_msg)
        val btn_ok = dialog.findViewById<Button>(R.id.btn_yes)
        val btn_no = dialog.findViewById<Button>(R.id.btn_no)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_ok.setOnClickListener {

            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        btn_no.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    companion object {
        var mPrinter: Printer? = null
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

        fun BrandName(context: Context?): String {
            return Build.MODEL
        }

        fun setupUI(view: View, context: Activity) {
            if (view !is EditText) {
                view.setOnTouchListener { v, event ->
                    hideSoftKeyboard(context)
                    false
                }
            }
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val innerView = view.getChildAt(i)
                    setupUI(innerView, context)
                }
            }
        }

        fun hideSoftKeyboard(activity: Activity) {
            val inputManager = activity.getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager
            val focusedView = activity.currentFocus
            if (focusedView != null) {
                try {
                    assert(inputManager != null)
                    inputManager.hideSoftInputFromWindow(
                        focusedView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                } catch (e: AssertionError) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun OnUSerZapped(model: ZappingResponce?, errCode: Int) {

        try {

            if(errCode==404){
                resetscreenfalse()
                binding!!.llMessage!!.visibility = View.VISIBLE
                binding!!.txtMessage!!.text = "Invalid QR Code..."
                binding!!.llMessage!!.setBackgroundColor(resources.getColor(R.color.solid_red))
             //   LogUtils.debug("@@@@#er",model.message)
                binding!!.lottieFail!!.playAnimation()
                binding!!.llCamera.visibility = View.GONE
                binding!!.lottieMain!!.visibility = View.GONE
                binding!!.Constraitlay.visibility = View.GONE
                binding!!.flMain.visibility = View.VISIBLE
                binding!!.lottieFail!!.visibility = View.VISIBLE
                val mess = model!!.message




                return
            }


            if (model!!.status!!) {
                LogUtils.debug("@@@er",model.message)
                if (zapping_data_show.equals("") || zapping_data_show.equals("0")) {
                    LogUtils.debug("@@@,er",model.message)
                    binding!!.llCamera.visibility = View.GONE
                    binding!!.llMessage!!.visibility = View.VISIBLE
                    val mess = model!!.message
                    binding!!.txtMessage!!.text = mess
                    binding!!.Constraitlay.visibility = View.GONE
                    binding!!.flMain.visibility = View.VISIBLE
                    binding!!.lottieMain!!.visibility = View.VISIBLE
                    binding!!.lottieFail!!.visibility = View.GONE
                    binding!!.lottieMain!!.playAnimation()
                    binding!!.ivstatus!!.setImageDrawable(resources.getDrawable(R.drawable.success))
                    binding!!.llMessage!!.setBackgroundColor(resources.getColor(R.color.greenallowed))
                    resetscreen()
                  //  updateToServer()
                } else if (zapping_data_show.equals("1")) {


                    ticketmodel = TicketModel()
                    ticketmodel!!.e_ticket = model!!.data!!.eticketPath.toString()


                    binding!!.llInfo.visibility = View.VISIBLE
                    binding!!.flMain.visibility = View.VISIBLE
                    binding!!.Constraitlay.visibility = View.GONE

                    //ticketmodel!!.e_ticket = "https://s3.ap-south-1.amazonaws.com/test.bucket.in/Smart-Reg/.base_event./eticket/1684413407BASIC2023VD1PPBIVV.png"
                    swipePlaceHolderView!!.addView(
                        ETIcketcardView(
                            this@EntryActivity_Zapping_New,
                            swipePlaceHolderView,
                            ticketmodel
                        )
                    )


                }
                else if (zapping_data_show.equals("2")) {
                    //show Information
                    var prefeMain = context!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)
                    var Row1 = prefeMain.getString("zapping_info_row1", "")
                    var Row2 = prefeMain.getString("zapping_info_row2", "")
                    var Row3 = prefeMain.getString("zapping_info_row3", "")
                    var Row4 = prefeMain.getString("zapping_info_row4", "")
                    var Row5 = prefeMain.getString("zapping_info_row5", "")

                    binding!!.Constraitlay.visibility = View.GONE
                    val gson = Gson()
                    var json = gson.toJson(model.data)

                    var jsonobject: JSONObject = JSONObject(json)



                    var printrow1 = ""
                    var printrow2 = ""
                    var printrow3 = ""
                    var printrow4 = ""
                    var printrow5 = ""

                    if (jsonobject.has(Row1)) {
                        printrow1 = jsonobject.getString(Row1).toString()
                    }
                    if (jsonobject.has(Row2)) {
                        printrow2 = jsonobject.getString(Row2).toString()
                    }
                    if (jsonobject.has(Row3)) {
                        printrow3 = jsonobject.getString(Row3).toString()
                    }
                    if (jsonobject.has(Row4)) {
                        printrow4 = jsonobject.getString(Row4).toString()
                    }
                    if (jsonobject.has(Row5)) {
                        printrow5 = jsonobject.getString(Row5).toString()
                    }


                    informodel = InfoModel(printrow1, printrow2, printrow3, printrow4, printrow5)
                    binding!!.llInfo.visibility = View.VISIBLE

                    swipePlaceHolderView!!.addView(InfoUserView(this@EntryActivity_Zapping_New,informodel!!,swipePlaceHolderView!!))


                    /*if(model!!.data!!.name!!.equals(Row1.toString())){
                        printrow1 = model.data!!.name.toString()
                    }
                    if(model.data!!.email!!.equals(Row2)){
                        printrow2 = model!!.data!!.email.toString()
                    }
                    if(model.data!!.phone!!.equals(Row3)){
                        printrow2 = model!!.data!!.phone.toString()
                    }

                    if(model.data!!.category!!.equals(Row3)){
                        printrow2 = model!!.data!!.category.toString()
                    }
                    if(model.data!!.gender!!.equals(Row3)){
                        printrow2 = model!!.data!!.gender.toString()
                    }*/

                }

            }
            else {
                binding!!.llMessage!!.visibility = View.VISIBLE
                binding!!.llMessage!!.setBackgroundColor(resources.getColor(R.color.solid_red))
                LogUtils.debug("@@@@#er",model.message)
                binding!!.lottieFail!!.playAnimation()
                binding!!.llCamera.visibility = View.GONE
                binding!!.lottieMain!!.visibility = View.GONE
                binding!!.Constraitlay.visibility = View.GONE
                binding!!.flMain.visibility = View.VISIBLE
                binding!!.lottieFail!!.visibility = View.VISIBLE
                val mess = model!!.message
                binding!!.txtMessage!!.text = mess
                resetscreenfalse()
            }
        } catch (ex: Exception) {


              resetscreen()
            LogUtils.debug("@@ERROR", ex.message.toString());

        }

    }

    override fun OnZappingUpdate(model: VerifyResponce?, errCode: Int) {

    }

    override fun onEditValue(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onUpgradeValue(position: Int) {
        TODO("Not yet implemented")
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateToServer() {
        var searchKeyword = qrcode_scanned
        if (searchKeyword.contains("BEGIN:VCARD")) {
            val arrOfStr = searchKeyword.split("UC:").toTypedArray()
            searchKeyword = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }

        }

        if (!CheckInternetConection.isInternetConnection(this@EntryActivity_Zapping_New)) {
            val sweetAlertDialog = SweetAlertDialog(this@EntryActivity_Zapping_New)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
            return
        }
       // binding!!.flMain.visibility = View.GONE


        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        var prefix: String = prefeMain!!.getString("PREFIX", "")!!;




        if (NetworkAlertUtility.isConnectingToInternet(this)) {
            resetscreen()
            val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
            var header: HashMap<String, String> = appUtils.Takeheader(this, "")
            prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            var api_name: String? = prefeMain!!.getString("zapping_user_update", "")
            var location: String? = prefeMain!!.getString("location", "")
            if(!api_name.isNullOrEmpty())
            {
                presenter!!.ZappingUpdate(this, map, header, false, api_name!!, searchKeyword, location!!)
            }

        } else {
            val sweetAlertDialog = SweetAlertDialog(this@EntryActivity_Zapping_New)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
        }
    }

    override val context: Context?
        get() = this

    override fun onSwipeIn() {
        binding!!.llInfo.visibility = View.GONE
        binding!!.llCamera.visibility = View.GONE
        binding!!.llMessage!!.visibility = View.VISIBLE
        val mess = "Allowed"
        binding!!.txtMessage!!.text = mess
        binding!!.lottieMain!!.visibility = View.VISIBLE
        binding!!.lottieFail!!.visibility = View.GONE
        binding!!.lottieMain!!.playAnimation()
        binding!!.ivstatus!!.setImageDrawable(resources.getDrawable(R.drawable.success))
        binding!!.llMessage!!.setBackgroundColor(resources.getColor(R.color.greenallowed))
       // updateToServer()
        clearStcak()
    }

    private fun clearStcak() {
        if (swipePlaceHolderView!!.childCount > 0) {
            swipePlaceHolderView!!.removeAllViews()
        }
    }

    override fun onSwipeOut() {
        binding!!.llInfo.visibility = View.GONE
        binding!!.llCamera.visibility = View.GONE
        binding!!.llMessage!!.visibility = View.VISIBLE
        binding!!.lottieMain!!.visibility = View.GONE
        binding!!.lottieFail!!.visibility = View.VISIBLE
        binding!!.lottieFail!!.playAnimation()
        binding!!.llMessage!!.setBackgroundColor(resources.getColor(R.color.solid_red))
        val mess = "Denied..."
        binding!!.txtMessage!!.text = mess
        clearStcak()
        resetscreenfalse()
    }

}