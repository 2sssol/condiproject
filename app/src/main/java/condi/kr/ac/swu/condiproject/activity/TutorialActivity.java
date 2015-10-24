package condi.kr.ac.swu.condiproject.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.Course;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;
import condi.kr.ac.swu.condiproject.receiver.SensorReceiver;
import condi.kr.ac.swu.condiproject.service.AccSensor;
import condi.kr.ac.swu.condiproject.view.CurvTextView;
import condi.kr.ac.swu.condiproject.view.adapter.PushListAdapter;

public class TutorialActivity extends RootActivity {

    private Course[] courses = new Course[6];

    private ImageView imgTotalGoalShot;                            // 최종 선택된 지도의 모습
    private TextView txtTutorialCourseSum, txtTutorialDaysSum, txtTutorialName1, txtTutorialName2, txtTutorialName3, txtTutorialName4;    // 경로 설명, 날짜 설명
    private Button btnMain;                                          // 메인으로 가기

    private List<Properties> list;      // 모든 코스들
    private List<Properties> members;   // 멤버들

    private ArrayList<String> selected = new ArrayList<String>();   // 선택된 코스들 이름
    private ArrayList<String> selectedMembers = new ArrayList<String>();    //선택한 멤버들 이름

    private ArrayList<ImageButton> imageButtons;
    private ImageButton
            btnTutorialCourseName1, btnTutorialCourseName2, btnTutorialCourseName3,
            btnTutorialCourseName4, btnTutorialCourseName5, btnTutorialCourseName6;

    private CurvTextView curvTextView;

    private ArrayList<TextView> textViews;
    private TextView
            txtTutorialSpeechBox1, txtTutorialSpeechBox2, txtTutorialSpeechBox3,
            txtTutorialSpeechBox4, txtTutorialSpeechBox5, txtTutorialSpeechBox6;

    private int days = 0;
    private double km = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        initActionBar("코스선택");
        initView();
        setCourses();
    }

    private void initView() {
        curvTextView = (CurvTextView) findViewById(R.id.txtTutorialCourseArray);

        // 말풍선
        txtTutorialSpeechBox1 = (TextView) findViewById(R.id.txtTutorialSpeechBox1);
        txtTutorialSpeechBox2 = (TextView) findViewById(R.id.txtTutorialSpeechBox2);
        txtTutorialSpeechBox3 = (TextView) findViewById(R.id.txtTutorialSpeechBox3);
        txtTutorialSpeechBox4 = (TextView) findViewById(R.id.txtTutorialSpeechBox4);
        txtTutorialSpeechBox5 = (TextView) findViewById(R.id.txtTutorialSpeechBox5);
        txtTutorialSpeechBox6 = (TextView) findViewById(R.id.txtTutorialSpeechBox6);

        txtTutorialSpeechBox1.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox2.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox3.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox4.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox5.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox6.setVisibility(View.INVISIBLE);

        textViews = new ArrayList<TextView>();
        textViews.add(txtTutorialSpeechBox1);
        textViews.add(txtTutorialSpeechBox2);
        textViews.add(txtTutorialSpeechBox3);
        textViews.add(txtTutorialSpeechBox4);
        textViews.add(txtTutorialSpeechBox5);
        textViews.add(txtTutorialSpeechBox6);

        // 이미지 버튼
        btnTutorialCourseName1 = (ImageButton) findViewById(R.id.btnTutorialCourseName1);
        btnTutorialCourseName2 = (ImageButton) findViewById(R.id.btnTutorialCourseName2);
        btnTutorialCourseName3 = (ImageButton) findViewById(R.id.btnTutorialCourseName3);
        btnTutorialCourseName4 = (ImageButton) findViewById(R.id.btnTutorialCourseName4);
        btnTutorialCourseName5 = (ImageButton) findViewById(R.id.btnTutorialCourseName5);
        btnTutorialCourseName6 = (ImageButton) findViewById(R.id.btnTutorialCourseName6);

        imageButtons = new ArrayList<ImageButton>();
        imageButtons.add(btnTutorialCourseName1);
        imageButtons.add(btnTutorialCourseName2);
        imageButtons.add(btnTutorialCourseName3);
        imageButtons.add(btnTutorialCourseName4);
        imageButtons.add(btnTutorialCourseName5);
        imageButtons.add(btnTutorialCourseName6);

        btnMain = (Button) findViewById(R.id.btnMain);
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectMainActivity();
            }
        });

        // 튜토리얼 세팅
        txtTutorialCourseSum = (TextView) findViewById(R.id.txtTutorialCourseSum);
        txtTutorialDaysSum = (TextView) findViewById(R.id.txtTutorialDaysSum);
        txtTutorialName1 = (TextView) findViewById(R.id.txtTutorialName1);
        txtTutorialName2 = (TextView) findViewById(R.id.txtTutorialName2);
        txtTutorialName3 = (TextView) findViewById(R.id.txtTutorialName3);
        txtTutorialName4 = (TextView) findViewById(R.id.txtTutorialName4);

    }

    public void setSelectedCourse(Properties prop) {

        int id = 0;
        for(int i=0; i<courses.length; i++) {
            if(prop.getProperty("cid").equals(courses[i].id))
                id = i;
        }

        imageButtons.get(id).setImageResource(R.drawable.course_button_red);
        imageButtons.get(id).setClickable(false);

        textViews.get(id).setVisibility(View.VISIBLE);
        textViews.get(id).setBackgroundResource(R.drawable.speechbubble_pink);
        textViews.get(id).setText(prop.getProperty("mname") + "님 선택");
    }

    /*
    * 코스 로드
    * */
    private void setCourses() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String dml = "select * " +
                        "from course " +
                        "where local=(" +
                        "select region " +
                        "from groups " +
                        "where id="+ Session.GROUPS+"" +
                        ")";
                return NetworkAction.sendDataToServer("course.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                /*
                * 모든 코스 로드
                * */
                if (!o.equals("error")) {

                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            try {
                                list = NetworkAction.parse("course.xml", "course");
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

                            /*
                            * 선택된 코스인지 확인하고 세팅
                            * */
                            new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    String dml = "select * from member where groups=" + Session.GROUPS;
                                    return NetworkAction.sendDataToServer("member.php", dml);
                                }

                                @Override
                                protected void onPostExecute(Object o) {
                                    super.onPostExecute(o);
                                    new AsyncTask() {


                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            /*
                                            * 코스 이름 세팅
                                            * */
                                            String[] names = new String[6];

                                            int i = 0;
                                            for (Properties p : list) {
                                                courses[i] = new Course(p);
                                                names[i] = courses[i].name;
                                                i++;
                                            }
                                            curvTextView.courseName(names);
                                            curvTextView.invalidate();

                                        }

                                        @Override
                                        protected Object doInBackground(Object[] params) {
                                            try {
                                                members = NetworkAction.parse("member.xml", "member");
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

                                            Properties mc;
                                            String mname = "";
                                            String cname = "";
                                            String ckm = "";
                                            String cid = "";

                                            int cnt = 0;

                                            /*
                                            * 선택된 코스 얻어오기 성공
                                            * 그 코스를 선택한 사람 얻어오기 성공
                                            * */
                                            for (Properties p : members) {
                                                mc = new Properties();

                                                for (Properties ps : list) {
                                                    if (p.getProperty("course").equals(ps.getProperty("id"))) {
                                                        selected.add(p.getProperty("course"));
                                                        selectedMembers.add(p.getProperty("nickname"));

                                                        km += Double.parseDouble(ps.getProperty("km"));
                                                        days += (int) Math.ceil(Double.parseDouble(ps.getProperty("km"))/3.5);

                                                        cname = ps.getProperty("name");
                                                        ckm = ps.getProperty("km");
                                                        cid = ps.getProperty("id");

                                                        switch (cnt) {
                                                            case 0 :
                                                                txtTutorialName1.setText(ps.getProperty("name"));
                                                                break;
                                                            case 1 :
                                                                txtTutorialName2.setText(ps.getProperty("name"));
                                                                break;
                                                            case 2 :
                                                                txtTutorialName3.setText(ps.getProperty("name"));
                                                                break;
                                                            case 3 :
                                                                txtTutorialName4.setText(ps.getProperty("name"));
                                                                break;
                                                        }
                                                        cnt++;
                                                    }
                                                }

                                                mname = p.getProperty("nickname");

                                                mc.setProperty("mname", mname);
                                                mc.setProperty("cname", cname);
                                                mc.setProperty("ckm", ckm);
                                                mc.setProperty("cid", cid);

                                                printErrorMsg(String.format("mname : %s, cname : %s, ckm : %s", mname, cname, ckm));

                                                setSelectedCourse(mc);
                                            }

                                            /*
                                            * 말풍선이랑 뷰에 반영
                                            * */
                                            curvTextView.selectedCourse(selected);

                                            txtTutorialDaysSum.setText(Integer.toString(days));
                                            txtTutorialCourseSum.setText(String.format("%.1f KM" , km));
                                        }
                                    }.execute();
                                }
                            }.execute();


                        }
                    }.execute();

                } else {
                    toastErrorMsg("코스 정보를 로드하지 못했습니다.");
                }
            }
        }.execute();
    }

    private void redirectMainActivity() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String dml = "update groups set goalkm="+km+", goaldays="+days+" where id="+Session.GROUPS;
                return NetworkAction.sendDataToServer(dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("success")) {
                    toastErrorMsg("어울림이 시작됩니다.");
                    startSensor();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    toastErrorMsg("main으로 넘어갈 수 없습니다.");
                }
            }
        }.execute();

    }


    private void startSensor() {
        SensorReceiver sensorReceiver = new SensorReceiver();
        IntentFilter mainFilter = new IntentFilter("condi.kr.ac.swu.condiproject.step");
        registerReceiver(sensorReceiver, mainFilter);
        startService(new Intent(getApplicationContext(), AccSensor.class));
    }
}
