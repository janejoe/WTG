package jj.wtg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class ConcertsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "concerts.db";
    static final int DATABASE_VERSION = 8;

    private static final String DATABASE_TABLE = "concert";
    public static final String CONCERT_TITLE_COLUMN = "title_concert";
    public static final String CONCERT_ID_COLUMN = "id_concert";


    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + CONCERT_TITLE_COLUMN
            + " text not null, " + CONCERT_ID_COLUMN + " text not null);";



    public ConcertsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE + "'");

        // Создаём новую таблицу
        onCreate(db);

    }


    // ---------------------------With TREEMAP----------------------------------------------------//
    public ArrayList<ConcertsForList> searchWithTree (ConcertsDatabaseHelper db,TreeSet<String> artistSet,
                                                      ArrayList<ConcertsForList> concertsForList){
        Map<String, String> concertsId = new TreeMap<>();
        SQLiteDatabase myDB = db.getReadableDatabase();

        Cursor cursor = myDB.query("concert", new String[] {ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN,
                        ConcertsDatabaseHelper.CONCERT_ID_COLUMN},
                null, null,
                null, null, null) ;

        String title;
        String idConcert;

        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN));
            idConcert = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_ID_COLUMN));
            concertsId.put(title,idConcert);
        }
        cursor.close();

        for (String artist : artistSet) {
            for (String key : concertsId.keySet()) {
                if (key.contains(artist)) {
                    concertsForList.add(new ConcertsForList(artist, concertsId.get(key)));
                    break;


                }
            }
        }

        return concertsForList;
    }

    //-----------------------------Without TREEMAP------------------------------------------------//

    public ArrayList<ConcertsForList> searchWithoutTree (ConcertsDatabaseHelper db,TreeSet<String> artistSet,
                                                      ArrayList<ConcertsForList> concertsForList ) {

        SQLiteDatabase mSqLiteDatabase = db.getReadableDatabase();

        Cursor cursor = mSqLiteDatabase.query("concert", new String[]{ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN,
                        ConcertsDatabaseHelper.CONCERT_ID_COLUMN},
                null, null,
                null, null, null);

        concertsForList = hardSearch(cursor, artistSet, concertsForList);
        //concertsForList = simpleSearch( cursor, artistSet, concertsForList);

        return concertsForList;
    }

//-------------------------------Fill DataBase With Concerts ID---------------------------------

     void getConcertsId (String strJson,
                               ConcertsDatabaseHelper concertsDatabaseHelper) {
        Map<String, String> concertsId = new TreeMap<>();
        JSONObject dataJsonObj = null;
         SQLiteDatabase mSqLiteDatabase = concertsDatabaseHelper.getReadableDatabase();


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

    }

    ArrayList<ConcertsForList> simpleSearch (Cursor cursor,TreeSet<String> artistSet,
                                             ArrayList<ConcertsForList> concertsForList){

        String title;
        String idConcert;
                for (String artist : artistSet) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                title = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN));
                if (title.contains(artist)) {
                    idConcert = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_ID_COLUMN));
                    concertsForList.add(new ConcertsForList(artist, idConcert));
                    break;
                    }
                }
            }
        cursor.close();
        return concertsForList;

    }

    ArrayList<ConcertsForList> hardSearch (Cursor cursor,TreeSet<String> artistSet,
                                           ArrayList<ConcertsForList> concertsForList){


        String title;
        String idConcert;



        artistSet.add("AAA");
        artistSet.add("ААА");
        artistSet.add("0000");
/* int indEng=0;
int indRus=0;
cursor.moveToFirst();

while (cursor.moveToNext()) {
title = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN));
if ( !title.startsWith("A")){
indEng++;

}
else {
indRus=indEng;
break;
}
}

while (cursor.moveToNext()) {
title = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN));
if ( !title.startsWith("А")){
indRus++;
}
else {
break;
}
}
*/
        int indEng = 6;
        int indRus = 203;

        int engArt=0;
        int rusArt=0;

        for (String art: artistSet){
            if ( !art.equals("AAA")){
                engArt++;
            }
            else break;
        }
        for (String art: artistSet){
            if ( !art.equals("ААА")){
                rusArt++;
            }
            else break;
        }



        Iterator<String> iterator = artistSet.iterator();
        String artist;


        for (int i=0; i != engArt; i++) {// исполнитель до англ включительно
            cursor.moveToFirst();
            artist = iterator.next().toString();
            for (int k = 0; k < indEng; k++){//концерты до англ
                title = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN));
                if (title.contains(artist )) {// если концерт содержит артиста, сохраняем
                    idConcert = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_ID_COLUMN));
                    concertsForList.add(new ConcertsForList(artist, idConcert));
                    break;
                }
                else
                    cursor.moveToNext(); // если не содержит, бежим дальше по бд
            }
        }

        for (int i=0; i != rusArt; i++) {// исполнитель до рус включительно
            artist = iterator.next().toString();
            cursor.moveToFirst();
            cursor.move(indEng); // сдвигаемся по бд до первого слова после английского AAA
            for (int k = 0; k < indRus; k++){//концерты до рус
                title = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN));
                if (title.contains(artist )) {// если концерт содержит артиста, сохраняем
                    idConcert = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_ID_COLUMN));
                    concertsForList.add(new ConcertsForList(artist, idConcert));
                    break;
                }
                else
                    cursor.moveToNext(); // если не содержит, бежим дальше по бд
            }
        }

        while ( iterator.hasNext()) {// русккий исполнитель
            cursor.moveToFirst();
            cursor.move(indRus); // сдвигаемся по бд до первого слова после русского AAA
            artist = iterator.next().toString();
            while (cursor.moveToNext()) {//концерты до конца
                title = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_TITLE_COLUMN));
                if (title.contains(artist)) {// если концерт содержит артиста, сохраняем
                    idConcert = cursor.getString(cursor.getColumnIndex(ConcertsDatabaseHelper.CONCERT_ID_COLUMN));
                    concertsForList.add(new ConcertsForList(artist, idConcert));
                    break;
                }
            }
        }
        return concertsForList;

    }



}

