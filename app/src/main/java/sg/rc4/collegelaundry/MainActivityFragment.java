package sg.rc4.collegelaundry;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.rc4.collegelaundry.utility.Vibrator;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @Bind(R.id.timerDisplay)
    TextView timerDisplay;

    Timer displayUpdateTimer;
    DateTime dtLaundryDone;
    boolean hasDoneAlerted = false;

    SharedPreferences pref;

    public MainActivityFragment() {
    }

    public void alert(){
        Vibrator v = new Vibrator(getContext());
        v.vibrate(2000);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_stat_college_laundry_notification_icon)
                        .setContentTitle("Your laundry is done!")
                        .setContentText("College Laundry");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getActivity(), MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(230141, mBuilder.build());
    }

    protected Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        Log.i("DEBUG", "MainActivityFragment::onResume");
        super.onResume();
        pref = getActivity().getSharedPreferences(getString(R.string.app_name), 0);
        String laundryDoneString = pref.getString(getString(R.string.dtLaundryDoneKey), null);
        Log.i("DEBUG", "MainActivityFragment::onResume - " + laundryDoneString);
        if (laundryDoneString != null) {
            dtLaundryDone = new DateTime(laundryDoneString);
            pref.edit().remove(getString(R.string.dtLaundryDoneKey)).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("DEBUG", "MainActivityFragment::onPause");
        SharedPreferences.Editor editor = pref.edit();
        if (dtLaundryDone != null) {
            editor.putString(getString(R.string.dtLaundryDoneKey), dtLaundryDone.toString());
        }
        editor.apply();
    }

    public void onStart() {
        super.onStart();

        displayUpdateTimer = new Timer();
        displayUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dtLaundryDone == null) {
                            timerDisplay.setText(R.string.timerDisplay);
                        } else {
                            Period diff = new Period(new DateTime(), dtLaundryDone);
                            int totalSeconds = diff.toStandardSeconds().getSeconds();
                            if (totalSeconds < 0) {
                                if (!hasDoneAlerted) {
                                    alert();
                                    hasDoneAlerted = true;
                                }
                                // the laundry has been done!!!
                                if (totalSeconds > -20) {
                                    timerDisplay.setText("Laundry is done!");
                                } else {
                                    // the laundry has been done!!!
                                    timerDisplay.setText("Done for " + DateUtils.formatElapsedTime(-1 * totalSeconds));
                                }
                            } else {
                                timerDisplay.setText(DateUtils.formatElapsedTime(totalSeconds));
                            }
                        }
                    }
                });
            }
        }, 200, 200);
    }

    @OnClick(R.id.timerDisplay)
    void timerDisplayTap(View v) {
        AlarmManager alarm = (AlarmManager)getContext().getSystemService(getContext().ALARM_SERVICE);
        Intent intent = new Intent(getContext(), OnAlarmReceive.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (dtLaundryDone == null) {
            int seconds = 35 * 60;
            hasDoneAlerted = false;
            dtLaundryDone = (new DateTime()).plusSeconds(seconds);
            alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ seconds * 1000, pendingIntent);
        } else {
            dtLaundryDone = null;
            alarm.cancel(pendingIntent);
        }
    }
}
