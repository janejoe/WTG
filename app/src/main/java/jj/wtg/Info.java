package jj.wtg;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
    ArrayList<ConcertsInfo> concertsInfo =  new ArrayList<ConcertsInfo>();

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
            concertsInfo = (ArrayList<ConcertsInfo>) bundle.getSerializable("concertsInfo");
        }

        ConcertsInfo item1 = concertsInfo.get(0);

        eventTextView.setText("Кто: " + item1.get(ConcertsInfo.TITLE));
        dateTextView.setText("Когда: " + item1.get(ConcertsInfo.DATE));
        timeTextView.setText("Во сколько: " +item1.get(ConcertsInfo.TIME));
        venueTextView.setText("Где: " +item1.get(ConcertsInfo.VENUE));
        priceTextView.setText("Цена: " + item1.get(ConcertsInfo.PRICE));




        addToCalendar = (Button)v.findViewById(R.id.toCalendarButton);

        addToCalendar.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toCalendarButton:

                Toast.makeText(getActivity(),"Событие добавлено",Toast.LENGTH_SHORT).show();

                addToCalendar.setEnabled(false);
        }
    }
}
