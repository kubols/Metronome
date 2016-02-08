package pl.edu.uksw.metronome;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private static String LOG = "MetronomeApp";
    private static String BPM_NAME = "bpm";
    private static String WORKING_NAME = "working";
    private boolean work = false;
    TextView bpmTextView;

    private Handler buttonHandler = new Handler();                  //handler to continuous increase or decrease bpm
    private static int DELAY = 70;                                  //delay time between runnable repeat
    private boolean incrementing = false;
    private boolean decrementing = false;
    Button incrementButton;
    Button decrementButton;

    int bpm = 90;

    BeepService beepService = null;                                 //reference to service, initialized on connection to service
    boolean serviceConnected = false;                               //boolean variable if service is bounded

    long start;
    long stop;

    private SQLiteDatabase geodb;
    private GeoDBOpenHelper dbhelp;


    DateFormat df = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
    String datestart = "";
    String lastedtime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // open DB
        dbhelp = new GeoDBOpenHelper(this);
        geodb = dbhelp.getWritableDatabase();

        bpmTextView = (TextView)findViewById(R.id.bpmTextView);
        bpmTextView.setText("" + (bpm));

        /*
         * increment button listeners
         */
        incrementButton = (Button)findViewById(R.id.incrementButton);
        // long press
        incrementButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                incrementing = true;
                buttonHandler.post(new ButtonsLongPressHandler());
                return false;
            }
        });
        // cancel press
        incrementButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) && incrementing) {
                    incrementing = false;
                }
                return false;
            }
        });
        // click
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment();
            }
        });

        /*
         * decrement button listeners
         */
        decrementButton = (Button)findViewById(R.id.decrementButton);
        // long press
        decrementButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                decrementing = true;
                buttonHandler.post(new ButtonsLongPressHandler());
                return false;
            }
        });
        // cancel press
        decrementButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) && decrementing) {
                    decrementing = false;
                }
                return false;
            }
        });
        // click
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, BeepService.class), connection, Context.BIND_AUTO_CREATE);
        startService(new Intent(this, BeepService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceConnected) {
            unbindService(connection);
            serviceConnected = false;
        }
    }

    @Override
    protected void onDestroy() {
        if (serviceConnected) {
            unbindService(connection);
            stopService(new Intent(this, BeepService.class));           // service is stopped only when application is completely closed
            serviceConnected = false;
        }
        super.onDestroy();
    }

    /*
     * Faster bpm button
     */
    public void increment(){
        if(bpm >= 30 && bpm < 200) {
            bpm++;
            bpmTextView.setText("" + (bpm));
            beepService.setBpm(bpm);
        }
    }

    /*
     * Slower bpm button
     */
    public void decrement(){
        if(bpm > 30 && bpm <= 200) {
            bpm--;
            bpmTextView.setText("" + (bpm));
            beepService.setBpm(bpm);
        }
    }

    /*
     * Start/Stop bpm button
     */
    public void start_stop(View view){
        if(beepService != null){
            // if metronome is not ticking, start
            if(!work) {
                work = true;
                beepService.playBeep(work, bpm);
            }
            else {
                work = false;
                beepService.playBeep(work, bpm);
            }
        }
    }

    public void insertEntry() {
        ContentValues cv = new ContentValues();
        cv.put(GeoDBOpenHelper.date, datestart);
        cv.put(GeoDBOpenHelper.lasted, lastedtime);
        geodb.insert(GeoDBOpenHelper.TABLE_NAME, null, cv);
        Log.d("cos", "a new entry was inserted:");
    }

    public void checkHistory(View view)
    {
        Intent myIntent = new Intent(this, BeepHistory.class);
        startActivity(myIntent);
    }

    private class ButtonsLongPressHandler implements Runnable {
        @Override
        public void run() {
            if (incrementing){
                increment();
                buttonHandler.postDelayed(new ButtonsLongPressHandler(), DELAY);
            }
            else if (decrementing){
                decrement();
                buttonHandler.postDelayed(new ButtonsLongPressHandler(), DELAY);
            }
        }
    };

    /*
     * class to interact with service
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(LOG, "Service connected");
            beepService = ((BeepService.MetronomeBinder)service).getService();
            serviceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG, "Service disconnected");
            beepService = null;
            serviceConnected = false;
        }
    };
}
