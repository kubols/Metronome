package pl.edu.uksw.metronome;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    private static String LOG = "MetronomeApp";
    private static String BPM_NAME = "bpm";
    private static String WORKING_NAME = "working";
    private boolean work = false;
    TextView bpmTextView;
    TextView tempoTextView;


    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;


    private Handler buttonHandler = new Handler();                  //handler to continuous increase or decrease bpm
    private static int DELAY = 70;                                  //delay time between runnable repeat
    private boolean incrementing = false;
    private boolean decrementing = false;
    ImageButton incrementButton;
    ImageButton decrementButton;

    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    RelativeLayout fabMore, fab1, fab2;
    private boolean isFabOpened;

    ImageView dot1, dot2, dot3, dot4;

    int bpm = 0;

    BeepService beepService = null;                                 //reference to service, initialized on connection to service
    boolean serviceConnected = false;                               //boolean variable if service is bounded

    long start;
    long stop;
    long time;


    private SQLiteDatabase db;
    private DBOpenHelper dbhelp;

    DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    String datestart = "";
    String lastedtime = "";
    Integer dotCounter = 0;
    Integer result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(broadcast, filter);

        // set up toolbar
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        dot1 = (ImageView)findViewById(R.id.dot1);
        dot2 = (ImageView)findViewById(R.id.dot2);
        dot3 = (ImageView)findViewById(R.id.dot3);
        dot4 = (ImageView)findViewById(R.id.dot4);



        //floating action button animations
        fabMore = (RelativeLayout)findViewById(R.id.fab_more);
        fab1 = (RelativeLayout)findViewById(R.id.fab1);
        fab2 = (RelativeLayout)findViewById(R.id.fab2);

        fabMore.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        isFabOpened = false;

        // open DB
        dbhelp = new DBOpenHelper(this);
        db = dbhelp.getWritableDatabase();

        bpmTextView = (TextView)findViewById(R.id.bpmTextView);
        bpmTextView.setText("" + (bpm));

        tempoTextView = (TextView)findViewById(R.id.tempo);
        tempoTextView.setText(assignTempo(bpm));

        /*
         * increment button listeners
         */
        incrementButton = (ImageButton)findViewById(R.id.incrementButton);
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
        decrementButton = (ImageButton)findViewById(R.id.decrementButton);
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


    private IntentFilter filter = new IntentFilter("pl.edu.uksw.metronome.Broadcast");

    public BroadcastReceiver broadcast = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            result = intent.getIntExtra("result",result);
            Log.i("cos", Integer.toString(result));
            setDot(result);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history:
                checkHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, BeepService.class), connection, Context.BIND_AUTO_CREATE);
        startService(new Intent(this, BeepService.class));

        Log.d(LOG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceConnected) {
            unbindService(connection);
            serviceConnected = false;
        }
        Log.d(LOG, "onStop");
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
            tempoTextView.setText(assignTempo(bpm));
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
            tempoTextView.setText(assignTempo(bpm));
            beepService.setBpm(bpm);
        }
    }


    public void setDot(Integer num)
    {
        if(num == 0) {
            dot4.setImageResource(R.drawable.dot);
            dot1.setImageResource(R.drawable.dot2);
        }
        else if(num == 1) {
            dot1.setImageResource(R.drawable.dot);
            dot2.setImageResource(R.drawable.dot2);
        }
        else if(num == 2) {
            dot2.setImageResource(R.drawable.dot);
            dot3.setImageResource(R.drawable.dot2);
        }
        else if(num == 3) {
            dot3.setImageResource(R.drawable.dot);
            dot4.setImageResource(R.drawable.dot2);
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
                start = System.currentTimeMillis();
                datestart = df.format(Calendar.getInstance().getTime());
                beepService.playBeep(work, bpm);
            }
            else {
                stop = System.currentTimeMillis();
                time = stop - start;
                long sec = time/1000;
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

                insertEntry();
                lastedtime = "";
                work = false;
                beepService.playBeep(work, bpm);
            }
        }
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id) {
            case R.id.fab_more:
                animateFAB();
                break;
            case R.id.fab1:
                Toast.makeText(this, "FAB 1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab2:
                Toast.makeText(this, "FAB 2", Toast.LENGTH_SHORT).show();
        }
    }

    private void animateFAB(){
        if(isFabOpened) {
            fabMore.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpened = false;
        }
        else {
            fabMore.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpened = true;
        }
    }

    public void setBpmManually(View view){
        /*AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());
        builder.setTitle("Set tempo").setMessage("Press");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bpm = 100;
                bpmTextView.setText("" + (bpm));
                tempoTextView.setText(assignTempo(bpm));
                beepService.setBpm(bpm);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();*/

        DialogFragment dialog = new PickBpmDialogFragment();
        dialog.show(getSupportFragmentManager(), "Dialog");
    }

    private String assignTempo(int bpm){
        if(bpm >= 30 && bpm < 40) return "Grave";
        else if(bpm >= 40 && bpm < 50) return "Largo";
        else if(bpm >= 50 && bpm < 60) return "Lento";
        else if(bpm >= 60 && bpm < 66) return "Larghetto";
        else if(bpm >= 66 && bpm < 76) return "Adagio";
        else if(bpm >= 76 && bpm < 108) return "Andante";
        else if(bpm >= 108 && bpm < 120) return "Moderato";
        else if(bpm >= 120 && bpm < 168) return "Allegro";
        else if(bpm >= 168 && bpm < 176) return "Vivace";
        else if(bpm >= 176 && bpm <= 200) return "Presto";
        else return "null";

    }

    public void insertEntry() {
        ContentValues cv = new ContentValues();
        // check if date is not empty
        if(datestart.length() > 9 && time > 1000) {
            cv.put(DBOpenHelper.date, datestart);
            cv.put(DBOpenHelper.lasted, lastedtime);
            cv.put(DBOpenHelper.lastedseconds, time);
            db.insert(DBOpenHelper.TABLE_NAME, null, cv);
            Log.d("cos", "a new entry was inserted:");
        }
    }

    public void checkHistory() {
        Intent myIntent = new Intent(this, BeepHistory.class);
        startActivity(myIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong("start", start);
        outState.putString("dateStart", datestart);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        start = savedInstanceState.getLong("start");
        datestart = savedInstanceState.getString("dateStart");
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
    }

    /*
     * class to interact with service
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(LOG, "Service connected");
            beepService = ((BeepService.MetronomeBinder)service).getService();
            serviceConnected = true;
            bpm = beepService.getBpm();                                                             //get bpm from service on connection with service
            bpmTextView.setText("" + (bpm));                                                        //set textView
            tempoTextView.setText(assignTempo(bpm));
            work = beepService.getRunning();                                                        //get boolean work from service
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG, "Service disconnected");
            beepService = null;
            serviceConnected = false;
        }
    };

    public class PickBpmDialogFragment extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Pick up tempo")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            bpm = 100;
                            bpmTextView.setText("" + (bpm));
                            tempoTextView.setText(assignTempo(bpm));
                            beepService.setBpm(bpm);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


}
