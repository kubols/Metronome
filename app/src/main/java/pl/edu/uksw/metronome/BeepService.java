package pl.edu.uksw.metronome;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class BeepService extends Service {

    private static String LOG_S = "MetronomeService";
    private int bpm = 90;
    public int dotNumber = 0;
    public int dotsNumber = 3;
    private boolean work = false;


    Handler handler;

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

    /*
     * method creates new Thread and loops the Runnable
     * takes two variables from MainActivity work (if it should start or stop)
     * bpm (beats per minute to set the tempo)
     * breaks the loop when work equals false
     */
    public void playBeep(boolean work, int bpm){
        if(work){
            Log.d(LOG_S, "beep, beep, beep " + bpm);
            setWork(work);

            handler = new Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    if(getWork()){
                        handler.postDelayed(this, 60000 / getBpm());
                        playMedia();
                        Intent intent = new Intent();
                        intent.putExtra("result", getDotNumber());
                        setDotNumber((getDotNumber() + 1) % getDotsNumber());
                        intent.setAction("pl.edu.uksw.metronome.Broadcast");
                        sendBroadcast(intent);
                    }
                }
            };
            handler.post(r);
        }
        else {
            Log.d(LOG_S, "stop");
            setWork(false);
        }
    }

    public void playMedia(){
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
            }
        });
        mp.start();
        Log.d(LOG_S, "playing beep in bpm: " + bpm);
    }

    public int getBpm(){
        return bpm;
    }

    public void setBpm(int bpm){
        this.bpm = bpm;
    }

    public boolean getWork() { return work; }

    public void setWork(boolean work){
        this.work = work;
    }

    public void setDotsNumber(Integer number) {this.dotsNumber = number;}

    public void setDotNumber(Integer number) {this.dotNumber = number;}

    public Integer getDotsNumber() {return dotsNumber;}

    public Integer getDotNumber() {return dotNumber;}

}
