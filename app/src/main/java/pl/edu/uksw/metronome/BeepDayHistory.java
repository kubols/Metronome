package pl.edu.uksw.metronome;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Created by Herzy on 2016-02-09.
 */
public class BeepDayHistory extends AppCompatActivity {

    private SQLiteDatabase db;
    private DBOpenHelper dbhelp;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayhistory);

        // set up toolbar
        Toolbar myToolbar = (Toolbar)findViewById(R.id.day_history_toolbar);
        setSupportActionBar(myToolbar);
        actionBar = getSupportActionBar();
        // enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        String date = "";

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                date = null;
            } else {
                date = extras.getString("date");
            }
        } else {
            date = (String) savedInstanceState.getSerializable("date");
        }

        if(date != null) {


            // OPEN DB
            dbhelp = new DBOpenHelper(this);
            db = dbhelp.getWritableDatabase();
            //  db.execSQL("DELETE FROM history WHERE date=''");
            String text = "";

            TextView daylist = (TextView) findViewById(R.id.daylist);
            String history = viewAllEntries(date);
              if (history.equals(""))
                 history = "No entries";

            daylist.setText(history);


            // CLOSE DB
            dbhelp.close();
        }
    }

    public String viewAllEntries(String tmpdate) {
        Cursor resultOfQuery = db.rawQuery("SELECT date, lasted FROM history WHERE date LIKE '"+tmpdate+"%'", null);
        resultOfQuery.moveToFirst();
        String result = "";
        while(!resultOfQuery.isAfterLast()) {
            String date = resultOfQuery.getString(0);
            String lasted = resultOfQuery.getString(1);
            result += date + " " + lasted  + System.getProperty("line.separator");
            resultOfQuery.moveToNext();
        }
        resultOfQuery.close();
        return result;
    }
}
