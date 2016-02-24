package pl.edu.uksw.metronome;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    String tempdate, today, yesterday;

    private final static String SUBHEAD = "subhead";
    LinearLayout my;
    //RelativeLayout my;

    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");


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

        Calendar cYesterday = Calendar.getInstance();
        cYesterday.add(Calendar.DATE, -1);


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
                    lastedtime += Long.toString(sec)+"s"+ " ";


                // add new layout with id (count) and two textViews
                if( df.format(Calendar.getInstance().getTime()).equals(sumhistory[count].substring(0, 10)) )
                {
                    tempdate = "Today";
                    today = sumhistory[count].substring(0, 10);
                }
                else if ( df.format(cYesterday.getTime()).equals(sumhistory[count].substring(0, 10)) )
                {
                    tempdate = "Yesterday";
                    yesterday = sumhistory[count].substring(0, 10);
                }
                else
                    tempdate = sumhistory[count].substring(0, 10);

                my = new HistoryHeaderLayout(this, tempdate , getString(R.string.summary) + " " + lastedtime);
                my.setId(count);
                my.setOnClickListener(this);
                linearLayout.addView(my);
                count++;
            }
        }

        // CLOSE DB
        dbhelp.close();


        SharedPreferences prefs = this.getSharedPreferences("pl.edu.uksw.metronome", Context.MODE_PRIVATE);

        // If history is first time opened
        if (!prefs.getBoolean("HistoryWasOpen", false)) {
            Toast.makeText(this, "Tap on date to show details", Toast.LENGTH_LONG).show();
            prefs.edit().putBoolean("HistoryWasOpen", true).commit();
        }
    }

    public void onClick(View v) {
        LinearLayout tmpLayout = (LinearLayout)v;                           // clicked linear layout
        //RelativeLayout tmpLayout = (RelativeLayout)v;
        View child = tmpLayout.findViewWithTag(SUBHEAD);                   // find View from parent layout
        TextView tmp = (TextView)child;                                   // assign textView from child
        String tmpdate = tmp.getText().toString();                       // get String from Header textView

        if(tmpdate == "Yesterday")
            tmpdate = yesterday;
        else if(tmpdate == "Today")
            tmpdate = today;

        String history = viewAllEntries(tmpdate);
        if (history.equals(""))
            history = "No entries";

        if(todelete != null)
        {
            mContainerView.removeView(todelete);
        }

        if(tmpLayout.getId() != tmpid) {
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                    R.layout.list_item_example, mContainerView, false);

            // create new textView and set text from database
            ((TextView)newView.findViewById(android.R.id.text1)).setText(history);
            mContainerView.addView(newView, tmpLayout.getId() + 1);
            todelete = newView;
            tmpid = tmpLayout.getId();
        }
        else
        {
            tmpid = -1;
        }
    }

    public String[] viewSumEntries() {
        Cursor resultOfQuery = db.query(DBOpenHelper.TABLE_NAME, DBOpenHelper.columns, null, null, null, null, "id DESC");
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
