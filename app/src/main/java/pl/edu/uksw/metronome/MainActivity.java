package pl.edu.uksw.metronome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static String LOG = "MetronomeApp";
    Intent intent;
    TextView bpmTextView;
    int bpm = 90;
    String b = "9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, BeepService.class);
        if(startService(intent) != null){
            Log.d(LOG, "Service started");
        } else Log.d(LOG,"Service not started");

        bpmTextView = (TextView)findViewById(R.id.bpmTextView);
        bpmTextView.setText(""+(bpm));
    }

    public void faster(View view){
        bpm+=1;
        bpmTextView.setText(""+(bpm));
    }

    public void slower(View view){
        bpm-=1;
        bpmTextView.setText(""+(bpm));
    }

    public void start_stop(View view){
        bpm=90;
        bpmTextView.setText(""+(bpm));
    }
}
