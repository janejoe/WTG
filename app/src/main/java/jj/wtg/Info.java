package jj.wtg;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Лера on 03.01.2016.
 */
public class Info extends Fragment implements View.OnClickListener{

    Button addToCalendar;
    private ConcertsInfo info;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.info_fragment, container, false);

        TextView eventTextView = (TextView)v.findViewById(R.id.eventTextView) ;
        TextView dateTextView = (TextView)v.findViewById(R.id.dateTextView);
        TextView timeTextView = (TextView)v.findViewById(R.id.timeTextView);
        TextView venueTextView = (TextView)v.findViewById(R.id.venueInfoTextView);
        TextView priceTextView = (TextView)v.findViewById(R.id.priceTextView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            info = (ConcertsInfo) bundle.getSerializable("concertsInfo");
        }

        eventTextView.setText(info.get(ConcertsInfo.TITLE).toUpperCase());
        dateTextView.setText(getString(R.string.date) + "  " + info.get(ConcertsInfo.DATE).toUpperCase());
        timeTextView.setText(getString(R.string.time) + "  " + info.get(ConcertsInfo.TIME).toUpperCase());
        venueTextView.setText(getString(R.string.venue) + "  " + info.get(ConcertsInfo.VENUE).toUpperCase());
        priceTextView.setText(getString(R.string.price) + "  " + info.get(ConcertsInfo.PRICE));


        addToCalendar = (Button)v.findViewById(R.id.toCalendarButton);
        addToCalendar.setOnClickListener(this);


        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toCalendarButton:
                putToCalendar();


        }
    }

    private void putToCalendar (){
        String data = info.get(ConcertsInfo.DATE);//21.04.2016,20:00
        String time = info.get(ConcertsInfo.TIME);
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(
                Integer.valueOf(data.substring(6)),    //year
                Integer.valueOf(data.substring(3, 5))-1,//mounth
                Integer.valueOf(data.substring(0, 2)),   // day
                Integer.valueOf(time.substring(0, 2)),   //hh
                Integer.valueOf(time.substring(3)));   //mm


        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, info.get(ConcertsInfo.TITLE))
                .putExtra(CalendarContract.Events.DESCRIPTION, getString(R.string.concert))
                .putExtra(CalendarContract.Events.EVENT_LOCATION, info.get(ConcertsInfo.VENUE))
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(CalendarContract.Events.HAS_ALARM, true);

        startActivity(intent);

    }



    }

