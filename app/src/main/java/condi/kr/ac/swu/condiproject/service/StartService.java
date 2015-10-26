package condi.kr.ac.swu.condiproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;

/**
 * Created by 8304 on 2015-10-27.
 */
public class StartService extends Service {

    Thread th;
    String user = Session.ID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        th = new Thread(new Runnable() {

            @Override
            public void run() {
                String result = "error";
                while (!result.equals("error")) {
                    String dml = "select groups from member where id='"+user+"'";
                    result = NetworkAction.sendDataToServer("groups.php");

                    if(!result.equals("error")) {
                        Intent intentResponse = new Intent("condi.kr.ac.swu.condiproject.groups");
                        sendBroadcast(intentResponse);
                    }

                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        th.start();
        return START_STICKY;
    }
}
