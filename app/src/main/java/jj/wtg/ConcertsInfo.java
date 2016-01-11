package jj.wtg;

import java.util.HashMap;

public class ConcertsInfo extends HashMap<String, String > {
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String VENUE = "venue";
    public static final String PRICE = "price";

    public ConcertsInfo(String title, String date, String time, String venue,String price) {
        super();
        super.put(TITLE, title);
        super.put(DATE, date);
        super.put(TIME, time);
        super.put(VENUE, venue);
        super.put(PRICE, price);
    }

}
