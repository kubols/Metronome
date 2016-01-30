package pl.edu.uksw.metronome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BeepService extends Service {

    private static String LOG_S = "MetronomeService";
    private static String BPM_NAME = "bpm";
    private static String WORKING_NAME = "working";
    private Boolean working;
    private int bpm;

    public void onCreate(){

    }

    public int onStartCommand(Intent intent, int flags, int Idf) {

        working = intent.getBooleanExtra("working", false);
        bpm = intent.getIntExtra("bpm", 90);
        Log.d(LOG_S, "working: " + working + " bpm: " + bpm);
        return Service.START_NOT_STICKY;
    }

    public IBinder onBind(Intent intent) { return null; }

}
