package com.runner.ui.activity.login

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.*
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.brother.ptouch.sdk.Printer
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.runner.R
import com.runner.View.OnspotView
import com.runner.databinding.*
import com.runner.extra.ValidationMethod
import com.runner.extras.LogUtils
import com.runner.extras.RoundImage
import com.runner.extras.StickerPrinting
import com.runner.extras.appUtils
import com.runner.helper.SunmiPrintHelper
import com.runner.model.Constant
import com.runner.model.OnspotModel
import com.runner.presenter.OnspotPresenter
import com.runner.tvs.Manaer.PrintfManager
import com.runner.ui.activity.BaseActivity
import com.runner.ui.fragment.NFCTAGREADWRITE
import okhttp3.MediaType
import okhttp3.RequestBody
import org.apache.commons.lang.WordUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class DiyOnspotActicvity : BaseActivity(), OnClickListener, OnspotView {

    var FormArray: JSONArray? = null
    var binding: ActivityDiyOnspotBinding? = null
    var requiredList: ArrayList<Int>? = null
    var titleList: ArrayList<String>? = null
    var typeList: ArrayList<String>? = null
    var valueList: ArrayList<String>? = null
    var dialogPrinting: Dialog? = null
    var field_nameList: ArrayList<String>? = null
    var EditText_List: ArrayList<EditText>? = null
    var warningList: ArrayList<TextView>? = null
    var activitymain: Activity? = null
    var prefeMain: SharedPreferences? = null
    var role_id = ""
    var vCard = false
    var presenter: OnspotPresenter? = null
    var iscontactcardprint = 0
    var code = ""
    var modelSave: OnspotModel? = null
    var mValuePhoneCOde = "+91"
    var ccp: CountryCodePicker? = null

    var isselected = false;

    var category = "";


    private var mPermissionIntent: PendingIntent? = null
    private var hasPermissionToCommunicate = false
    private val command = "" // La commande TSPL à envoyer

    private var printfManager: PrintfManager? = null
    private val uuid =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID générique pour les appareils Bluetooth série
    private var mUsbManager: UsbManager? = null

    private var device: UsbDevice? = null

    var BrandName = ""

    val REQUEST_ID_MULTIPLE_PERMISSIONS = 101

    lateinit var imageView: RoundImage
    var ACCESS_KEY: String = ""
    var SECRET_KEY: String = ""
    var south_ap_region = ""
    var MY_BUCKET = ""
    var APP_NAME = ""

    var image_to_Send: String = ""
    lateinit var edtValue: EditText
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    var databaseReference: DatabaseReference? = null
    var databaseReferenceChild: DatabaseReference? = null
    override val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED,
                            false
                        )
                    ) {
                        if (device != null) {
                            hasPermissionToCommunicate =
                                true
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diy_onspot)
        presenter = OnspotPresenter()
        presenter!!.setView(this)
        SunmiPrintHelper.instance.initSunmiPrinterService(this)
        SunmiPrintHelper.instance.initPrinter()



        prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)


        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.primary_color)
        }




        ACCESS_KEY = prefeMain!!.getString("S3_ACCESS_KEY", "")!!
        SECRET_KEY = prefeMain!!.getString("S3_SECRATE_KEY", "")!!
        south_ap_region = prefeMain!!.getString("S3_REGION", "")!!
        MY_BUCKET = prefeMain!!.getString("S3_BUCKET", "")!!
        //   APP_NAME = prefeMain!!.getString("AppName", "")!!
        APP_NAME = "regDemo"

        role_id = prefeMain!!.getString("ROLE_ID", "").toString()

        if (!prefeMain!!.getString("FORM1", "")!!.isEmpty()) {
            FormArray = JSONArray(prefeMain!!.getString("FORM1", ""))
        }


        category = prefeMain!!.getString("CategoryChooseForm", "").toString()
        LogUtils.debug("@@category", category)

        appUtils.checkmanagestorage(this)

        binding!!.ivBack.setOnClickListener({
            backPress1()
        })


        /* if(role_id.equals("4")){
             binding!!.settings.visibility = GONE
         }*/

        // MachineSetting()

        /*  binding!!.changeMachine!!.setOnClickListener {
            prefeMain = this.getSharedPreferences("FRIENDS", MODE_PRIVATE)
            val PRINTER_TYPE = prefeMain!!.getString("PRINTER_TYPE", "Sunmi")//remove default value
            PRINTER_TYPE?.let { it1 -> showMachine(it1!!.toString()) }

        }*/

        activitymain = this
        activitymain!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        requiredList = ArrayList<Int>()
        titleList = ArrayList<String>()
        typeList = ArrayList<String>()
        valueList = ArrayList<String>()
        field_nameList = ArrayList<String>()
        EditText_List = ArrayList<EditText>()
        warningList = ArrayList<TextView>()
        if (FormArray != null) {
            setupForm();
        }
        setupUI(binding!!.rlmains, this)
        binding!!.btnOnsiteSubmit.setOnClickListener(this)
        binding!!.settings.setOnClickListener(this)

        binding!!.settings.setOnLongClickListener(OnLongClickListener {
            val editor: SharedPreferences.Editor = prefeMain!!.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("from", "")
            intent.putExtra("role_id", "")
            intent.putExtra("user_id", "")
            startActivity(intent)
            finish()
            true
        })

        if (ccp != null) {
            ccp!!.setOnCountryChangeListener { selectedCountry ->
                mValuePhoneCOde = "+" + selectedCountry.phoneCode

            }
        }
        val PRINTER_TYPE = prefeMain!!.getString("PRINTER_TYPE", "")
        val PRINTER_TYPE_TSC = prefeMain!!.getString("PRINTER_TYPE_TSC", "")
        mUsbManager = getSystemService(USB_SERVICE) as UsbManager
        if (PRINTER_TYPE.equals("TSC") && PRINTER_TYPE_TSC.equals("USB")) {

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
            val deviceList: java.util.HashMap<String, UsbDevice> = mUsbManager!!.getDeviceList()
            Log.d("@@Detect ", deviceList.size.toString() + " USB device(s) found")
            /*   Toast.makeText(
                   this@DiyOnspotActicvity,
                   deviceList.size.toString() + " USB device(s) found",
                   Toast.LENGTH_LONG
               ).show()*/
            val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
            while (deviceIterator.hasNext()) {
                device = deviceIterator.next()
                //   Log.e("@@devicename",""+device!!.deviceName)
                if (device!!.getVendorId() == 4611) {
                    //Toast.makeText(MainActivity.this, device.toString(), 0).show();
                    break
                }
            }


            try {
                val mPermissionIntent: PendingIntent
                mPermissionIntent = PendingIntent.getBroadcast(
                    this@DiyOnspotActicvity,
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
        if (PRINTER_TYPE.equals("TVS")) {
            printfManager = PrintfManager.getInstance(context)
            printfManager!!.defaultConnection()

            printfManager!!.addBluetoothChangLister(object : PrintfManager.BluetoothChangLister {
                override fun chang(name: String?, address: String?) {

                }
            })

        }
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        databaseReferenceChild = FirebaseDatabase.getInstance().reference
        FirebaseApp.initializeApp(this)


    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun setupForm() {

        for (i in 0 until FormArray!!.length()) {
            requiredList!!.add(FormArray!!.getJSONObject(i).getInt("required"))
            titleList!!.add(FormArray!!.getJSONObject(i).getString("field_title"))
            typeList!!.add(FormArray!!.getJSONObject(i).getString("field_type"))
            field_nameList!!.add(FormArray!!.getJSONObject(i).getString("field_name"))

            if (FormArray!!.getJSONObject(i).isNull("value")) {
                valueList!!.add("")
            } else {
                valueList!!.add(FormArray!!.getJSONObject(i).getString("value"))
            }
        }
        CreateForm()
    }

    private fun CreateForm() {
        for (i in 0 until titleList!!.size) {


            if (typeList!!.get(i).equals("text")) {
                val mInflater = LayoutInflater.from(activitymain)
                val bindingView: RowAddFormBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.row_add_form, null, false)
                if (requiredList!!.get(i) == 1) {
                    bindingView!!.inputLayoutNAme.hint = titleList!!.get(i) + " *"
                } else {
                    bindingView!!.inputLayoutNAme.hint = titleList!!.get(i)
                }
                bindingView!!.edName.inputType = InputType.TYPE_CLASS_TEXT


                if (field_nameList!!.get(i).equals("Age", ignoreCase = true)) {
                    bindingView!!.edName!!.inputType = InputType.TYPE_CLASS_NUMBER
                    bindingView!!.edName!!.maxEms = 2
                }

                EditText_List!!.add(bindingView!!.edName)
                warningList!!.add(bindingView!!.txtErrorEmail)
                binding!!.container!!.addView(bindingView!!.rlMainLayout)


                bindingView!!.edName.setText(valueList!!.get(i))


                if (!field_nameList!!.get(i).contains("linkedin")) {
                    capatilize(bindingView!!.edName, 1)
                }


                /*     if (field_nameList!!.get(i).equals("first_name")) {
                         capatilize(bindingView!!.edName, 1)
                     } else {
                         capatilize(bindingView!!.edName, 0)
                     }*/


            } else if (typeList!!.get(i).contains("email")) {
                val mInflater = LayoutInflater.from(activitymain)
                val bindingView: RowAddFormBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.row_add_form, null, false)
                if (requiredList!!.get(i) == 1) {
                    bindingView!!.inputLayoutNAme.hint = titleList!!.get(i) + " *"
                } else {
                    bindingView!!.inputLayoutNAme.hint = titleList!!.get(i)
                }
                bindingView!!.edName.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                EditText_List!!.add(bindingView!!.edName)
                warningList!!.add(bindingView!!.txtErrorEmail)
                binding!!.container!!.addView(bindingView!!.rlMainLayout)
            } else if (typeList!!.get(i).contains("phone")) {
                val mInflater = LayoutInflater.from(activitymain)
                val bindingView: RowMobileBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.row_mobile, null, false)

                if (requiredList!!.get(i) == 1) {

                    bindingView!!.inputLayoutMobile.hint = titleList!!.get(i) + " *"
                } else {
                    bindingView!!.inputLayoutMobile.hint = titleList!!.get(i)
                }

                bindingView!!.edPhone.inputType = InputType.TYPE_CLASS_NUMBER

                ccp = bindingView!!.ccp

                var event_id = prefeMain!!.getString("event_id", "");
                var short_code = prefeMain!!.getString("default_country_code", "IN");

                //ccp!!.setCountryForNameCode(short_code!!)
                ccp!!.setCountryForNameCode(short_code!!)
                mValuePhoneCOde = "+" + ccp!!.selectedCountryCode


                //  ccp.getCoun
                /*  if(event_id.equals("13")){
                    ccp!!.setCountryForNameCode("AE")
                    mValuePhoneCOde =  "+971"
                }*/

                EditText_List!!.add(bindingView!!.edPhone)
                warningList!!.add(bindingView!!.txtErrorEmail)
                binding!!.container!!.addView(bindingView!!.rlMainLayout)
            } else if (typeList!!.get(i).contains("image")) {
                val mInflater = LayoutInflater.from(activitymain)
                val bindingView: RowProfileBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.row_profile, null, false)
                edtValue = bindingView.edtValue
                EditText_List!!.add(bindingView.edtValue)
                bindingView.ivprofile.setOnClickListener {
                    if (checkAndRequestPermissions(this@DiyOnspotActicvity)) {
                        chooseImage()
                    }
                    imageView = bindingView.ivprofile


                }
                warningList!!.add(bindingView!!.txtErrorEmail)
                binding!!.container!!.addView(bindingView!!.rlMainLayout)

            } else if (typeList!!.get(i).equals("textarea")) {
                val mInflater = LayoutInflater.from(activitymain)
                val bindingView: RowDescriptionBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.row_description, null, false)

                if (requiredList!!.get(i) == 1) {
                    bindingView!!.inputLayoutNAme.hint = titleList!!.get(i) + " *"
                } else {
                    bindingView!!.inputLayoutNAme.hint = titleList!!.get(i)
                }
                bindingView!!.edName.inputType = InputType.TYPE_CLASS_TEXT
                EditText_List!!.add(bindingView!!.edName)
                warningList!!.add(bindingView!!.txtErrorEmail)
                binding!!.container!!.addView(bindingView!!.rlMainLayout)
            } else if (typeList!!.get(i).contains("select")) {
                val mInflater = LayoutInflater.from(activitymain)
                val bindingView: RowDropdownBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.row_dropdown, null, false)

                if (requiredList!!.get(i) == 1) {
                    bindingView!!.spinnerGender.hint = titleList!!.get(i) + " *"
                } else {
                    bindingView!!.spinnerGender.hint = titleList!!.get(i)
                }
                var listSpinner1: ArrayList<String> = ArrayList<String>()
                listSpinner1.add("--select--")
                var spinnerDialog: SpinnerDialog? = null
                var listSpinner: List<String> = valueList!!.get(i).split("|").toList()
                for (k in 0 until listSpinner.size) {
                    listSpinner1.add(listSpinner.get(k))
                }

                bindingView.edName.setOnClickListener {
                    spinnerDialog!!.showSpinerDialog()
                }

                spinnerDialog = SpinnerDialog(
                    this@DiyOnspotActicvity,
                    listSpinner1,
                    titleList!!.get(i),
                    R.style.DialogAnimations_SmileWindow,
                    "Close"
                )




                spinnerDialog!!.setCancellable(true) // for cancellable

                spinnerDialog!!.setShowKeyboard(false) // for open keyboard by default
                EditText_List!!.add(bindingView!!.value)
                warningList!!.add(bindingView!!.txtError)

                spinnerDialog!!.bindOnSpinerListener { item, position ->

                    bindingView!!.value.setText(item)
                    bindingView.edName.setText(item)
                }


                /* val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
                     this,
                     android.R.layout.simple_spinner_item,
                     listSpinner1 as List<Any?>
                 )
                 adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                 bindingView!!.spinnerGender.setAdapter(adapter)
                 EditText_List!!.add(bindingView!!.value)
                 warningList!!.add(bindingView!!.txtError)

                 bindingView!!.spinnerGender!!?.onItemSelectedListener =
                     object : AdapterView.OnItemSelectedListener {
                         override fun onNothingSelected(parent: AdapterView<*>?) {

                         }

                         override fun onItemSelected(
                             parent: AdapterView<*>?,
                             view: View?,
                             position: Int,
                             id: Long
                         ) {


                             bindingView!!.value.setText(listSpinner1.get(position))
                             if (titleList!!.get(i).contains("Specialty")) {

                                 if (listSpinner1.get(position).contains("Others")) {

                                     bindingView!!.edOther!!.setText("")
                                     bindingView!!.inputLayoutNAme!!.visibility = VISIBLE

                                     bindingView!!.edOther!!.addTextChangedListener(object :
                                         TextWatcher {
                                         override fun afterTextChanged(s: Editable?) {
                                             bindingView!!.value.setText(bindingView.edOther!!.text.toString())
                                         }

                                         override fun beforeTextChanged(
                                             s: CharSequence?,
                                             start: Int,
                                             count: Int,
                                             after: Int
                                         ) {
                                         }

                                         override fun onTextChanged(
                                             s: CharSequence?,
                                             start: Int,
                                             before: Int,
                                             count: Int
                                         ) {


                                         }
                                     });

                                 } else {
                                     bindingView!!.inputLayoutNAme!!.visibility = GONE
                                 }


                             }


                         }*/



                binding!!.container!!.addView(bindingView!!.rlMainLayout)
            } else if (typeList!!.get(i).contains("radio", ignoreCase = true)) {
                val mInflater = LayoutInflater.from(activitymain)
                val bindingView: RowCheckboxMainBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.row_checkbox_main, null, false)

                if (requiredList!!.get(i) == 1) {
                    bindingView!!.lblText.text = titleList!!.get(i) + " *"
                } else {
                    bindingView!!.lblText.text = titleList!!.get(i)
                }

                bindingView.lblText.visibility = View.VISIBLE
                EditText_List!!.add(bindingView.edEmail)
                warningList!!.add(bindingView!!.txtErrorEmail)
                var listSpinner: List<String> = valueList!!.get(i).split(",").toList()

                for (j in 0 until listSpinner.size) {

                    var radioButton: RadioButton = RadioButton(this)
                    radioButton.setText(listSpinner!!.get(j))

                    radioButton.setOnClickListener {
                        bindingView.edEmail.setText(radioButton.text.toString())
                    }

                    bindingView!!.radioGroup.addView(radioButton)
                }
                binding!!.container!!.addView(bindingView!!.rlMainLayout)

            } else if (typeList!!.get(i).contains("checkbox", ignoreCase = true)) {

                val mInflater = LayoutInflater.from(activitymain)
                var values_list = java.util.ArrayList<String>()
                val bindingView: RowCheckboxBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.row_checkbox, null, false)

                if (requiredList!!.get(i) == 1) {
                    bindingView!!.lblText.text = titleList!!.get(i) + " *"
                } else {
                    bindingView!!.lblText.text = titleList!!.get(i)
                }

                bindingView.lblText.visibility = View.VISIBLE

                EditText_List!!.add(bindingView.edEmail)
                warningList!!.add(bindingView!!.txtErrorEmail)


                var listSpinner: List<String> = valueList!!.get(i).split(",").toList()


                for (j in 0 until listSpinner.size) {

                    var checkbox: CheckBox = CheckBox(this)
                    checkbox.setText(listSpinner!!.get(j))

                    checkbox.setOnClickListener {

                        if (checkbox.isChecked()) {
                            values_list!!.add(checkbox.text.toString())
                        } else {
                            values_list!!.remove(checkbox.text.toString())
                        }
                        bindingView!!.edEmail!!.setText(
                            values_list.toString().replace("[", "").replace("]", "")
                                .replace(", ", ",")
                        )
                    }

                    bindingView!!.containerCheckbox.addView(checkbox)
                }
                binding!!.container!!.addView(bindingView!!.rlMainLayout)
            }
        }
    }

    fun checkAndRequestPermissions(context: Activity?): Boolean {
        val WExtstorePermission = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val cameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        val listPermissionsNeeded: MutableList<String> = java.util.ArrayList()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                .add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                context, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> if (ContextCompat.checkSelfPermission(
                    this@DiyOnspotActicvity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT
                )
                    .show()
            } else if (ContextCompat.checkSelfPermission(
                    this@DiyOnspotActicvity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "FlagUp Requires Access to Your Storage.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                chooseImage()
            }
        }
    }

    private fun chooseImage() {
        val array = arrayOf("image/png")
        ImagePicker.with(this@DiyOnspotActicvity).cameraOnly()
            .crop(1f, 1f)
            .galleryMimeTypes(array)
            .compress(950)
            .maxResultSize(500, 500)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri = data!!.data
            val selectedImage = data!!.data
            val myFile = File(selectedImage!!.path)

            imageView.setImageURI(uri)
            /*if (myFile.absolutePath == "") {
                imageView.setVisibility(GONE)
                txtUpload.setVisibility(VISIBLE)
            } else {
                imageView.setVisibility(VISIBLE)
                txtUpload.setVisibility(GONE)
            }*/
            uplod_Amazon_s3(myFile.absolutePath, myFile)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uplod_Amazon_s3(absolutePath: String, myFile: File) {

        val bmp = (imageView.drawable as BitmapDrawable).bitmap
        val new_path: String = convertToPNG(bmp, myFile.name)!!
        val new_file = File(new_path)
        val s3Client: AmazonS3 = AmazonS3Client(BasicSessionCredentials(ACCESS_KEY, SECRET_KEY, ""))
        s3Client.setRegion(Region.getRegion(south_ap_region))
        val transferUtility = TransferUtility(s3Client, applicationContext)
        val transferObserver = transferUtility.upload(
            MY_BUCKET + APP_NAME + "/onspot", new_file.name,
            new_file,
            CannedAccessControlList.PublicReadWrite
        )
        //https://s3.ap-south-1.amazonaws.com/jlf2022regdata.bucket/IMG_20230111_171913494.jpg
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {

                //String url = SECRET_KEY+""+/$fileName.$extension"
                val url =
                    "https://s3.ap-south-1.amazonaws.com/" + MY_BUCKET + APP_NAME + "/onspot/" + transferObserver.key

                edtValue.setText(url)
                image_to_Send = url
                // findViewById(R.id.warning_iv_profile).setVisibility(View.GONE);
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

                //Implement the code to handle the file uploaded progress.
            }

            override fun onError(id: Int, exception: java.lang.Exception) {

                //Implement the code to handle the file upload error.
            }
        })

        /* String bucket = getString(R.string.s3_bucket);


        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint("https://s3.ap-south-1.amazonaws.com/jlf2022regdata.bucket/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(new OkHttpClient()));
        AwsS3 aws = builder.build().create(AwsS3.class);
        Policy policy=new Policy.f;
        policy.setId();
        String formattedDate = ""+System.currentTimeMillis();
        try {
            Bitmap bm = BitmapFactory.decodeFile(path_recv);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            TypedInput body = new TypedByteArray("image/jpg", b);
            String imageName = "files_" + System.currentTimeMillis();
            String oauth = AWSOauth.getOAuthAWS(getApplicationContext(), imageName.trim());
            String host = bucket + ".s3.amazonaws.com";
            String mimeType = body.mimeType();
            long length = body.length();
            aws.upload(oauth, length, host, formattedDate, mimeType, oauth, body, new Callback<String>() {

                @Override
                public void success(String s, Response response) {


                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        }catch (Exception e){

        }*/
    }

    fun convertToPNG(image: Bitmap, name: String): String? {
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(
                "NLC23_" + name.replace(".jpg", ""),
                ".png",
                storageDir
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var outStream: FileOutputStream? = null
        try {
            outStream = FileOutputStream(imageFile)
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFile!!.absolutePath
    }

    private fun capatilize(ed_name: EditText, flag: Int) {
        ed_name.addTextChangedListener(object : TextWatcher {
            var mStart = 0
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                mStart = i + i3
            }

            override fun afterTextChanged(editable: Editable) {
                val capitalizedText = WordUtils.capitalize(ed_name.getText().toString())
                if (capitalizedText != ed_name.getText().toString()) {
                    ed_name.addTextChangedListener(object : TextWatcher {
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

                            /*  if(flag==1){
                                if(s.toString().contains(" ",ignoreCase = true)){
                                    ed_name.isEnabled = false
                                  var name =  ed_name.text.toString()
                                   var h1 = name.substring(0, name.indexOf(' '))
                                    ed_name.setText(h1)
                                }else  {
                                    ed_name.isEnabled = true
                                }
                            }*/
                        }

                        override fun afterTextChanged(s: Editable) {
                            ed_name.setSelection(mStart)
                            ed_name.removeTextChangedListener(this)
                        }
                    })
                    ed_name.setText(capitalizedText)
                }
            }
        })


    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.btn_onsite_submit -> {
                val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
                var header: HashMap<String, String> = appUtils.Takeheader(this, "")
                for (i in 0 until titleList!!.size) {
                    // Validation Starts ...
                    if (requiredList!!.get(i) == 1) {

                        if (EditText_List!!.get(i).text.toString().equals("")) {
                            warningList!!.get(i).visibility = VISIBLE
                            return
                        }
                        warningList!!.get(i).visibility = GONE

                        if (typeList!!.get(i)!!.contains("email")) {

                            if (!ValidationMethod.emailValidation(EditText_List!!.get(i).text.toString())) {
                                warningList!!.get(i).visibility = VISIBLE
                                warningList!!.get(i).text = "please Enter valid Email"
                                return
                            }
                            warningList!!.get(i).visibility = GONE

                        }

                        if (typeList!!.get(i)!!.contains("select")) {

                            if (EditText_List!!.get(i).text.toString().contains("select")) {
                                warningList!!.get(i).visibility = VISIBLE
                                return
                            }
                            warningList!!.get(i).visibility = GONE

                            if (titleList!!.get(i)
                                    .contains("Specialty") && EditText_List!!.get(i).text.equals("Others")
                            ) {


                            }

                        }

                        if (typeList!!.get(i).contains("image")) {
                            if (image_to_Send.isEmpty()) {
                                warningList!!.get(i).visibility = VISIBLE
                                return
                            }
                            warningList!!.get(i).visibility = GONE
                        }

                    }

                    map[field_nameList!!.get(i)] = RequestBody.create(
                        MediaType.parse("text/plain"),
                        EditText_List!!.get(i).text.toString()
                    )
                }
                // Validation Ends ...
                //   map["phone"] = RequestBody.create(MediaType.parse("text/plain"), binding!!.edtEmailMobile.text.toString().trim().replace(getString(R.string.countrycode) + " ", "").trim())


                map["country_code"] =
                    RequestBody.create(MediaType.parse("text/plain"), mValuePhoneCOde)
                map["form_category"] = RequestBody.create(MediaType.parse("text/plain"), category)

                prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
                var api_name: String? = "usersave"
                presenter!!.SaveUser(this, map, header, true, "", api_name!!)


                /* if(!typeList!!.get(i).contains("image",ignoreCase = true)){

                    }else{
                        map[field_nameList!!.get(i)] = RequestBody.create(MediaType.parse("text/plain"), image_to_Send)
                    }*/


            }
        }
        /*R.id.settings -> {
            appUtils.showGenericDialog(this, true)
        }*/

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
    }

    override fun OnSavedUSer(model: OnspotModel?, errCode: Int) {

        try {
            /*mImageLoader = ImageLoader.getInstance();
                            mImageLoader.init(ImageLoaderConfiguration.createDefault(OnspotRegistrationActivity.this));
                            mImageLoader.displayImage(dataobjects.getString("qrcode_path"),ivQr);*/
            if (model!!.status == false) {
                val sweetAlertDialog = SweetAlertDialog(this@DiyOnspotActicvity)
                sweetAlertDialog.setTitleText("Alert!!")
                    .setContentText(model.message)
                    .show()
                return
            }

            val dialogMain = Dialog(this@DiyOnspotActicvity, R.style.my_dialog)
            dialogMain.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogMain.setContentView(R.layout.dialog_success)
            dialogMain.setCancelable(false)
            var strmsg: String? = ""
            val tv_msg = dialogMain.findViewById<TextView>(R.id.msg)
            val iv_cross = dialogMain.findViewById<ImageView>(R.id.iv_cross)
            val btn_ok = dialogMain.findViewById<Button>(R.id.btn_ok)
            val add = dialogMain.findViewById<Button>(R.id.add)

            btn_ok.visibility = View.GONE
            val btnNFCtAG = dialogMain.findViewById<Button>(R.id.btnNFCtAG)
            val btn_cancel = dialogMain.findViewById<Button>(R.id.btn_cancel)

            if (model.message != null || model!!.message.equals("", ignoreCase = true)) {
                strmsg = model!!.message!!
            }
            var event_id = prefeMain!!.getString("event_id", "");
            if (event_id.equals("32")) {
                btnNFCtAG.visibility = View.VISIBLE
            }

            //hide add guest button
            add.visibility = View.GONE


            add.setOnClickListener {
                val intent = Intent(
                    this@DiyOnspotActicvity,
                    MemberRegistration1::class.java
                )
                val gson = Gson()
                var obj: JSONObject = JSONObject(gson.toJson(model!!.data!!))
                Log.e("@@>>>obj ", obj.toString())
                intent.putExtra("type", "team1")
                intent.putExtra("id", obj.getString("id"))
                intent.putExtra("remaining_qty", 4)
                intent.putExtra("category", "")
                startActivity(intent)
            }

            var prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
            val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
            BrandName = appUtils.BrandName(this@DiyOnspotActicvity).toString()
            if (PRINTER_TYPE.equals("TSC", ignoreCase = true) || PRINTER_TYPE.equals(
                    "TVS",
                    ignoreCase = true
                )
            ) {
                btn_ok.visibility = View.VISIBLE

            } else if (!BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) && !BrandName.contains(
                    Constant.SUNMI_K2MINI
                ) && !BrandName.contains(Constant.SUNMI_V2s_STGL_MODEL)
            ) {
                btn_ok.visibility = View.GONE
                btn_cancel.setText("Ok")
            }



            iv_cross.setOnClickListener {
                dialogMain.dismiss()
                callDiyScreen()
            }
            var vcardList = ""
            val obj = Gson().toJson(model!!.data!!)
            try {
                var jso = JSONObject(obj)
                vcardList = jso.getString("vcard")
            } catch (e: Exception) {

            }


            btnNFCtAG.setOnClickListener {

                if (event_id.equals("32")) {
                    if (!vCard) {
                        Toast.makeText(
                            this@DiyOnspotActicvity,
                            "Please print the badge",
                            Toast.LENGTH_LONG
                        ).show()
                        return@setOnClickListener
                    }
                }
                if (vcardList.isNullOrEmpty()) {
                    Toast.makeText(this@DiyOnspotActicvity, "NFC Tag Empty", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }
                var code2 = "BEGIN:VCARD\n" +
                        "VERSION:2.1\n" +
                        //"N:;" +  fname_list.get(position)+/*" "+ Lname_list.get(position) +";;;\n" +*/
                        //   "TEL:" +phone_list.get(position) + "\n" +
                        /*  "EMAIL:" + email_list.get(position) + "\n" +*/
                        /* "ORG:" + company_list.get(position) + "\n" +*/
                        /*   "TITLE:" + designationlist.get(position) + "\n" +*/
                        /*"URL;TYPE= Linkedin:" + linkedin_List.get(position) + "\n" +
                    "URL;TYPE= Website:" + website_List.get(position) + "\n" +*/
                        /* "UC:" + code_list.get(position) + "\n" +*/
                        "END:VCARD\n";

                var bundle = Bundle()
                bundle.putString("contact", code2)
                bundle.putString("uri", vcardList)
                val bottomSheetDialogFragment = NFCTAGREADWRITE.newInstance(bundle)

                bottomSheetDialogFragment.show(
                    supportFragmentManager,
                    bottomSheetDialogFragment.getTag()
                )
                vCard = true
            }

            btn_cancel.setOnClickListener {
                dialogMain.dismiss()
                callDiyScreen()
            }

            tv_msg.text = strmsg
            dialogMain.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogMain.show()

            btn_ok.setOnClickListener {
                var event_id = prefeMain!!.getString("event_id", "");

                callDiyScreen()

                try {
                    modelSave = model

                    btn_ok.isEnabled = false
                    Handler().postDelayed({
                        btn_ok.isEnabled = true
                    }, 1900)


                    dialogPrinting = Dialog(this, R.style.my_dialog)
                    dialogPrinting!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialogPrinting!!.getWindow()!!
                        .setBackgroundDrawableResource(android.R.color.transparent);
                    dialogPrinting!!.setContentView(R.layout.toast_center)
                    dialogPrinting!!.setCancelable(true)
                    var textView = dialogPrinting!!.findViewById<TextView>(R.id.toastText);
                    textView.textSize = 14f
                    textView.setText("Printing in Progress")
                    dialogPrinting!!.show()
                    dialogPrinting!!.setCancelable(false)

                    handlerPrinting.removeCallbacks(handlerRunnable)
                    handlerPrinting.postDelayed(handlerRunnable, 1500.toLong())


                } catch (ex: Exception) {
                    appUtils.openErrorDialog(this, ex!!.message!!)
                }
            }



        } catch (ex: Exception) {

        }

    }

    var handlerPrinting = Handler()
    var handlerRunnable: Runnable = object : Runnable {
        override fun run() {
            Log.e("@@@sPrint", "" + "isPrint")

            try {
                if (modelSave!!.status == true) {

                    var category = ""
                    var prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)
                    val editor = prefeMain.edit()
                    val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
                    LogUtils.debug("@@SunmiPrinter", PRINTER_TYPE.toString())
                    var isPrint = prefeMain.getString("PRINT_STATUS", "").toString()
                    Log.e("isPrint", "" + isPrint)
                    if (isPrint.equals("1")) {

                        iscontactcardprint = prefeMain!!.getInt("SWITCH_VALUE", 0)

                        val gson = Gson()
                        var obj: JSONObject = JSONObject(gson.toJson(modelSave!!.data!!))


                        var first_name = ""
                        var last_name = ""
                        var email = ""
                        var phone = ""
                        var company = ""
                        var designation = ""
                        var unique_code = ""
                        var linkedin_url = ""


                        if (obj.has("category")) {
                            if (!obj.getString("category").isNullOrEmpty()) {
                                category = obj.getString("category")
                            }

                        }


                        if (obj.has("first_name")) {
                            if (!obj.getString("first_name").isNullOrEmpty()) {
                                first_name = obj.getString("first_name")
                            }
                        }

                        if (obj.has("last_name")) {
                            if (!obj.getString("last_name").isNullOrEmpty()) {
                                last_name = obj.getString("last_name")
                            }


                        }

                        if (obj.has("email")) {

                            if (!obj.getString("email").isNullOrEmpty()) {
                                email = obj.getString("email")
                            }


                        }

                        if (obj.has("phone")) {

                            if (!obj.getString("phone").isNullOrEmpty()) {
                                phone = obj.getString("phone")
                            }
                        }

                        if (obj.has("company")) {
                            if (!obj.getString("company").isNullOrEmpty()) {
                                company = obj.getString("company")
                            }
                        }

                        if (obj.has("place_of_work")) {
                            if (!obj.getString("place_of_work").isNullOrEmpty()) {
                                company = obj.getString("place_of_work")
                            }

                        }

                        if (obj.has("designation")) {
                            if (!obj.getString("designation").isNullOrEmpty()) {
                                designation = obj.getString("designation")
                            }
                        }
                        if (obj.has("unique_code")) {
                            if (!obj.getString("unique_code").isNullOrEmpty()) {
                                unique_code = obj.getString("unique_code")
                            }
                        }


                        if (obj.has("linkedin_profile")) {
                            if (!obj.getString("linkedin_profile").isNullOrEmpty()) {
                                linkedin_url = obj.getString("linkedin_profile")
                            }

                        }

                        var name = ""
                        if (first_name.isEmpty()) {
                            if (obj.has("name")) {
                                if (!obj.getString("name").isNullOrEmpty()) {
                                    name = obj.getString("name")
                                }

                            }
                        } else {
                            name = first_name + " " + last_name
                        }

                        if (iscontactcardprint == 1) {
                            code = "BEGIN:VCARD\n" +
                                    "VERSION:2.1\n" +
                                    "N:;" + first_name + " " + last_name + ";;;\n" +
                                    "TEL:" + mValuePhoneCOde + phone + "\n" +
                                    "EMAIL:" + email + "\n" +
                                    "ORG:" + company + "\n" +
                                    "TITLE:" + designation + "\n" +
                                    "URL;TYPE= Linkedin:" + linkedin_url + "\n" +
                                    "UC:" + unique_code + "\n" +
                                    "END:VCARD\n";
                        } else {
                            code = unique_code
                        }

                        if (PRINTER_TYPE.equals(Constant.SUNMI_K2MINI, ignoreCase = true)) {


                            var jsonArray: JSONArray = JSONArray()

                            var jsonOk: JSONObject = obj;

                            var android_id = Settings.Secure.getString(
                                this@DiyOnspotActicvity.getContentResolver(),
                                Settings.Secure.ANDROID_ID
                            );
                            jsonOk.put(
                                "timestamp",
                                "" + System.currentTimeMillis() + "_" + android_id
                            )
                            jsonArray.put(jsonOk)
                            try {
                                SendData(jsonArray);
                            } catch (e: Exception) {
                                Log.e("@@EEE", "" + e.message)
                            }


                        } else if (PRINTER_TYPE.equals(
                                "TSC",
                                ignoreCase = true
                            ) || PRINTER_TYPE.equals(
                                Constant.SUNMI,
                                ignoreCase = true
                            ) || PRINTER_TYPE.equals("TVS", ignoreCase = true)
                        ) {

                            Log.e("isPrintssd34", "" + "ssdsghds3454")
                            var print_row1 = ""
                            var print_row2 = ""
                            var print_row3 = ""
                            var print_row4 = ""
                            var print_row5 = ""

                            try {


                                if (category.equals(
                                        "Exhibitor",
                                        ignoreCase = true
                                    ) || category.equals("Speaker", ignoreCase = true)
                                ) {

                                    editor.putString("ROW1", "name")
                                    editor.putString("ROW2", "")
                                    editor.putString("ROW3", "company")
                                    editor.putString("ROW4", "unique_code")
                                    editor.putString("ROW5", "category")

                                } else {
                                    editor.putString("ROW1", "name")
                                    editor.putString("ROW2", "")
                                    editor.putString("ROW3", "company")
                                    editor.putString("ROW4", "unique_code")
                                    editor.putString("ROW5", "")
                                }

                                editor!!.commit()

                                var prefeMain = this@DiyOnspotActicvity.getSharedPreferences(
                                    "FRIENDS",
                                    Activity.MODE_PRIVATE
                                )
                                var Row1 = prefeMain.getString("ROW1", "")
                                var Row2 = prefeMain.getString("ROW2", "")
                                var Row3 = prefeMain.getString("ROW3", "")//unique_code
                                var Row4 = prefeMain.getString("ROW4", "")
                                var Row5 = prefeMain.getString("ROW5", "")




                                print_row1 = obj.getString(Row1)



                                if (!Row2!!.isEmpty()) {
                                    if (obj.has(Row2)) {
                                        if (!obj.isNull(Row2)) {
                                            print_row2 = obj.getString(Row2)
                                        }
                                    }
                                }


                                if (!Row3!!.isEmpty()) {
                                    if (obj.has(Row3)) {
                                        if (!obj.isNull(Row3)) {
                                            print_row3 = obj.getString(Row3)
                                        }
                                    }

                                }


                                if (!Row4!!.isEmpty()) {
                                    if (obj.has(Row4)) {
                                        if (!obj.isNull(Row4)) {
                                            print_row4 = obj.getString(Row4)
                                        }
                                    }
                                }

                                if (!Row5!!.isEmpty()) {
                                    if (obj.has(Row5)) {
                                        if (!obj.isNull(Row5)) {
                                            print_row5 = obj.getString(Row5)
                                        }
                                    }
                                }

                                Log.e("isPrintssd45", code)
                                LogUtils.debug(
                                    "@@Values12",
                                    print_row1 + "" + print_row2 + "" + print_row3 + "" + print_row4 + "" + print_row5 + "" + code
                                )
                            } catch (e: java.lang.Exception) {
                                LogUtils.debug("@@Errorex", e.message)
                            }


                            editor.commit()


                            /* if (obj.has(Row1)) {
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
                             }*/

                            BrandName = BrandName(this@DiyOnspotActicvity)

                            //val relativeLayout = RelativeLayout(context)
                            LogUtils.debug("@@@Sereretrrtunmi", "erL")


                            if (PRINTER_TYPE.equals(
                                    "TVS",
                                    ignoreCase = true
                                ) || PRINTER_TYPE.equals(
                                    "TSC",
                                    ignoreCase = true
                                ) || BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) || BrandName.contains(
                                    Constant.SUNMI_K2MINI
                                )
                            ) {

                                Log.e("PRINTER_TYPE_TSC4", PRINTER_TYPE.toString())
                                if (!appUtils.BrandMan(this@DiyOnspotActicvity).toString()
                                        .contains("sunmi", ignoreCase = true)
                                ) {
                                    runOnUiThread {
                                        Log.e("@@Comejere", "asd")
                                        val layoutParams =
                                            binding!!.rlBadgeprintSunmi.getLayoutParams()
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
                                LogUtils.debug("@@@Serunmi", "Constant.SUNMI_V2s_STGL_errerMODEL")
                                binding!!.rlBadgeprintSunmi.setLayoutParams(layoutParams)


                            } else {
                                val layoutParams = binding!!.rlBadgeprintSunmi.getLayoutParams()
                                layoutParams.width = dpToPx(190)
                                layoutParams.height = dpToPx(245)

                                binding!!.rlBadgeprintSunmi.setLayoutParams(layoutParams)
                                val params =
                                    binding!!.llInside.getLayoutParams() as RelativeLayout.LayoutParams
                                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                                params.setMargins(0, 10, 0, 5)
                                binding!!.llInside.setLayoutParams(params)


                            }

                            if (PRINTER_TYPE.equals(
                                    "TVS",
                                    ignoreCase = true
                                ) || PRINTER_TYPE.equals(
                                    "TSC",
                                    ignoreCase = true
                                ) || BrandName.contains(Constant.SUNMI_V2_PLUS_MODEL) || BrandName.contains(
                                    Constant.SUNMI_K2MINI
                                )
                            ) {
                                val PRINTER_TYPE_TSC = prefeMain.getString("PRINTER_TYPE_TSC", "")
                                Log.e("PRINTER_TYPE_TSC", "" + PRINTER_TYPE_TSC)
                                if (PRINTER_TYPE_TSC.equals("USB", ignoreCase = true)) {
                                    StickerPrinting.sunmiPrinting(
                                        "spot",
                                        this@DiyOnspotActicvity,
                                        if (BrandName.contains(Constant.SUNMI_K2MINI)) binding!!.rlBadgeprint else binding!!.rlBadgeprintSunmi,
                                        code,
                                        print_row1,
                                        print_row2,
                                        print_row3,
                                        print_row4,
                                        print_row5,
                                        BrandName, mUsbManager!!, device!!
                                    )
                                } else if (PRINTER_TYPE_TSC.equals(
                                        "Bluetooth",
                                        ignoreCase = true
                                    )
                                ) {
                                    StickerPrinting.sunmiPrinting(
                                        "spot",
                                        this@DiyOnspotActicvity,
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
                                    val HeightPrint2 = prefeMain.getInt("HeightPrint", 0)
                                    if (PRINTER_TYPE.equals("TVS", ignoreCase = true)) {
                                        StickerPrinting.sunmiPrintingTVS(
                                            "spot",
                                            this@DiyOnspotActicvity,
                                            if (BrandName.contains(Constant.SUNMI_K2MINI)) binding!!.rlBadgeprint else binding!!.rlBadgeprintSunmi,
                                            code,
                                            print_row1,
                                            print_row2,
                                            print_row3,
                                            print_row4,
                                            print_row5,
                                            BrandName, printfManager
                                        )
                                    } else {
                                        if ((HeightPrint2 == 1 || HeightPrint2 == 0) && BrandName.contains(
                                                Constant.SUNMI_K2MINI
                                            )
                                        ) {
                                            binding!!.rlBadgeprintk2mini.visibility = View.VISIBLE
                                            binding!!.rlBadgeprintk2mininew.visibility = View.GONE
                                        } else if ((HeightPrint2 == 2) && BrandName.contains(
                                                Constant.SUNMI_K2MINI
                                            )
                                        ) {
                                            binding!!.rlBadgeprintk2mini.visibility = View.GONE
                                            binding!!.rlBadgeprintk2mininew.visibility =
                                                View.VISIBLE
                                        }



                                        LogUtils.debug(
                                            "@@Values",
                                            print_row1 + "" + print_row2 + "" + print_row3 + "" + print_row4 + "" + print_row5 + "" + code
                                        )
                                        StickerPrinting.sunmiPrinting(
                                            "spot",
                                            this@DiyOnspotActicvity,
                                            if ((HeightPrint2 == 1 || HeightPrint2 == 0) && BrandName.contains(
                                                    Constant.SUNMI_K2MINI
                                                )
                                            ) binding!!.rlBadgeprintk2mini else if ((HeightPrint2 == 2) && BrandName.contains(
                                                    Constant.SUNMI_K2MINI
                                                )
                                            ) binding!!.rlBadgeprintk2mininew else binding!!.rlBadgeprintSunmi,
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
                                LogUtils.debug("@@@Sunmi", "Constant.SUNMI_V2s_STGL_MODEL")
                                LogUtils.debug(
                                    "@@Valuesere",
                                    print_row1 + " " + print_row2 + " " + print_row3 + " " + print_row4 + " " + print_row5 + " " + code
                                )
                                StickerPrinting.sunmiPrinting(
                                    "spot",
                                    this@DiyOnspotActicvity,
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
                        } else if (PRINTER_TYPE.equals(Constant.BROTHER, ignoreCase = true)) {
                            var prefeMain = this@DiyOnspotActicvity.getSharedPreferences(
                                "FRIENDS",
                                Activity.MODE_PRIVATE
                            )
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
                                this@DiyOnspotActicvity,
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
                                this@DiyOnspotActicvity,
                                "https://live.dreamcast.in/eventbot/EtEdgeIndustry4Delhi/printBatch/1/app",
                                "redeem"
                            )
                        }


                        // ProgressDialog progressDialog = new ProgressDialog(OnspotRegistrationActivity.this);
                        // progressDialog.setMessage("printing...");
                        //  progressDialog.setCancelable(false);
                        // progressDialog.show();

                    } else {
                        val sweetAlertDialogPrint = SweetAlertDialog(context)
                        sweetAlertDialogPrint.setCancelable(false)
                        sweetAlertDialogPrint.setTitleText("Alert!")
                            .setContentText(modelSave!!.message)
                            .show()
                        sweetAlertDialogPrint.setConfirmClickListener {
                            sweetAlertDialogPrint.cancel()
                            Handler().postDelayed({
                                val intent =
                                    Intent(this@DiyOnspotActicvity, DiyOnspotActicvity::class.java)
                                startActivity(intent)

                            }, 500L)
                        }

                    }
                    vCard = true
                    try {
                        if (dialogPrinting != null && dialogPrinting!!.isShowing) {
                            dialogPrinting!!.dismiss()
                        }
                    } catch (e: Exception) {

                    }
                } else {

                }


            } catch (e: Exception) {

            }

        }


    }

    private fun SendData(jsonArray: JSONArray) {
        val key = databaseReference?.child("/PRINTING/")
            ?.child("/${prefeMain!!.getString("PRINTER_TYPE_device", "DEVICE")}/")?.push()?.key
        Log.e("@@ERROR1: ", "" + jsonArray)
        databaseReference?.child("/PRINTING/")
            ?.child("/${prefeMain!!.getString("PRINTER_TYPE_device", "DEVICE")}/")
            ?.setValue(jsonArray.toString())
            ?.addOnSuccessListener {
                //  dialog.dismiss()
                Log.e("@@ERROR2: ", "" + "1")

                // Inflate custom layout


                val dialog = Dialog(this, R.style.my_dialog)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setContentView(R.layout.toast_center)
                dialog.setCancelable(false)
                dialog.show()
                Handler().postDelayed({
                    try {
                        if (dialog != null && dialog.isShowing) {
                            dialog.dismiss()
                        }
                    } catch (E: Exception) {

                    }


                }, 4000L)

// Create a Handler to delay the toast cancellation

// Create a Handler to delay the toast cancellation

                //Toast.makeText(this,"Printing in Progress",Toast.LENGTH_LONG).show()
            }
            ?.addOnFailureListener { e ->
                Log.e("Error", e.message!!)
                Log.e("@@ERROR3: ", "" + e.message!!)
            }


    }


    private fun callDiyScreen() {
        val intent = Intent(this@DiyOnspotActicvity, DiyOnspotActicvity::class.java)

        finish()

        startActivity(intent)
    }

    override val context: Context?
        get() = this

    override fun selectFileButtonOnClick() {
        TODO("Not yet implemented")
    }

    override fun printButtonOnClick() {
        TODO("Not yet implemented")
    }

    fun BrandName(context: Context?): String {
        return Build.MODEL
    }

    fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun handleResult(frag: Fragment?, data: Intent) {
        if (frag is NFCTAGREADWRITE) { // custom interface with no signitures
            frag!!.onNewIntent(data)
        }
        val frags: List<Fragment> = frag!!.childFragmentManager.fragments
        if (frags != null) {
            for (f in frags) {
                if (f != null) handleResult(f, data)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {

        try {
            super.onNewIntent(intent)

            val frags = supportFragmentManager.fragments
            if (frags != null) {
                for (f in frags) {
                    if (f != null) handleResult(f, intent)
                }
            }

        } catch (e: Exception) {

        }

        // Update the fragment's intent with the new NFC intent
        setIntent(intent)
        // Handle the NFC event

    }

    override fun onBackPressed() {
        backPress1()
    }

    private fun backPress1() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

/*private fun showMachine( PRINTER_TYPE:String ) {

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
    val sunmi_v2_s = dialog.findViewById<TextView>(R.id.sunmi)
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


