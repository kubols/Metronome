package pl.edu.uksw.metronome;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BeepService extends Service {

    private static String LOG_S = "MetronomeService";

    /*
     * this class will return the Service instance, MetronomeBinder object will be returned on binding with the MainActivity
     */
    public class MetronomeBinder extends Binder {

        BeepService getService() {
            return BeepService.this;
        }

    }

    private final IBinder myBinder = new MetronomeBinder();

    public void onCreate(){

    }

    public int onStartCommand(Intent intent, int flags, int Idf) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_S, "Binding...");
        return myBinder;
    }

    public void playBeep(boolean work, int bpm){
        if(work) {
            Log.d(LOG_S, "beep, beep, beep " + bpm);
            playMedia();
        }
        else Log.d(LOG_S, "stop");
    }

    private void playMedia(){
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep);
    }

}
