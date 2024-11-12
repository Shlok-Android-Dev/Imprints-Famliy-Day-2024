package com.runner.extras


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.runner.R
import com.runner.databinding.BottomSheetDialogErrorBinding
import com.runner.model.Constant
import com.runner.printdemo.Activity_Settings
import com.runner.ui.activity.login.ChooseZappingLocationActivity
import com.runner.ui.activity.login.LoginActivity
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object appUtils {
    fun colorStatusBar(window: Window, context: Context, shouldChangeStatusBarTintToDark: Boolean) {
        val activity = context as Activity
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decor = activity.window.decorView
            window.navigationBarColor = ContextCompat.getColor(activity, R.color.black)
            window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
            decor.systemUiVisibility = 0
        }
    }



    fun checkmanagestorage(context: Context){
       if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getpermission = Intent()
                getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                context!!.startActivity(getpermission)
            }
        }

    }

    fun ConstantError(Error: String, contxt: Context,responce_msg: String,code :Int) {

        try{
            val context = contxt.applicationContext
            var bottomSheetDialog = BottomSheetDialog(contxt)
            val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            bottomSheetDialog.setCancelable(false)
            var bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_error, null)
            var bottomSheetBinding = BottomSheetDialogErrorBinding.bind(bottomSheetView)
            bottomSheetDialog!!.setContentView(bottomSheetView)

            bottomSheetDialog!!.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val parentLayout =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                parentLayout?.let { it ->
                    val behaviour = BottomSheetBehavior.from(it)
                    setupFullHeight(it)
                    behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
            bottomSheetBinding!!.txtError.setText( ""+Error)
            bottomSheetBinding!!.txtTittle.setText( "ERROR \n"+ code+" - "+responce_msg)
            bottomSheetBinding!!.tvProceedToRedeem.setOnClickListener{
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog!!.show()
        }
        catch (e:Exception)
        {}


    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }


    fun openErrorDialog(context: Context, msg : String){


        val dialogMain = Dialog(context, R.style.my_dialog)
        dialogMain.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogMain.setContentView(R.layout.dialog_alert)
        dialogMain.setCancelable(false)

        var  btn_ok: Button = dialogMain!!.findViewById(R.id.btn_ok)
        var  err_msg: TextView = dialogMain!!.findViewById(R.id.err_msg)
        err_msg.setText(msg)
        btn_ok.setOnClickListener {
            dialogMain.dismiss()
        }
        dialogMain.show()



    }


    @SuppressLint("SuspiciousIndentation")
    fun  showGenericDialog(context: Context,showprint:Boolean){

        val dialog = Dialog(context, R.style.my_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_options)
        val refresh = dialog.findViewById<TextView>(R.id.refresh)
        val change_printing = dialog.findViewById<TextView>(R.id.change_printing)
       if(showprint) {
           change_printing.text =context.getString(R.string.select_macine)
       }else{
           change_printing.text =context.getString(R.string.change_zapping_laction)
       }
        val logout = dialog.findViewById<TextView>(R.id.logout)
      var   prefeMain = context.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)


        change_printing.setOnClickListener {

           if(showprint) {

               dialog.dismiss()
               val editor = prefeMain!!.edit()
               val dialog = Dialog(context, R.style.my_dialog)
               dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
               dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
               dialog.setContentView(R.layout.dialog_printers)

               val brother = dialog.findViewById<TextView>(R.id.brother)
               val other = dialog.findViewById<TextView>(R.id.other)
               val sunmi_v2_s_plus = dialog.findViewById<TextView>(R.id.sunmi_v2_s_plus)

               brother.setOnClickListener {
                   editor.putString("PRINTER_TYPE", Constant.BROTHER)
                   editor.commit()

                   val intent = Intent(context, Activity_Settings::class.java)
                   context.startActivity(intent)

                   dialog.dismiss()
               }

               sunmi_v2_s_plus.setOnClickListener {
                   editor.putString("PRINTER_TYPE", Constant.SUNMI)
                   editor.commit()
                   dialog.dismiss()
               }

               other.setOnClickListener {
                   editor.putString("PRINTER_TYPE", Constant.OTHER)
                   editor.commit()
                   dialog.dismiss()
                   val dialog = Dialog(context, R.style.my_dialog)
                   dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                   dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                   dialog.setContentView(R.layout.dialog_choose_print_app)
                   val printShare = dialog.findViewById<TextView>(R.id.printShare)
                   val print_hand = dialog.findViewById<TextView>(R.id.print_hand)
                   val self_print = dialog.findViewById<TextView>(R.id.self_print)
                   printShare.setOnClickListener {
                       editor.putString("PRINTAPP", Constant.PRINTSHARE)
                       editor.commit()
                       dialog.dismiss()
                   }
                   print_hand.setOnClickListener {
                       editor.putString("PRINTAPP", Constant.PRINTHAND)
                       editor.commit()
                       dialog.dismiss()
                   }
                   self_print.setOnClickListener {
                       editor.putString("PRINTAPP", Constant.SELFPRINT)
                       editor.commit()
                       dialog.dismiss()
                   }
                   dialog.show()
               }
               dialog.show()
           }else{
               val intent = Intent(context, ChooseZappingLocationActivity::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
               context.startActivity(intent)
              var activity : Activity =  context as Activity
               activity.finish()
           }

        }




        refresh.setOnClickListener {
            var prefeMain = context.getSharedPreferences("FRIENDS", Activity.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = prefeMain!!.edit()
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("from","reg")
            intent.putExtra("role_id",prefeMain!!.getString("ROLE_ID",""))
            intent.putExtra("user_id",prefeMain!!.getString("USER_ID",""))
            context.startActivity(intent)
             var actcty =context as Activity
            actcty.finish()
        }
        logout.setOnClickListener {

            val editor: SharedPreferences.Editor = prefeMain!!.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("from","")
            intent.putExtra("role_id","")
            intent.putExtra("user_id","")
            context.startActivity(intent)
            var actcty =context as Activity
            actcty.finish()
        }

        dialog.show()

    }

    /*public fun DialogImage(mediaLink: String?,context: Context) {
        val dialog = Dialog(context)
        val bindingdialog: DialogImageviewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_imageview, null, false)
        dialog.setContentView(bindingdialog.root)
        dialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.black))
        dialog.show()
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog!!.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog!!.getWindow()!!.setAttributes(lp)
        var loadImage= LoadImage(context)

        loadImage.LoadImageNoProgressPicasso( mediaLink!!, bindingdialog.ivView)
        bindingdialog.rlmain.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
    }*/
    fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission!!) !== PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    /*fun WordCapital(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            var mStart = 0
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i2: Int, i3: Int) {
                mStart = i + i3
            }

            override fun afterTextChanged(editable: Editable?) {
                val capitalizedText: String = WordUtils.capitalize(editText.text.toString())
                if (capitalizedText != editText.text.toString()) {
                    editText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            editText.setSelection(mStart)
                            editText.removeTextChangedListener(this)
                        }
                    })
                    editText.setText(capitalizedText)
                }
            }
        })
    }*/

    @SuppressLint("SuspiciousIndentation")
    fun compressImage(imageUri: String, context: Context): String? {
        val filePath = imageUri
        var scaledBitmap: Bitmap? = null
        val options: BitmapFactory.Options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = actualWidth / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath!!)
            val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0)
          val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90F)
            } else if (orientation == 3) {
                matrix.postRotate(180F)
           } else if (orientation == 8) {
                matrix.postRotate(270F)
           }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.width, scaledBitmap.height, matrix,
                    true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename: String = getFilename()
        try {
            out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }



    private fun getRealPathFromURI(contentURI: String, context: Context): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor: Cursor = context.getContentResolver().query(contentUri, null, null, null, null)!!
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = width * height.toFloat()
        val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    fun getFilename(): String {
       // val file = File(Environment.getExternalStorageDirectory().getPath(), "Lipka/Images")


        val dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (!dirPath.exists()) {
            dirPath.mkdirs()
        }
        return dirPath.getAbsolutePath().toString() + "/" + System.currentTimeMillis() + ".jpg"
    }

    fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(orientation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true)
    }

    fun CheckDatesOnlyWithTime(d1: String?, d2: String?): Boolean {
        var b = false
        val mFormatter = SimpleDateFormat("HH:mm")
        try {
            b = if (mFormatter.parse(d1).before(mFormatter.parse(d2))) {
                true //If start date is before end date
            } else if (mFormatter.parse(d1) == mFormatter.parse(d2)) {
                false //If two dates are equal
            } else {
                false //If start date is after the end date
            }
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return b
    }
    fun CheckDates(d1: String?, d2: String?): Boolean {
        var b = false
        val mFormatter = SimpleDateFormat("dd/MM/yyyy")
        try {
            b = if (mFormatter.parse(d1).before(mFormatter.parse(d2))) {
                true //If start date is before end date
            } else if (mFormatter.parse(d1) == mFormatter.parse(d2)) {
                true //If two dates are equal
            } else {
                false //If start date is after the end date
            }
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return b
    }
    fun CheckDatesFuture(d1: String?, d2: String?): Boolean {
        var b = false
        val mFormatter = SimpleDateFormat("yyyy-MM-dd")
        try {
            b = if (mFormatter.parse(d1).before(mFormatter.parse(d2))) {
                false //If start date is before end date
            } else if (mFormatter.parse(d1) == mFormatter.parse(d2)) {
                true //If two dates are equal
            } else {
                true //If start date is after the end date
            }
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return b
    }


    fun printDifferenceMIn(startDate: Date, endDate: Date): IntArray?{
        var different: Long = endDate.getTime() - startDate.getTime()


        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli
        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli


        return intArrayOf(elapsedDays.toInt(), elapsedHours.toInt(), elapsedMinutes.toInt(), elapsedSeconds.toInt())
    }

    fun CheckDatesWithTime(d1: String?, d2: String?): Boolean {
        var b = false
        val mFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            b = if (mFormatter.parse(d1).before(mFormatter.parse(d2))) {
                false //If start date is before end date
            } else if (mFormatter.parse(d1).equals(mFormatter.parse(d2))) {
                true //If two dates are equal
            } else {
                true //If start date is after the end date
            }
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return b
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
        val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            try {
                assert(inputManager != null)
                inputManager.hideSoftInputFromWindow(focusedView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS)
            } catch (e: AssertionError) {
                e.printStackTrace()
            }
        }
    }
    fun viewfile(context: Context, invoice: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(invoice))
        context.startActivity(browserIntent)
    }

    fun LogFunction(key: String, value: String) {
        Log.e(key, value)
    }

    /*open fun show_lert(title: String?, Messege: String?, flag: Int, context: Context?) {
        val dialog = context?.let { Dialog(it) }
        dialog?.setCancelable(false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_alert)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog!!.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog!!.getWindow()!!.setAttributes(lp)

        val textView: TextView = dialog!!.findViewById(R.id.alert_messege)
        val txtAlertTitle: TextView = dialog!!.findViewById(R.id.txtAlertTitle)
        val ok: RelativeLayout = dialog!!.findViewById(R.id.rlDone)
        val cancel: ImageView = dialog!!.findViewById(R.id.ivCancel)
        dialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent))

        textView.setText(Messege)
        txtAlertTitle.setText(title)
        cancel.setOnClickListener(View.OnClickListener {
            dialog?.dismiss()
        })
        ok.setOnClickListener(View.OnClickListener {
            dialog?.dismiss()
            val activity: Activity = context as Activity
            if (flag == 5) {

            } else if (flag == 7) {
                activity.finish()
            }
        })
        dialog?.show()

    }*/












    fun Takeheader(context: Context, token: String): HashMap<String, String> {

        val header = HashMap<String, String>()
        header["Authorization"] =token
        header["Request_Type"] = "1"
        return header;

    }








    fun ParseDateFormat(createdAt: String?): String? {

        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "dd MMM yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var date: Date? = null
        var str: String? = null

        try {
            date = inputFormat.parse(createdAt)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str


    }

    fun getMsgString(type: Int): String {

        when(type)
        {
            0 -> "Please enter the Serial No"
            1 -> "Please enter the Package weight "
            2 -> "Please enter the Package quantity "

        }

        return ""

    }



    fun inputFilter(edittext: EditText, textCharchter: String) {

    }

    fun BrandName(profileSettingActivity: Context): Any {
        return Build.MODEL.toString()
    }
    fun BrandMan(profileSettingActivity: Context): Any {
        return Build.MANUFACTURER.toString()
    }
    fun dpToPx(dp: Int,context:Context): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
    var filter: InputFilter = object : InputFilter {
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            val blockCharacterSet = "~#^|$%*!@/()-'\":;,?{}=!$^';,?×÷<>{}€£¥₩%~`¤♡♥_|《》¡¿°•○●□■◇◆♧♣▲▼▶◀↑↓←→☆★▪:-);-):-D:-(:'(:O 1234567890"
            return if (source != null && !blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }
    }


}