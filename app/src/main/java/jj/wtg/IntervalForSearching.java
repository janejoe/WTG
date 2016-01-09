package jj.wtg;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class IntervalForSearching extends Fragment implements View.OnClickListener {

    private Button search;
    private Button dateSearchButton;
    private TextView dateFromPicker;
    private TextView dateFromPicker2;
    private TextView tracksCount;

    private int yearStart;
    private int monthStart;
    private int dayStart;

    private int yearEnd;
    private int monthEnd;
    private int dayEnd;

    private int selectedCount;
    private List<String> list;
   // HashMap <String,String> artistSet = new HashMap<String,String>() ;
    private HashSet <String> artistSet = new HashSet <> ();

    public ArrayList<HashMap<String,String>> resultData = new ArrayList<>();
    ScanAsyncTask scanAsyncTask;
    private Dialog dialogProgress;
   // private static final String TITLE = "title";
    //private static final String DESCRIPTION = "description";
    ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.interval_fragment, container, false);

        search = (Button) v.findViewById(R.id.searchButton);
        dateSearchButton = (Button) v.findViewById(R.id.dateSearchButton);
        dateFromPicker = (TextView)v.findViewById(R.id.dataFromDatePicker);
        dateFromPicker2 = (TextView)v.findViewById(R.id.dataFromDatePicker2);
        tracksCount = (TextView)v.findViewById(R.id.count);
        progress = (ProgressBar)v.findViewById(R.id.progress);

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
                        .append(monthStart + 1).append("-")
                        .append(dayStart).append("-")
                        .append(yearStart).append(" "));
    }

    private void updateDisplay2() {
        dateFromPicker2.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(monthEnd + 1).append("-")
                        .append(dayEnd).append("-")
                        .append(yearEnd).append(" "));
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchButton:

                dialogProgress = new Dialog(getActivity());
                dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogProgress.setContentView(R.layout.progress_bar_scan);
                dialogProgress.show();



                Log.d("result searchButton ", String.valueOf(artistSet));


                // [... Выполните задачу в фоновом режиме, обновите переменную myProgress...]
                // publishProgress(myProgress);
                // [... Продолжение выполнения фоновой задачи ...]
                // Верните значение, ранее переданное в метод onPostExecute




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

                final Spinner spinner = (Spinner) dialog3.findViewById(R.id.spinner);
                list = new ArrayList<>();
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

                        yearStart = picker.getYear();
                        monthStart = picker.getMonth();
                        dayStart = picker.getDayOfMonth();

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

                        yearEnd = picker2.getYear();
                        monthEnd = picker2.getMonth();
                        dayEnd = picker2.getDayOfMonth();

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

                        selectedCount = spinner.getSelectedItemPosition();
                        tracksCount.setText(list.get(selectedCount));
                        dialog3.cancel();
                    }
                });


        }

        try {
            new ScanAsyncTask().execute(selectedCount).get();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private class ScanAsyncTask extends AsyncTask<Integer, Integer, HashSet <String> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected HashSet <String> doInBackground(Integer... parameter) {
            int myProgress = 0;
            publishProgress(myProgress);
            VKParameters params = new VKParameters();
            if (parameter[0] != 3) {
                params.put(VKApiConst.COUNT, Integer.valueOf(list.get(parameter[0])));}
            else params.put(VKApiConst.COUNT, 6000);

            VKRequest requestAudio = VKApi.audio().get(params);
                requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override

                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        for (int i = 0; i < ((VKList<com.vk.sdk.api.model.VKApiAudio>) response.parsedModel).size(); i++) {
                            com.vk.sdk.api.model.VKApiAudio vkApiAudio = ((VKList<com.vk.sdk.api.model.VKApiAudio>) response.parsedModel).get(i);
                            artistSet.add(vkApiAudio.artist);

                //artistSet.put(TITLE, vkApiAudio.artist);
                // artistSet.put(DESCRIPTION, "description");
                // resultData.add(artistSet);
            }

                }

                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                    super.attemptFailed(request, attemptNumber, totalAttempts);
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                }

                @Override
                public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                    super.onProgress(progressType, bytesLoaded, bytesTotal);
                }
            });



            // [... Выполните задачу в фоновом режиме, обновите переменную myProgress...]
            // [... Продолжение выполнения фоновой задачи ...]
            // Верните значение, ранее переданное в метод onPostExecute
            Log.d("result doInBackground ", String.valueOf(artistSet));
            return artistSet;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {

          //  dialogProgress.cancel();

        }

        @Override
        protected void onPostExecute(HashSet<String>  result) {
           // super.onPostExecute(result);
            //заполнить адаптер
           /* Iterator iter = result.keySet().iterator();
            while (iter.hasNext()) {

                String key=(String)iter.next();
                String value=(String)result.get(key);

            }*/

            /*Iterator<String> itr = result.iterator();
            Toast.makeText(getActivity(), itr.next(), Toast.LENGTH_SHORT).show();*/

            //ItemList.hm = new HashMap<String, String>(artistSet);

          // dialogProgress.cancel();

           //Fragment fragment = new ItemList();

           //FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            //fragmentTransaction.replace(R.id.frameContent, fragment).addToBackStack( "tag" ).commit();



        }
    }
}
