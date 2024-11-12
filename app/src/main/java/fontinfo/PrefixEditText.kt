package fontinfo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PrefixEditText : AppCompatEditText {
    var originalLeftPadding = -1f

    constructor(context: Context?) : super(context!!) {
        setFont()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        setFont()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        setFont()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calculatePrefix()
    }

    private fun calculatePrefix() {
        if (originalLeftPadding == -1f) {
            val prefix = tag as String
            val widths = FloatArray(prefix.length)
            paint.getTextWidths(prefix, widths)
            var textWidth = 0f
            for (w in widths) {
                textWidth += w
            }
            originalLeftPadding = compoundPaddingLeft.toFloat()
            setPadding(
                (textWidth + originalLeftPadding).toInt(),
                paddingRight, paddingTop,
                paddingBottom
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val prefix = tag as String
        canvas.drawText(prefix, originalLeftPadding, getLineBounds(0, null).toFloat(), paint)
    }

    private fun setFont() {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/mont.ttf")
        setTypeface(typeface) //function used to set font
    }
}