package fontinfo;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomButton extends androidx.appcompat.widget.AppCompatButton {

    public CustomButton(Context context)
    { super(context); setFont(); }

    public CustomButton(Context context, AttributeSet set)
    { super(context,set); setFont(); }

    public CustomButton(Context context, AttributeSet set, int defaultStyle)
    { super(context,set,defaultStyle); setFont(); }

    private void setFont() {

        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),"fonts/mont.ttf");
        setTypeface(typeface); //function used to set font

    }
}
