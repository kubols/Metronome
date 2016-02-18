package pl.edu.uksw.metronome;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
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
    private ActionBar actionBar;
    private ViewGroup mContainerView;
    ViewGroup todelete = null;

    public AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    public AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
    Integer todel = 0;
    Integer tmpid = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beephistory);
        mContainerView = (ViewGroup) findViewById(R.id.history);

        // set up toolbar
        Toolbar myToolbar = (Toolbar)findViewById(R.id.history_toolbar);
        setSupportActionBar(myToolbar);
        actionBar = getSupportActionBar();
        // enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);

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
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.history);
        TextView tmp = (TextView)v;
        String tmpdate = tmp.getText().toString();
        tmpdate = tmpdate.substring(0, 10);

        String history = viewAllEntries(tmpdate);
        if (history.equals(""))
            history = "No entries";

       // tmp.append("costam costam witam panstwa");
        if(todelete != null)
        {
            mContainerView.removeView(todelete);
        }

        if(tmp.getId() != tmpid) {
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                    R.layout.list_item_example, mContainerView, false);

            // Set the text in the new row to a random country.
            ((TextView) newView.findViewById(android.R.id.text1)).setText(history);
            Log.i("cos", Integer.toString(tmp.getId()));
            mContainerView.addView(newView, tmp.getId() + 1);
            todelete = newView;
            tmpid = tmp.getId();
        }
        else
        {
            tmpid = -1;
        }
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

    public String viewAllEntries(String tmpdate) {
        db = dbhelp.getWritableDatabase();
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
        dbhelp.close();
        return result;
    }
}
