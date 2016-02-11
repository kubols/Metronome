package pl.edu.uksw.metronome;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Herzy on 2016-02-08.
 */
public class BeepHistory extends AppCompatActivity implements View.OnClickListener {

    private static String H_LOG = "MetronomeApp_History";
    private SQLiteDatabase db;
    private DBOpenHelper dbhelp;
    String tmpdate = "temp10lengt";
    Integer sumtime[] = new Integer[999];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beephistory);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.history);

        // OPEN DB
        dbhelp = new DBOpenHelper(this);
        db = dbhelp.getWritableDatabase();
        //    db.execSQL("DELETE FROM history WHERE lasted=''");
        String text = "";

        String sumhistory[] = viewSumEntries();
        Integer count = 0;
        if(sumhistory[0] == null && sumtime[0] == null)
        {
            TextView date = new TextView(this);
            date.setText("No entries");
            linearLayout.addView(date);
        }
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

                TextView date = new TextView(this);
                date.setText(sumhistory[count].substring(0, 10) + " " + lastedtime + System.getProperty("line.separator"));
                date.setId(count);
                date.setOnClickListener(this);
                linearLayout.addView(date);

                count++;
            }
        }

        // CLOSE DB
        dbhelp.close();
    }

    public void onClick(View v) {
        TextView tmp = (TextView)v;
        String tmpdate = tmp.getText().toString();
        tmpdate = tmpdate.substring(0, 10);

        Intent myIntent = new Intent(this, BeepDayHistory.class);
        myIntent.putExtra("date", tmpdate);
        startActivity(myIntent);
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
            Log.i(H_LOG,"d: "+date +" t: " + tmpdate);
            if (date.startsWith(tmpdate.substring(0,10)))
            {
                sumtime[i] += (lastedsec/1000)*1000; // ignore miliseconds
                Log.i(H_LOG,"rowne");
            }
            else
            {
                i++;
                result[i] = date;
                sumtime[i] = 0;
                sumtime[i] += (lastedsec/1000)*1000; // ignore miliseconds
                tmpdate = date;
                Log.i(H_LOG,"nierowne");
            }
            resultOfQuery.moveToNext();
        }
        resultOfQuery.close();
        return result;
    }
}
