package com.alphadominche.steampunkhmi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelperEvents;
import de.greenrobot.event.EventBus;

/**
 * @author zackargyle
 *         A background service to handle hourly syncing
 *         of local database with remote. Started after a
 *         successful login by startUpdateService()
 */
public class SPUpdateService extends Service {
    SPUpdateThread thread;
    private boolean running = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { ///SPLog.debug("got command to start running");
        if (!running) {
            thread = new SPUpdateThread();
            thread.start();
            running = true;
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        thread.running = false;
        super.onDestroy();
    }

}

/**
 * @author zackargyle
 *         A thread that broadcasts a sync event
 *         every 1 hour. The event is received by SPActivity,
 *         which handles the network calls. An activity context is
 *         required to get the DefaultPersistenceService instance.
 */
class SPUpdateThread extends Thread {
    public boolean running;
    private final int ONE_HOUR = 1000 * 60 * 60;
    private final int FIVE_MINUTES = 1000 * 5 * 60;
    private final int THIRTY_SECONDS = 1000 * 30;

    public void run() { //SPLog.debug("UPDATE THREAD STOPPED");
        this.running = true;

        try {
            sleep(THIRTY_SECONDS);
//    		sleep(FIVE_MINUTES);
        } catch (Exception e) {
        }

        while (this.running) {
            try {
                EventBus.getDefault().post(
                        new DefaultPersistenceServiceHelperEvents.SyncDatabase());
                sleep(ONE_HOUR);
//	        	sleep(FIVE_MINUTES);
            } catch (Exception e) {
            }
        } //SPLog.debug("UPDATE THREAD STOPPED");
    }

}
