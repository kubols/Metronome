package pl.edu.uksw.metronome;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Herzy on 2016-02-08.
 */
public class BeepHistory extends AppCompatActivity {

    private SQLiteDatabase db;
    private DBOpenHelper dbhelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beephistory);

        // OPEN DB
        dbhelp = new DBOpenHelper(this);
        db = dbhelp.getWritableDatabase();

        TextView listContent = (TextView)findViewById(R.id.historylist);
        String history = viewAllEntries();
        if (history == "")
            history = "No entries";

        listContent.setText(history);

        // CLOSE DB
        dbhelp.close();


    }



    public String viewAllEntries() {
        Cursor resultOfQuery = db.query(DBOpenHelper.TABLE_NAME, DBOpenHelper.columns, null, null, null, null, null);
        resultOfQuery.moveToFirst();
        String result = "";
        while(!resultOfQuery.isAfterLast()) {
            long id = resultOfQuery.getLong(0);
            String date = resultOfQuery.getString(1);
            String lasted = resultOfQuery.getString(2);
            result += date + " " + lasted +  System.getProperty("line.separator");

            resultOfQuery.moveToNext();
        }
        resultOfQuery.close();
        return result;
    }
}
