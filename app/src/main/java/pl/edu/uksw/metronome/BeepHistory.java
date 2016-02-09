package pl.edu.uksw.metronome;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Herzy on 2016-02-08.
 */
public class BeepHistory extends AppCompatActivity {

    private SQLiteDatabase db;
    private DBOpenHelper dbhelp;
    String tmpdate = "temp10leng";
    Integer sumtime[] = new Integer[999];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beephistory);

        // OPEN DB
        dbhelp = new DBOpenHelper(this);
        db = dbhelp.getWritableDatabase();
        String text = "";

        TextView listContent = (TextView)findViewById(R.id.historylist);
      //  String history = viewAllEntries();
      //  if (history == "")
       //     history = "No entries";

        //listContent.setText(history);

        String sumhistory[] = viewSumEntries();
        Integer count = 0;
        if(sumhistory[0] == null && sumtime[0] == null)
            listContent.setText("No entries");
        else
        {
            while(sumhistory[count] != null)
            {
                String lastedtime = "";
                long sec = sumtime[count]/1000;
                long min = sec/60;
                long hour = min/60;
                sec = sec%60;
                min = min%60;

                if(hour > 0)
                    lastedtime += Long.toString(hour)+"h"+" ";
                if(min > 0)
                    lastedtime += Long.toString(min)+"min"+" ";
                if(sec > 0)
                    lastedtime += Long.toString(sec)+"s"+" ";

                text += sumhistory[count].substring(0,10) + " " + lastedtime + System.getProperty("line.separator");
                Log.i("cos", sumhistory[count]);
                count++;
            }
            listContent.setText(text);
        }

        // CLOSE DB
        dbhelp.close();


    }

    public String[] viewSumEntries() {
        Cursor resultOfQuery = db.query(DBOpenHelper.TABLE_NAME, DBOpenHelper.columns, null, null, null, null, null);
        resultOfQuery.moveToFirst();
        String result[] = new String[999];
        Integer i = -1;
        while(!resultOfQuery.isAfterLast()) {
            long id = resultOfQuery.getLong(0);
            String date = resultOfQuery.getString(1);
            Integer lastedsec = resultOfQuery.getInt(3);
            Log.i("cos",date +" " + tmpdate);
            if (date.startsWith(tmpdate.substring(0,10)))
            {
                sumtime[i] += lastedsec;
                Log.i("cos","rowne");
            }
            else
            {
                i++;
                result[i] = date;
                sumtime[i] = 0;
                tmpdate = date;
            }
            resultOfQuery.moveToNext();
        }
        resultOfQuery.close();
        return result;
    }


    public String viewAllEntries() {
        Cursor resultOfQuery = db.query(DBOpenHelper.TABLE_NAME, DBOpenHelper.columns, null, null, null, null, null);
        resultOfQuery.moveToFirst();
        String result = "";
        while(!resultOfQuery.isAfterLast()) {
            long id = resultOfQuery.getLong(0);
            String date = resultOfQuery.getString(1);
            String lasted = resultOfQuery.getString(2);
            Integer lastedsec = resultOfQuery.getInt(3);
            result += date + " " + lasted + " " + lastedsec + System.getProperty("line.separator");

            resultOfQuery.moveToNext();
        }
        resultOfQuery.close();
        return result;
    }
}
