package jj.wtg;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import java.util.TreeMap;
import java.util.TreeSet;

public class MainSearching extends Fragment implements View.OnClickListener {

    private Button search;
    private Button updateButton;
    ProgressBar progress;

    private Map<String, String> concertsId = new TreeMap<>();
    private TreeSet<String> artistSet= new TreeSet<>();

    private ArrayList<ConcertsForList> concertsForList = new ArrayList<>();
    private ArrayList<ConcertsInfo> concertsInfo = new ArrayList<>();

    private ConcertsDatabaseHelper concertsDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.interval_fragment, container, false);

        search = (Button) v.findViewById(R.id.searchButton);
        updateButton = (Button) v.findViewById(R.id.dateSearchButton);
        progress = (ProgressBar) v.findViewById(R.id.progress);

        search.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        font();

        return v;
    }

    private void font() {
        Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), RepeatData.TYPEFONT);
        search.setTypeface(type2);
        updateButton.setTypeface(type2);
    }

    private TreeSet getArtistSet() {

        VKParameters params = new VKParameters();
        params.put(VKApiConst.COUNT, 6000);

        VKRequest requestAudio = VKApi.audio().get(params);
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override

            public void onComplete(VKResponse response) {
                super.onComplete(response);
                for (int i = 0; i < ((VKList<com.vk.sdk.api.model.VKApiAudio>) response.parsedModel).size(); i++) {
                    com.vk.sdk.api.model.VKApiAudio vkApiAudio = ((VKList<com.vk.sdk.api.model.VKApiAudio>) response.parsedModel).get(i);
                    artistSet.add(vkApiAudio.artist);
                }
                try {
                    new ScanAsyncTask().execute();

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
        return artistSet;
    }
    //--------------------Build url for request----------------------------------------

    String getUrlPonominalu() throws URISyntaxException {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.cultserv.ru")
                .appendPath("/jtransport/iphone/get_events")
                .appendQueryParameter("category", "10")
                .appendQueryParameter("min_date", df.format(date))
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

    //-------------------------------Fill DataBase With Concerts ID---------------------------------

    private Map getConcertsId (String strJson, Map<String, String> concertsId) {
        JSONObject dataJsonObj = null;
        concertsDatabaseHelper = new ConcertsDatabaseHelper(getActivity(), "concerts.db", null, 8);
        mSqLiteDatabase  = concertsDatabaseHelper.getReadableDatabase();

        try {
            dataJsonObj = new JSONObject(strJson);
            JSONArray events = dataJsonObj.getJSONArray("message");

            // Get all concerts into hashmap with id and title
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                concertsId.put(event.getString("title").substring(7), event.getString("id"));
            }

            ContentValues newValues = new ContentValues();
            // Задайте значения для каждого столбца
            for (String key : concertsId.keySet()) {
                newValues.put(ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN,  key);
                newValues.put(ConcertsDatabaseHelper.CONCERT_ID_COLUMN, concertsId.get(key));
                // Вставляем данные в таблицу
                mSqLiteDatabase.insert("concert", null, newValues);
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
               //update
                break;

            case R.id.searchButton:
                artistSet = getArtistSet();
                break;
        }
    }

    private class ScanAsyncTask extends AsyncTask<Void, Void, ArrayList<ConcertsInfo>> {
        @Override
        protected void onPreExecute() {
            progress.setVisibility(getView().VISIBLE);
            search.setVisibility(getView().INVISIBLE);
            updateButton.setVisibility(getView().INVISIBLE);
        }

        @Override
        protected ArrayList<ConcertsInfo> doInBackground(Void... parameter) {

            //----Check DataBase
            File database=getActivity().getDatabasePath("concerts.db");
            if (!database.exists()) {
                // Database is not Found
                if (!(artistSet.isEmpty())) {
                    String content = null;
                    content = getAllConcerts();
                    //Get all concert's titles and id's
                    concertsId = getConcertsId(content, concertsId);
                }

            }

            concertsDatabaseHelper = new ConcertsDatabaseHelper(getActivity(), "concerts.db", null, 8);
          /*  String content = null;
            content = getAllConcerts();
            //Get all concert's titles and id's
            concertsId = getConcertsId(content, concertsId);*/

            try {
                mSqLiteDatabase  = concertsDatabaseHelper.getReadableDatabase();
            }
            catch ( SQLiteException e) {
                e.printStackTrace();
            }

            Cursor cursor = mSqLiteDatabase.query("concert", new String[] {ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN,
                            ConcertsDatabaseHelper.CONCERT_ID_COLUMN},
                    null, null,
                    null, null, null) ;

            String title;
            String idConcert;

            for (String artist : artistSet) {
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    title = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN));
                    if (title.contains(artist)) {
                        idConcert = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_ID_COLUMN));
                        concertsForList.add(new ConcertsForList(artist, idConcert));
                        //get info
                        try {
                            String contentInfo;
                            contentInfo = getEventsInfo(idConcert);
                            concertsInfo = infoMap(contentInfo, concertsInfo);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            cursor.close();
            return concertsInfo;
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


