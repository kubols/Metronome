package pl.edu.uksw.metronome;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class BeepService extends Service {

    private static String LOG_S = "MetronomeService";
    //private final AtomicBoolean running = new AtomicBoolean(false);
    private int bpm = 90;
    private boolean work = false;

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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_S, "running... " + this);
                    while(getWork()){
                        Log.d(LOG_S, "inside loop - " + getWork() + " - status");
                        try {
                            playMedia();
                            Thread.sleep(60000/ getBpm());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        else {
            Log.d(LOG_S, "stop");
            setWork(false);
            //running.set(false);
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

//    public boolean getRunning(){
//        if(running.get()) return true;
//        else return false;
//    }

}
