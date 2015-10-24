package condi.kr.ac.swu.condiproject.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;
import condi.kr.ac.swu.condiproject.view.CustomCircularRingView;
import condi.kr.ac.swu.condiproject.view.PatchPointView;

public class MyActivity extends BaseActivity {

    private CustomCircularRingView myView;
    private PatchPointView patchPointView;

    private static String GRAY = "#FF777777";
    private static String BLUE = "#ff30c9ff";
    private static String WHITE = "#ffffff";

    private List<Properties> walks;
    private LinearLayout barChart, barDate;

    private int walk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initActionBar("나의 걸음");
        iniView();
        initGraph();
    }

    private void iniView() {
        myView = (CustomCircularRingView) findViewById(R.id.customCircularRingView2);
        myView.changePercentage(0);
        myView.invalidate();

        patchPointView = (PatchPointView) findViewById(R.id.patchPointView2);
    }

    /*
    * init메소드
    * */
    private void initGraph() {
        barChart = (LinearLayout) findViewById(R.id.barChart);
        barDate = (LinearLayout) findViewById(R.id.barDate);
        new loadingWalk().execute();
    }

    /*
    * 걸음수 로드 내부 클래스
    * */
    private class loadingWalk extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
           String dml = "select * from walk where user='"+ Session.ID+"'";
            return NetworkAction.sendDataToServer("mywalk.php", dml);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("success")) {
                new readWalk().execute();
            } else {
                System.out.println("데이터 가져오기 오류 : walk");
            }
        }
    }

    private class readWalk extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                walks = NetworkAction.parse("mywalk.xml", "walk");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(!walks.isEmpty()) {
                drawGragh(walks);
            } else {
                System.out.println("걸음 기록이 없습니다.");
            }
        }
    }

    /*
    * 그래프 그리기
    *   1. drawChart : 막대그래프
    *   2. drawDate : 날짜
    * */
    public void drawGragh(List<Properties> walks) {
        drawChart(walks);
        drawDate(walks);
    }

    public void drawChart(List<Properties> walks) {
        View view;
        TextView km;
        float scaleFactor;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        scaleFactor = metrics.density;
        System.out.println("scaleFactor : "+scaleFactor);

        float maxValue = 0;
        for(Properties p : walks) {
            if(toKMFloat(p) > maxValue)
                maxValue = toKMFloat(p);
        }

        int barWidth = (int) (129*scaleFactor);
        int barHeight;
        System.out.println("barWidth : " + barWidth);

        String color = GRAY;
        int count = 0;
        float normalWalk = 3.5f;
        for(Properties p : walks) {
            view = new View(this);
            km = new TextView(this);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.BOTTOM);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));


            LinearLayout.LayoutParams linearLayoutlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if(toKMFloat(p) > normalWalk)
                color = BLUE;
            else
                color = GRAY;
            view.setBackgroundColor(Color.parseColor(color));
            km.setTextColor(Color.parseColor(color));
            km.setText(toKMString(p));

            barHeight = (int) (toKMFloat(p)/maxValue * 100 * scaleFactor);
            if(barHeight == 0)
                barHeight = (int)scaleFactor;
            view.setLayoutParams(new LinearLayout.LayoutParams(barWidth, barHeight));
            km.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            km.setGravity(Gravity.CENTER_HORIZONTAL);

            linearLayout.addView(km, linearLayoutlp);
            linearLayout.addView(view);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            params.setMargins(25*(int)scaleFactor, 25*(int)scaleFactor, 0, 0);
            linearLayout.setLayoutParams(params);
            barChart.addView(linearLayout);

            count++;
        }
    }

    public void drawDate(List<Properties> walks) {
        TextView view1, view2;

        float scaleFactor;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        scaleFactor = metrics.density;

        int barWidth = (int) (129*scaleFactor);

        String[] dates = new String[2];
        String color1 = GRAY;
        String color2= GRAY;

        int count = 0;
        for(Properties p : walks) {
            view1 = new TextView(this);
            view2 = new TextView(this);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams linearLayoutlp = new LinearLayout.LayoutParams(
                    barWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            dates = getToday(p);

            if(count == walks.size()-1) {
                color1 = WHITE;
                color2 = BLUE;

                dates[0] = dates[0].substring(3,dates[0].length());
                view1.setBackgroundResource(R.drawable.date_oval);
            } else if(count==0) {
                color1 = GRAY;
                color2 = GRAY;

                dates[0] = dates[0].substring(1,dates[0].length());
            } else {
                color1 = GRAY;
                color2 = GRAY;

                dates[0] = dates[0].substring(3,dates[0].length());
            }
            view1.setTextColor(Color.parseColor(color1));
            view1.setText(dates[0]);
            view1.setLayoutParams(new LinearLayout.LayoutParams(barWidth, barWidth));
            view1.setGravity(Gravity.CENTER);

            view2.setTextColor(Color.parseColor(color2));
            view2.setText(dates[1]);
            view2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            view2.setGravity(Gravity.CENTER_HORIZONTAL);

            linearLayout.addView(view1);
            linearLayout.addView(view2, linearLayoutlp);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            params.setMargins(25*(int)scaleFactor, 25*(int)scaleFactor, 0, 0);
            linearLayout.setLayoutParams(params);
            barDate.addView(linearLayout);

            count++;
        }
    }

    /*
    * 날짜와 걸음수 변환 메소드
    * */
    private String toKMString(Properties p) {
        return (isWalkEmpty(getCurrentWalk(p)) ?
                "0"
                : Double.toString(Math.round(Integer.parseInt(getCurrentWalk(p)) * 0.011559 * 100)/100));
    }

    private float toKMFloat(Properties p) {
        return (!isWalkEmpty(getCurrentWalk(p)) ?
                Math.round(Integer.parseInt(getCurrentWalk(p)) * 0.011559 * 100)/100
                : 0.0f);
    }

    private String getCurrentWalk(Properties p) {
        return p.getProperty("currentwalk");
    }

    private String[] getToday(Properties p) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat f1 = new SimpleDateFormat("MM/dd");
        SimpleDateFormat f2 = new SimpleDateFormat("E");

        Date date;
        String[] dates = new String[2];
        try {
            date = f.parse(p.getProperty("today"));
            String d1 = f1.format(date);
            String d2 = f2.format(date);

            dates[0] = d1;
            dates[1] = d2;
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("날짜 변환 오류!!");
        }
        return dates;
    }

    private boolean isWalkEmpty(String walk) {
        return ((walk.equals("")) ? true  : false);
    }

    /*----------------- sesder --------------------------*/
    private void setSensor() {
        registerReceiver(broadcastReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.step"));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            walk = Integer.parseInt(intent.getStringExtra("walk"));
           // pcurrent1_km.setText(String.format("%s", ( Math.round(walk * 0.011559 * 100)/100)));
           // pcurrent1_step.setText(String.format("%s",walk));

        }
    };

}
