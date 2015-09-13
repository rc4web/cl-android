package sg.rc4.collegelaundry;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;

import net.danlew.android.joda.JodaTimeAndroid;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tabHost)
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);

        //Material Design :)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar_background));
        }
        ButterKnife.bind(this);

        tabHost.setup();

        MainActivityFragment washerFragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.washerFragment);
        Bundle washerBundle = new Bundle();
        washerBundle.putInt("timer", 35 * 60);
        washerBundle.putInt("notificationId", 1576623);
        washerFragment.setArguments(washerBundle);
        TabHost.TabSpec washerTab = tabHost.newTabSpec("Washer");
        washerTab.setContent(R.id.tab1);
        washerTab.setIndicator("Washer");
        tabHost.addTab(washerTab);

        MainActivityFragment dryerFragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.dryerFragment);
        Bundle dryerBundle = new Bundle();
        dryerBundle.putInt("timer", 30 * 60);
        dryerBundle.putInt("notificationId", 654311);
        dryerFragment.setArguments(dryerBundle);
        TabHost.TabSpec dryerTab = tabHost.newTabSpec("Dryer");
        dryerTab.setContent(R.id.tab2);
        dryerTab.setIndicator("Dryer");
        tabHost.addTab(dryerTab);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
