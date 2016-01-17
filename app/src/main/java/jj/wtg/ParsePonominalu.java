package jj.wtg;

import android.net.ParseException;
import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public  class ParsePonominalu {
    //--------------------Build url for request ALL CONCERTS----------------------------------------
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

    String getAllConcerts() throws ParseException {
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

    //--------------------Build url for request INFO ABOUT CONCERT----------------------------------

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

    String getEventsInfo(String eventId) throws ParseException {
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

    ArrayList<ConcertsInfo>  infoMap(String strJson, ArrayList<ConcertsInfo> concertsInfo) {
        JSONObject dataJsonObj = null;

        try {
            dataJsonObj = new JSONObject(strJson);
            JSONObject event = dataJsonObj.getJSONObject("message");
            JSONObject place = event.getJSONObject("venue");


            concertsInfo.add(new ConcertsInfo(
                    event.getString("title").substring(7),
                    event.getString("str_date"),
                    event.getString("str_time"),
                    place.getString("title"),
                    event.getString("min_price")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return concertsInfo;
    }

    ArrayList<ConcertsInfo> fillInfoList (ArrayList<ConcertsForList> concertsForList,
                                                  ArrayList<ConcertsInfo> concertsInfo){

        ConcertsForList concert;
        String id;

        for (int i=0; i<concertsForList.size(); i++){
            try {
                String contentInfo;
                concert = concertsForList.get(i);
                id = concert.get(ConcertsForList.ID);

                contentInfo = getEventsInfo(id);
                concertsInfo = infoMap(contentInfo, concertsInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return concertsInfo;
    }
}


