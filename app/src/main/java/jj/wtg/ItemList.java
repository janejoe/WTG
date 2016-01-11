package jj.wtg;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
import java.util.Objects;


public class ItemList  extends Fragment{

    ArrayList<ConcertsForList> myList;
    private ArrayList<ConcertsInfo> concertsInfo = new ArrayList<ConcertsInfo>();
    IntervalForSearching intervalForSearching;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.list_fragment, container, false);
        ListView lv = (ListView)v.findViewById(R.id.concertListView);
        Bundle bundle = this.getArguments();

        myList = new ArrayList<>();

        if (bundle != null) {
            myList = (ArrayList<ConcertsForList>) bundle.getSerializable("concertsForList");
        }
        else Toast.makeText(getActivity(), "empty!", Toast.LENGTH_SHORT).show();

        ListAdapter adapter = new SimpleAdapter(getActivity(), myList , R.layout.list_item,
                new String[]{ConcertsForList.TITLE, ConcertsForList.ID},
                new int[]{R.id.nameListTextView, R.id.venueListTextView});

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Fragment fragment = new Info();
                Bundle bundle = new Bundle();
                bundle.putSerializable("concertsInfo", concertsInfo);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.frameContent, fragment).addToBackStack( "tag" ).commit();
            }
        });

        lv.setAdapter(adapter);


        return v;
    }


    //Parse ponominalu

    //Build url for request
    String getUrlPonominalu(String eventId) throws URISyntaxException {
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

    private String getAllConcerts(String eventId) throws ParseException {
        String testUrl = null;
        URL url = null; // Get URL

        try {
            testUrl = getUrlPonominalu(eventId);
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

    private ArrayList<ConcertsInfo>  consertsMap(String strJson, ArrayList<ConcertsInfo> concertsInfo) {
        JSONObject dataJsonObj = null;

        try {
            dataJsonObj = new JSONObject(strJson);
            JSONArray events = dataJsonObj.getJSONArray("message");

            // Get all concerts into hashmap with id and title
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                concertsInfo.add(new ConcertsInfo(
                        event.getString("title").substring(7),
                        event.getString("str_date"),
                        event.getString("str_time"),
                        event.getString("min_price"),
                        "0"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return concertsInfo;
    }

private class ScanAsyncTask extends AsyncTask<String, Integer, ArrayList<ConcertsInfo>> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<ConcertsInfo> doInBackground(String... parameter) {
        int myProgress = 0;
        //publishProgress(myProgress);

        //work with ponominalu

            String content;
            content = getAllConcerts(parameter[0]);
            concertsInfo = consertsMap(content, concertsInfo); //Get all concert's titles and id's

        return concertsInfo;
    }

}
}


