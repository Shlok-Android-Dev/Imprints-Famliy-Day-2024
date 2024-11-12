package com.runner.ui.activity.login


import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.usb.UsbManager
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.brother.ptouch.sdk.LabelInfo
import com.brother.ptouch.sdk.Printer
import com.brother.ptouch.sdk.PrinterInfo
import com.brother.ptouch.sdk.PrinterStatus
import com.brother.sdk.lmprinter.*
import com.brother.sdk.lmprinter.setting.*
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

import com.ruhe.utils.NetworkAlertUtility
import com.runner.R
import com.runner.View.OnspotView
import com.runner.adapter.CategoryAdapter
import com.runner.databinding.ActivityLoginBinding
import com.runner.databinding.ActivityOnspotRegistrationBinding
import com.runner.extra.ValidationMethod
import com.runner.extras.StickerPrinting
import com.runner.extras.appUtils
import com.runner.helper.BluetoothUtil
import com.runner.helper.ESCUtil
import com.runner.helper.SunmiPrintHelper

import com.runner.model.Constant
import com.runner.model.OnspotModel
import com.runner.presenter.OnspotPresenter
import com.runner.printdemo.common.Common
import com.runner.printdemo.common.MsgDialog
import com.runner.printdemo.common.MsgHandle
import com.runner.printdemo.printprocess.ImagePrint
import com.runner.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_onspot_registration.*
import okhttp3.RequestBody
import org.apache.commons.lang.WordUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class OnlineOnspotRegistrationActivity : BaseActivity(), OnspotView,OnItemSelectedListener,OnClickListener {

      var presenter : OnspotPresenter? =null
    var binding: ActivityOnspotRegistrationBinding? = null
     override var mHandle: MsgHandle? = null
    private var earlier_name = ArrayList<String>()
    private var covid_list = ArrayList<String>()
    private var gender_list = ArrayList<String>()
    private var type_list = ArrayList<String>()
    private var attendingon_list = ArrayList<String>()
    protected var mPrintResult: PrinterStatus? = null
    protected var mPrinterInfo: PrinterInfo? = null
    override var mDialog: MsgDialog? = null
    private var amount_to_paid = 0
    private var single_day_price = 0
    private var paymentType = ""
    private var date_list = ArrayList<String>()
    private var rl_badgeprint: RelativeLayout? = null
    private var mValuePhoneCOde = "+91"
    private var mypath: File? = null
    private var Earlier_saved = ""
    private var Covid_saved = ""
    private var Type_saved = ""
    private var Gender_Saved = ""
    private var Profession_Saved = ""
    private var sharedPreferences1: SharedPreferences? = null
    protected var mImageFiles: ArrayList<String?>? = null

    var type: String? = ""
    var sharedPreferences: SharedPreferences? = null


    private var testFont: String? = null
    private var record = 0
    private var isBold = false
    private var isUnderLine = false
    private val mStrings = arrayOf(
        "CP437",
        "CP850",
        "CP860",
        "CP863",
        "CP865",
        "CP857",
        "CP737",
        "CP928",
        "Windows-1252",
        "CP866",
        "CP852",
        "CP858",
        "CP874",
        "Windows-775",
        "CP855",
        "CP862",
        "CP864",
        "GB18030",
        "BIG5",
        "KSC5601",
        "utf-8"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onspot_registration)
        presenter = OnspotPresenter()
        presenter!!.setView(this)

        testFont = null
        record = 17
        isBold = false
        isUnderLine = false
        type = intent.getSerializableExtra("type") as String?








        sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this@OnlineOnspotRegistrationActivity)
        SunmiPrintHelper.instance.initSunmiPrinterService(this)
        SunmiPrintHelper.instance.initPrinter()

        binding!!.ch23!!.setOnClickListener(this)
      binding!!.ch24!!.setOnClickListener(this)
      binding!!.ch25!!.setOnClickListener(this)
      binding!!.ch26!!.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.white)
        }
        if (type.equals("paid", ignoreCase = true)) {
            binding!!.txtFee!!.visibility = View.GONE
        } else {
            binding!!.txtFee!!.visibility = View.GONE
        }
        binding!!.title!!.setText("Online Registration ")


        binding!!.ivBack.setOnClickListener(this)


        /*
        Unhide This........

       spinneer_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                final countrypicker.JobPicker picker = countrypicker.JobPicker.newInstance("Select Country",country.toString());

                picker.setListener(new CountryPickerListener() {

                    @Override
                    public void onSelectCountry(String name, String code) {


                        spinneer_country.setText(name);

                                */
        /*try {
                                    object.put("country",name);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/
        /*
                        System.out.println("@@name"+name);
                        //edcountyCOde.setText(CountryPicker.getCurrencyCode(code).toString());
                        hideSoftKeyboard(OnspotRegistrationActivity.this);
                        picker.dismiss();

                        loadState(code, finalI);

                    }
                });

                picker.show(OnspotRegistrationActivity.this.getSupportFragmentManager(), "COUNTRY_PICKER");


            }
        });

        spinner_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(spinneer_country.getText().toString().isEmpty()){

                    warning_country.setVisibility(View.VISIBLE);
                    return;
                }
                warning_country.setVisibility(View.GONE);

                final countrypicker.JobPickerState picker = countrypicker.JobPickerState.newInstance("Select State",state_Array.toString());

                picker.setListener(new CountryPickerListener() {

                    @Override
                    public void onSelectCountry(String name, String code) {


                        spinner_state.setText(name);

                              */
        /*  try {
                                    object.put("state",name);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/
        /*
                        System.out.println("@@name"+name);
                        //edcountyCOde.setText(CountryPicker.getCurrencyCode(code).toString());
                        hideSoftKeyboard(OnspotRegistrationActivity.this);
                        picker.dismiss();

                       // loadCity(code, finalI);

                    }
                });

                picker.show(OnspotRegistrationActivity.this.getSupportFragmentManager(), "STATE_PICKER");



            }
        });



        spinner_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(spinner_state.getText().toString().isEmpty()){

                    warning_state.setVisibility(View.VISIBLE);
                    return;
                }
                warning_state.setVisibility(View.GONE);


                final countrypicker.JobPickerCity picker = countrypicker.JobPickerCity.newInstance("Select City",city_Array.toString());

                picker.setListener(new CountryPickerListener() {

                    @Override
                    public void onSelectCountry(String name, String code) {


                        spinner_city.setText(name);

                               */
        /* try {
                                    object.put("city",name);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/
        /*
                        System.out.println("@@name"+name);
                        //edcountyCOde.setText(CountryPicker.getCurrencyCode(code).toString());
                        hideSoftKeyboard(OnspotRegistrationActivity.this);
                        picker.dismiss();



                    }
                });

                picker.show(OnspotRegistrationActivity.this.getSupportFragmentManager(), "STATE_PICKER");




            }
        });
*/
        binding!!.radiogroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val pos = binding!!.radiogroup!!.indexOfChild(findViewById(checkedId))
            when (pos) {
                0 -> paymentType = "Cash"
                1 -> paymentType = "UPI"
                2 -> paymentType = "Card"
            }
        })

     /*   binding!!.ccp!!.setOnCountryChangeListener(OnCountryChangeListener { selectedCountry ->
            mValuePhoneCOde = "+" + selectedCountry.phoneCode
        })*/
            binding!!.edName!!.addTextChangedListener(object : TextWatcher {
            var mStart = 0
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                mStart = i + i3
            }

            override fun afterTextChanged(editable: Editable) {
                val capitalizedText = WordUtils.capitalize(binding!!.edName!!.getText().toString())
                if (capitalizedText != binding!!.edName!!.getText().toString()) {
                    binding!!.edName!!.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable) {
                            binding!!.edName!!.setSelection(mStart)
                            binding!!.edName!!.removeTextChangedListener(this)
                        }
                    })
                    binding!!.edName!!.setText(capitalizedText)
                }
            }
        })


        setupUI(binding!!.rlmains, this)
        mPrinterInfo = PrinterInfo()
        mPrinter = Printer()
        mPrinterInfo = mPrinter!!.printerInfo
        gender_list.clear()
        gender_list.add("--Select Gender--")
        gender_list.add("Male")
        gender_list.add("Female.")
        gender_list.add("Other")
        val adapter_title = CategoryAdapter(applicationContext, R.layout.custom_spinner_layout, gender_list)
        binding!!.spinnerGender.setAdapter(adapter_title)
        val prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
        type_list.add("Select Option")
        type_list.add("VIP")
        type_list.add("General Visitor")
        type_list.add("Symposium")
        type_list.add("Complimentary")
        val adapter_type =
            CategoryAdapter(applicationContext, R.layout.custom_spinner_layout, type_list)
        binding!!.spinnerType!!.setAdapter(adapter_type)
        try {
            val prefeMain1 = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            val array = JSONArray(prefeMain1.getString("CATAGORY", ""))
            earlier_name.clear()
            earlier_name.add("--select--")
            earlier_name.add("Yes")
            earlier_name.add("No")
            /*  for(int i=0 ;i<array.length();i++){
                session_name .add(array.getJSONObject(i).getString("name"));
            }*/
            val adapter =
                CategoryAdapter(applicationContext, R.layout.custom_spinner_layout, earlier_name)
           binding!!.spinnerEarlier!!.setAdapter(adapter)
        } catch (exception: Exception) {
        }
        try {
            val prefeMain1 = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            val array = JSONArray(prefeMain1.getString("ATTENDING_ON", ""))
            attendingon_list.add("--select--")
            for (i in 0 until array.length()) {
                attendingon_list.add(array[i].toString())
            }
            val adapter11 =
                CategoryAdapter(applicationContext, R.layout.child_activity, attendingon_list)
            binding!!.spinnerProfession!!.setAdapter(adapter11)
        } catch (exception: Exception) {
        }
        covid_list.clear()
        covid_list.add("--select--")
        covid_list.add("Partially Vaccinated")
        covid_list.add("Fully Vaccinated")
        covid_list.add("Booster Shot")
        val adapter_covid =
            CategoryAdapter(applicationContext, R.layout.custom_spinner_layout, covid_list)
        binding!!.spinnerCovidStatus.setAdapter(adapter_covid)
        binding!!.spinnerEarlier!!.setOnItemSelectedListener(this)
        binding!!.spinnerGender!!.setOnItemSelectedListener(this)
        binding!!.spinnerCovidStatus!!.setOnItemSelectedListener(this)
        binding!!.spinnerProfession!!.setOnItemSelectedListener(this)
       binding!!.spinnerType!!.setOnItemSelectedListener(this)
        sharedPreferences1 = PreferenceManager
            .getDefaultSharedPreferences(this@OnlineOnspotRegistrationActivity)
       binding!!.btnOnsiteSubmit!!.setOnClickListener(this)
    }

    private fun calculatePrice() {

        if (Type_saved.contains("General Visitor") || Type_saved.contains("Symposium")) {
            amount_to_paid = date_list.size * single_day_price
            binding!!.amt.visibility = View.VISIBLE
            binding!!.amt.text = "Amount to paid Rs.$amount_to_paid"
             binding!!.llDate!!.visibility = View.VISIBLE
             binding!!.llPaymentMode!!.visibility = View.VISIBLE
        } else {
            binding!!.amt.visibility = View.GONE
             binding!!.llDate!!.visibility = View.GONE
             binding!!.llPaymentMode!!.visibility = View.GONE

        }
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
        //String runApp = sharedPreferences.getString("RUNAPP","1");
        //printTemplateSample();
        mDialog = MsgDialog(this)
        mHandle = MsgHandle(this, mDialog)
        myPrint = ImagePrint(this, mHandle, mDialog)
        val bluetoothAdapter = super.getBluetoothAdapter()
        myPrint!!.setBluetoothAdapter(bluetoothAdapter)
        mImageFiles = ArrayList()
        mImageFiles!!.add(mypath!!.absolutePath)

        (myPrint as ImagePrint).files = mImageFiles
        // when use bluetooth print set the adapter
        if (!checkUSB()) return
        val printTread: V4PrinterThread = V4PrinterThread(this)
        printTread.start()
    }

    fun printTemplateSample() {
        try {
            print()
        } catch (e: Exception) {

            //e.printStackTrace();
        }
    }

    fun print() {
        val printTread: PrinterThread = PrinterThread()
        printTread.start()
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, l: Long) {
        if (R.id.spinner_earlier == adapterView!!.id) {
            Earlier_saved = earlier_name[position]
        }
        else if (R.id.spinner_profession == adapterView!!.id) {
            Profession_Saved = attendingon_list[position]
        }
        else if (R.id.spinner_gender == adapterView!!.id) {
            Gender_Saved = gender_list[position]

        }
        else if (R.id.spinner_covid_status == adapterView!!.id) {
            Covid_saved = covid_list[position]
        }
        else if (R.id.spinner_type == adapterView!!.id) {
            //dropdownw
            Type_saved = type_list[position]
            if (Type_saved.contains("General Visitor") || Type_saved.contains("Symposium")) {
                 binding!!.llDate!!.visibility = View.VISIBLE
                binding!!.llPaymentMode!!.visibility = View.VISIBLE
                date_list.clear()
                var single_day_price1 = ""
                single_day_price = 0
                amount_to_paid = 0

                binding!!.amt.text = "Amount to paid Rs.0"
                val prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
                if (Type_saved.contains("General Visitor")) {
                    single_day_price1 = prefeMain!!.getString("Visitor_price", "").toString()
                }
                if (Type_saved.contains("Symposium")) {
                    single_day_price1 = prefeMain!!.getString("Symposium_price", "").toString()
                }

                single_day_price = single_day_price1.toInt()
                val date = SimpleDateFormat("dd", Locale.getDefault()).format(Date())

                binding!!.ch23!!.isClickable = true
                binding!!.ch24!!.isClickable = true
                binding!!.ch25!!.isClickable = true
                binding!!.ch26!!.isClickable = true
                binding!!.ch23!!.isChecked = false
                binding!!.ch24!!.isChecked = false
                binding!!.ch25!!.isChecked = false
                binding!!.ch26!!.isChecked = false
                visibleDAte(date)
            } else {
                if (Type_saved.contains("Select")) {
                    binding!!.llDate!!.visibility = View.GONE
                } else {
                     binding!!.llDate!!.visibility = View.VISIBLE
                    val date = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
                    visibleDAte(date)
                }
                amount_to_paid = 0

               binding!!.amt.visibility = View.GONE
                val date = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
                 binding!!.llPaymentMode!!.visibility = View.GONE

                binding!!.ch23!!.isChecked = true
                binding!!.ch24!!.isChecked = true
                binding!!.ch25!!.isChecked = true
                binding!!.ch26!!.isChecked = true
                binding!!.ch23!!.isClickable = false
                binding!!.ch24!!.isClickable = false
                binding!!.ch25!!.isClickable = false
                binding!!.ch26!!.isClickable = false
                if (date.contains("23")) {
                    date_list.clear()
                    date_list.add("23")
                    date_list.add("24")
                    date_list.add("25")
                    date_list.add("26")
                }
                if (date.contains("24")) {
                    date_list.clear()
                    date_list.add("24")
                    date_list.add("25")
                    date_list.add("26")
                }
                if (date.contains("25")) {
                    date_list.clear()
                    date_list.add("25")
                    date_list.add("26")
                }
                if (date.contains("26")) {
                    date_list.clear()
                    date_list.add("26")
                }
            }
        }
    }

    private fun visibleDAte(date: String) {
        if (date.contains("23")) {
            binding!!.ch23!!.visibility = View.VISIBLE
            binding!!.ch24!!.visibility = View.VISIBLE
            binding!!.ch25!!.visibility = View.VISIBLE
            binding!!.ch26!!.visibility = View.VISIBLE
        }
        if (date.contains("24")) {
            binding!!.ch23!!.visibility = View.GONE
            binding!!.ch24!!.visibility = View.VISIBLE
            binding!!.ch25!!.visibility = View.VISIBLE
            binding!!.ch26!!.visibility = View.VISIBLE
        }
        if (date.contains("25")) {
            binding!!.ch23!!.visibility = View.GONE
            binding!!.ch24!!.visibility = View.GONE
            binding!!.ch25!!.visibility = View.VISIBLE
            binding!!.ch26!!.visibility = View.VISIBLE
        }
        if (date.contains("26")) {
            binding!!.ch23!!.visibility = View.GONE
            binding!!.ch24!!.visibility = View.GONE
            binding!!.ch25!!.visibility = View.GONE
            binding!!.ch26!!.visibility = View.VISIBLE
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    override fun selectFileButtonOnClick() {}
    override fun printButtonOnClick() {}
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

    protected fun doPrint() {
        try {
            mImageFiles = ArrayList()
            // String runApp=sharedPreferences.getString("RUNAPP","1");
            if (File(mypath!!.absolutePath).exists()) {
                mImageFiles!!.add(mypath!!.absolutePath)
            }
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

    private inner class V4PrinterThread(val context: Context) : Thread() {
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

    private fun setCustomPaper() {
        when (mPrinterInfo!!.printerModel) {
            PrinterInfo.Model.RJ_4030, PrinterInfo.Model.RJ_4040, PrinterInfo.Model.RJ_3050, PrinterInfo.Model.RJ_3150, PrinterInfo.Model.TD_2020, PrinterInfo.Model.TD_2120N, PrinterInfo.Model.TD_2130N, PrinterInfo.Model.TD_4100N, PrinterInfo.Model.TD_4000 -> {}
            else -> {}
        }
    }

    //customSetting = sharedPreferences.getString("customSetting", "");
    private val preferences: Unit
        private get() {
            if (mPrinterInfo == null) {
                mPrinterInfo = PrinterInfo()

                return
            }

            var input: String? = ""
            mPrinterInfo!!.printerModel = PrinterInfo.Model.valueOf(
                sharedPreferences1!!.getString("printerModel", "")!!
            )
            println("@@@printerModel" + mPrinterInfo!!.printerModel)
            if (sharedPreferences!!.getString("port", "").equals("NET", ignoreCase = true)) {
                mPrinterInfo!!.port = PrinterInfo.Port.NET
            } else {
                mPrinterInfo!!.port = PrinterInfo.Port.USB
            }
            println("@@@@@port" + mPrinterInfo!!.port)
            mPrinterInfo!!.ipAddress = sharedPreferences1!!.getString("address", "")
            println("@@@ipAddress" + mPrinterInfo!!.ipAddress)
            mPrinterInfo!!.macAddress = sharedPreferences1!!.getString("macAddress", "")!!.uppercase(
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
                        val paper = sharedPreferences1!!.getString("paperSize", "")
                        mPrinterInfo!!.labelNameIndex = LabelInfo.PT.valueOf(paper!!)
                            .ordinal
                        mPrinterInfo!!.isAutoCut = java.lang.Boolean.parseBoolean(
                            sharedPreferences1!!.getString("autoCut", "")
                        )
                        mPrinterInfo!!.isCutAtEnd = java.lang.Boolean
                            .parseBoolean(sharedPreferences1!!.getString("endCut", ""))
                        mPrinterInfo!!.isHalfCut = java.lang.Boolean.parseBoolean(
                            sharedPreferences1!!.getString("halfCut", "")
                        )
                        mPrinterInfo!!.isSpecialTape = java.lang.Boolean
                            .parseBoolean(
                                sharedPreferences1!!.getString(
                                    "specialType", ""
                                )
                            )
                    }
                    else -> {}
                }
            } else {
                mPrinterInfo!!.paperSize = PrinterInfo.PaperSize
                    .valueOf(sharedPreferences1!!.getString("paperSize", "")!!)
            }
            mPrinterInfo!!.orientation = PrinterInfo.Orientation
                .valueOf(sharedPreferences1!!.getString("orientation", "")!!)
            println("@@@orientation" + mPrinterInfo!!.orientation)
            input = sharedPreferences1!!.getString("numberOfCopies", "1")
            if (input == "") input = "1"
            mPrinterInfo!!.numberOfCopies = input!!.toInt()
            println("@@@numberOfCopies" + mPrinterInfo!!.numberOfCopies)
            mPrinterInfo!!.halftone = PrinterInfo.Halftone.valueOf(
                sharedPreferences1!!.getString("halftone", "")!!
            )
            println("@@@halftone" + mPrinterInfo!!.halftone)
            mPrinterInfo!!.printMode = PrinterInfo.PrintMode
                .valueOf(sharedPreferences1!!.getString("printMode", "")!!)
            println("@@@printMode" + mPrinterInfo!!.printMode)
            mPrinterInfo!!.pjCarbon = java.lang.Boolean.parseBoolean(
                sharedPreferences1!!.getString("pjCarbon", "")
            )
            println("@@@pjCarbon" + mPrinterInfo!!.pjCarbon)
            input = sharedPreferences1!!.getString("pjDensity", "")
            if (input == "") input = "5"
            mPrinterInfo!!.pjDensity = input!!.toInt()
            println("@@@pjDensity" + mPrinterInfo!!.pjDensity)
            mPrinterInfo!!.pjFeedMode = PrinterInfo.PjFeedMode
                .valueOf(sharedPreferences1!!.getString("pjFeedMode", "")!!)
            println("@@@pjFeedMode" + mPrinterInfo!!.pjFeedMode)
            mPrinterInfo!!.align = PrinterInfo.Align.valueOf(
                sharedPreferences1!!.getString("align", "")!!
            )
            println("@@@align" + mPrinterInfo!!.align)
            input = sharedPreferences1!!.getString("leftMargin", "")
            if (input == "") input = "0"
            mPrinterInfo!!.margin.left = input!!.toInt()
            println("@@@margin.left" + mPrinterInfo!!.margin.left)
            mPrinterInfo!!.valign = PrinterInfo.VAlign.valueOf(
                sharedPreferences1!!.getString("valign", "")!!
            )
            println("@@@valign" + mPrinterInfo!!.valign)
            input = sharedPreferences1!!.getString("topMargin", "")
            if (input == "") input = "0"
            mPrinterInfo!!.margin.top = input!!.toInt()
            input = sharedPreferences1!!.getString("customPaperWidth", "")
            if (input == "") input = "5"
            mPrinterInfo!!.customPaperWidth = input!!.toInt()
            println("@@@customPaperWidth" + mPrinterInfo!!.customPaperWidth)
            input = sharedPreferences1!!.getString("customPaperLength", "0")
            if (input == "") input = "0"
            mPrinterInfo!!.customPaperLength = input!!.toInt()
            println("@@@customPaperLength" + mPrinterInfo!!.customPaperLength)
            input = sharedPreferences1!!.getString("customFeed", "")
            if (input == "") input = "0"
            mPrinterInfo!!.customFeed = input!!.toInt()
            println("@@@customFeed" + mPrinterInfo!!.customFeed)
            //customSetting = sharedPreferences.getString("customSetting", "");
            mPrinterInfo!!.paperPosition = PrinterInfo.Align
                .valueOf(sharedPreferences1!!.getString("paperPostion", "LEFT")!!)
            println("@@@paperPosition" + mPrinterInfo!!.paperPosition)
            mPrinterInfo!!.dashLine = java.lang.Boolean.parseBoolean(
                sharedPreferences1!!.getString("dashLine", "false")
            )
            println("@@@dashLine" + mPrinterInfo!!.dashLine)
            mPrinterInfo!!.rjDensity = sharedPreferences1!!.getString(
                "rjDensity", ""
            )!!.toInt()
            println("@@@rjDensity" + mPrinterInfo!!.rjDensity)
            mPrinterInfo!!.rotate180 = java.lang.Boolean.parseBoolean(
                sharedPreferences1!!.getString("rotate180", "")
            )
            println("@@@rotate180" + mPrinterInfo!!.rotate180)
            mPrinterInfo!!.peelMode = java.lang.Boolean.parseBoolean(
                sharedPreferences1!!.getString("peelMode", "")
            )
            println("@@@peelMode" + mPrinterInfo!!.peelMode)
            mPrinterInfo!!.mode9 = java.lang.Boolean.parseBoolean(
                sharedPreferences1!!.getString(
                    "mode9", ""
                )
            )
            println("@@@mode9" + mPrinterInfo!!.mode9)
            mPrinterInfo!!.dashLine = java.lang.Boolean.parseBoolean(
                sharedPreferences1!!.getString("dashLine", "")
            )
            println("@@@dashLine" + mPrinterInfo!!.dashLine)
            input = sharedPreferences1!!.getString("pjSpeed", "2")
            mPrinterInfo!!.pjSpeed = input!!.toInt()
            println("@@@pjSpeed" + mPrinterInfo!!.pjSpeed)
            mPrinterInfo!!.rollPrinterCase = PrinterInfo.PjRollCase
                .valueOf(
                    sharedPreferences1!!.getString(
                        "printerCase",
                        "PJ_ROLLCASE_OFF"
                    )!!
                )
            println("@@@rollPrinterCase" + mPrinterInfo!!.rollPrinterCase)
            mPrinterInfo!!.skipStatusCheck = java.lang.Boolean.parseBoolean(
                sharedPreferences1!!.getString("skipStatusCheck", "false")
            )
            println("@@@skipStatusCheck" + mPrinterInfo!!.skipStatusCheck)
            input = sharedPreferences1!!.getString("processTimeou", "")
            if (input == "") input = "0"
            input = sharedPreferences1!!.getString("imageThresholding", "")
            if (input == "") input = "127"
            mPrinterInfo!!.thresholdingValue = input!!.toInt()
            println("@@@thresholdingValue" + mPrinterInfo!!.thresholdingValue)
            input = sharedPreferences1!!.getString("scaleValue", "")
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
                    sharedPreferences1!!.getString("autoCut", "")
                )
                mPrinterInfo!!.isCutAtEnd = java.lang.Boolean.parseBoolean(
                    sharedPreferences1!!.getString("endCut", "")
                )
            }
        }

    protected fun isLabelPrinter(model: PrinterInfo.Model?): Boolean {
        return when (model) {
            PrinterInfo.Model.QL_710W, PrinterInfo.Model.QL_720NW, PrinterInfo.Model.PT_E550W, PrinterInfo.Model.PT_P750W -> true
            else -> false
        }
    }

   /* fun saveuser(jsonobjectMember: String) {
        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..")
        if (!CheckInternetConection.isInternetConnection(this@OnlineOnspotRegistrationActivity)) {
            val sweetAlertDialog = SweetAlertDialog(this@OnlineOnspotRegistrationActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText("please check Internet Connection...")
                .show()
            return
        }
        try {
            val webServiceHandler = WebServiceHandler(this@OnlineOnspotRegistrationActivity)
            webServiceHandler.webServiceListener = WebServiceListener { response ->



                //  response = "{\"success\":\"true\",\"message\":\"Login successfully.\",\"description\":{\"id\":\"4\",\"name\":\"Webcontxt\",\"email\":\"arjun@webcontxt.com\",\"logo\":\"https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAAUqAAAAJGUzMmJjZjA3LTM5ZjItNDkwNS05ODdlLWZkNzBmZGIzOGUyYg.png\",\"status\":\"1\",\"themecolor\":\"\",\"Question\":[{\"question\":\"Are you happy with your existing setup for customer feedback?\",\"action\":\"radio\",\"id\":\"1\"  ,\"actiondata\":[{\"id\":\"1\",\"smileypath\":\"http://172.16.16.74/novartis/uploads/smiley_very_sad.png\"},{\"id\":\"2\",\"smileypath\":\"http://172.16.16.74/novartis/uploads/smiley_sad.png\"},{\"id\":\"3\",\"smileypath\":\"http://172.16.16.74/novartis/uploads/smiley_neutral.png\"},{\"id\":\"4\",\"smileypath\":\"http://172.16.16.74/novartis/uploads/smiley_happy.png\"},{\"id\":\"5\",\"smileypath\":\"http://172.16.16.74/novartis/uploads/smiley_very_happy.png\"}]},{\"question\":\"How would you rate our services today?\",\"action\":\"rating\",\"id\":\"2\",\"starnumber\":\"5\"},{\"question\":\"How likely are you to recommend us to your friends and famliy?\",\"action\":\"number\",\"id\":\"3\",\"numbering\":\"5\"}]}}";
                Handler().postDelayed({ btn_onsite_submit!!.isEnabled = true }, 1500)
                try {
                    val jsonObject = JSONObject(response)

                    val success = jsonObject.getString("status")
                    if (success.equals("true", ignoreCase = true)) {

                        val dialog =
                            Dialog(this@OnlineOnspotRegistrationActivity, R.style.my_dialog)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(R.layout.dialog_success)
                        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
                        val height = (resources.displayMetrics.heightPixels * 0.45).toInt()
                        dialog.window!!.setLayout(width, height)
                        dialog.setCancelable(false)
                        val tv_msg = dialog.findViewById<TextView>(R.id.msg)
                        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
                        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)
                        // LottieAnimationView lottie_main = dialog.findViewById(R.id.lottie_main);
                        //   lottie_main.playAnimation();
                        tv_msg.text = jsonObject.getString("message")
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        btn_cancel.setOnClickListener {
                            dialog.dismiss()
                            Handler().postDelayed({
                                val intent = Intent(
                                    this@OnlineOnspotRegistrationActivity,
                                    OnlineOnspotRegistrationActivity::class.java
                                )
                                intent.putExtra("type", "paid")
                                startActivity(intent)
                                finish()
                            }, 800)
                        }
                        btn_ok.setOnClickListener {
                            dialog.dismiss()
                            val prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
                            val PRINTBY = prefeMain.getString("PRINTBY", Constant.SUNMI_V2_PLUS)
                            if (PRINTBY.equals(Constant.SUNMI_V2_PLUS, ignoreCase = true)) {
                                val lblprintname = findViewById<TextView>(R.id.lblprintname_sunmi)
                                val lblprintcategory =
                                    findViewById<TextView>(R.id.lblprintcategory_sunmi)
                                lblprintcategory.visibility = View.GONE
                                //TextView lblPrintDesignation = findViewById(R.id.lblPrintDesignation);
                                val qrcode_assigned =
                                    findViewById<TextView>(R.id.qrcode_assigned_sunmi)
                                val qrcode_assigned_sunmi =
                                    findViewById<TextView>(R.id.qrcode_assigned_sunmi)
                                val ivQr = findViewById<ImageView>(R.id.ivQr_sunmi)
                                // work here...
                                try {
                                    val jsonObjectinnerarr = jsonObject.getJSONObject("data")
                                    // JSONObject  jsonObjectinner = jsonObjectinnerarr.getJSONObject(0);
                                    val name = jsonObjectinnerarr.getString("first_name")
                                    if (name.length > 15) {
                                        lblprintname.textSize = 15f
                                    }
                                    *//* if(jsonObjectinnerarr.getString("category").length()>15){
                                                                                 lblprintcategory.setTextSize(15);
                                                                             }*//*lblprintname.setText(
                                        name.uppercase(
                                            Locale.getDefault()
                                        )
                                    )
                                    qrcode_assigned_sunmi.setText(
                                        jsonObjectinnerarr.getString("unique_code").uppercase(
                                            Locale.getDefault()
                                        )
                                    )
                                    //  lblprintcategory.setText(jsonObjectinnerarr.getString("category").substring(0, 1).toUpperCase()+jsonObjectinnerarr.getString("category").substring(1).toUpperCase());
                                    var bmp: Bitmap? = null
                                    val writer = QRCodeWriter()
                                    try {
                                        val bitMatrix = writer.encode(
                                            jsonObjectinnerarr.getString("unique_code"),
                                            BarcodeFormat.QR_CODE,
                                            512,
                                            512
                                        )
                                        val width = bitMatrix.width
                                        val height = bitMatrix.height
                                        bmp = Bitmap.createBitmap(
                                            width,
                                            height,
                                            Bitmap.Config.RGB_565
                                        )
                                        for (x in 0 until width) {
                                            for (y in 0 until height) {
                                                bmp.setPixel(
                                                    x,
                                                    y,
                                                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                                                )
                                            }
                                        }
                                        ivQr.setImageBitmap(bmp)
                                    } catch (e: WriterException) {
                                        e.printStackTrace()
                                    }
                                    val tempDir =
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
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
                                    val uniqueId =
                                        "IMGBADGE_" + System.currentTimeMillis().toString() + ".png"


                                    *//*  rl_badgeprint.setDrawingCacheEnabled(true);
                                
                                        rl_badgeprint.buildDrawingCache(true);
                                        Bitmap bitmap1 = Bitmap.createBitmap(rl_badgeprint.getWidth(), rl_badgeprint.getHeight()
                                                , Bitmap.Config.ARGB_8888);
                                        Canvas bitmapHolder = new Canvas(bitmap1);
                                        rl_badgeprint.draw(bitmapHolder);
                                        rl_badgeprint.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                        rl_badgeprint.layout(0, 0, rl_badgeprint.getMeasuredWidth(), rl_badgeprint.getMeasuredHeight());
                                        Bitmap bitmap = rl_badgeprint.getDrawingCache();
                                       // Bitmap bitmap =  rl_badgeprint.getDrawableState()).getBitmap();

                                
                                
                                        *//*rl_badgeprint = findViewById(R.id.rl_badgeprint_sunmi)
                                    rl_badgeprint.setDrawingCacheEnabled(true)
                                    rl_badgeprint.buildDrawingCache()
                                    val bitmap_ = drawToBitmap(
                                        rl_badgeprint,
                                        rl_badgeprint.getWidth(),
                                        rl_badgeprint.getHeight()
                                    )
                                    rl_badgeprint.setDrawingCacheEnabled(true)

                                    //  Matrix matrix = new Matrix();

                                    //matrix.postRotate(-90);

                                    // Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap_, 0, 0, bitmap_.getWidth(), bitmap_.getHeight(), matrix, true);

                                    *//* File  mypath = new File(tempDir, uniqueId);
                                        FileOutputStream fos = null;
                                        try {
                                            mypath.createNewFile();
                                            fos = new FileOutputStream(mypath);
                                            bitmap_.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                            fos.flush();
                                            fos.close();
                                            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap_, "Screen", "screen");
                                        }catch (FileNotFoundException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();

                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();

                                        }*//*
                                    val finalBmp = bmp
                                    Handler().postDelayed({
                                        if (!BluetoothUtil.isBlueToothPrinter) {
                                            val pos = arrayOf(
                                                resources.getString(R.string.align_left),
                                                resources.getString(R.string.align_mid),
                                                resources.getString(R.string.align_right)
                                            )
                                            //SunmiPrintHelper.getInstance().setAlign(0);
                                            // SunmiPrintHelper.getInstance().printText(nameString, 42, true, isUnderLine, testFont);
                                            SunmiPrintHelper.getInstance().setAlign(0)
                                            //   SunmiPrintHelper.getInstance().print1Line();
                                            //   SunmiPrintHelper.getInstance().printText("       ", 18, false, isUnderLine, testFont);
                                            SunmiPrintHelper.getInstance().printBitmap(bitmap_, 0)
                                            SunmiPrintHelper.getInstance().feedPaper()

                                            //  SunmiPrintHelper.getInstance().printBitmap(bitmap_,0);
                                            // SunmiPrintHelper.getInstance().printText("       ", 48, false, isUnderLine, testFont);
                                            //   SunmiPrintHelper.getInstance().feedPaper();
                                            *//*  //SunmiPrintHelper.getInstance().print1Line();
                                                                                            SunmiPrintHelper.getInstance().printText(nameString, 42, true, isUnderLine, testFont);
                                                                                            SunmiPrintHelper.getInstance().print1Line();
                                                                                            SunmiPrintHelper.getInstance().print1Line();
                                                                        
                                                                                            SunmiPrintHelper.getInstance().printText(companyString, 35, false, isUnderLine, testFont);
                                                                                            SunmiPrintHelper.getInstance().print1Line();
                                                                                            SunmiPrintHelper.getInstance().print1Line();
                                                                        
                                                                                            SunmiPrintHelper.getInstance().printText(desgString, 35, false, isUnderLine, testFont);
                                                                                            SunmiPrintHelper.getInstance().print1Line();
                                                                                            SunmiPrintHelper.getInstance().print1Line();
                                                                                            SunmiPrintHelper.getInstance().printQr("EEMA2022", 6, 3);
                                                                                            SunmiPrintHelper.getInstance().printText("EEMA2022", 35, false, isUnderLine, testFont);
                                                                                            SunmiPrintHelper.getInstance().print1Line();
                                                                                            SunmiPrintHelper.getInstance().printText("           ", 25, false, isUnderLine, testFont);
                                                                                            //  SunmiPrintHelper.getInstance().printText("", 29, isBold, isUnderLine, testFont);
                                                                                            SunmiPrintHelper.getInstance().print1Line();*//*
                                        } else {
                                            printByBluTooth(name)
                                        }
                                    }, 500L)
                                } catch (e: Exception) {
                                }
                                Handler().postDelayed({
                                    val intent = Intent(
                                        this@OnlineOnspotRegistrationActivity,
                                        OnlineOnspotRegistrationActivity::class.java
                                    )
                                    intent.putExtra("type", "paid")
                                    startActivity(intent)
                                    finish()
                                }, 1500)
                            } else if (PRINTBY.equals(Constant.BROTHER, ignoreCase = true)) {
                                val lblprintname = findViewById<TextView>(R.id.lblprintname)
                                val lblprintCompany = findViewById<TextView>(R.id.lblprintCompany)
                                val lblPrintDesignation =
                                    findViewById<TextView>(R.id.lblPrintDesignation)
                                val qrcode_assigned = findViewById<TextView>(R.id.qrcode_assigned)
                                val ivQr = findViewById<ImageView>(R.id.ivQr)
                                try {
                                    val jsonObjectinnerarr = jsonObject.getJSONObject("data")
                                    // JSONObject  jsonObjectinner = jsonObjectinnerarr.getJSONObject(0);
                                    val fname: String =
                                        jsonObjectinnerarr.getString("first_name").lowercase(
                                            Locale.getDefault()
                                        )
                                    val name =
                                        WordUtils.capitalize(fname) + " " + jsonObjectinnerarr.getString(
                                            "last_name"
                                        ).uppercase(
                                            Locale.getDefault()
                                        )
                                    //   String name =  jsonObjectinnerarr.getString("title") +" "+jsonObjectinnerarr.getString("first_name")+" "+jsonObjectinnerarr.getString("last_name");
                                    if (name.length > 18) {
                                        lblprintname.textSize = 30f
                                    }
                                    if (jsonObjectinnerarr.getString("designation")
                                            .toString().length > 16
                                    ) {
                                        // lblPrintDesignation.setTextSize(30);
                                    }
                                    if (jsonObjectinnerarr.getString("company")
                                            .toString().length > 16
                                    ) {
                                        lblprintCompany.textSize = 30f
                                    }
                                    lblprintname.text = name
                                    val upper = jsonObjectinnerarr.getString("company")
                                    lblprintCompany.text = upper
                                    lblPrintDesignation.visibility = View.GONE
                                    lblPrintDesignation.text =
                                        jsonObjectinnerarr.getString("designation")
                                    qrcode_assigned.text =
                                        "" + jsonObjectinnerarr.getString("unique_code")
                                    val writer = QRCodeWriter()
                                    try {
                                        val bitMatrix = writer.encode(
                                            jsonObjectinnerarr.getString("unique_code"),
                                            BarcodeFormat.QR_CODE,
                                            512,
                                            512
                                        )
                                        val width = bitMatrix.width
                                        val height = bitMatrix.height
                                        val bmp = Bitmap.createBitmap(
                                            width,
                                            height,
                                            Bitmap.Config.RGB_565
                                        )
                                        for (x in 0 until width) {
                                            for (y in 0 until height) {
                                                bmp.setPixel(
                                                    x,
                                                    y,
                                                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                                                )
                                            }
                                        }
                                        ivQr.setImageBitmap(bmp)
                                    } catch (e: WriterException) {
                                        e.printStackTrace()
                                    }


                                    // do Printing here....
                                    rl_badgeprint = findViewById(R.id.rl_badgeprint)
                                    Handler().postDelayed({ Printing() }, 700L)
                                    val dialog = Dialog(
                                        this@OnlineOnspotRegistrationActivity,
                                        R.style.my_dialog
                                    )
                                    dialog.setContentView(R.layout.reprint_alert_layoot)
                                    dialog.setCancelable(false)
                                    val tv_reprint = dialog.findViewById<TextView>(R.id.tv_print)
                                    val tv_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
                                    tv_reprint.setOnClickListener {
                                        tv_reprint.isEnabled = false
                                        Handler().postDelayed({
                                            Printing()
                                            tv_reprint.isEnabled = true
                                        }, 1000L)
                                    }
                                    tv_cancel.setOnClickListener {
                                        dialog.dismiss()
                                        try {
                                            val intent = Intent(
                                                this@OnlineOnspotRegistrationActivity,
                                                OnlineOnspotRegistrationActivity::class.java
                                            )
                                            intent.putExtra("type", type)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                        } catch (exception: Exception) {
                                            exception.printStackTrace()

                                        }
                                    }
                                    dialog.show()
                                } catch (e: JSONException) {
                                    e.printStackTrace()

                                }
                            } else if (PRINTBY.equals(Constant.OTHER, ignoreCase = true)) {

                                // Print share code....
                            }
                        }
                        dialog.show()
                    } else {
                        val sweetAlertDialog =
                            SweetAlertDialog(this@OnlineOnspotRegistrationActivity)
                        sweetAlertDialog.setTitleText("Alert!!")
                            .setContentText(jsonObject.getString("message"))
                            .show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(jsonobjectMember)
                webServiceHandler.RegisterUserOnline(jsonObject.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/



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



    override fun onBackPressed() {
        finish()
    }

    fun setclickListener(view: View?) {}
    private fun getIntegerArray(stringArray: ArrayList<String>): ArrayList<Int> {
        val result = ArrayList<Int>()
        for (stringValue in stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(stringValue.toInt())
            } catch (nfe: NumberFormatException) {
                //System.out.println("Could not parse " + nfe);
                Log.w("NumberFormat", "Parsing failed! $stringValue can not be an integer")
            }
        }
        return result
    }

    companion object {
        var mPrinter: Printer? = null
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

        private fun hideSoftKeyboard(activity: Activity) {
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

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.btn_onsite_submit -> {

             /*   if (Type_saved == "Select Option") {
                    binding!!.warningCategory!!.visibility = View.VISIBLE
                    return
                }
                binding!!.warningCategory!!.visibility = View.GONE*/
                if (binding!!.edName!!.getText().toString().isEmpty()) {
                    binding!!.warningName.visibility= View.VISIBLE

                    return
                }
                binding!!.warningName.visibility= View.GONE
                if (binding!!.edEmail!!.getText().toString().isEmpty()) {
                    binding!!.warningEmail!!.setVisibility(View.VISIBLE)
                    binding!!.warningEmail!!.setText("*Please fill Email")
                    return
                }
                binding!!.warningEmail!!.setVisibility(View.GONE)
                if (!ValidationMethod.emailValidation(binding!!.edEmail!!.getText().toString())) {
                    binding!!.warningEmail!!.setVisibility(View.VISIBLE)
                    binding!!.warningEmail!!.setText("*Invalid Email")
                    return
                }
                binding!!.warningEmail!!.setVisibility(View.GONE)
                if (binding!!.edPhone!!.getText().toString().isEmpty()) {
                  binding!!.warningPhone!!.setVisibility(View.VISIBLE)
                    binding!!.warningPhone!!.setText("*Please fill mobile number")
                    return
                }
                binding!!.warningPhone!!.setVisibility(View.GONE)
                if (binding!!.edPhone!!.getText().toString().length < 10) {
                    binding!!.warningPhone!!.setVisibility(View.VISIBLE)
                    binding!!.warningPhone!!.setText("*mobile number should not less than 10 digits")
                    return
                }
                binding!!.warningPhone!!.setVisibility(View.GONE)


                if (Type_saved.contains("General Visitor") || Type_saved.contains("Symposium")) {
                    if (date_list.size == 0) {
                       binding!!.warningDate.visibility = View.VISIBLE
                        return
                    }
                    binding!!.warningDate.visibility = View.GONE
                    if (paymentType.isEmpty()) {
                       binding!!.warningPaymentType.visibility= View.VISIBLE
                        return
                    }
                    binding!!.warningPaymentType.visibility= View.GONE
                }

                val `object` = JSONObject()
                try {
                    val app_created =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)
                    val name = binding!!.edName!!.getText().toString().trim { it <= ' ' }


                    //String name = ed_name.getText().toString().toUpperCase() ;
                    // String designation_upper = ed_designation.getText().toString().toUpperCase() ;
                    // String company_upper = ed_companyname.getText().toString().toUpperCase();
                    val dates = getIntegerArray(date_list)
                    Collections.sort(dates)
                    `object`.put("name", name)
                    `object`.put("email", binding!!.edEmail!!.getText().toString())
                    `object`.put("phone", binding!!.edPhone!!.getText().toString())
                    `object`.put(
                        "company",
                        WordUtils.capitalize(binding!!.edCompanyname!!.getText().toString().trim { it <= ' ' })
                    )
                    `object`.put(
                        "designation",
                        WordUtils.capitalize(binding!!.edDesignation!!.getText().toString().trim { it <= ' ' })
                    )
                    `object`.put(
                        "dates",
                        dates.toString().replace("[", "").replace("]", "").replace(", ", ",")
                    )
                    `object`.put("city", WordUtils.capitalize(binding!!.edCity!!.getText().toString()))
                    `object`.put("country_code", mValuePhoneCOde)
                    `object`.put("category", Type_saved)
                    `object`.put("amount", amount_to_paid.toString() + "")
                    `object`.put("payment_mode", paymentType)
                    val prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
                    val Mobile_str = prefeMain.getString("Phone_cache", "")













                    val sweetAlertDialognew = SweetAlertDialog(
                        this@OnlineOnspotRegistrationActivity,
                        SweetAlertDialog.WARNING_TYPE
                    )
                    sweetAlertDialognew.confirmText = "ok"

                    /*  Dialog dialog = new Dialog(OnlineOnspotRegistrationActivity.this,R.style.my_dialog);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_success);
                            dialog.setCancelable(false);
                            TextView tv_msg = dialog.findViewById(R.id.msg);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            tv_msg.setText("Registration Done");
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));




                            dialog.show();*/

                    /*object.put("title", Gender_Saved);
                             object.put("first_name",name);
                             object.put("last_name",ed_last_name.getText().toString().trim().toUpperCase());
                             object.put("organisation",ed_companyname.getText().toString().trim().toUpperCase());
                             object.put("designation",ed_designation.getText().toString().trim().toUpperCase());
                             object.put("email",ed_email.getText().toString().trim());
                             object.put("phone",ed_phone.getText().toString());
                            object.put("category",designation_upper.trim());
                            object.put("company",company_upper.trim());
                            object.put("country_code",mValuePhoneCOde);
                            object.put("attending_on", Profession_Saved);
                            object.put("vaccination",Covid_saved);
                            object.put("attending_as", Earlier_saved);
                            object.put("city", spinner_city.getText().toString().trim());
                            object.put("user_type","onsite");
                            object.put("app_created",app_created);*/

                    //{"name":"Ritesh","email":"ritesh.g@webcontxt.in","user_type":"onsite","company":"webcontxt","designation":"developer"}

                    /*    if(type.equalsIgnoreCase("paid")){
                                object.put("payment","1000");
                            }else
                            {
                                object.put("payment","");
                            }*/binding!!.btnOnsiteSubmit!!.setEnabled(false)
                    SaveUSer(`object`.toString())
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }


            }

            R.id.iv_back -> {
                  finish()
            }

            R.id.ch_23 ->{
                if (binding!!.ch23!!.isChecked()) {
                    date_list.add("23")
                    calculatePrice()
                } else {
                    date_list.remove("23")
                    calculatePrice()
                }
            }

            R.id.ch_24 ->{
                if (binding!!.ch24!!.isChecked()) {
                    date_list.add("24")
                    calculatePrice()
                } else {
                    date_list.remove("24")
                    calculatePrice()
                }
            }

            R.id.ch_25 ->{
                if ( binding!!.ch25!!.isChecked()) {
                    date_list.add("25")
                    calculatePrice()
                } else {
                    date_list.remove("25")
                    calculatePrice()
                }
            }

            R.id.ch_26 ->{
                if ( binding!!.ch26!!.isChecked()) {
                    date_list.add("26")
                    calculatePrice()
                } else {
                    date_list.remove("26")
                    calculatePrice()
                }
            }

        }

    }


    private fun SaveUSer(userobject : String) {
        if (NetworkAlertUtility.isConnectingToInternet(this)) {
            //StickerPrinting.sunmiPrinting("spot",this@OnlineOnspotRegistrationActivity,binding!!.rlBadgeprintSunmi,binding!!.edName.text.toString(),binding!!.edDesignation.text.toString(),binding!!.edCompanyname.text.toString(),"ABCDEFG23");
            val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()

            var header: HashMap<String, String> = appUtils.Takeheader(this, "")
            //  map["Username"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edtEmailMobile.text.toString().trim().replace(getString(R.string.countrycode) + " ", "").trim())
            //  map["Password"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edtPasword.text.toString().trim())
            //  map["fcm_token"] = RequestBody.create(MediaType.parse("text/plain"), mFcmToken)
            // map["Device_type"] = RequestBody.create(MediaType.parse("text/plain"), "Android")
          //  presenter!!.Saveuser(this, map, header, true, userobject)
        } else {
            //  appUtils.show_lert(getResources().getString(R.string.alert), getResources().getString(R.string.no_internet_avialable), 1, this)
        }
    }

    override fun OnSavedUSer(model: OnspotModel?, errCode: Int) {


        if(model!!.status!!){

            try {

                val dialog = Dialog(this@OnlineOnspotRegistrationActivity, R.style.my_dialog)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_success)
                val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
                val height = (resources.displayMetrics.heightPixels * 0.45).toInt()
                dialog.window!!.setLayout(width, height)
                dialog.setCancelable(false)

                val tv_msg = dialog.findViewById<TextView>(R.id.msg)
                val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
                val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)

                tv_msg.text = model!!.message
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                btn_cancel.setOnClickListener {
                    dialog.dismiss()
                    Handler().postDelayed({
                        val intent = Intent(
                            this@OnlineOnspotRegistrationActivity,
                            OnlineOnspotRegistrationActivity::class.java
                        )
                        intent.putExtra("type", "paid")
                        startActivity(intent)
                        finish()
                    }, 800)

                }

                btn_ok.setOnClickListener {
                    dialog.dismiss()



                   // StickerPrinting.sunmiPrinting("spot",this@OnlineOnspotRegistrationActivity,binding!!.rlBadgeprintSunmi,binding!!.edName.text.toString(),binding!!.edDesignation.text.toString(),binding!!.edCompanyname.text.toString(),"ABCDEFG23");


                   /* val PRINTBY = prefeMain.getString("PRINTBY", Constant.SUNMI_V2_PLUS)
                    if (PRINTBY.equals(Constant.SUNMI_V2_PLUS, ignoreCase = true)) {
                        val lblprintname: TextView = findViewById(R.id.lblprintname_sunmi)
                        val lblprintcategory: TextView =
                            findViewById(R.id.lblprintcategory_sunmi)
                        lblprintcategory.visibility = View.GONE
                        //TextView lblPrintDesignation = findViewById(R.id.lblPrintDesignation);
                        val qrcode_assigned: TextView = findViewById(R.id.qrcode_assigned_sunmi)
                        val qrcode_assigned_sunmi: TextView =
                            findViewById(R.id.qrcode_assigned_sunmi)
                        val ivQr: ImageView = findViewById(R.id.ivQr_sunmi)
                        // work here...
                        try {
                           // val jsonObjectinnerarr: JSONObject = jsonObject.getJSONObject("data")
                            // JSONObject  jsonObjectinner = jsonObjectinnerarr.getJSONObject(0);
                            val name = jsonObjectinnerarr.getString("first_name")
                            if (name.length > 15) {
                                lblprintname.textSize = 15f
                            }
                            *//* if(jsonObjectinnerarr.getString("category").length()>15){
                                                 lblprintcategory.setTextSize(15);
                                             }*//*lblprintname.text =
                                name.uppercase(Locale.getDefault())
                            qrcode_assigned_sunmi.text =
                                jsonObjectinnerarr.getString("unique_code")
                                    .uppercase(Locale.getDefault())
                            //  lblprintcategory.setText(jsonObjectinnerarr.getString("category").substring(0, 1).toUpperCase()+jsonObjectinnerarr.getString("category").substring(1).toUpperCase());
                            var bmp: Bitmap? = null
                            val writer = QRCodeWriter()
                            try {
                                val bitMatrix = writer.encode(
                                    jsonObjectinnerarr.getString("unique_code"),
                                    BarcodeFormat.QR_CODE,
                                    512,
                                    512
                                )
                                val width = bitMatrix.width
                                val height = bitMatrix.height
                                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                                for (x in 0 until width) {
                                    for (y in 0 until height) {
                                        bmp.setPixel(
                                            x,
                                            y,
                                            if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                                        )
                                    }
                                }
                                ivQr.setImageBitmap(bmp)
                            } catch (e: WriterException) {
                                e.printStackTrace()
                            }
                            val tempDir =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
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
                            val uniqueId =
                                "IMGBADGE_" + System.currentTimeMillis().toString() + ".png"


                            *//*  rl_badgeprint.setDrawingCacheEnabled(true);

        rl_badgeprint.buildDrawingCache(true);
        Bitmap bitmap1 = Bitmap.createBitmap(rl_badgeprint.getWidth(), rl_badgeprint.getHeight()
                , Bitmap.Config.ARGB_8888);
        Canvas bitmapHolder = new Canvas(bitmap1);
        rl_badgeprint.draw(bitmapHolder);
        rl_badgeprint.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        rl_badgeprint.layout(0, 0, rl_badgeprint.getMeasuredWidth(), rl_badgeprint.getMeasuredHeight());
        Bitmap bitmap = rl_badgeprint.getDrawingCache();
       // Bitmap bitmap =  rl_badgeprint.getDrawableState()).getBitmap();



        *//*rl_badgeprint = findViewById(R.id.rl_badgeprint_sunmi)
                            rl_badgeprint!!.setDrawingCacheEnabled(true)
                            rl_badgeprint!!.buildDrawingCache()
                            val bitmap_ = drawToBitmap(
                                rl_badgeprint,
                                rl_badgeprint!!.getWidth(),
                                rl_badgeprint!!.getHeight()
                            )
                            rl_badgeprint!!.setDrawingCacheEnabled(true)

                            //  Matrix matrix = new Matrix();

                            //matrix.postRotate(-90);

                            // Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap_, 0, 0, bitmap_.getWidth(), bitmap_.getHeight(), matrix, true);

                            *//* File  mypath = new File(tempDir, uniqueId);
        FileOutputStream fos = null;
        try {
            mypath.createNewFile();
            fos = new FileOutputStream(mypath);
            bitmap_.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap_, "Screen", "screen");
        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }*//*
                            val finalBmp = bmp
                            Handler().postDelayed({
                                if (!BluetoothUtil.isBlueToothPrinter) {
                                    val pos = arrayOf(
                                        resources.getString(R.string.align_left),
                                        resources.getString(R.string.align_mid),
                                        resources.getString(R.string.align_right)
                                    )
                                    //SunmiPrintHelper.getInstance().setAlign(0);
                                    // SunmiPrintHelper.getInstance().printText(nameString, 42, true, isUnderLine, testFont);
                                    SunmiPrintHelper.getInstance().setAlign(0)
                                    //   SunmiPrintHelper.getInstance().print1Line();
                                    //   SunmiPrintHelper.getInstance().printText("       ", 18, false, isUnderLine, testFont);
                                    SunmiPrintHelper.getInstance().printBitmap(bitmap_, 0)
                                    SunmiPrintHelper.getInstance().feedPaper()

                                    //  SunmiPrintHelper.getInstance().printBitmap(bitmap_,0);
                                    // SunmiPrintHelper.getInstance().printText("       ", 48, false, isUnderLine, testFont);
                                    //   SunmiPrintHelper.getInstance().feedPaper();
                                    *//*  //SunmiPrintHelper.getInstance().print1Line();
                                        SunmiPrintHelper.getInstance().printText(nameString, 42, true, isUnderLine, testFont);
                                        SunmiPrintHelper.getInstance().print1Line();
                                        SunmiPrintHelper.getInstance().print1Line();

                                        SunmiPrintHelper.getInstance().printText(companyString, 35, false, isUnderLine, testFont);
                                        SunmiPrintHelper.getInstance().print1Line();
                                        SunmiPrintHelper.getInstance().print1Line();

                                        SunmiPrintHelper.getInstance().printText(desgString, 35, false, isUnderLine, testFont);
                                        SunmiPrintHelper.getInstance().print1Line();
                                        SunmiPrintHelper.getInstance().print1Line();
                                        SunmiPrintHelper.getInstance().printQr("EEMA2022", 6, 3);
                                        SunmiPrintHelper.getInstance().printText("EEMA2022", 35, false, isUnderLine, testFont);
                                        SunmiPrintHelper.getInstance().print1Line();
                                        SunmiPrintHelper.getInstance().printText("           ", 25, false, isUnderLine, testFont);
                                        //  SunmiPrintHelper.getInstance().printText("", 29, isBold, isUnderLine, testFont);
                                        SunmiPrintHelper.getInstance().print1Line();*//*
                                } else {
                                    printByBluTooth(name)
                                }
                            }, 500L)
                        } catch (e: Exception) {
                        }
                        Handler().postDelayed({
                            val intent = Intent(
                                this@OnlineOnspotRegistrationActivity,
                                OnlineOnspotRegistrationActivity::class.java
                            )
                            intent.putExtra("type", "paid")
                            startActivity(intent)
                            finish()
                        }, 1500)
                    }
                    else if (PRINTBY.equals(Constant.BROTHER, ignoreCase = true)) {
                        val lblprintname: TextView = findViewById(R.id.lblprintname)
                        val lblprintCompany: TextView = findViewById(R.id.lblprintCompany)
                        val lblPrintDesignation: TextView =
                            findViewById(R.id.lblPrintDesignation)
                        val qrcode_assigned: TextView = findViewById(R.id.qrcode_assigned)
                        val ivQr: ImageView = findViewById(R.id.ivQr)
                        try {
                           // val jsonObjectinnerarr: JSONObject = jsonObject.getJSONObject("data")
                            // JSONObject  jsonObjectinner = jsonObjectinnerarr.getJSONObject(0);
                         //   val fname = jsonObjectinnerarr.getString("first_name") .lowercase(Locale.getDefault())
                           // val name = WordUtils.capitalize(fname) + " " + jsonObjectinnerarr.getString(
                                    "last_name"
                                )
                                    .uppercase(Locale.getDefault())
                            //   String name =  jsonObjectinnerarr.getString("title") +" "+jsonObjectinnerarr.getString("first_name")+" "+jsonObjectinnerarr.getString("last_name");
                            if (name.length > 18) {
                                lblprintname.textSize = 30f
                            }
                            if (jsonObjectinnerarr.getString("designation")
                                    .toString().length > 16
                            ) {
                                // lblPrintDesignation.setTextSize(30);
                            }
                            if (jsonObjectinnerarr.getString("company")
                                    .toString().length > 16
                            ) {
                                lblprintCompany.textSize = 30f
                            }
                            lblprintname.text = name
                            val upper = jsonObjectinnerarr.getString("company")
                            lblprintCompany.text = upper
                            lblPrintDesignation.visibility = View.GONE
                            lblPrintDesignation.text =
                                jsonObjectinnerarr.getString("designation")
                            qrcode_assigned.text =
                                "" + jsonObjectinnerarr.getString("unique_code")
                            val writer = QRCodeWriter()
                            try {
                                val bitMatrix = writer.encode(
                                    jsonObjectinnerarr.getString("unique_code"),
                                    BarcodeFormat.QR_CODE,
                                    512,
                                    512
                                )
                                val width = bitMatrix.width
                                val height = bitMatrix.height
                                val bmp =
                                    Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                                for (x in 0 until width) {
                                    for (y in 0 until height) {
                                        bmp.setPixel(
                                            x,
                                            y,
                                            if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                                        )
                                    }
                                }
                                ivQr.setImageBitmap(bmp)
                            } catch (e: WriterException) {
                                e.printStackTrace()
                            }


                            // do Printing here....
                            rl_badgeprint = findViewById(R.id.rl_badgeprint)
                            Handler().postDelayed({ Printing() }, 700L)
                            val dialog =
                                Dialog(this@OnlineOnspotRegistrationActivity, R.style.my_dialog)
                            dialog.setContentView(R.layout.reprint_alert_layoot)
                            dialog.setCancelable(false)
                            val tv_reprint = dialog.findViewById<TextView>(R.id.tv_print)
                            val tv_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
                            tv_reprint.setOnClickListener {
                                tv_reprint.isEnabled = false
                                Handler().postDelayed({
                                    Printing()
                                    tv_reprint.isEnabled = true
                                }, 1000L)
                            }
                            tv_cancel.setOnClickListener {
                                dialog.dismiss()
                                try {
                                    val intent = Intent(
                                        this@OnlineOnspotRegistrationActivity,
                                        OnlineOnspotRegistrationActivity::class.java
                                    )
                                    intent.putExtra("type", type)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                } catch (exception: Exception) {
                                    exception.printStackTrace()

                                }
                            }
                            dialog.show()
                        } catch (e: JSONException) {
                            e.printStackTrace()

                        }
                    }
                    else if (PRINTBY.equals(Constant.OTHER, ignoreCase = true)) {

                        // Print share code....
                    }*/
                }
                dialog.show()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }else{
            val sweetAlertDialog = SweetAlertDialog(this@OnlineOnspotRegistrationActivity)
            sweetAlertDialog.setTitleText("Alert!!")
                .setContentText(model!!.message)
                .show()
        }
    }

    override val context: Context?
        get() = this

}