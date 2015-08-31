package sg.rc4.collegelaundry;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.DateUtils;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @Bind(R.id.timerDisplay)
    TextView timerDisplay;

    Timer displayUpdateTimer;
    DateTime dtLaundryDone;
    boolean isDone = false;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

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
                            timerDisplay.setText(DateUtils.formatElapsedTime(diff.toStandardSeconds().getSeconds()));
                        }
                    }
                });
            }
        }, 200, 200);
        return view;
    }

    @OnClick(R.id.timerDisplay)
    void timerDisplayTap(View v) {
        if (dtLaundryDone == null) {
            dtLaundryDone = (new DateTime()).plusMinutes(35);

        } else {
            dtLaundryDone = null;
        }
    }
}
