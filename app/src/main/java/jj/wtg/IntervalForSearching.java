package jj.wtg;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.net.ParseException;
import android.net.Uri;
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

import android.widget.Spinner;
import android.widget.TextView;


import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import com.vk.sdk.api.model.VKList;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import java.util.TreeMap;
import java.util.TreeSet;

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

    private Map<String, String> concertsId = new TreeMap<>();
    private TreeSet<String> artistSet= new TreeSet<>();

    private ArrayList<ConcertsForList> concertsForList = new ArrayList<>();
    private ArrayList<ConcertsInfo> concertsInfo = new ArrayList<>();

    ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.interval_fragment, container, false);

        search = (Button) v.findViewById(R.id.searchButton);
        dateSearchButton = (Button) v.findViewById(R.id.dateSearchButton);
        dateFromPicker = (TextView) v.findViewById(R.id.dataFromDatePicker);
        dateFromPicker2 = (TextView) v.findViewById(R.id.dataFromDatePicker2);
        tracksCount = (TextView) v.findViewById(R.id.count);
        progress = (ProgressBar) v.findViewById(R.id.progress);

        search.setOnClickListener(this);
        dateSearchButton.setOnClickListener(this);
        font();

        return v;
    }

    private void font() {
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

    private TreeSet getArtistSet(Integer count) {
        VKParameters params = new VKParameters();
        if (count != 3) {
            params.put(VKApiConst.COUNT, Integer.valueOf(list.get(count)));
        } else params.put(VKApiConst.COUNT, 6000);

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
                try {
                    new ScanAsyncTask().execute(selectedCount);

                } catch (Exception e) {
                    e.printStackTrace();
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
        Log.d("result doInBackground ", String.valueOf(artistSet));
        return artistSet;
    }

    //Parse ponominalu

    //Build url for request
    String getUrlPonominalu() throws URISyntaxException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.cultserv.ru")
                .appendPath("/jtransport/iphone/get_events")
                .appendQueryParameter("category", "10")
                .appendQueryParameter("min_date", new StringBuilder()
                        .append(yearStart).append("-")
                        .append(monthStart + 1).append("-")
                        .append(dayStart).append("").toString())
                .appendQueryParameter("max_date", new StringBuilder()
                        .append(yearEnd).append("-")
                        .append(monthEnd + 1).append("-")
                        .append(dayEnd).append("").toString())
                .appendQueryParameter("one_for_event", "true")
                .appendQueryParameter("region_id", "1")
                .appendQueryParameter("session", "123")
                .appendQueryParameter("exclude", "image,link,address,original_image,venue,slide," +
                        "tags,date,dates,has_offer,str_date,str_time,event,min_price,max_price," +
                        "ticket_count,eticket_possible,end_date,categories_ids,type,split_titles," +
                        "add_title");
        Uri uri = builder.build();
        String testUrl = uri.toString();
        return testUrl;

    }

    StringBuilder getContent(URL url, StringBuilder content) {
        try {
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    private String getAllConcerts() throws ParseException {
        String testUrl = null;
        URL url = null; // Get URL

        try {
            testUrl = getUrlPonominalu();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            url = new URL(testUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Get Content
        StringBuilder content = new StringBuilder();
        content = getContent(url, content);
        return content.toString();
    }

    private Map consertsMap(String strJson, Map<String, String> concertsId) {
        JSONObject dataJsonObj = null;

        try {
            dataJsonObj = new JSONObject(strJson);
            JSONArray events = dataJsonObj.getJSONArray("message");

            // Get all concerts into hashmap with id and title
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                concertsId.put(event.getString("title").substring(7), event.getString("id"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return concertsId;
    }


//----------------------------------------------------------


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateSearchButton:
                //first dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.start_data_picker);
                dialog.show();

                final DatePicker picker = (DatePicker) dialog.findViewById(R.id.startDatePicker);

                //second dialog
                final Dialog dialog2 = new Dialog(getActivity());
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.end_date_picker);

                final DatePicker picker2 = (DatePicker) dialog2.findViewById(R.id.endDatePicker);

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
                break;

            case R.id.searchButton:
                artistSet = getArtistSet(selectedCount);
                break;
        }

    }


    private class ScanAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ConcertsInfo>> {

        @Override
        protected void onPreExecute() {
            progress.setVisibility(getView().VISIBLE);
            search.setVisibility(getView().INVISIBLE);
            dateSearchButton.setVisibility(getView().INVISIBLE);
            dateFromPicker.setVisibility(getView().INVISIBLE);
            dateFromPicker2.setVisibility(getView().INVISIBLE);
            tracksCount.setVisibility(getView().INVISIBLE);

        }

        @Override
        protected ArrayList<ConcertsInfo> doInBackground(Integer... parameter) {

            if (!(artistSet.isEmpty())) {
                //work with ponominalu

                String content = null;
                content = getAllConcerts();
                //Get all concert's titles and id's
                concertsId = consertsMap(content, concertsId);

                //eguals artists and concerts

                for (String artist : artistSet) {
                    for (String key : concertsId.keySet()) {
                        if (key.contains(artist)) {
                            concertsForList.add(new ConcertsForList(artist, concertsId.get(key)));
                            //get info
                            try {
                                String contentInfo;
                                contentInfo = getEventsInfo(concertsId.get(key));
                                concertsInfo = infoMap(contentInfo, concertsInfo);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }

            }

            return concertsInfo;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

           /* if (dialogProgress != null) {
                dialogProgress.cancel();
            }*/

        }

        @Override
        protected void onPostExecute(ArrayList<ConcertsInfo> result) {
            super.onPostExecute(result);
            progress.setVisibility(View.GONE);

            if (!result.isEmpty()) {
                Fragment fragment = new ItemList();

                Bundle bundle = new Bundle();
                bundle.putSerializable("concertsInfo", result);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameContent, fragment).addToBackStack("tag").commit();
            }
        }
    }

    //Build url for request
    String getInfoUrl(String eventId) throws URISyntaxException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.cultserv.ru")
                .appendPath("/jtransport/iphone/get_subevent")
                .appendQueryParameter("id", eventId)
                .appendQueryParameter("session", "123")
                .appendQueryParameter("exclude","id,description,eng_title,title_roditelniy," +
                        "title_datelniy,title_vinitelniy,title_tvoritelniy,title_predlozhniy,date," +
                        "image,original_image,events,alias,marker,region_id,metro,del_price,zoom," +
                        "central_kassa,priority,production_url,show_3d,event_id,event,max_price," +
                        "slide,ticket_count,eticket_possible,eticket_only,credit_card_payment,tags," +
                        "sectors,desc_hash,categories,categories_ids,metaDescription,mini_slide," +
                        "slide_mask,wishdate,city_required,type,age,link,et_rep_sending_time_long," +
                        "without_check,without_check_on_venue_kassa,without_check_on_central_desk," +
                        "without_check_on_delivery,has_offer,moderated,ticketCount,commission," +
                        "int_type,sold_out,show_begin_date,split_titles,company,meropriyatie_id");
        Uri uri = builder.build();
        String testUrl = uri.toString();
        return testUrl;

    }


    private String getEventsInfo(String eventId) throws ParseException {
        String testUrl = null;
        URL url = null; // Get URL

        try {
            testUrl = getInfoUrl(eventId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            url = new URL(testUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Get Content
        StringBuilder content = new StringBuilder();
        content = getContent(url, content);
        return content.toString();
    }

    private ArrayList<ConcertsInfo>  infoMap(String strJson, ArrayList<ConcertsInfo> concertsInfo) {
        JSONObject dataJsonObj = null;

        try {
            dataJsonObj = new JSONObject(strJson);
            JSONObject event = dataJsonObj.getJSONObject("message");


            concertsInfo.add(new ConcertsInfo(
                    event.getString("title").substring(7),
                    event.getString("str_date"),
                    event.getString("str_time"),
                    event.getString("min_price"),
                    event.getString("min_price")));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return concertsInfo;
    }

    }


