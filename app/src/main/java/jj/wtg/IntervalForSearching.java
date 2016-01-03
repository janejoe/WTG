package jj.wtg;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Лера on 03.01.2016.
 */
public class IntervalForSearching extends Fragment implements View.OnClickListener {

    private Button search;
    private Button dateSearchButton;
    private TextView dateFromPicker;
    private TextView dateFromPicker2;
    private int year;
    private int month;
    private int day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.interval_fragment, container, false);

        search = (Button) v.findViewById(R.id.searchButton);
        dateSearchButton = (Button) v.findViewById(R.id.dateSearchButton);
        dateFromPicker = (TextView)v.findViewById(R.id.dataFromDatePicker);
        dateFromPicker2 = (TextView)v.findViewById(R.id.dataFromDatePicker2);

        search.setOnClickListener(this);
        dateSearchButton.setOnClickListener(this);

        font();

        return v;
    }

    private void font(){
        Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), RepeatData.TYPEFONT);
        search.setTypeface(type2);
        dateSearchButton.setTypeface(type2);
    }



    private void updateDisplay() {
        dateFromPicker.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(month + 1).append("-")
                        .append(day).append("-")
                        .append(year).append(" "));
    }

    private void updateDisplay2() {
        dateFromPicker2.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(month + 1).append("-")
                        .append(day).append("-")
                        .append(year).append(" "));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchButton:

                Fragment fragment = new ItemList();

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.frameContent, fragment).addToBackStack( "tag" ).commit();
            break;
            case R.id.dateSearchButton:
                //first dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.start_data_picker);
                dialog.show();

                final DatePicker picker = (DatePicker)dialog.findViewById(R.id.startDatePicker);

                //second dialog
                final Dialog dialog2 = new Dialog(getActivity());
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.end_date_picker);

                final DatePicker picker2 = (DatePicker)dialog2.findViewById(R.id.endDatePicker);

                //third dialog
                final Dialog dialog3 = new Dialog(getActivity());
                dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog3.setContentView(R.layout.number_aidio);

                Spinner spinner = (Spinner) dialog3.findViewById(R.id.spinner);
                List<String> list = new ArrayList<>();
                list.add("100");
                list.add("200");
                list.add("300");
                list.add("Все");

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row, list);
                spinner.setAdapter(dataAdapter);

                //button of first dialog
                Button beginingIntervalButton = (Button) dialog.findViewById(R.id.beginingIntervalButton);

                beginingIntervalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        year = picker.getYear();
                        month = picker.getMonth();
                        day = picker.getDayOfMonth();

                        updateDisplay();

                        dialog.cancel();
                        dialog2.show();
                    }
                });

                //button of second dialog
                Button endIntervalButton = (Button) dialog2.findViewById(R.id.endIntervalButton);

                endIntervalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        year = picker2.getYear();
                        month = picker2.getMonth();
                        day = picker2.getDayOfMonth();

                        updateDisplay2();

                        dialog2.cancel();
                        dialog3.show();
                    }
                });
                //third button
                Button numberAudioButton = (Button) dialog3.findViewById(R.id.number_of_audio);

                numberAudioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog3.cancel();
                    }
                });


        }
    }
}
