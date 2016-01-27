package pl.edu.uksw.metronome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView bpmTextView;
    int bpm = 90;
    String b = "9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
