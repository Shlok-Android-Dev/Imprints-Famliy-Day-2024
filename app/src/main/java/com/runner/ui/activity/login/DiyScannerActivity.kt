package com.runner.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.Settings
import android.transition.Slide
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener
import com.brother.ptouch.sdk.LabelInfo
import com.brother.ptouch.sdk.Printer
import com.brother.ptouch.sdk.PrinterInfo
import com.brother.ptouch.sdk.PrinterStatus
import com.brother.sdk.lmprinter.*
import com.brother.sdk.lmprinter.setting.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.camera.CameraSettings
import com.ruhe.utils.NetworkAlertUtility
import com.runner.R
import com.runner.View.RedemptionView
import com.runner.adapter.UserListAdapter
import com.runner.adapter.UserListAdapterDiy
import com.runner.cn.guanmai.scanner.IScannerManager
import com.runner.cn.guanmai.scanner.SupporterManager
import com.runner.databinding.ActivityDiyScannerBinding
import com.runner.extras.LogUtils
import com.runner.extras.StickerPrinting
import com.runner.extras.appUtils
import com.runner.helper.SunmiPrintHelper
import com.runner.manager.CheckInternetConection
import com.runner.model.Constant
import com.runner.model.RedemptionListModel
import com.runner.model.VerifyResponce
import com.runner.presenter.RedemptionPresenter
import com.runner.printdemo.common.Common
import com.runner.printdemo.common.MsgDialog
import com.runner.printdemo.common.MsgHandle
import com.runner.printdemo.printprocess.ImagePrint
import com.runner.tvs.Manaer.PrintfManager
import com.runner.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.chooseprinter_layout.*
import kotlinx.android.synthetic.main.chooseprinter_layout.cvCross
import kotlinx.android.synthetic.main.search_manually_dialog_layout.*
import kotlinx.android.synthetic.main.xml_list_item.view.*
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class DiyScannerActivity : BaseActivity(), UserListAdapterDiy.Clicklistener ,RedemptionView{
    val PRINT_HAND_FREEMIUM = "com.dynamixsoftware.printhand"
    val PRINT_HAND_PREMIUM = "com.dynamixsoftware.printhand.premium"
    protected var mPrintResult: PrinterStatus? = null
    protected var mPrinterInfo: PrinterInfo? = null
    var rl_badgeprint: RelativeLayout? = null
    private var mScannerManager: SupporterManager<*>? = null
    var presenter: RedemptionPresenter? = null
    var binding: ActivityDiyScannerBinding? = null
    var sharedPreferences: SharedPreferences? = null
    protected var mImageFiles: ArrayList<String?>? = null
    override var mHandle: MsgHandle? = null
    var is_front = 1
    override var mDialog: MsgDialog? = null
     var receiver: BroadcastReceiver? = null
    private var mUsbManager: UsbManager? = null

    private var device: UsbDevice? = null
    var mEnter = 0

    private var printfManager: PrintfManager? = null

    private var mPermissionIntent: PendingIntent? = null

    var qrcode_scanned = ""
    lateinit var public_array :String

    var prefeMain: SharedPreferences? = null
    var BrandName = ""

    var mCount = 0
    var app_start = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
    var name_list: MutableList<String> = ArrayList()
    var fname_list: MutableList<String> = ArrayList()
    var Lname_list: MutableList<String> = ArrayList()
    var email_list: MutableList<String> = ArrayList()
    var phone_list: MutableList<String> = ArrayList()
    var id_list: MutableList<String> = ArrayList()
    var company_list: MutableList<String> = ArrayList()
    var category_list: MutableList<String> = ArrayList()
    var code_list: MutableList<String> = ArrayList()
    var designationlist: MutableList<String> = ArrayList()
    var status_list: MutableList<String> = ArrayList()
    var isPrintList: MutableList<Int> = ArrayList()
    var isAllotList: MutableList<Int> = ArrayList()
    var attending_on: MutableList<String> = ArrayList()
    var etickit_List: MutableList<String> = ArrayList()


    var templeList: MutableList<String> = ArrayList()
    var fest_date_list: MutableList<String> = ArrayList()
    var linkedin_List: MutableList<String> = ArrayList()
    var website_List: MutableList<String> = ArrayList()
    var Registration_TypeList: MutableList<String> = ArrayList()

    var mypath: File? = null
    private val testFont: String? = null
    private val record = 0
    private val isBold = false
    private val isUnderLine = false

    var edsearchvalue = "";

    var iscontactcardprint = 0

    var code = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_diy_scanner)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        presenter = RedemptionPresenter()
        presenter!!.setView(this)
        binding!!.ivSwitch.visibility = View.GONE
        is_front = intent.getSerializableExtra("Camera") as Int
        SunmiPrintHelper.instance.initSunmiPrinterService(this)
        SunmiPrintHelper.instance.initPrinter()
        setupUI(binding!!.flMain, this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val txtsearchtext = findViewById<TextView>(R.id.txtsearchtext)
        val prefeMain1 = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        txtsearchtext.text = "Please enter "+prefeMain1.getString("searchfield","")!!.replace(",","/")
        binding!!.inputLayoutNAme!!.setHint(prefeMain1.getString("searchfield","")!!.replace(",","/"))
        mPrinterInfo = PrinterInfo()
        mPrinter = Printer()
        mPrinterInfo = mPrinter!!.printerInfo
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@DiyScannerActivity)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@DiyScannerActivity)
        appUtils.checkmanagestorage(this)
       /* binding!!.searchManualy!!.setOnLongClickListener {
            appUtils.showGenericDialog(this,true)
            false
        }*/



        if (Build.VERSION.SDK_INT >= 23) {
            val PERMISSION_ALL = 1
            val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SEND_SMS)
            if (!hasPermissions(this@DiyScannerActivity, *PERMISSIONS)) {
                ActivityCompat.requestPermissions(this@DiyScannerActivity, PERMISSIONS, PERMISSION_ALL)
            }
        }

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.primary_color)
        }

        findViewById<View>(R.id.ivcross).setOnClickListener { openBackDialog() }
        findViewById<View>(R.id.search_manualy).setOnClickListener {

            OpenSearchDialog()
           // slideUpDown()
        }
        findViewById<View>(R.id.flMain).setOnClickListener(View.OnClickListener {
            if (binding!!.edUnique!!.hasFocus()) {
                setupUI(findViewById(R.id.flMain), this@DiyScannerActivity)
                binding!!.edUnique!!.clearFocus()
                return@OnClickListener
            }
           // slideDown()
        })

        val PRINTER_TYPE = prefeMain1!!.getString("PRINTER_TYPE", "")
        val PRINTER_TYPE_TSC = prefeMain1!!.getString("PRINTER_TYPE_TSC", "")
        mUsbManager = getSystemService(USB_SERVICE) as UsbManager
        if(PRINTER_TYPE.equals("TSC")&&PRINTER_TYPE_TSC.equals("USB"))
        {

            val flags = PendingIntent.FLAG_IMMUTABLE // or PendingIntent.FLAG_MUTABLE

            mPermissionIntent =
                PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent(ACTION_USB_PERMISSION),
                    flags
                )
            val filter: IntentFilter = IntentFilter(ACTION_USB_PERMISSION)
            registerReceiver(mUsbReceiver, filter)

            // val accessoryList: Array<UsbAccessory> = mUsbManager!!.getAccessoryList()
            val deviceList: HashMap<String, UsbDevice> = mUsbManager!!.getDeviceList()
            Log.d("@@Detect ", deviceList.size.toString() + " USB device(s) found")
            /*Toast.makeText(
                this@EntryActivity,
                deviceList.size.toString() + " USB device(s) found",
                Toast.LENGTH_LONG
            ).show()*/
            val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
            while (deviceIterator.hasNext()) {
                device = deviceIterator.next()
                //   Log.e("@@devicename",""+device!!.deviceName)
                if (device!!.getVendorId() == 4611 ) {
                    //Toast.makeText(MainActivity.this, device.toString(), 0).show();
                    break
                }
            }


            try {
                val mPermissionIntent: PendingIntent
                mPermissionIntent = PendingIntent.getBroadcast(
                    this@DiyScannerActivity,
                    0,
                    Intent(ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_ONE_SHOT
                )
                mUsbManager!!.requestPermission(
                    device,
                    mPermissionIntent
                )
            } catch (e: java.lang.Exception) {
            }

        }
        if(PRINTER_TYPE.equals("TVS"))
        {
            printfManager = PrintfManager.getInstance(this)
            printfManager!!.defaultConnection()

            printfManager!!.addBluetoothChangLister(object : PrintfManager.BluetoothChangLister {
                override fun chang(name: String?, address: String?) {
                    Log.e("@@Name: ",name+" Address: "+address)
                }
            })


        }


        findViewById<View>(R.id.ivSwitch).setOnClickListener {
            if (is_front == 1) {
                is_front = -1
                val intent = Intent(this@DiyScannerActivity, DiyScannerActivity::class.java)
                intent.putExtra("Camera", -1)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@DiyScannerActivity, DiyScannerActivity::class.java)
                intent.putExtra("Camera", 1)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }


        BrandName = BrandName(this)
        if (!BrandName.contains("L2") && !BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) && !BrandName.contains(Constant.SUNMI_V2s_STGL_MODEL) && !BrandName.contains(Constant.SUNMI_K2MINI)) {


            resetscreen()
            findViewById<View>(R.id.scan_msg).visibility = View.GONE
            binding!!.barcodeScanner!!.decodeContinuous(callback)
            if (is_front != -1) {
                val cameraSettings = CameraSettings()
                cameraSettings.requestedCameraId = is_front
                binding!!.barcodeScanner!!.setCameraSettings(cameraSettings)
            }
            findViewById<View>(R.id.llGif).visibility = View.VISIBLE
            binding!!.barcodeScanner!!.setVisibility(View.VISIBLE)

        } else {


            /* findViewById(R.id.scan_msg).setVisibility(View.VISIBLE);
            initScanner();
            findViewById(R.id.llGif).setVisibility(View.GONE);
            barcode_scanner.setVisibility(View.GONE);
            layout_tool.setBackgroundColor(getResources().getColor(R.color.greenallowed));*/
            // mScannerManager.scannerEnable(true);
            resetscreen()
            findViewById<View>(R.id.scan_msg).visibility = View.GONE
            binding!!.barcodeScanner!!.decodeContinuous(callback)
            if (is_front != -1) {
                val cameraSettings = CameraSettings()
                cameraSettings.requestedCameraId = is_front
                binding!!.barcodeScanner!!.setCameraSettings(cameraSettings)
            }
            findViewById<View>(R.id.llGif).visibility = View.VISIBLE
            binding!!.barcodeScanner!!.setVisibility(View.VISIBLE)
            // mScannerManager.scannerEnable(true);
        }


       binding!!.btnSubmit!!.setOnClickListener(View.OnClickListener { // @@ fetch list ...
            if (binding!!.edUnique!!.getText().toString().isEmpty()) {
                binding!!.warningTxt!!.setText("please fill name/mobile number/email")
                binding!!.warningTxt!!.setVisibility(View.VISIBLE)
                return@OnClickListener
            }
           binding!!.warningTxt!!.setVisibility(View.GONE)
            getUserList(binding!!.edUnique!!.getText().toString())
        })

        findViewById<View>(R.id.ivback).setOnClickListener {
            findViewById<View>(R.id.llUserlist).visibility = View.GONE
            binding!!.llCamera!!.setVisibility(View.VISIBLE)
            resetscreen()
        }

        binding!!.ivcross.setOnClickListener({
            finish()
        })
    }



    private fun OpenSearchDialog(){
        val dialog = Dialog(this, R.style.my_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.search_manually_dialog_layout)
        dialog.setCancelable(false)
        dialog.show()
        val cvCross = dialog.cvCross.findViewById<CardView>(R.id.cvCross)
        val ed_name = dialog.ed_name.findViewById<EditText>(R.id.ed_name)
        val warning_name = dialog.warning_name.findViewById<TextView>(R.id.warning_name)
        val btnSearch = dialog.btnSearch.findViewById<Button>(R.id.btnSearch)
        edsearchvalue = ed_name.text.toString().trim()
        cvCross.setOnClickListener({
            dialog.dismiss()
        })

        btnSearch.setOnClickListener({
            if(ed_name.text.toString().isEmpty()){
                warning_name.visibility = View.VISIBLE
                return@setOnClickListener
            }
            warning_name.visibility = View.GONE

            getUserList(edsearchvalue);
            dialog.dismiss()
        })
    }

    private fun  MachineSetting() {
        prefeMain = this.getSharedPreferences("FRIENDS", MODE_PRIVATE)
        val PRINTER_TYPE = prefeMain!!.getString("PRINTER_TYPE", "")
        if(PRINTER_TYPE!!.contains("Sunmi",ignoreCase = true)){
              binding!!.changeMachine!!.visibility = View.GONE

            if( prefeMain!!.getString("MACHINE", "").equals("")){

          //   showMachine(PRINTER_TYPE)
            }
        }else{
            binding!!.changeMachine!!.visibility = View.GONE
        }
    }

   /* private fun showMachine(PRINTER_TYPE: String?) {

        if(!PRINTER_TYPE!!.contains("Sunmi",ignoreCase = true)){
            val sweetAlertDialog = SweetAlertDialog(this)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("Printer type is not Sunmi")
                .show()
           return
        }

        val dialog = Dialog(this, R.style.my_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_machine)
        val editor = prefeMain!!.edit()
        val sunmi_v2_s_plus = dialog.findViewById<TextView>(R.id.sunmi_v2_s_plus_machine)
        val sunmi_v2_s = dialog.findViewById<TextView>(R.id.sunmi_v2s_machine)
        sunmi_v2_s.setOnClickListener {
            editor.putString("MACHINE", Constant.SUNMI_V2s_MACHINE)
            editor.commit()
            dialog.dismiss()
        }
        sunmi_v2_s_plus.setOnClickListener {
            editor.putString("MACHINE", Constant.SUNMI_V2s_PLUS_MACHINE)
            editor.commit()
            dialog.dismiss()
        }
        dialog.show()
    }*/

    private fun openBackDialog() {
        val dialog = Dialog(this, R.style.my_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_back)
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.45).toInt()
        dialog.window!!.setLayout(width, height)
        dialog.setCancelable(false)
        val tv_msg = dialog.findViewById<TextView>(R.id.msg)
        val btn_ok = dialog.findViewById<Button>(R.id.btn_yes)
        val btn_no = dialog.findViewById<Button>(R.id.btn_no)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_ok.setOnClickListener {
            finish()
        }
        btn_no.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun getUserList(searchKeyword: String) {
        var searchKeyword = searchKeyword
        public_array = ""
        binding!!.edUnique!!.setText(searchKeyword)
        qrcode_scanned = searchKeyword
        binding!!.edUnique!!.setText(searchKeyword)
        if (!CheckInternetConection.isInternetConnection(this@DiyScannerActivity)) {
            val sweetAlertDialog = SweetAlertDialog(this@DiyScannerActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
            return
        }
        if (searchKeyword.contains("BEGIN:VCARD")) {
            val arrOfStr = searchKeyword.split("UC:").toTypedArray()
            searchKeyword = arrOfStr[1].replace("END:VCARD", "").trim { it <= ' ' }

        }

        name_list.clear()
        fname_list.clear()
        Lname_list.clear()
        email_list.clear()
        templeList.clear()
        phone_list.clear()
        id_list.clear()
        fest_date_list.clear()
        company_list.clear()
        code_list.clear()
        designationlist.clear()
        status_list.clear()
        isPrintList.clear()
        isAllotList.clear()
        attending_on.clear()
        category_list.clear()
        etickit_List.clear()
        linkedin_List.clear()
        website_List.clear()
        Registration_TypeList.clear()
        hideSoftKeyboard(this)

        if (NetworkAlertUtility.isConnectingToInternet(this)) {
            val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()

            var header: HashMap<String, String> = appUtils.Takeheader(this, "")
            //  map["Username"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edtEmailMobile.text.toString().trim().replace(getString(R.string.countrycode) + " ", "").trim())
            //  map["Password"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edtPasword.text.toString().trim())
            //  map["fcm_token"] = RequestBody.create(MediaType.parse("text/plain"), mFcmToken)
            // map["Device_type"] = RequestBody.create(MediaType.parse("text/plain"), "Android")
            prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            var api_name : String? = prefeMain!!.getString("Search_api", "")

            if (BrandName.contains(Constant.SUNMI_K2MINI)) {
                presenter!!.SearchUSer(this, map, header, true, searchKeyword,api_name!!)
            }
            else
            {

                presenter!!.SearchUSer(this, map, header, true, searchKeyword,api_name!!)

            }


        } else {
            val sweetAlertDialog = SweetAlertDialog(this@DiyScannerActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
        }




    }
    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            Log.e("@@QrCodesed","qrcode_scanned")
            if (result.text != null) {
                qrcode_scanned = ""
                binding!!.barcodeScanner!!.pause()
                qrcode_scanned = result.text
                Log.e("@@QrCode",qrcode_scanned)
                try {
                    if (qrcode_scanned.contains("?q=")) {
                        val array = qrcode_scanned.split("q=").toTypedArray()
                        qrcode_scanned = array[1]
                        binding!!.edUnique!!.setText(qrcode_scanned)
                    }
                    Log.e("@@QrCodesedsd","sdssds")
                    getUserList(qrcode_scanned)
                } catch (e: Exception) {
                    binding!!.llCamera!!.visibility = View.VISIBLE
                    binding!!.llMessage!!.visibility = View.GONE
                    resetscreen()
                }
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {

        }
    }

    private fun initScanner() {
        mScannerManager = SupporterManager<IScannerManager>(this, object : SupporterManager.IScanListener {
            override fun onScannerResultChange(result: String) {

                binding!!.edUnique!!.setText("")
                qrcode_scanned = ""
                Handler().postDelayed({
                    binding!!.edUnique!!.setText(result)
                    qrcode_scanned = result
                    //@@...

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

    private fun MbroacastReceiver(){
        val filterBrod = IntentFilter()
        filterBrod.addAction("com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED")
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                try {

                    Toast.makeText(this@DiyScannerActivity,"sucessfully",Toast.LENGTH_LONG).show()
                    var mcoDe = intent!!.getStringExtra("data")

                    if(isFinishing&& mcoDe!=null && mcoDe.isNotEmpty()) {
                        binding!!.edUnique!!.setText("")
                        qrcode_scanned = ""

                        Handler().postDelayed({
                            binding!!.edUnique!!.setText(mcoDe)
                            qrcode_scanned = mcoDe
                            //@@...

                            if (qrcode_scanned.contains("?q=")) {
                                val array = qrcode_scanned.split("q=").toTypedArray()

                                qrcode_scanned = array[1]
                            }
                            getUserList(qrcode_scanned)
                        }, 400L)
                    }
                }
                catch (e:Exception)
                {

                }

            }
        }
        registerReceiver(receiver, filterBrod)
    }

    override fun onBackPressed() {
        if ( binding!!.llUserlist!!.visibility == View.VISIBLE) {
            binding!!.llUserlist!!.visibility = View.GONE
            binding!!.llCamera!!.visibility = View.VISIBLE
            resetscreen()
            return
        } else {
            finish()
        }
    }

    private fun resetscreen() {
        Handler().postDelayed({
            binding!!.llCamera!!.visibility = View.VISIBLE
            binding!!.llMessage!!.visibility = View.GONE
            findViewById<View>(R.id.llUserlist).visibility = View.GONE
            binding!!.edUnique!!.setText("")
            binding!!.edUnique!!.setSelection(binding!!.edUnique!!.text.toString().length)
            BrandName = BrandName(applicationContext)

            if (!BrandName.contains("L2") && !BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) && !BrandName.contains(
                    Constant.SUNMI_V2s_STGL_MODEL) && !BrandName.contains(
                        Constant.SUNMI_K2MINI
                )
            ) {
                findViewById<View>(R.id.scan_msg).visibility = View.GONE
                binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.transparent))
                binding!!.barcodeScanner!!.resume()
                binding!!.barcodeScanner.refreshDrawableState()
            } else {
                findViewById<View>(R.id.scan_msg).visibility = View.GONE
                binding!!.layoutTool!!.setBackgroundColor(resources.getColor(R.color.transparent))
                binding!!.barcodeScanner!!.resume()
                binding!!.barcodeScanner.refreshDrawableState()
                binding!!.barcodeScanner.setStatusText("")
            }
        }, 300)
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
        try {
            var prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
            val isPrint = prefeMain.getString("REDEMPTION_PRINT_STATUS", "")
            iscontactcardprint = prefeMain!!.getInt("SWITCH_VALUE",0)

            if(iscontactcardprint==1){
                code =  "BEGIN:VCARD\n" +
                        "VERSION:2.1\n" +
                        "N:;" + fname_list.get(position) +" "+ Lname_list.get(position) +";;;\n" +
                        "TEL:" + phone_list.get(position) + "\n" +
                        "EMAIL:" + email_list.get(position) + "\n" +
                        "ORG:" + company_list.get(position) + "\n" +
                        "TITLE:" + designationlist.get(position) + "\n" +
                        "URL;TYPE= Linkedin:" + linkedin_List.get(position) + "\n" +
                        "URL;TYPE= Website:" + website_List.get(position) + "\n" +
                        "UC:" + code_list.get(position) + "\n" +
                        "END:VCARD\n";

                LogUtils.debug("@@Linked In",linkedin_List.get(position))
            } else{
                code = code_list.get(position).toUpperCase()
            }


            if(isPrint!!.equals("1")) {
                Log.e("PRINTER_TYPE_TSC3", PRINTER_TYPE.toString())
                if (PRINTER_TYPE.equals(Constant.SUNMI_K2MINI, ignoreCase = true)) {


                    Log.e("@@ERROR0: ", "123 " + JSONArray(public_array).get(position))
                    var jsonArray: JSONArray = JSONArray()

                    var jsonOk: JSONObject = JSONArray(public_array).get(position) as JSONObject;
                    var android_id = Settings.Secure.getString(
                        this@DiyScannerActivity.getContentResolver(),
                        Settings.Secure.ANDROID_ID
                    );
                    jsonOk.put("timestamp", "" + System.currentTimeMillis() + "_" + android_id)
                    jsonArray.put(jsonOk)
                    try {
                     //   SendData(jsonArray);
                    } catch (e: Exception) {
                        Log.e("@@EEE", "" + e.message)
                    }


                } else if (PRINTER_TYPE.equals("TVS", ignoreCase = true) || PRINTER_TYPE.equals(
                        Constant.SUNMI,
                        ignoreCase = true
                    ) || PRINTER_TYPE.equals("TSC", ignoreCase = true)
                ) {

                    var prefeMain = this!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)
                    var Row1 = prefeMain.getString("ROW1", "")
                    var Row2 = prefeMain.getString("ROW2", "")
                    var Row3 = prefeMain.getString("ROW3", "")//unique_code
                    var Row4 = prefeMain.getString("ROW4", "")
                    var Row5 = prefeMain.getString("ROW5", "")

                    var print_row1 = ""
                    var print_row2 = ""
                    var print_row3 = ""
                    var print_row4 = ""
                    var print_row5 = ""


                    var array = JSONArray(public_array)
                    var obj = array.getJSONObject(position)

                    if (obj.has(Row1)) {
                        print_row1 = obj.getString(Row1)
                    }
                    if (obj.has(Row2)) {
                        print_row2 = obj.getString(Row2)
                    }
                    if (obj.has(Row3)) {
                        print_row3 = obj.getString(Row3)
                    }
                    if (obj.has(Row4)) {
                        print_row4 = obj.getString(Row4)
                    }
                    if (obj.has(Row5)) {
                        print_row5 = obj.getString(Row5)
                    }
                    BrandName = BrandName(this)

                    //val relativeLayout = RelativeLayout(context)
                    Log.e("PRINTER_TYPE_TSC3", PRINTER_TYPE.toString())


                    if (PRINTER_TYPE.equals("TVS", ignoreCase = true) || PRINTER_TYPE.equals(
                            "TSC",
                            ignoreCase = true
                        ) || BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) || BrandName.contains(
                            Constant.SUNMI_K2MINI
                        )
                    ) {
                        Log.e("PRINTER_TYPE_TSC4", PRINTER_TYPE.toString())
                        if (!appUtils.BrandMan(this).toString().contains("sunmi",ignoreCase = true)) {
                            runOnUiThread {
                                Log.e("@@Comejere", "asd")
                                val layoutParams = binding!!.rlBadgeprintSunmi.getLayoutParams()
                                layoutParams.width = dpToPx(190)
                                layoutParams.height = dpToPx(200)
                                binding!!.rlBadgeprintSunmi.setLayoutParams(layoutParams)

                            }
                        } else {
                            Log.e("@@sunmi#", "1")
                            val layoutParams = binding!!.rlBadgeprintSunmi.getLayoutParams()
                            layoutParams.width = dpToPx(255)
                            layoutParams.height = dpToPx(255)
                            binding!!.rlBadgeprintSunmi.setLayoutParams(layoutParams)


                        }


                    } else if (BrandName.contains(Constant.SUNMI_V2s_STGL_MODEL)) {
                        val layoutParams = binding!!.rlBadgeprintV2s.getLayoutParams()
                        layoutParams.width = dpToPx(195)
                        layoutParams.height = dpToPx(245)

                        binding!!.rlBadgeprintSunmi.setLayoutParams(layoutParams)
                    } else {
                        runOnUiThread {
                            Log.e("@@Comejere", "asd")
                            val layoutParams = binding!!.rlBadgeprintSunmi.getLayoutParams()
                            layoutParams.width = dpToPx(190)
                            layoutParams.height = dpToPx(245)

                            binding!!.rlBadgeprintSunmi.setLayoutParams(layoutParams)
                            var linearLayout: LinearLayout =
                                findViewById<LinearLayout>(R.id.llInside)
                            val params =
                                linearLayout.getLayoutParams() as RelativeLayout.LayoutParams
                            params.removeRule(RelativeLayout.CENTER_IN_PARENT)
                            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                            params.setMargins(0, 10, 0, 0)
                            linearLayout.setLayoutParams(params)
                            linearLayout.requestLayout()
                        }

                    }

                    if (PRINTER_TYPE.equals("TVS", ignoreCase = true) || PRINTER_TYPE.equals(
                            "TSC",
                            ignoreCase = true
                        ) || BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) || BrandName.contains(
                            Constant.SUNMI_K2MINI
                        )
                    ) {
                        val PRINTER_TYPE_TSC =
                            prefeMain.getString("PRINTER_TYPE_TSC", "").toString()
                        if (PRINTER_TYPE_TSC.equals("USB", ignoreCase = true)) {
                            Log.e("PRINTER_TYPE_TSC1", PRINTER_TYPE_TSC)
                            StickerPrinting.sunmiPrinting(
                                "redeem",
                                this,
                                if (BrandName.contains(Constant.SUNMI_K2MINI)) binding!!.rlBadgeprint else binding!!.rlBadgeprintSunmi,
                                code,
                                print_row1,
                                print_row2,
                                print_row3,
                                print_row4,
                                print_row5,
                                BrandName,
                                mUsbManager,
                                device
                            )
                        } else if (PRINTER_TYPE_TSC.equals("Bluetooth", ignoreCase = true)) {
                            Log.e("PRINTER_TYPE_TSC2", PRINTER_TYPE_TSC)
                            StickerPrinting.sunmiPrinting(
                                "redeem",
                                this,
                                if (BrandName.contains(Constant.SUNMI_K2MINI)) binding!!.rlBadgeprint else binding!!.rlBadgeprintSunmi,
                                code,
                                print_row1,
                                print_row2,
                                print_row3,
                                print_row4,
                                print_row5,
                                BrandName,
                                mUsbManager,
                                device
                            )
                        } else {
                            Log.e("PRINTER_TYPE_TSC3", PRINTER_TYPE.toString())
                            if (PRINTER_TYPE.equals("TVS", ignoreCase = true)) {
                                // printfManager!!.defaultConnection()
                                StickerPrinting.sunmiPrintingTVS(
                                    "redeem",
                                    this,
                                    if (BrandName.contains(Constant.SUNMI_K2MINI)) binding!!.rlBadgeprint else binding!!.rlBadgeprintSunmi,
                                    code,
                                    print_row1,
                                    print_row2,
                                    print_row3,
                                    print_row4,
                                    print_row5,
                                    BrandName,
                                    printfManager
                                )

                            } else {
                                Log.e("PRINTER_TYPE_TSC3", PRINTER_TYPE_TSC.toString())
                                StickerPrinting.sunmiPrinting(
                                    "redeem",
                                    this,
                                    binding!!.rlBadgeprintSunmi,
                                    code,
                                    print_row1,
                                    print_row2,
                                    print_row3,
                                    print_row4,
                                    print_row5,
                                    BrandName
                                )
                            }


                        }


                    } else if (BrandName.contains(Constant.SUNMI_V2s_STGL_MODEL)) {
                        StickerPrinting.sunmiPrinting(
                            "redeem",
                            this,
                            binding!!.rlBadgeprintSunmi,
                            code,
                            print_row1,
                            print_row2,
                            print_row3,
                            print_row4,
                            print_row5,
                            BrandName
                        )
                    } else if (PRINTER_TYPE.equals(Constant.BROTHER, ignoreCase = true)) {
                        var prefeMain =
                            this!!.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)
                        var Row1 = prefeMain.getString("ROW1", "")
                        var Row2 = prefeMain.getString("ROW2", "")
                        var Row3 = prefeMain.getString("ROW3", "")//unique_code
                        var Row4 = prefeMain.getString("ROW4", "")
                        var Row5 = prefeMain.getString("ROW5", "")

                        var print_row1 = ""
                        var print_row2 = ""
                        var print_row3 = ""
                        var print_row4 = ""
                        var print_row5 = ""

                        var array = JSONArray(public_array)
                        var obj = array.getJSONObject(position)

                        if (obj.has(Row1)) {
                            print_row1 = obj.getString(Row1)
                        }
                        if (obj.has(Row2)) {
                            print_row2 = obj.getString(Row2)
                        }
                        if (obj.has(Row3)) {
                            print_row3 = obj.getString(Row3)
                        }
                        if (obj.has(Row4)) {
                            print_row4 = obj.getString(Row4)
                        }
                        if (obj.has(Row5)) {
                            print_row5 = obj.getString(Row5)
                        }

                        StickerPrinting.BROTHER_PRINTING(
                            "redeem",
                            this,
                            binding!!.rlBadgeprint,
                            code,
                            print_row1,
                            print_row2,
                            print_row3,
                            print_row4,
                            print_row5,
                            BrandName
                        )
                    } else if (PRINTER_TYPE.equals(Constant.OTHER, ignoreCase = true)) {
                        StickerPrinting.PrintShare_Printhand_Printing(
                            binding!!.webview,
                            this,
                            "https://live.dreamcast.in/eventbot/EtEdgeIndustry4Delhi/printBatch/1/app",
                            "redeem"
                        )
                    }
                } else {
                    //Alloted
                }

            }
            UpdateToServer(id_list.get(position), code_list.get(position))
        }catch (ex:Exception){

        }
    }

    /*private fun SendData(jsonArray: JSONArray){
        val key = databaseReference?.child("/PRINTING/")?.child("/${prefeMain!!.getString("PRINTER_TYPE_device","DEVICE")}/")?.push()?.key
        Log.e("@@ERROR1: ",""+jsonArray)
        databaseReference?.child("/PRINTING/")?.child("/${prefeMain!!.getString("PRINTER_TYPE_device","DEVICE")}/")?.setValue(jsonArray.toString())
            ?.addOnSuccessListener {
                //  dialog.dismiss()
                Log.e("@@ERROR2: ",""+"1")

                // Inflate custom layout


                val dialog = Dialog(this, R.style.my_dialog)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setContentView(R.layout.toast_center)
                dialog.setCancelable(false)
                dialog.show()
                Handler().postDelayed({
                    try {
                        if(dialog!=null && dialog.isShowing)
                        {
                            dialog.dismiss()
                        }
                    }
                    catch (E:Exception)
                    {

                    }


                }, 4000L)

// Create a Handler to delay the toast cancellation

// Create a Handler to delay the toast cancellation

                //Toast.makeText(this,"Printing in Progress",Toast.LENGTH_LONG).show()
            }
            ?.addOnFailureListener { e ->
                Log.e("Error", e.message!!)
                Log.e("@@ERROR3: ",""+ e.message!!)
            }


    }*/

    fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
    fun PxToDP(px: Int): Int {
        val scale = resources.displayMetrics.density
        return  (px-0.5f/scale).toInt()
    }


    private fun startPrintHandActivityFailed(intent: Intent): Boolean {
        return (startActivityFailed(intent, PRINT_HAND_FREEMIUM)
                && startActivityFailed(intent, PRINT_HAND_PREMIUM))
    }


    private fun showStartPrintHandActivityErrorDialog() {
        Toast.makeText(this, getString(R.string.message_error_start_intent), Toast.LENGTH_LONG)
            .show()
    }

    private fun startActivityFailed(intent: Intent, packageName: String): Boolean {
        return startActivityForResultFailed(intent, packageName, -1)
    }

     fun startActivityForResultFailed(
        intent: Intent,
        packageName: String,
        requestCode: Int
    ): Boolean {
        return try {
            intent.setPackage(packageName)
            startActivityForResult(intent, requestCode)
            false
        } catch (e: ActivityNotFoundException) {
            true
        }
    }

    override fun onAlloteClicked(position: Int) {
        Allot_ToServer(id_list[position], code_list[position])
    }

    override fun onshowTicket(position: Int) {
        findViewById<View>(R.id.rl_etickit).visibility = View.VISIBLE
        val webView = findViewById<ImageView>(R.id.webview1)
        Glide.with(this@DiyScannerActivity)
            .load(etickit_List[position])
            .into(webView)

        /*   WebView webView = findViewById(R.id.webview);
        try {



            webView.loadUrl(etickit_List.get(position));
            webView.getSettings().setJavaScriptEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }*/findViewById<View>(R.id.cross_e_tickit).setOnClickListener {
            findViewById<View>(R.id.rl_etickit).visibility = View.GONE
        }
    }

    override fun onCashlessRedeem(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onEditValue(position: Int) {
        TODO("Not yet implemented")
    }

    private fun PritingByUsB() {
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
        val bitmap = rl_badgeprint!!.drawingCache
        mypath = File(tempDir, uniqueId)
        val s = mypath!!.absolutePath
        try {
            mypath!!.createNewFile()
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
            val bitmapdata = bos.toByteArray()

//write the bytes in file
            val fos = FileOutputStream(mypath)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
        } catch (e: Exception) {

        }
        val file = File(mypath!!.absolutePath)
        var data_uri: Uri? = null
        data_uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                Objects.requireNonNull(
                    applicationContext
                ),
                com.runner.BuildConfig.APPLICATION_ID + ".provider", file
            )

        } else {
            Uri.fromFile(file)
        }
        val i = Intent(Intent.ACTION_VIEW)
        i.setPackage("com.dynamixsoftware.printershare")
        i.setDataAndType(data_uri, "image/jpeg")
        startActivity(i)
    }

    private fun UpdateToServer(id: String, code: String) {

        if (NetworkAlertUtility.isConnectingToInternet(this)) {
            val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()

            var header: HashMap<String, String> = appUtils.Takeheader(this, "")
            presenter!!.UpdateToServer(this, map, header, false, id,code,"updatediy")
        } else {
            val sweetAlertDialog = SweetAlertDialog(this@DiyScannerActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
        }
    }

    private fun Allot_ToServer(id: String, code: String) {

      /*  if (NetworkAlertUtility.isConnectingToInternet(this)) {
            val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()

            var header: HashMap<String, String> = appUtils.Takeheader(this, "")
            //  map["Username"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edtEmailMobile.text.toString().trim().replace(getString(R.string.countrycode) + " ", "").trim())
            //  map["Password"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edtPasword.text.toString().trim())
            //  map["fcm_token"] = RequestBody.create(MediaType.parse("text/plain"), mFcmToken)
            // map["Device_type"] = RequestBody.create(MediaType.parse("text/plain"), "Android")
            presenter!!.LoginUser(this, map, header, true, binding!!.edtEmailMobile.text.toString().trim())
        } else {
            val sweetAlertDialog = SweetAlertDialog(this@EntryActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            resetscreen()
        }*/
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



     fun Printing() {
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
        rl_badgeprint!!.isDrawingCacheEnabled = true
        rl_badgeprint!!.buildDrawingCache()

        // Work here for bitmap......
        val bitmap = drawToBitmap(rl_badgeprint, rl_badgeprint!!.width, rl_badgeprint!!.height)
        rl_badgeprint!!.isDrawingCacheEnabled = true

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

        }
        val runApp = sharedPreferences!!.getString("RUNAPP", "1")
        // printTemplateSample();
        mDialog = MsgDialog(this)
        mHandle = MsgHandle(this, mDialog)
        myPrint = ImagePrint(this, mHandle, mDialog)

        // when use bluetooth print set the adapter
        val bluetoothAdapter = super.getBluetoothAdapter()
        myPrint!!.setBluetoothAdapter(bluetoothAdapter)


        // printTemplateSample();
        mImageFiles = ArrayList()
        mImageFiles!!.add(mypath!!.absolutePath)

        (myPrint as ImagePrint).files = mImageFiles
        // when use bluetooth print set the adapter
        if (!checkUSB()) return
        val printTread: V4PrinterThread = V4PrinterThread(this)
        printTread.start()
    }

    private inner class V4PrinterThread(val context: Context) : Thread() {
        private fun waitForUSBAuthorizationRequest(port: PrinterInfo.Port) {
            if (port == PrinterInfo.Port.USB) {
                while (true) {
                    if (Common.mUsbAuthorizationState != Common.UsbAuthorizationState.NOT_DETERMINED) {
                        break
                    }

                    try {
                        sleep(50)
                    }
                    catch (e: InterruptedException) {

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
            mHandle!!.setResult("Success")
            msg = mHandle!!.obtainMessage(Common.MSG_PRINT_END)
            mHandle!!.sendMessage(msg)
        }
    }

    fun printTemplateSample() {
        try {
            print()
        } catch (e: Exception) {
        }
    }

    fun print() {
        val printTread: PrinterThread = PrinterThread()
        printTread.start()
    }

    protected inner class PrinterThread : Thread() {
        override fun run() {

            // set info. for printing
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

    @SuppressLint("SuspiciousIndentation")
    fun setPrinterInfo() {
        preferences
        println("getPreferences")
        setCustomPaper()
        mPrinter!!.printerInfo = mPrinterInfo
        val setPrinterInfoResult = mPrinter!!.setPrinterInfo(mPrinterInfo)
        if (mPrinterInfo!!.port == PrinterInfo.Port.USB) {

            while (true) {
                if (Common.mUsbAuthorizationState != Common.UsbAuthorizationState.NOT_DETERMINED)

                break
            }
        }
    }

    protected fun doPrint() {
        try {
            mImageFiles = ArrayList()
            val runApp = sharedPreferences!!.getString("RUNAPP", "1")
            mImageFiles!!.add(mypath!!.absolutePath)

            if (mImageFiles!!.contains(null)) {
                mImageFiles!!.remove(null)
            }
            ///storage/sdcard0/DCIM/Camera/IMG_20160315_105844.jpg
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
                sharedPreferences!!.getString("printerModel", "")!!
            )
            println("@@@printerModel" + mPrinterInfo!!.printerModel)
            if (sharedPreferences!!.getString("port", "").equals("NET", ignoreCase = true)) {
                mPrinterInfo!!.port = PrinterInfo.Port.NET
            } else {
                mPrinterInfo!!.port = PrinterInfo.Port.USB
            }
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
                            sharedPreferences!!.getString("autoCut", "")
                        )
                        mPrinterInfo!!.isCutAtEnd = java.lang.Boolean
                            .parseBoolean(sharedPreferences!!.getString("endCut", ""))
                        mPrinterInfo!!.isHalfCut = java.lang.Boolean.parseBoolean(
                            sharedPreferences!!.getString("halfCut", "")
                        )
                        mPrinterInfo!!.isSpecialTape = java.lang.Boolean
                            .parseBoolean(
                                sharedPreferences!!.getString(
                                    "specialType", ""
                                )
                            )
                    }
                    else -> {

                    }
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
                sharedPreferences!!.getString("pjCarbon", "")
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
                sharedPreferences!!.getString("dashLine", "false")
            )
            println("@@@dashLine" + mPrinterInfo!!.dashLine)
            mPrinterInfo!!.rjDensity = sharedPreferences!!.getString(
                "rjDensity", ""
            )!!.toInt()
            println("@@@rjDensity" + mPrinterInfo!!.rjDensity)
            mPrinterInfo!!.rotate180 = java.lang.Boolean.parseBoolean(
                sharedPreferences!!.getString("rotate180", "")
            )
            println("@@@rotate180" + mPrinterInfo!!.rotate180)
            mPrinterInfo!!.peelMode = java.lang.Boolean.parseBoolean(
                sharedPreferences!!.getString("peelMode", "")
            )
            println("@@@peelMode" + mPrinterInfo!!.peelMode)
            mPrinterInfo!!.mode9 = java.lang.Boolean.parseBoolean(
                sharedPreferences!!.getString(
                    "mode9", ""
                )
            )
            println("@@@mode9" + mPrinterInfo!!.mode9)
            mPrinterInfo!!.dashLine = java.lang.Boolean.parseBoolean(
                sharedPreferences!!.getString("dashLine", "")
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
                sharedPreferences!!.getString("skipStatusCheck", "false")
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
                    sharedPreferences!!.getString("autoCut", "")
                )
                mPrinterInfo!!.isCutAtEnd = java.lang.Boolean.parseBoolean(
                    sharedPreferences!!.getString("endCut", "")
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
            if(receiver!=null)
            {
                unregisterReceiver(receiver)
            }
        }
        catch (e:Exception)
        {}


    }

    fun slideUpDown() {
        val Panel = findViewById<View>(R.id.llsearch)
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
        val Panel = findViewById<View>(R.id.llsearch)
        if (Panel.visibility == View.VISIBLE) {
            val bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down)
            Panel.startAnimation(bottomDown)
            Panel.visibility = View.GONE
        }
    }



    private fun codeParse(value: Int): Byte {
        var res: Byte = 0x00
        when (value) {
            0 -> res = 0x00
            1, 2, 3, 4 -> res = (value + 1).toByte()
            5, 6, 7, 8, 9, 10, 11 -> res = (value + 8).toByte()
            12 -> res = 21
            13 -> res = 33
            14 -> res = 34
            15 -> res = 36
            16 -> res = 37
            17, 18, 19 -> res = (value - 17).toByte()
            20 -> res = 0xff.toByte()
            else -> {}
        }
        return res
    }

    companion object {
        //original code.....
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
    }

    @SuppressLint("SuspiciousIndentation")
    override fun OnSearchUser(model: RedemptionListModel?, errCode: Int) {

        try {
            if (model!!.getStatus()!!) {
                val gson = Gson()
                val listString = gson.toJson(model!!.data!!)
                var array = JSONArray(listString)


                if(array.length()==0){
                    SweetAlertDialog(this@DiyScannerActivity)
                        .setTitleText("Alert!")
                        .setContentText("No Data Found")
                        .show()
                    return
                }

                public_array = listString.toString()
                for(i in 0 until array!!.length()){
                 var obj : JSONObject=   array.getJSONObject(i)


                    if(obj.has("id")){
                        if(obj.isNull("id")){
                            id_list!!.add("")
                        }else {
                            id_list!!.add(obj.getString("id"))
                        }
                    }else{
                        id_list!!.add("")
                    }

                    if(obj.has("first_name")){
                        if(obj.isNull("first_name")){
                            fname_list!!.add("")
                        }else {
                            fname_list!!.add(obj.getString("first_name"))
                        }
                    }else {
                        fname_list!!.add("")
                    }

                    if(obj.has("last_name")){
                        if(obj.isNull("last_name")){
                            Lname_list!!.add("")
                        }else {
                            Lname_list!!.add(obj.getString("last_name"))
                        }
                    }else{
                        Lname_list!!.add("")
                    }


                    if(fname_list.get(i).isEmpty() && Lname_list.get(i).isEmpty()){

                        if(obj.isNull("name")){
                            name_list.add("")
                        }else{
                            name_list.add(obj.getString("name"))
                        }


                    }else{
                        var name = fname_list.get(i) +" "+Lname_list.get(i)
                        name_list.add(name.toString())
                    }

                    if(obj.has("temple")){
                        if(obj.isNull("temple")){
                            templeList!!.add("")
                        }else {
                            templeList!!.add(obj.getString("temple"))
                        }
                    }else{
                        templeList!!.add("")
                    }


                    if(obj.has("linkedin_profile")){
                        if(obj.isNull("linkedin_profile")){
                            linkedin_List!!.add("")
                        }else {
                            linkedin_List!!.add(obj.getString("linkedin_profile"))
                        }
                    }else{
                        linkedin_List!!.add("")
                    }

                    if(obj.has("website")){
                        if(obj.isNull("website")){
                            website_List!!.add("")
                        }else {
                            website_List!!.add(obj.getString("website"))
                        }
                    }else{
                        website_List!!.add("")
                    }

                    if(obj.has("user_type")){
                        if(obj.isNull("user_type")){
                            Registration_TypeList!!.add("")
                        }else {
                            Registration_TypeList!!.add(obj.getString("user_type"))
                        }
                    }else{
                        Registration_TypeList!!.add("")
                    }
                    if(obj.has("eticket_path")){
                        if(obj.isNull("eticket_path")){
                            etickit_List!!.add("")
                        }else {
                            etickit_List!!.add(obj.getString("eticket_path"))
                        }
                    }else{
                        etickit_List!!.add("")
                    }

              /*      if(obj.has("fname") && obj.has("lname")){
                      name_list.add(obj.getString("fname") +" "+obj.getString("lname"))
                    }else{
                        name_list.add("")
                    }*/

                    if(obj.has("email")){
                        if(obj.isNull("email")){
                            email_list!!.add("")
                        }else {
                            email_list!!.add(obj.getString("email"))
                        }
                    }else{
                        email_list!!.add("")
                    }

                    if(obj.has("phone")){
                        if(obj.isNull("phone")){
                            phone_list!!.add("")
                        }else {
                            phone_list!!.add(obj.getString("phone"))
                        }
                    }else{
                        phone_list!!.add("")
                    }


                    if(obj.has("company")){
                        if(obj.isNull("company")){
                            company_list!!.add("")
                        }else {
                            company_list!!.add(obj.getString("company"))
                        }
                    }else{
                        company_list!!.add("")
                    }

                    if(obj.has("designation")){
                        if(obj.isNull("designation")){
                            designationlist!!.add("")
                        }else {
                            designationlist!!.add(obj.getString("designation"))
                        }
                    }else{
                        designationlist!!.add("")
                    }


                    if(obj.has("unique_code")){
                        if(obj.isNull("unique_code")){
                            code_list!!.add("")
                        }else {
                            code_list!!.add(obj.getString("unique_code"))
                        }
                    }else{
                        code_list!!.add("")
                    }

                    if(obj.has("category")){
                        if(obj.isNull("category")){
                            category_list!!.add("")
                        }else {
                            category_list!!.add(obj.getString("category"))
                        }
                    }else{
                        category_list!!.add("")
                    }

                    if(obj.has("is_printed")){
                        if(obj.isNull("is_printed")){
                            isPrintList!!.add(0)
                        }else {
                            isPrintList!!.add(obj.getInt("is_printed"))
                        }
                    }else{
                        isPrintList!!.add(0)
                    }

                    if(obj.has("city_date_of_attendance")){
                        if(obj.isNull("city_date_of_attendance")){
                            attending_on!!.add("")
                        }else {
                            attending_on!!.add(obj.getString("city_date_of_attendance"))
                        }
                    }else{
                        attending_on!!.add("")
                    }
                }

                binding!!.llUserlist!!.visibility = View.VISIBLE
                binding!!.llCamera!!.visibility = View.GONE
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)



                val adapter = UserListAdapterDiy(
                    this@DiyScannerActivity,
                    name_list,
                    email_list,
                    phone_list,
                    id_list,
                    company_list,
                    code_list,
                    isPrintList,
                    status_list,
                    isAllotList,
                    attending_on,
                    category_list,
                    designationlist,
                    templeList,
                    Registration_TypeList,
                    etickit_List

                )

                binding!!.recyclerview.setHasFixedSize(true)
                binding!!.recyclerview!!.layoutManager = LinearLayoutManager(this@DiyScannerActivity)
                binding!!.recyclerview!!.adapter = adapter
                adapter.notifyDataSetChanged()


            } else {
                SweetAlertDialog(this@DiyScannerActivity)
                    .setTitleText("Alert!")
                    .setContentText(model!!.getMessage())
                    .show()
            }

        }catch (ex : Exception){
            resetscreen()

        }


    }

    override fun OnUpdateToServer(model: VerifyResponce?, errCode: Int) {

        try {
            if(model!!.status!!){
                var  sweetAlertDialog = SweetAlertDialog(this@DiyScannerActivity);
                try {
                    sweetAlertDialog.setTitleText("")
                        .setContentText(model.message)
                        .show();
                } catch (e: JSONException) {
                    throw  RuntimeException(e);
                }
                sweetAlertDialog.setCancelable(false)

                sweetAlertDialog.setConfirmClickListener {
                    Handler().postDelayed({
                        val intent = Intent(this@DiyScannerActivity, FinalDiyActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 500L)
                    sweetAlertDialog.dismiss()
                }

            }else{
                SweetAlertDialog(this@DiyScannerActivity)
                    .setTitleText("Alert!")
                    .setContentText(model.message)
                    .show()
            }

        }catch (e:Exception){
            e.printStackTrace()
        }


    }

    override val context: Context?
        get() = this
}