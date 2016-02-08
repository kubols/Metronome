package pl.edu.uksw.metronome;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

/**
 * Created by Herzy on 2016-02-03.
 */
public class GeoDBOpenHelper  extends SQLiteOpenHelper {


    public final static String LOG_DB = "LOG_DB";

    // a DB with only one table _ID COUNTRY CAPITALE MONEY POPULATION (it's enough for the example)

    public final static String DB_NAME = "testgeo2.db";
    public final static int DB_VERSION = 1;
    final static String TABLE_NAME = "fruits";
    final static String _ID = "id";
    final static String date = "date";
    final static String lasted = "lasted";
    final static String columns[] = {_ID,date,lasted};

    private Context theContext;

    final static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME +"("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + date + " TEXT, "
            + lasted + " TEXT)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_DB,"in onCreate() of the GeoDBOpenHelper");
        db.execSQL(CREATE_TABLE);
        Log.d(LOG_DB, "after table creation");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_DB,"in onUpgrade() of the GeoDBOpenHelper");
    }

    public GeoDBOpenHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        theContext = context;
    }

    public void deleteDatabase() {
        theContext.deleteDatabase(DB_NAME);
        Log.d(LOG_DB, "in deleteDatabase(" + DB_NAME + ") of the GeoDBOpenHelper");
    }


    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_NAME, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }
}
