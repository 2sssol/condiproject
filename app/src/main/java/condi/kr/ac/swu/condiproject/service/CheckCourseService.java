package condi.kr.ac.swu.condiproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;

public class CheckCourseService extends Service {

    private Thread th;
    private String groups = Session.GROUPS;
    private String user = Session.ID;
    private List<Properties> list;
    private int count = 0;
    private boolean[] isAllOks ;

    @Override
    public void onCreate() {
        super.onCreate();

        th = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            String dml = "select * from member where groups = "+groups+" and id!='"+user+"'";
                            return NetworkAction.sendDataToServer("checkCourse.php", dml);
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            if(!o.equals("error")) {
                                new AsyncTask() {
                                    @Override
                                    protected Object doInBackground(Object[] params) {
                                        try {
                                            list = NetworkAction.parse("checkCourse.xml", "member");
                                        } catch (XmlPullParserException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object o) {
                                        super.onPostExecute(o);

                                       isAllOks = new boolean[list.size()];
                                        for(Properties p : list) {
                                            if(!isAllOks[count]) {
                                                if (!p.getProperty("course").equals("") || p.getProperty("course") != null) {
                                                    Intent intentResponse = new Intent("condi.kr.ac.swu.condiproject.course");
                                                    intentResponse.putExtra("id", p.getProperty("id"));
                                                    intentResponse.putExtra("nickname", p.getProperty("nickname"));
                                                    intentResponse.putExtra("course", p.getProperty("course"));
                                                    sendBroadcast(intentResponse);

                                                    isAllOks[count] = true;
                                                }
                                            }
                                            count++;
                                        }
                                    }
                                }.execute();
                            }
                        }
                    }.execute();


                    count = 0;

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!th.isAlive())
            th.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        th.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
