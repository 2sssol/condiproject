package condi.kr.ac.swu.condiproject.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;

public class AccSensor extends Service implements SensorEventListener {

    private SensorManager sm;
    private Sensor accSensor;
    private String dml;
    private String today;
    private String user = Session.ID;

    public int MY_WALK_COUNT = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        /*
        * date
        * */
        DateFormat df = new SimpleDateFormat("yyMMdd");
        df.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        today = df.format(new Date());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    dml = "update walk set currentwalk="+ MY_WALK_COUNT +" where user='"+ user+"' and date_format(today,'%y%m%d')=str_to_date('"+ today +"','%y%m%d')";
                    System.out.println("걸음 : "+ dml + "\n ==> "+NetworkAction.sendDataToServer(dml)+" : "+MY_WALK_COUNT);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(accSensor != null)
            sm.unregisterListener(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(accSensor != null)
            sm.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_GAME);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    if ((event.values[0] < -10) || (event.values[0] > 10)
                            || (event.values[1] < -10) || (event.values[1] > 10)
                            || (event.values[2] < -10) || (event.values[2] > 10)) {

                        ++MY_WALK_COUNT;
                        Intent intentResponse = new Intent("condi.kr.ac.swu.condiproject.step");
                        intentResponse.putExtra("walk", Integer.toString(MY_WALK_COUNT));
                        sendBroadcast(intentResponse);
                    }

                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }




}
