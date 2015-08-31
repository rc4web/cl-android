package sg.rc4.collegelaundry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;
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
        v.vibrate();
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
                getActivity().runOnUiThread(new Runnable() {
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
                                timerDisplay.setText("Done for " + DateUtils.formatElapsedTime(-1 * totalSeconds));
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
        if (dtLaundryDone == null) {
            hasDoneAlerted = false;
            dtLaundryDone = (new DateTime()).plusSeconds(35 * 60);
            AlarmManager alarm = (AlarmManager)getContext().getSystemService(getContext().ALARM_SERVICE);
            Intent intent = new Intent(getContext(), OnAlarmReceive.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 35 * 60 * 1000, pendingIntent);
        } else {
            dtLaundryDone = null;
        }
    }
}
