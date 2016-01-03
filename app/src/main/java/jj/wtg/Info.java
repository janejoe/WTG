package jj.wtg;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Лера on 03.01.2016.
 */
public class Info extends Fragment implements View.OnClickListener{

    Button addToCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.info_fragment, container, false);

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
