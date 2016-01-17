package jj.wtg;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;


public class MainActivity extends AppCompatActivity {

    private String[] scope = new String[]{VKScope.WALL, VKScope.AUDIO};

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    public static final String APP_PREFERENCES = "my_settings";
    public static final String APP_PREFERENCES_DB_VERSION = "db_version";
    public static final String APP_PREFERENCES_START_DATE = "start_date";
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        // String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        //System.out.println(Arrays.asList(fingerprints));


        VKSdk.login(this, scope);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }


        Fragment fragment = new MainSearching();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frameContent, fragment).addToBackStack( "tag" ).commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(getApplicationContext(),"Error!", Toast.LENGTH_SHORT).show();

            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 2) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        //-------------------------------SET PREFERENCES--------------------------------------------
        // Если первый запуск
        if (! mSettings.contains(APP_PREFERENCES_DB_VERSION)){
            SharedPreferences.Editor editor =mSettings.edit();
            editor.putLong(APP_PREFERENCES_START_DATE, System.currentTimeMillis());
            editor.putInt(APP_PREFERENCES_DB_VERSION, ConcertsDatabaseHelper.DATABASE_VERSION);
            editor.apply();
            startAlarm(mSettings.getLong(APP_PREFERENCES_START_DATE,System.currentTimeMillis() ));
        }

    }
    public void startAlarm(long startDate) {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, startDate, AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

}