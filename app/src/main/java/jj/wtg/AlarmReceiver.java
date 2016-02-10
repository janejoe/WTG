package jj.wtg;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.File;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++)
        {
            if(procInfos.get(i).processName.equals("jj.wtg"))
            {
                File database = context.getDatabasePath("concerts.db");
                if ( database.exists() ) {
                    //Get old version
                    settings = context.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                    int old_version = settings.getInt(MainActivity.APP_PREFERENCES_DB_VERSION,
                            ConcertsDatabaseHelper.DATABASE_VERSION);

                    // Get content
                    ConcertsDatabaseHelper concertsDatabaseHelper;
                    ParsePonominalu parsePonominalu = new ParsePonominalu();
                    String content = null;
                    content = parsePonominalu.getAllConcerts();

                    //Update our bd
                    concertsDatabaseHelper = new ConcertsDatabaseHelper(context, "concerts.db", null,
                            old_version+1);
                    //Get all concert's titles and id's
                    concertsDatabaseHelper.getConcertsId(content, concertsDatabaseHelper);

                    //edit SharedPreferences


                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(MainActivity.APP_PREFERENCES_DB_VERSION, old_version+1);
                    editor.putInt(MainActivity.APP_PREFERENCES_ENG_IND, ConcertsDatabaseHelper.indEng);
                    editor.putInt(MainActivity.APP_PREFERENCES_RUS_IND, ConcertsDatabaseHelper.indRus);
                    editor.apply();
                                    }
                break;
            }
        }

        }

    }

