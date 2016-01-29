package pl.edu.uksw.metronome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Jakub on 29/01/16.
 */
public class BeepService extends Service {

    public void onCreate(){

    }

    public int onStartCommand(Intent intent, int flags, int Idf) {

        return Service.START_NOT_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

}
