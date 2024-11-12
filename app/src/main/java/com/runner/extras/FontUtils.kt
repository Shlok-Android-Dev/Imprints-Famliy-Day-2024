package com.runner.extras

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.runner.R

class FontUtils(context: Context) {

    var contex = context
        var preferences: SharedPreferences = contex.getSharedPreferences("FRIENDS", MODE_PRIVATE)
        var isfontkey: String? = preferences!!.getString("Font_TYPE", "")

        fun Fonts(view: TextView,type:String) {
            LogUtils.debug("@@Fontvalue", isfontkey)

            Log.e("@@@SCOM","321")
            if (isfontkey!!.contains("Poppins Font")) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                     Log.e("@@@SCOM","31231")
                     if (type.contains("r1")){

                         var typeface =  contex.resources.getFont(R.font.poppinsbold)
                         view.setTypeface(typeface)
                     }
                     if (type.contains("r2")){
                         var typeface =  contex.resources.getFont(R.font.poppinsbold)
                         view.setTypeface(typeface)
                     }
                     if (type.contains("r3")){
                         var typeface =  contex.resources.getFont(R.font.popins_medium)
                         view.setTypeface(typeface)
                     }
                     if (type.contains("r4")){
                         var typeface =  contex.resources.getFont(R.font.popins_medium)
                         view.setTypeface(typeface)
                     }

                     if (type.contains("r5")){
                         var typeface =  contex.resources.getFont(R.font.poppinsbold)
                         view.setTypeface(typeface)
                     }

                }
                else
                 {
                    Log.e("@@@SCOM","1")
                     if (type.contains("r1")){

                         Log.e("@@@SCOM","223")
                         val customFont: Typeface =
                             Typeface.createFromAsset(contex.getAssets(), "fonts/poppinsbold.ttf")

                         view.setTypeface(customFont)
                     }
                     if (type.contains("r2")){
                         Log.e("@@@SCOM","2")
                         val customFont: Typeface =
                             Typeface.createFromAsset(contex.getAssets(), "fonts/poppinsbold.ttf")

                         view.setTypeface(customFont)
                     }
                     if (type.contains("r3")){
                         Log.e("@@@SCOM","3")
                         val customFont: Typeface =
                             Typeface.createFromAsset(contex.getAssets(), "fonts/popins_medium.ttf")

                         view.setTypeface(customFont)
                     }
                     if (type.contains("r4")){
                         Log.e("@@@SCOM","4")
                         val customFont: Typeface =
                             Typeface.createFromAsset(contex.getAssets(), "fonts/popins_medium.ttf")

                         view.setTypeface(customFont)
                     }

                     if (type.contains("r5")){
                         Log.e("@@@SCOM","5")
                         try {
                             val customFont: Typeface =
                                 Typeface.createFromAsset(contex.getAssets(), "fonts/poppinsbold.ttf")

                             view.setTypeface(customFont)
                             Log.e("@@@SCOM","5 true")
                         }
                         catch (e:Exception)
                         {
                             Log.e("@@@SCOM","5"+e.message)
                         }

                     }

                 }
            } else if (isfontkey!!.contains("PlayFair Font")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if (type.contains("r1")){
                        var typeface =  contex.resources.getFont(R.font.playfair_black)
                        view.setTypeface(typeface)
                    }
                    if (type.contains("r2")){
                        var typeface =  contex.resources.getFont(R.font.playfair_black)
                        view.setTypeface(typeface)
                    }
                    if (type.contains("r3")){
                        var typeface =  contex.resources.getFont(R.font.playfair_semibold)
                        view.setTypeface(typeface)
                    }
                    if (type.contains("r4")){
                        var typeface =  contex.resources.getFont(R.font.playfair_semibold)
                        view.setTypeface(typeface)
                    }

                    if (type.contains("r5")){
                        var typeface =  contex.resources.getFont(R.font.playfair_black)
                        view.setTypeface(typeface)
                    }

                }

                else
                {
                    if (type.contains("r1")){
                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/playfair_black.ttf")

                        view.setTypeface(customFont)
                    }
                    if (type.contains("r2")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/playfair_black.ttf")

                        view.setTypeface(customFont)
                    }
                    if (type.contains("r3")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/playfair_semibold.ttf")

                        view.setTypeface(customFont)
                    }
                    if (type.contains("r4")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/playfair_semibold.ttf")

                        view.setTypeface(customFont)
                    }

                    if (type.contains("r5")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/playfair_black.ttf")

                        view.setTypeface(customFont)
                    }
                }

            } else if (isfontkey!!.contains("Roboto Font")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if (type.contains("r1")){
                        var typeface =  contex.resources.getFont(R.font.robot_black)
                        view.setTypeface(typeface)
                    }
                    if (type.contains("r2")){
                        var typeface =  contex.resources.getFont(R.font.robot_black)
                        view.setTypeface(typeface)
                    }
                    if (type.contains("r3")){
                        var typeface =  contex.resources.getFont(R.font.roboto_medium)
                        view.setTypeface(typeface)
                    }
                    if (type.contains("r4")){
                        var typeface =  contex.resources.getFont(R.font.roboto_medium)
                        view.setTypeface(typeface)
                    }

                    if (type.contains("r5")){
                        var typeface =  contex.resources.getFont(R.font.robot_black)
                        view.setTypeface(typeface)
                    }

                } else  {

                    if (type.contains("r1")){
                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/robot_black.ttf")

                        view.setTypeface(customFont)
                    }
                    if (type.contains("r2")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/robot_black.ttf")

                        view.setTypeface(customFont)
                    }
                    if (type.contains("r3")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/roboto_medium.ttf")

                        view.setTypeface(customFont)
                    }
                    if (type.contains("r4")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/roboto_medium.ttf")

                        view.setTypeface(customFont)
                    }

                    if (type.contains("r5")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/robot_black.ttf")

                        view.setTypeface(customFont)
                    }
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if (type.contains("r1")){
                        var typeface =  contex.resources.getFont(R.font.inter_black)
                        view.setTypeface(typeface)
                    }
                    if (type.contains("r2")){
                        var typeface =  contex.resources.getFont(R.font.inter_black)
                        view.setTypeface(typeface)
                    }
                    if (type.contains("r3")){
                        var typeface =  contex.resources.getFont(R.font.inter_bold)
                        view.setTypeface(typeface)
                    }
                    if (type.contains("r4")){
                        var typeface =  contex.resources.getFont(R.font.inter_bold)
                        view.setTypeface(typeface)
                    }

                    if (type.contains("r5")){
                        var typeface =  contex.resources.getFont(R.font.inter_black)
                        view.setTypeface(typeface)
                    }

                } else  {

                    if (type.contains("r1")){
                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/inter_black.ttf")

                        view.setTypeface(customFont)
                    }
                    if (type.contains("r2")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/inter_black.ttf")

                        view.setTypeface(customFont)
                    }
                    if (type.contains("r3")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/inter_bold.ttf")

                        view.setTypeface(customFont)
                    }
                    if (type.contains("r4")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/inter_bold.ttf")

                        view.setTypeface(customFont)
                    }

                    if (type.contains("r5")){

                        val customFont: Typeface =
                            Typeface.createFromAsset(contex.getAssets(), "fonts/inter_black.ttf")

                        view.setTypeface(customFont)
                    }
                }
            }
        }







}