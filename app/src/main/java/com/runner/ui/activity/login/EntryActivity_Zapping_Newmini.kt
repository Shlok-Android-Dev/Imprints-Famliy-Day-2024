package com.runner.ui.activity.login


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.brother.ptouch.sdk.LabelInfo
import com.brother.ptouch.sdk.Printer
import com.brother.ptouch.sdk.PrinterInfo
import com.brother.ptouch.sdk.PrinterStatus
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
import com.runner.databinding.ActivityEntryMiniBinding
import com.runner.extra.SunmiScanner
import com.runner.extras.*
import com.runner.manager.CheckInternetConection
import com.runner.model.*
import com.runner.presenter.ZappingPresenter
import com.runner.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_entry_mini.view.*
import kotlinx.android.synthetic.main.chooseprinter_layout.*
import kotlinx.android.synthetic.main.chooseprinter_layout.cvCross
import kotlinx.android.synthetic.main.search_manually_dialog_layout.*
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*
import java.util.regex.Pattern


class EntryActivity_Zapping_Newmini : BaseActivity(), UserListAdapter.Clicklistener, ZappingView,
    SwipecardCallback {
    var binding: ActivityEntryMiniBinding? = null
    protected var mPrintResult: PrinterStatus? = null
    protected var mPrinterInfo: PrinterInfo? = null
    var presenter: ZappingPresenter? = null
    var sharedPreferences: SharedPreferences? = null
    protected var mImageFiles: ArrayList<String?>? = null
    var is_front = 1
    var Emptyscan = false
    private var mScannerManager: SupporterManager<*>? = null
    var qrcode_scanned = ""
    var prefeMain: SharedPreferences? = null
    var BrandName = ""
    var mScan = false
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_entry_mini)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.primary_color)
        }

        presenter = ZappingPresenter()
        presenter!!.setView(this)
        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        binding!!.title1!!.text = "QR Scan\n[ ${prefeMain!!.getString("location", "")} ]"
        binding!!.txtWelcome1.setText(prefeMain!!.getString("location", "").toString())
        zapping_data_show = prefeMain!!.getString("zapping_data_show", "").toString()
        role_id = prefeMain!!.getString("ROLE_ID", "").toString()
        setupUI(binding!!.flMain, this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        is_front = intent.getSerializableExtra("Camera") as Int
        val txtsearchtext = findViewById<TextView>(R.id.txtsearchtext)
        txtsearchtext.text = getString(R.string.mannual_txt)
        mPrinterInfo = PrinterInfo()
        mPrinter = Printer()
        mPrinterInfo = mPrinter!!.printerInfo
        sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this@EntryActivity_Zapping_Newmini)
        sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this@EntryActivity_Zapping_Newmini)
        if (Build.VERSION.SDK_INT >= 23) {
            val PERMISSION_ALL = 1
            val PERMISSIONS = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS
            )
            if (!hasPermissions(this@EntryActivity_Zapping_Newmini, *PERMISSIONS)) {
                ActivityCompat.requestPermissions(
                    this@EntryActivity_Zapping_Newmini,
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

        findViewById<View>(R.id.ivSwitch).setOnClickListener {
            if (is_front == 1) {
                is_front = -1
                val intent = Intent(
                    this@EntryActivity_Zapping_Newmini,
                    EntryActivity_Zapping_Newmini::class.java
                )
                intent.putExtra("Camera", -1)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(
                    this@EntryActivity_Zapping_Newmini,
                    EntryActivity_Zapping_Newmini::class.java
                )
                intent.putExtra("Camera", 1)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        // prefeMain = getSharedPreferences("Auth", MODE_PRIVATE)
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
            binding!!.layoutTool.visibility=View.VISIBLE
            binding!!.llGif!!.visibility = View.VISIBLE
            binding!!.Constraitlay!!.visibility = View.VISIBLE
            binding!!.flMain!!.visibility = View.GONE
            binding!!.barcodeScanner!!.setVisibility(View.VISIBLE)
        } else {
            binding!!.barcodeScanner!!.setVisibility(View.GONE)
            initScanner()
            binding!!.llGif!!.visibility = View.GONE
            binding!!.Constraitlay!!.visibility = View.GONE
            binding!!.flMain!!.visibility = View.VISIBLE
            if(!BrandName.contains(
                    Constant.SUNMI_K2MINI)) {
                mScannerManager!!.scannerEnable(true)
            }
            binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.greenallowed))
        }
        binding!!.btnSubmit!!.setOnClickListener(View.OnClickListener { // @@ fetch list ...
            if (binding!!.edUnique!!.getText().toString().isEmpty()) {
                binding!!.warningTxt!!.setVisibility(View.VISIBLE)
                return@OnClickListener
            }
            binding!!.warningTxt!!.setVisibility(View.GONE)
            getUserList(binding!!.edUnique!!.getText().toString())
        })
        binding!!.searchManualy!!.setOnClickListener {
            // slideUpDown()
            if (!BrandName.contains("L2") && !BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) && !BrandName.contains(
                    Constant.SUNMI_V2s_STGL_MODEL
                ) && !BrandName.contains(Constant.SUNMI_K2MINI)
            ) {
                OpenSearchDialog()
            }
        }
        binding!!.btnreset!!.requestFocus()
        binding!!.btnreset!!.setOnClickListener {
            Log.e("@@Clickfaceke","1")
        }
        binding!!.btnScan!!.setOnClickListener {
            // slideUpDown()
            if(mScan)
            {
                return@setOnClickListener
            }
            binding!!.btnScan!!.isEnabled=false
            Log.e("@@Clickfaceke","2")
            OpenSearchDialog()

            Handler().postDelayed({
                try{
                    binding!!.btnScan!!.isEnabled=true
                }catch (e:Exception)
                {

                }

            }, 1500L)
        }
        /* binding!!.searchManualy!!.setOnLongClickListener {
             appUtils.showGenericDialog(this,false)
             false
         }*/
        binding!!.ivcross!!.setOnClickListener { openBackDialog() }
        // binding!!.flMain!!.setOnClickListener { slideDown() }
        binding!!.ivSwitch!!.setOnClickListener {
            if (is_front == 1) {
                val intent = Intent(
                    this@EntryActivity_Zapping_Newmini,
                    EntryActivity_Zapping_Newmini::class.java
                )
                intent.putExtra("Camera", -1)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(
                    this@EntryActivity_Zapping_Newmini,
                    EntryActivity_Zapping_Newmini::class.java
                )
                intent.putExtra("Camera", 1)
                startActivity(intent)
                finish()
            }
        }


        binding!!.ivback!!.setOnClickListener {
            findViewById<View>(R.id.llUserlist).visibility = View.GONE
            binding!!.llCamera.setVisibility(View.VISIBLE)
            resetscreen()
        }

        if(BrandName.contains(Constant.SUNMI_K2MINI))
        {
            binding!!.flMain!!.visibility = View.GONE
            binding!!.Constraitlay!!.visibility = View.VISIBLE
            binding!!.txtScanQr.textSize=60f
            binding!!.txtMessage.textSize=55f
            binding!!.txtUSERname.textSize=70f
            binding!!.txtEVENTWISE.textSize=70f
            binding!!.txtWelcome.textSize=60f
            binding!!.btnScan.textSize=50f
            //binding!!.btnScan.setText("SEARCH MANUALLY")

            binding!!.btnScan.setPadding(25, 25, 25, 25);

            binding!!.txtShowYourQr.textSize=42f
            binding!!.lottiemain.minimumWidth=350
            val layoutParams = binding!!.lottiemain.getLayoutParams()
            layoutParams.width = 1050 // Set your desired width here, either in pixels or other dimension units
            layoutParams.height = 1050 // Set your desired width here, either in pixels or other dimension units

            binding!!.lottiemain.setLayoutParams(layoutParams)
        }
        else
        {
            binding!!.flMain!!.visibility = View.VISIBLE
            binding!!.Constraitlay!!.visibility = View.VISIBLE
            val layoutParams = binding!!.guideline.getLayoutParams() as ConstraintLayout.LayoutParams
            layoutParams.guidePercent = -0.2f // Set your desired percent value between 0 and 1
            binding!!.guideline.setLayoutParams(layoutParams)
        }
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

            if(mScan)
            {
                return@setOnClickListener
            }
            btnSearch!!.isEnabled=false

            Handler().postDelayed({
                try{
                    btnSearch.isEnabled=true
                }catch (e:Exception)
                {

                }

            }, 1500L)

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

        if(searchKeyword.isNullOrEmpty())
        {
            return
        }
        LogUtils.debug("@#WW",searchKeyword)
        var searchKeyword = searchKeyword
        if (!CheckInternetConection.isInternetConnection(this@EntryActivity_Zapping_Newmini)) {
            val sweetAlertDialog = SweetAlertDialog(this@EntryActivity_Zapping_Newmini)
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
        if (searchKeyword.contains("BEGIN:VCARD")|| (searchKeyword.contains("BEGINVCARD",ignoreCase = true))) {
            val arrOfStr = searchKeyword.split("UC:").toTypedArray()
            try {
                searchKeyword = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }
            }
            catch (e:Exception)
            {
                if(searchKeyword.contains("ENDVCARD",ignoreCase = true))
                {
                    val pattern = Pattern.compile("UC(.*?)ENDVCARD")
                    val matcher = pattern.matcher(searchKeyword)
                    if (matcher.find()) {
                        searchKeyword = matcher.group(1)

                    } else {
                        return
                    }
                }
            }
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

        if (searchKeyword.contains("BEGIN:VCARD")) {
            return
        }
        if (NetworkAlertUtility.isConnectingToInternet(this)) {

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

            edsearchvalue=""
        } else {
            val sweetAlertDialog = SweetAlertDialog(this@EntryActivity_Zapping_Newmini)
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
                binding!!.edUnique.setText(qrcode)
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
                    mScan=true
                    try
                    {
                        if(dialog!=null&& dialog!!.isShowing){
                            dialog!!.dismiss()

                        }
                    }
                    catch (e:Exception)
                    {

                    }
                    if(Emptyscan)
                    {
                        return
                    }

                    ScammCage()

                    binding!!.tvNote.setText(data.replace("?",""))

                    val result = binding!!.tvNote.text.toString().trim().toNumericString()
                    //   Log.e("@@text",binding!!.tvNote.text.toString().trim().toNumericString())
                    binding!!.edUnique!!.setText("")
                    qrcode_scanned = ""
                    Handler().postDelayed({
                        binding!!.edUnique!!.setText(data.replace("?",""))
                        qrcode_scanned = result
                        //@@...
                        Log.e("@@Inner Track", qrcode_scanned)
                        if (qrcode_scanned.contains("?q=")) {
                            val array = qrcode_scanned.split("q=").toTypedArray()
                            Log.e("@@keyword Breaked", array[1])
                            qrcode_scanned = array[1]
                        }
                        Emptyscan=true
                        getUserList(qrcode_scanned)
                    }, 400L)
                    Handler().postDelayed({
                        Emptyscan=false
                    }, 8000L)


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
                        mScan=true
                        ScammCage()
                        if(dialog!=null){
                            dialog!!.dismiss()


                        }
                        binding!!.edUnique!!.setText("")
                        qrcode_scanned = ""
                        Handler().postDelayed({
                            binding!!.edUnique!!.setText(result)
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

    private fun ScammCage() {
        Handler().postDelayed({
            try{
                mScan=false
            }catch (e:Exception)
            {

            }

        }, 3000L)
    }

    override fun onBackPressed() {
        if (binding!!.llUserlist!!.visibility == View.VISIBLE) {
            binding!!.llUserlist!!.visibility = View.GONE
            binding!!.llCamera.visibility = View.VISIBLE
            resetscreen()
            return
        } else {
            if (binding!!.llsearch.visibility == View.VISIBLE) {
                slideDown()
            } else {
                openBackDialog()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun resetscreen() {
        Handler().postDelayed({
            binding!!.llCamera.visibility = View.VISIBLE
            binding!!.llMessage!!.visibility = View.GONE
            binding!!.llUserlist.visibility = View.GONE

            binding!!.edUnique!!.setText(resources.getString(R.string.app_Code))
            binding!!.edUnique.setSelection(binding!!.edUnique!!.text.toString().length)
            BrandName = BrandName(applicationContext)
            if (!BrandName.contains("L2") && !BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) && !BrandName.contains(
                    Constant.SUNMI_V2s_STGL_MODEL
                ) && !BrandName.contains(Constant.SUNMI_K2MINI)
            ) {
                binding!!.layoutTool.visibility=View.VISIBLE
                binding!!.layoutTool.setBackgroundColor(resources.getColor(R.color.transparent))
                binding!!.barcodeScanner!!.resume()
                binding!!.barcodeScanner!!.refreshDrawableState()
                binding!!.Constraitlay!!.visibility = View.GONE
                binding!!.flMain!!.visibility = View.VISIBLE
            } else {
                binding!!.barcodeScanner!!.visibility = View.GONE
                if(!BrandName.contains(
                        Constant.SUNMI_K2MINI))
                {
                    mScannerManager!!.scannerEnable(true)
                    mScannerManager!!.scannerEnable(true)
                }

                binding!!.Constraitlay!!.visibility = View.VISIBLE
                binding!!.flMain!!.visibility = View.GONE
                binding!!.layoutTool.setBackgroundColor(resources.getColor(R.color.greenallowed))
            }
        }, if(prefeMain!!.getString("event_id","0").equals("26")) 12000 else 5300)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun resetscreenfalse() {
        Handler().postDelayed({
            binding!!.llCamera.visibility = View.VISIBLE
            binding!!.llMessage!!.visibility = View.GONE
            binding!!.llUserlist.visibility = View.GONE
            binding!!.edUnique.setText(resources.getString(R.string.app_Code))
            binding!!.edUnique.setSelection(binding!!.edUnique!!.text.toString().length)
            BrandName = BrandName(applicationContext)
            if (!BrandName.contains("L2") && !BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) && !BrandName.contains(
                    Constant.SUNMI_V2s_STGL_MODEL
                ) && !BrandName.contains(Constant.SUNMI_K2MINI)
            ) {
                binding!!.layoutTool.setBackgroundColor(resources.getColor(R.color.transparent))
                binding!!.layoutTool.visibility=View.VISIBLE
                binding!!.barcodeScanner!!.resume()
                binding!!.barcodeScanner!!.refreshDrawableState()
                binding!!.Constraitlay!!.visibility = View.GONE
                binding!!.flMain!!.visibility = View.VISIBLE
            } else {
                binding!!.barcodeScanner!!.visibility = View.GONE
                if(!BrandName.contains(
                        Constant.SUNMI_K2MINI)) {
                    mScannerManager!!.scannerEnable(true)
                    mScannerManager!!.scannerEnable(true)
                }
                binding!!.Constraitlay!!.visibility = View.VISIBLE
                binding!!.flMain!!.visibility = View.GONE
                binding!!.layoutTool.setBackgroundColor(resources.getColor(R.color.greenallowed))
            }
        }, if(prefeMain!!.getString("event_id","0").equals("26")) 5000 else 6500)
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


    fun getUriFromFile(file: File?, context: Context): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return Uri.fromFile(file)
        } else {
            try {
                return FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    file!!
                )
            } catch (e: Exception) {
            }
        }
        return null
    }


    private fun Printing() {
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
        binding!!.rlBadgeprint!!.isDrawingCacheEnabled = true
        val bitmap = binding!!.rlBadgeprint!!.drawingCache
        mypath = File(tempDir, uniqueId)
        val s = mypath!!.absolutePath
        try {
            mypath!!.createNewFile()
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
            val bitmapdata = bos.toByteArray()
            val fos = FileOutputStream(mypath)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
        } catch (e: Exception) {

        }
        val runApp = sharedPreferences!!.getString("RUNAPP", "1")
        printTemplateSample()
    }

    fun printTemplateSample() {
        try {
            print()
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    fun print() {
        val printTread: PrinterThread = PrinterThread()
        printTread.start()
    }

    protected inner class PrinterThread : Thread() {
        override fun run() {
            try {
                setPrinterInfo()
                mPrintResult = PrinterStatus()
                mPrinter!!.startCommunication()
                doPrint()
                mPrinter!!.endCommunication()
            } catch (e: Exception) {
            }
        }
    }

    fun setPrinterInfo() {
        preferences
        println("getPreferences")
        setCustomPaper()
        mPrinter!!.printerInfo = mPrinterInfo
    }

    protected fun doPrint() {
        try {
            mImageFiles = ArrayList()
            val runApp = sharedPreferences!!.getString("RUNAPP", "1")
            mImageFiles!!.add(mypath!!.absolutePath)
            if (mImageFiles!!.contains(null)) {
                mImageFiles!!.remove(null)
            }
            val count = mImageFiles!!.size
            for (i in 0 until count) {
                val strFile = mImageFiles!![i]
                if (!isFinishing) {
                    mPrintResult = mPrinter!!.printFile(strFile)

                    // if error, stop print next files
                    if (mPrintResult!!.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                        break
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun setCustomPaper() {
        when (mPrinterInfo!!.printerModel) {
            PrinterInfo.Model.RJ_4030, PrinterInfo.Model.RJ_4040, PrinterInfo.Model.RJ_3050, PrinterInfo.Model.RJ_3150, PrinterInfo.Model.TD_2020, PrinterInfo.Model.TD_2120N, PrinterInfo.Model.TD_2130N, PrinterInfo.Model.TD_4100N, PrinterInfo.Model.TD_4000 -> {}
            else -> {}
        }
    }

    private val preferences: Unit
        private get() {
            if (mPrinterInfo == null) {
                mPrinterInfo = PrinterInfo()
                return
            }
            var input: String? = ""
            mPrinterInfo!!.printerModel = PrinterInfo.Model.valueOf(
                sharedPreferences!!
                    .getString("printerModel", "")!!
            )
            println("@@@printerModel" + mPrinterInfo!!.printerModel)
            mPrinterInfo!!.port = PrinterInfo.Port.NET
            println("@@@@@port" + mPrinterInfo!!.port)
            mPrinterInfo!!.ipAddress = sharedPreferences!!.getString("address", "")
            println("@@@ipAddress" + mPrinterInfo!!.ipAddress)
            mPrinterInfo!!.macAddress = sharedPreferences!!.getString("macAddress", "")!!.uppercase(
                Locale.getDefault()
            )
            println("@@@macAddress" + mPrinterInfo!!.macAddress)
            if (isLabelPrinter(mPrinterInfo!!.printerModel)) {
                mPrinterInfo!!.paperSize = PrinterInfo.PaperSize.CUSTOM
                println("@@@paperSize" + mPrinterInfo!!.paperSize)
                when (mPrinterInfo!!.printerModel) {
                    PrinterInfo.Model.QL_710W, PrinterInfo.Model.QL_720NW -> {
                        mPrinterInfo!!.labelNameIndex = 15
                        println("@@@labelNameIndex" + mPrinterInfo!!.labelNameIndex)
                        mPrinterInfo!!.isAutoCut = true
                        println("@@@isAutoCut" + mPrinterInfo!!.isAutoCut)
                        mPrinterInfo!!.isCutAtEnd = true
                        println("@@@isCutAtEnd" + mPrinterInfo!!.isCutAtEnd)
                    }
                    PrinterInfo.Model.PT_E550W, PrinterInfo.Model.PT_P750W -> {
                        val paper = sharedPreferences!!.getString("paperSize", "")
                        mPrinterInfo!!.labelNameIndex = LabelInfo.PT.valueOf(paper!!)
                            .ordinal
                        mPrinterInfo!!.isAutoCut = java.lang.Boolean.parseBoolean(
                            sharedPreferences!!
                                .getString("autoCut", "")
                        )
                        mPrinterInfo!!.isCutAtEnd = java.lang.Boolean
                            .parseBoolean(sharedPreferences!!.getString("endCut", ""))
                        mPrinterInfo!!.isHalfCut = java.lang.Boolean.parseBoolean(
                            sharedPreferences!!
                                .getString("halfCut", "")
                        )
                        mPrinterInfo!!.isSpecialTape = java.lang.Boolean
                            .parseBoolean(
                                sharedPreferences!!.getString(
                                    "specialType", ""
                                )
                            )
                    }
                    else -> {}
                }
            } else {
                mPrinterInfo!!.paperSize = PrinterInfo.PaperSize
                    .valueOf(sharedPreferences!!.getString("paperSize", "")!!)
            }
            mPrinterInfo!!.orientation = PrinterInfo.Orientation
                .valueOf(sharedPreferences!!.getString("orientation", "")!!)
            println("@@@orientation" + mPrinterInfo!!.orientation)
            input = sharedPreferences!!.getString("numberOfCopies", "1")
            if (input == "") input = "1"
            mPrinterInfo!!.numberOfCopies = input!!.toInt()
            println("@@@numberOfCopies" + mPrinterInfo!!.numberOfCopies)
            mPrinterInfo!!.halftone = PrinterInfo.Halftone.valueOf(
                sharedPreferences!!
                    .getString("halftone", "")!!
            )
            println("@@@halftone" + mPrinterInfo!!.halftone)
            mPrinterInfo!!.printMode = PrinterInfo.PrintMode
                .valueOf(sharedPreferences!!.getString("printMode", "")!!)
            println("@@@printMode" + mPrinterInfo!!.printMode)
            mPrinterInfo!!.pjCarbon = java.lang.Boolean.parseBoolean(
                sharedPreferences!!
                    .getString("pjCarbon", "")
            )
            println("@@@pjCarbon" + mPrinterInfo!!.pjCarbon)
            input = sharedPreferences!!.getString("pjDensity", "")
            if (input == "") input = "5"
            mPrinterInfo!!.pjDensity = input!!.toInt()
            println("@@@pjDensity" + mPrinterInfo!!.pjDensity)
            mPrinterInfo!!.pjFeedMode = PrinterInfo.PjFeedMode
                .valueOf(sharedPreferences!!.getString("pjFeedMode", "")!!)
            println("@@@pjFeedMode" + mPrinterInfo!!.pjFeedMode)
            mPrinterInfo!!.align = PrinterInfo.Align.valueOf(
                sharedPreferences!!
                    .getString("align", "")!!
            )
            println("@@@align" + mPrinterInfo!!.align)
            input = sharedPreferences!!.getString("leftMargin", "")
            if (input == "") input = "0"
            mPrinterInfo!!.margin.left = input!!.toInt()
            println("@@@margin.left" + mPrinterInfo!!.margin.left)
            mPrinterInfo!!.valign = PrinterInfo.VAlign.valueOf(
                sharedPreferences!!
                    .getString("valign", "")!!
            )
            println("@@@valign" + mPrinterInfo!!.valign)
            input = sharedPreferences!!.getString("topMargin", "")
            if (input == "") input = "0"
            mPrinterInfo!!.margin.top = input!!.toInt()
            input = sharedPreferences!!.getString("customPaperWidth", "")
            if (input == "") input = "5"
            mPrinterInfo!!.customPaperWidth = input!!.toInt()
            println("@@@customPaperWidth" + mPrinterInfo!!.customPaperWidth)
            input = sharedPreferences!!.getString("customPaperLength", "0")
            if (input == "") input = "0"
            mPrinterInfo!!.customPaperLength = input!!.toInt()
            println("@@@customPaperLength" + mPrinterInfo!!.customPaperLength)
            input = sharedPreferences!!.getString("customFeed", "")
            if (input == "") input = "0"
            mPrinterInfo!!.customFeed = input!!.toInt()
            println("@@@customFeed" + mPrinterInfo!!.customFeed)
            //customSetting = sharedPreferences.getString("customSetting", "");
            mPrinterInfo!!.paperPosition = PrinterInfo.Align
                .valueOf(sharedPreferences!!.getString("paperPostion", "LEFT")!!)
            println("@@@paperPosition" + mPrinterInfo!!.paperPosition)
            mPrinterInfo!!.dashLine = java.lang.Boolean.parseBoolean(
                sharedPreferences!!
                    .getString("dashLine", "false")
            )
            println("@@@dashLine" + mPrinterInfo!!.dashLine)
            mPrinterInfo!!.rjDensity = sharedPreferences!!.getString(
                "rjDensity", ""
            )!!.toInt()
            println("@@@rjDensity" + mPrinterInfo!!.rjDensity)
            mPrinterInfo!!.rotate180 = java.lang.Boolean.parseBoolean(
                sharedPreferences!!
                    .getString("rotate180", "")
            )
            println("@@@rotate180" + mPrinterInfo!!.rotate180)
            mPrinterInfo!!.peelMode = java.lang.Boolean.parseBoolean(
                sharedPreferences!!
                    .getString("peelMode", "")
            )
            println("@@@peelMode" + mPrinterInfo!!.peelMode)
            mPrinterInfo!!.mode9 = java.lang.Boolean.parseBoolean(
                sharedPreferences!!.getString(
                    "mode9", ""
                )
            )
            println("@@@mode9" + mPrinterInfo!!.mode9)
            mPrinterInfo!!.dashLine = java.lang.Boolean.parseBoolean(
                sharedPreferences!!
                    .getString("dashLine", "")
            )
            println("@@@dashLine" + mPrinterInfo!!.dashLine)
            input = sharedPreferences!!.getString("pjSpeed", "2")
            mPrinterInfo!!.pjSpeed = input!!.toInt()
            println("@@@pjSpeed" + mPrinterInfo!!.pjSpeed)
            mPrinterInfo!!.rollPrinterCase = PrinterInfo.PjRollCase
                .valueOf(
                    sharedPreferences!!.getString(
                        "printerCase",
                        "PJ_ROLLCASE_OFF"
                    )!!
                )
            println("@@@rollPrinterCase" + mPrinterInfo!!.rollPrinterCase)
            mPrinterInfo!!.skipStatusCheck = java.lang.Boolean.parseBoolean(
                sharedPreferences!!
                    .getString("skipStatusCheck", "false")
            )
            println("@@@skipStatusCheck" + mPrinterInfo!!.skipStatusCheck)
            input = sharedPreferences!!.getString("processTimeou", "")
            if (input == "") input = "0"
            input = sharedPreferences!!.getString("imageThresholding", "")
            if (input == "") input = "127"
            mPrinterInfo!!.thresholdingValue = input!!.toInt()
            println("@@@thresholdingValue" + mPrinterInfo!!.thresholdingValue)
            input = sharedPreferences!!.getString("scaleValue", "")
            if (input == "") input = "0"
            try {
                mPrinterInfo!!.scaleValue = input!!.toDouble()
                println("@@@scaleValue" + mPrinterInfo!!.scaleValue)
            } catch (e: NumberFormatException) {
                mPrinterInfo!!.scaleValue = 1.0
            }
            if (mPrinterInfo!!.printerModel == PrinterInfo.Model.TD_4000
                || mPrinterInfo!!.printerModel == PrinterInfo.Model.TD_4100N
            ) {
                mPrinterInfo!!.isAutoCut = java.lang.Boolean.parseBoolean(
                    sharedPreferences!!
                        .getString("autoCut", "")
                )
                mPrinterInfo!!.isCutAtEnd = java.lang.Boolean.parseBoolean(
                    sharedPreferences!!
                        .getString("endCut", "")
                )
            }
        }

    protected fun isLabelPrinter(model: PrinterInfo.Model?): Boolean {
        return when (model) {
            PrinterInfo.Model.QL_710W, PrinterInfo.Model.QL_720NW, PrinterInfo.Model.PT_E550W, PrinterInfo.Model.PT_P750W -> true
            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mScannerManager != null) {
            mScannerManager!!.recycle()
        }

        try {
            if (sunmiScanner != null)
                sunmiScanner!!.destory()
        }catch (e:Exception)
        {

        }
    }

    fun slideUpDown() {
        val Panel = binding!!.llsearch
        var isPanelShown = Panel.isShown
        if (!isPanelShown) {
            // Show the panel
            val bottomUp = AnimationUtils.loadAnimation(
                this,
                R.anim.bottom_up
            )
            Panel.startAnimation(bottomUp)
            Panel.visibility = View.VISIBLE
            isPanelShown = true
        } else {
            // Hide the Panel
            val bottomDown = AnimationUtils.loadAnimation(
                this,
                R.anim.bottom_down
            )
            Panel.startAnimation(bottomDown)
            Panel.visibility = View.GONE
            isPanelShown = false
        }
    }

    fun slideDown() {
        val Panel = binding!!.llsearch
        if (Panel.visibility == View.VISIBLE) {
            val bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down)
            Panel.startAnimation(bottomDown)
            Panel.visibility = View.GONE
        }
    }
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {

        if (BrandName.contains(
                Constant.SUNMI_K2MINI
            )
        ) {
            val action = event.action
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    //各种键值屏蔽,请根据自己的需求修改
                    if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || event.keyCode == KeyEvent.KEYCODE_BACK || event.keyCode == KeyEvent.KEYCODE_MENU || event.keyCode == KeyEvent.KEYCODE_HOME || event.keyCode == KeyEvent.KEYCODE_POWER) return super.dispatchKeyEvent(
                        event
                    )

                    //其他的键值,一般是扫码器数据
                    if (sunmiScanner != null) sunmiScanner!!.analysisKeyEvent(event)
                    return true
                }
                else -> {}
            }
        }
        return super.dispatchKeyEvent(event)
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
            if (model!!.status!!) {


                if (zapping_data_show.equals("") || zapping_data_show.equals("0")) {
                    binding!!.llCamera.visibility = View.GONE
                    binding!!.llMessage!!.visibility = View.VISIBLE
                    val mess = model!!.message

                    Log.e("@@EID",prefeMain!!.getString("event_id","").toString())
                    if(prefeMain!!.getString("event_id","").toString().equals("26"))
                    {
                        binding!!.txtEVENTWISE!!.visibility=View.VISIBLE
                        binding!!.txtEVENTWISE!!.setText("PLASTIVISION INDIA 2023")
                    }
                    else
                    {
                        binding!!.txtEVENTWISE!!.visibility=View.GONE
                    }

                    binding!!.txtWelcome!!.visibility=View.GONE
                    binding!!.txtUSERname!!.visibility=View.GONE
                    binding!!.txtMessage!!.text = mess
                    //   binding!!.txtUSERname!!.text = model.data!!.name!!.toUpperCase()
                    binding!!.lottieMain!!.visibility = View.VISIBLE
                    binding!!.lottieFail!!.visibility = View.GONE
                    binding!!.lottieMain!!.playAnimation()
                    binding!!.ivstatus!!.setImageDrawable(resources.getDrawable(R.drawable.success))
                    binding!!.llMessage!!.setBackgroundColor(resources.getColor(R.color.greenallowed))

                    binding!!.Constraitlay!!.visibility = View.GONE
                    binding!!.flMain!!.visibility = View.VISIBLE
                    resetscreen()


                    updateToServer()
                }
                else if (zapping_data_show.equals("1")) {


                    ticketmodel = TicketModel()
                    ticketmodel!!.e_ticket = model!!.data!!.eticketPath.toString()


                    binding!!.llInfo.visibility = View.VISIBLE

                    //ticketmodel!!.e_ticket = "https://s3.ap-south-1.amazonaws.com/test.bucket.in/Smart-Reg/.base_event./eticket/1684413407BASIC2023VD1PPBIVV.png"
                    swipePlaceHolderView!!.addView(
                        ETIcketcardView(
                            this@EntryActivity_Zapping_Newmini,
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

                    swipePlaceHolderView!!.addView(InfoUserView(this@EntryActivity_Zapping_Newmini,informodel!!,swipePlaceHolderView!!))


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


            } else {
                binding!!.txtUSERname!!.visibility=View.GONE
                binding!!.txtEVENTWISE!!.visibility=View.GONE
                binding!!.txtWelcome!!.visibility=View.GONE
                binding!!.llCamera.visibility = View.GONE
                binding!!.llMessage!!.visibility = View.VISIBLE
                binding!!.lottieMain!!.visibility = View.GONE
                binding!!.lottieFail!!.visibility = View.VISIBLE
                binding!!.lottieFail!!.playAnimation()
                binding!!.llMessage!!.setBackgroundColor(resources.getColor(R.color.solid_red))
                val mess = model!!.message
                binding!!.txtMessage!!.text = mess
                binding!!.Constraitlay!!.visibility = View.GONE
                binding!!.flMain!!.visibility = View.VISIBLE
                resetscreenfalse()
            }
        } catch (ex: Exception) {
            LogUtils.debug("@@ERROR", ex.message.toString());

            resetscreen()

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

        if (qrcode_scanned.contains("BEGIN:VCARD")) {
            val arrOfStr = qrcode_scanned.split("UC:").toTypedArray()
            qrcode_scanned = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }
        }

        if (qrcode_scanned.contains("BEGIN:VCARD")) {
            return
        }
        var searchKeyword = qrcode_scanned
        if (!CheckInternetConection.isInternetConnection(this@EntryActivity_Zapping_Newmini)) {
            val sweetAlertDialog = SweetAlertDialog(this@EntryActivity_Zapping_Newmini)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
            return
        }


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

            // presenter!!.ZappingUpdate(this, map, header, false, api_name!!, searchKeyword, location!!)
        } else {
            val sweetAlertDialog = SweetAlertDialog(this@EntryActivity_Zapping_Newmini)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
        }
    }

    override val context: Context?
        get() = this

    override fun onSwipeIn() {

    }

    private fun clearStcak() {

    }

    override fun onSwipeOut() {

    }

}