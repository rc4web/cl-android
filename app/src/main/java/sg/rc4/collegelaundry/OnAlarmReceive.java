package sg.rc4.collegelaundry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sam on 31/8/15.
 */
public class OnAlarmReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("INFO", "BroadcastReceiver, in onReceive:");

        // Start the MainActivity
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
