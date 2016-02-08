package pl.edu.uksw.metronome;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Herzy on 2016-02-03.
 */
public class DBOpenHelper  extends SQLiteOpenHelper {


    public final static String LOG_DB = "LOG_DB";
    public final static String DB_NAME = "metronome.db";
    public final static int DB_VERSION = 1;
    final static String TABLE_NAME = "history";
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
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public DBOpenHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        theContext = context;
    }

    public void deleteDatabase() {
        theContext.deleteDatabase(DB_NAME);
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
