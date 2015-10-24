package condi.kr.ac.swu.condiproject.activity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.GlobalApplication;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;
import condi.kr.ac.swu.condiproject.view.CircularNetworkImageView;
import condi.kr.ac.swu.condiproject.view.CustomCircularRingView;
import condi.kr.ac.swu.condiproject.view.PatchPointView;
import condi.kr.ac.swu.condiproject.view.adapter.GroupListAdapter;

public class GroupActivity extends BaseActivity {

    private CustomCircularRingView myView;
    private PatchPointView patchPointView;

    private TextView txtTotalDate, txtTotalKM ; // 전체 일수, km
    private TextView txtPercent, txtCurrentDate, txtCurrentKM;

    private ListView grouplv;
    private GroupListAdapter adapter;

    private int walk = 0;
    private int period;
    private float totalKM;

    // my
    private CircularNetworkImageView p1;
    private TextView pname1, pcurrent1, pkm1, pcourse1;

    // thread
    private Handler graphHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        initActionBar("어울림");
        initView();
    }

    private void initView() {
        // graph
        myView = (CustomCircularRingView) findViewById(R.id.customCircularRingView);
        patchPointView = (PatchPointView) findViewById(R.id.patchPointView);
        txtPercent = (TextView) findViewById(R.id.txtPercent);
        txtCurrentKM = (TextView) findViewById(R.id.txtCurrentKM);

        // date
        txtTotalDate = (TextView) findViewById(R.id.txtTotalDate);
        txtTotalKM = (TextView) findViewById(R.id.txtTotalKM);
        txtCurrentDate = (TextView) findViewById(R.id.txtCurrentDate);
        setDateKM ();

        // my
        p1 = (CircularNetworkImageView) findViewById(R.id.p1);
        pname1 = (TextView) findViewById(R.id.pname1);
        pcurrent1 = (TextView) findViewById(R.id.pcurrent1);
        pcourse1 = (TextView) findViewById(R.id.pcourse1);
        pkm1 = (TextView) findViewById(R.id.pkm1);
        setMy();
    }

    private void setDateKM () {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select goaldays, goalkm, date_format(startdate,'%y%m%d') as startdate " +
                        "from groups " +
                        "where id="+ Session.GROUPS;
                return NetworkAction.sendDataToServer("goaldayskm.php",dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                String[] results;
                if(o.equals("error"))
                    printErrorMsg("error : cannot select goaldays, goalkm");
                else {
                    results = o.toString().split("/");
                    txtTotalDate.setText(results[0]);
                    txtTotalKM.setText(results[1]+"KM");
                    totalKM = Float.parseFloat(results[1]);
                    /*
                    * current date
                    * */
                    try {
                        Date startDate = new SimpleDateFormat("yyMMdd").parse(results[3]);       // startdate
                        Date today = new Date();

                        period = today.compareTo(startDate);
                        txtCurrentDate.setText(period);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    private void setMy() {
        setProfileURL(p1, Session.PROFILE);
        pname1.setText(Session.NICKNAME);
        setSensor();

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from course where id="+Session.COURSE;
                return NetworkAction.sendDataToServer("course.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                if(!o.equals("error")) {
                    new AsyncTask() {
                        List<Properties> my;
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            try {
                                my = NetworkAction.parse("course.php", "course");
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
                            pcourse1.setText(my.get(0).getProperty("name"));
                            pkm1.setText(my.get(0).getProperty("km"));
                        }
                    }.execute();
                }
            }
        }.execute();

    }

    private void setPatchPointView() {


    }

    private void setMyView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int percent = 0;
                int totalStep = 0;
                while (percent<100) {
                    String dml = "select sum(currentwalk) as count " +
                            "from walk " +
                            "where user in (select id from member where groups="+Session.GROUPS+")";
                    totalStep = Integer.parseInt(NetworkAction.sendDataToServer("sum.php", dml));
                    percent = (int) ((Math.round(totalStep * 0.011559 * 100)/100)/totalKM);
                }
            }
        }).start();
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select sum(currentwalk) as count " +
                        "from walk " +
                        "where user in (select id from member where groups="+Session.GROUPS+")";
                return NetworkAction.sendDataToServer("sum.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                /*
                * percent, graph
                * */


            }
        }.execute();
    }

    /*
    * ================================================================== sensor ======================================================================
    * */
    private void setSensor() {
        registerReceiver(broadcastReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.step"));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            walk = Integer.parseInt(intent.getStringExtra("walk"));
            pcurrent1.setText(String.format("%s KM | %s 걸음", ( Math.round(walk * 0.011559 * 100)/100), walk));
        }
    };

    public void setProfileURL(final CircularNetworkImageView profile, final String profileImageURL) {

        Application app = GlobalApplication.getGlobalApplicationContext();
        if (app == null)
            throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");

        if (profile != null && profileImageURL != null) {
            if(profileImageURL.equals(""))
                profile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
            else
                profile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/"+profileImageURL, ((GlobalApplication) app).getImageLoader());
        } else  {
            profile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
        }
    }
}
