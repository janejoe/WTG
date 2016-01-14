package jj.wtg;

import android.app.Fragment;
import android.content.Intent;
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
    TextView eventTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView venueTextView;
    TextView priceTextView;
    ConcertsInfo info;

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



        eventTextView.setText("Кто: " + info.get(ConcertsInfo.TITLE));
        dateTextView.setText("Когда: " + info.get(ConcertsInfo.DATE));
        timeTextView.setText("Во сколько: " +info.get(ConcertsInfo.TIME));
        venueTextView.setText("Где: " +info.get(ConcertsInfo.VENUE));
        priceTextView.setText("Цена: " + info.get(ConcertsInfo.PRICE));




        addToCalendar = (Button)v.findViewById(R.id.toCalendarButton);

        addToCalendar.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toCalendarButton:

                Calendar beginTime = Calendar.getInstance();
                beginTime.set(2016, 0, 19, 7, 30);
                Calendar endTime = Calendar.getInstance();
                endTime.set(2016, 0, 19, 8, 30);
                Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, info.get(ConcertsInfo.TITLE))
                    .putExtra(CalendarContract.Events.DESCRIPTION, "Концерт")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivity(intent);

                addToCalendar.setEnabled(false);
        }
    }

}
