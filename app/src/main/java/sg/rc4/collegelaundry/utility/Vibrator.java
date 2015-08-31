 package sg.rc4.collegelaundry.utility;

 import android.content.Context;

/**
 * Created by sam on 31/8/15.
 */
public class Vibrator {

    private Context context;

    public Vibrator(Context context) {
        this.context = context;
    }

    public void vibrate() {
        vibrate(500);
    }

    public void vibrate(int milliseconds) {
        android.os.Vibrator v = (android.os.Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        v.vibrate(milliseconds);
    }
}
