package fontinfo

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class CustomRadioButton : AppCompatRadioButton {
    constructor(context: Context?) : super(context) {
        setFont()
    }

    constructor(context: Context?, set: AttributeSet?) : super(context, set) {
        setFont()
    }

    constructor(context: Context?, set: AttributeSet?, defaultStyle: Int) : super(
        context,
        set,
        defaultStyle
    ) {
        setFont()
    }

    private fun setFont() {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/mont.ttf")
        setTypeface(typeface) //function used to set font
    }
}