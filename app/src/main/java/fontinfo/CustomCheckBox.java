package fontinfo;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomCheckBox extends androidx.appcompat.widget.AppCompatCheckBox {

    public CustomCheckBox(Context context)
    { super(context); setFont(); }

    public CustomCheckBox(Context context, AttributeSet set)
    { super(context,set); setFont(); }

    public CustomCheckBox(Context context, AttributeSet set, int defaultStyle)
    { super(context,set,defaultStyle); setFont(); }

    private void setFont() {

        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),"fonts/mont.ttf");
        setTypeface(typeface); //function used to set font

    }
}
