package fontinfo;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomButtonBold extends androidx.appcompat.widget.AppCompatButton {

    public CustomButtonBold(Context context)
    { super(context); setFont(); }

    public CustomButtonBold(Context context, AttributeSet set)
    { super(context,set); setFont(); }

    public CustomButtonBold(Context context, AttributeSet set, int defaultStyle)
    { super(context,set,defaultStyle); setFont(); }

    private void setFont() {

        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),"fonts/mont_bold.ttf");
        setTypeface(typeface); //function used to set font

    }
}
