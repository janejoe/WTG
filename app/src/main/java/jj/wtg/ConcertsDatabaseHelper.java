package jj.wtg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


public class ConcertsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "concerts.db";
    private static final int DATABASE_VERSION = 1;

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
}
