package fontinfo;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomTextViewRegular extends androidx.appcompat.widget.AppCompatTextView {

    public CustomTextViewRegular(Context context)
    { super(context); setFont(); }

    public CustomTextViewRegular(Context context, AttributeSet set)
    { super(context,set); setFont(); }

    public CustomTextViewRegular(Context context, AttributeSet set, int defaultStyle)
    { super(context,set,defaultStyle); setFont(); }

    private void setFont() {

        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),"fonts/regurlar.ttf");
        setTypeface(typeface); //function used to set font

    }
}
