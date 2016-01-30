package pl.edu.uksw.metronome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static String LOG = "MetronomeApp";
    private static String BPM_NAME = "bpm";
    private static String WORKING_NAME = "working";
    private Boolean working = false;
    Intent intent;
    TextView bpmTextView;
    int bpm = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bpmTextView = (TextView)findViewById(R.id.bpmTextView);
        bpmTextView.setText("" + (bpm));
    }

    public void faster(View view){
        bpm+=1;
        bpmTextView.setText("" + (bpm));
    }

    public void slower(View view){
        bpm-=1;
        bpmTextView.setText("" + (bpm));
    }

    public void start_stop(View view){
        if (!working) {
            working = true;
            intent = new Intent(this, BeepService.class);
            intent.putExtra(BPM_NAME, bpm);
            intent.putExtra(WORKING_NAME, working);
            if(startService(intent) != null){
                Log.d(LOG, "Service started");
            } else Log.d(LOG,"Service not started");
        }
        else {
            working = false;
            stopService(intent);
            Log.d(LOG, "Stopping service");
        }
    }
}
