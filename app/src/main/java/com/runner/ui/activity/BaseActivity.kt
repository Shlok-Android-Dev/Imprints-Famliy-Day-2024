package com.runner.ui.activity


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.InputType
import android.text.Spanned
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import com.brother.ptouch.sdk.PrinterInfo
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ruhe.utils.Utils
import com.runner.R
import com.runner.extras.LogUtils
import com.runner.extras.ProgressDialogClass
import com.runner.extras.appUtils
import com.runner.helper.See
import com.runner.helper.SunmiPrintHelper
import com.runner.model.Constant
import com.runner.printdemo.common.Common
import com.runner.printdemo.common.Common.UsbAuthorizationState
import com.runner.printdemo.common.MsgDialog
import com.runner.printdemo.common.MsgHandle
import com.runner.printdemo.printprocess.BasePrint
import com.runner.ui.activity.login.EntryActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.text.DecimalFormat
import java.util.*


abstract class BaseActivity : AppCompatActivity() {

    private var toast: Toast? = null
    lateinit var BOLD: Typeface
    lateinit var SEMIBOLD: Typeface
    lateinit var REGUlAR: Typeface
    lateinit var ITALIC: Typeface

    var mProgressDialog: ProgressDialogClass? = null

    val screenHeight: Int
        get() {
            val displayMetrics = resources.displayMetrics
            return displayMetrics.heightPixels
        }

    // TODO getScreen Widht
    val screenWidht: Int
        get() {
            val displayMetrics = resources.displayMetrics
            return displayMetrics.widthPixels
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance()

       var prefeMain = this@BaseActivity.getSharedPreferences("FRIENDS", MODE_PRIVATE)
        val PRINTER_TYPE = prefeMain.getString("PRINTER_TYPE", "")
            supportActionBar!!.hide()


        if(PRINTER_TYPE!!.contains("Sunmi",ignoreCase = true)) {
            SunmiPrintHelper.instance.initSunmiPrinterService(this)
            SunmiPrintHelper.instance.initPrinter()


            val handler = Handler()
            val timer = Timer(false)
            val timerTask: TimerTask = object : TimerTask() {
                override fun run() {

                    handler.post {

                       var BrandName = appUtils.BrandName(this@BaseActivity).toString()

                        LogUtils.debug("@@statusPrint",SunmiPrintHelper.instance.showPrinterStatus(this@BaseActivity))
                        if(SunmiPrintHelper.instance.showPrinterStatus(this@BaseActivity).equals("1")&& BrandName.contains(Constant.SUNMI_K2MINI))
                        {

                            var prefeMainedit = getSharedPreferences("FRIENDS", AppCompatActivity.MODE_PRIVATE)

                            if(prefeMainedit.getInt("HeightPrint",0)!=0)
                            {
                                val editor = prefeMainedit.edit()
                                editor.putInt("HeightPrint", 0)
                                editor.putInt("statusPrint", 1)
                                editor.commit()
                                Toast.makeText(this@BaseActivity,"Printer Setting Reset for K2MINI",Toast.LENGTH_LONG).show()
                            }


                        }
                    }
                }
            }

            timer.scheduleAtFixedRate(timerTask, 3000, 3000) // every 5 seconds.




        }


    }

    fun toast(@StringRes message: Int) {
        toast(getString(message))
    }

    fun setAnimViewVisible(lParentContent: View, vTarget: View, duration: Long) {

        try {
            Handler().postDelayed({

                TransitionManager.beginDelayedTransition(lParentContent as ViewGroup)
                vTarget.visibility = View.VISIBLE


            }, duration)
        } catch (e: Exception) {
            e.printStackTrace()
            vTarget.visibility = View.VISIBLE
        }


    }

    fun setAnimViewGone(lParentContent: View, vTarget: View, duration: Long) {

        try {
            Handler().postDelayed({

                TransitionManager.beginDelayedTransition(lParentContent as ViewGroup)
                vTarget.visibility = View.GONE


            }, duration)
        } catch (e: Exception) {
            e.printStackTrace()
            vTarget.visibility = View.GONE
        }


    }
    fun setOveridePendingTransisi(context: Activity) {
        try {
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun toast(toastMessage: String?) {
        if (toastMessage != null && !toastMessage.isEmpty()) {
            if (toast != null) toast!!.cancel()
            toast = Toast.makeText(this.applicationContext, toastMessage, Toast.LENGTH_LONG)
            toast!!.show()

        }
    }

    fun getPixelValue(context: Context, dimenId: Int): Int {
        var resources = context.getResources()

        val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId.toFloat(),
                resources.getDisplayMetrics()
        ).toInt()

        return px
    }

    fun encodeImageWithOutDirectory(path: String): String {

        val fileName = path.substring(path.lastIndexOf("/") + 1)
        //
        val file = File(path)
        See.logE("fileName", "" + fileName)
        //

        var encodedBase64: String = ""
        var encImage: String = ""
        try {
            val fileInputStreamReader = FileInputStream(file)
            val bytes = ByteArray(file.length().toInt())
            fileInputStreamReader.read(bytes)
            encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            encImage = encodedBase64!!.toString()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        return encImage

    }

    fun getTextFromHtml(str_html: String): Spanned? {

        val htmlAsSpanned = Html.fromHtml(str_html)

        return htmlAsSpanned
    }

    fun setEditTextPasswordTypeHide(etText: EditText) {

//        etText.transformationMethod = PasswordTransformationMethod()
        etText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

    }

    fun setEditTextPasswordTypeVisible(etText: EditText) {

        etText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

    }

    // TODO set Margins
    fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = v.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }


    // TODO show keyboard
    fun showKeyboard(ettext: EditText) {
        ettext.setSelection(ettext.text.toString().trim { it <= ' ' }.length)
        ettext.requestFocus()
        ettext.postDelayed({
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.showSoftInput(ettext, 0)
        }, 200)
    }

    // TODO gridlayout recycler view
    fun calculateNoOfColumns(): Int {
        val displayMetrics = resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 180).toInt()
    }

    fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

    }

    fun setLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        }
    }


    fun collumnMenu(): Int {
        var c = Companion.getScreenWidht(this) / 320
        if (c < 3) c = 3
        return c
    }


    fun setFlagFullScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    fun setBlackStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE


        }
    }

    fun createDrawableFromView(context: Context, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT)
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    fun Konektivitas(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED) {
            //we are connected to a network

            true
        } else {
            false
        }

    }

    companion object {


        fun math(f: Float): Int {
            val c = (f + 0.5f).toInt()
            val n = f + 0.5f
            return if ((n - c) % 2 == 0f) f.toInt() else c
        }

        fun getMimeType(url: String): String? {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
            return type
        }


        // TODO create custom marker
        fun createDrawableFromView(context: Context, view: View): Bitmap {
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            view.layoutParams = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT)
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            view.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmap)
            view.draw(canvas)

            return bitmap
        }

        // TODO: 23/02/18 getExtension from file
        fun getExtension(filename: String): String {
            val filenameArray = filename.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val extension = filenameArray[filenameArray.size - 1]
            println(extension)

            return extension

        }

        fun labelPrice(price: Double): String {

            val formatter = DecimalFormat("#,###")
            val harga = "Rp " + formatter.format(price)

            return harga.replace(",", ".")
        }

        fun labelBilangan(price: Double): String {

            val formatter = DecimalFormat("#,###")
            val harga = formatter.format(price)

            return harga.replace(",", ".")
        }

        fun getFileNameFromURL(url: String?): String {
            if (url == null) {
                return ""
            }
            try {
                val resource = URL(url)
                val host = resource.host
                if (host.length > 0 && url.endsWith(host)) {
                    // handle ...example.com
                    return ""
                }
            } catch (e: MalformedURLException) {
                return ""
            }

            val startIndex = url.lastIndexOf('/') + 1
            val length = url.length

            // find end index for ?
            var lastQMPos = url.lastIndexOf('?')
            if (lastQMPos == -1) {
                lastQMPos = length
            }

            // find end index for #
            var lastHashPos = url.lastIndexOf('#')
            if (lastHashPos == -1) {
                lastHashPos = length
            }

            // calculate the end index
            val endIndex = Math.min(lastQMPos, lastHashPos)
            return url.substring(startIndex, endIndex)
        }




        fun getScreenWidht(baseActivity: BaseActivity): Int {
            val displayMetrics = DisplayMetrics()
            baseActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    }

    open fun loadProgressBar(context: Context?, message: String?, cancellable: Boolean) {
        try {
            mProgressDialog = ProgressDialogClass(this)
            mProgressDialog!!.setCancelable(false)
            if (context != null && !(context as Activity).isFinishing && !mProgressDialog!!.isShowing) mProgressDialog!!.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    open fun dismissProgressBar(context: Context?) {
        try {
            if (mProgressDialog != null) {
                if (context != null && !(context as Activity).isFinishing && mProgressDialog!!.isShowing) {
                    mProgressDialog!!.dismiss()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    open fun getAlertDialogBuilder(title: String?, message: String?, cancellable: Boolean): android.app.AlertDialog.Builder? {
        return android.app.AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancellable)
    }

    open fun enableLoadingBar(context: Context?, enable: Boolean, s: String?) {
        if (enable) {
            loadProgressBar(context, s, false)
        } else {
            dismissProgressBar(context)
        }
    }

    open fun onError(reason: String?) {
        onError(reason, false)
    }


    open fun onError(reason: String?, finishOnOk: Boolean) {
        val activity: Activity = this
        if (!activity.isFinishing) {
            if (reason?.let { Utils.validateString(it) }!!) {
                getAlertDialogBuilder(null, reason, false)?.setPositiveButton(getString(R.string.button_ok), if (finishOnOk) object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                        finish()
                    }
                } else null)?.show()
            } else {
                getAlertDialogBuilder(null, getString(R.string.default_error), false)
                        ?.setPositiveButton(getString(R.string.button_ok), if (finishOnOk) object : DialogInterface.OnClickListener {
                            override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                                finish()
                            }
                        } else null)?.show()
            }
        }
    }


    open fun show_lert(color: Int, Messege: String?, flag: Int, context: Activity?) {
     /*   val dialog = context?.let { Dialog(it) }
        dialog?.setCancelable(false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_alert)
        val textView: TextView = dialog!!.findViewById(R.id.alert_messege)
        val ok: TextView = dialog!!.findViewById(R.id.ok)
        textView.setText(Messege)
        ok.setOnClickListener(View.OnClickListener {
            dialog?.dismiss()
            if (flag == 5) {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            else  if (flag == 4) {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            else  if (flag == 7) {
               finish()
            }
        })
        dialog?.show()*/
    }

    val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    public open val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @TargetApi(12)
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false
                        )
                    ) Common.mUsbAuthorizationState =
                        UsbAuthorizationState.APPROVED else Common.mUsbAuthorizationState =
                        UsbAuthorizationState.DENIED
                }
            }
        }
    }
    var myPrint: BasePrint? = null
    open var mHandle: MsgHandle? = null
    open var mDialog: MsgDialog? = null

    abstract fun selectFileButtonOnClick()

    abstract fun printButtonOnClick()

    /**
     * Called when [Printer Settings] button is tapped
     */


    /**
     * show message when BACK key is clicked
     */
    /* @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
           // showTips();
        }
        return false;
    }*/

    /**
     * show message when BACK key is clicked
     */
    /* @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
           // showTips();
        }
        return false;
    }*/
    /**
     * show the BACK message
     */


    /**
     * get the BluetoothAdapter
     */
    @SuppressLint("MissingPermission")
    open fun getBluetoothAdapter(): BluetoothAdapter? {
        val bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter()
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE
            )
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(enableBtIntent)
        }
        return bluetoothAdapter
    }

    @TargetApi(12)
    open fun getUsbDevice(usbManager: UsbManager?): UsbDevice? {
        if (myPrint!!.printerInfo.port != PrinterInfo.Port.USB) {
            return null
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            val msg = mHandle!!.obtainMessage(Common.MSG_WRONG_OS)
            // mHandle.sendMessage(msg);
            return null
        }
        val usbDevice = myPrint!!.getUsbDevice(usbManager)
        if (usbDevice == null) {
            val msg = mHandle!!.obtainMessage(Common.MSG_NO_USB)
            // mHandle.sendMessage(msg);
            return null
        }
        return usbDevice
    }


    /**
     * set sub title
     */
    @TargetApi(12)
    open fun checkUSB(): Boolean {
        if (myPrint!!.printerInfo.port != PrinterInfo.Port.USB) {
            return true
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            val msg = mHandle!!.obtainMessage(Common.MSG_WRONG_OS)
            //  mHandle.sendMessage(msg);
            return false
        }
        val usbManager = getSystemService(USB_SERVICE) as UsbManager
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
            this, 0,
            Intent(ACTION_USB_PERMISSION), 0
        )
        registerReceiver(mUsbReceiver, IntentFilter(ACTION_USB_PERMISSION))
        if (!usbManager.hasPermission(usbDevice)) {
            Common.mUsbAuthorizationState = UsbAuthorizationState.NOT_DETERMINED
            usbManager.requestPermission(usbDevice, permissionIntent)
        } else {
            Common.mUsbAuthorizationState = UsbAuthorizationState.APPROVED
        }
        return true
    }



}
