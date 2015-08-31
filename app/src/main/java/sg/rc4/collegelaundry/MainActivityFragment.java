package sg.rc4.collegelaundry;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @Bind(R.id.timerDisplay)
    TextView timerDisplay;

    CountDownTimer timer;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.timerDisplay) void timerDisplayTap(View v) {
        if (timer == null) {
            timer = new CountDownTimer(35 * 60 * 1000, 500) {

                public void onTick(long millisUntilFinished) {
                    timerDisplay.setText(DateUtils.formatElapsedTime(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    timerDisplay.setText("done!");
                }
            }.start();
        } else {
            timer.cancel();
            timer = null;
        }
    }
}
