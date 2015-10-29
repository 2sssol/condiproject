package condi.kr.ac.swu.condiproject.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.Course;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;
import condi.kr.ac.swu.condiproject.view.CurvTextView;

public class SelectCourseActivity extends RootActivity {

    private List<Properties> list;
    private List<Properties> members;
    private Course[] courses = new Course[6];

    private ArrayList<String> selected = new ArrayList<String>();
    private ArrayList<String> selectedMembers = new ArrayList<String>();

    private CurvTextView txtCourseArray;
    private Button btnSelectFinal;

    private ImageButton btnCourseName1, btnCourseName2, btnCourseName3, btnCourseName4, btnCourseName5, btnCourseName6;
    private TextView
            txtSpeechBox1, txtSpeechBox2, txtSpeechBox3, txtSpeechBox4, txtSpeechBox5, txtSpeechBox6,
            txtCourseInfoName, txtCourseInfo1, txtCourseInfo2, txtChoiceCourseKM, txtUserName;

    private int clickedPosition = -1;

    private ArrayList<ImageButton> imageButtons;
    private ArrayList<TextView> textViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);
        initActionBar("코스 선택");
        initView();
        setCourses();
    }

    private void initView() {
        txtCourseArray = (CurvTextView) findViewById(R.id.txtCourseArray);
        btnSelectFinal = (Button) findViewById(R.id.btnSelectFinal);

        txtCourseInfoName = (TextView) findViewById(R.id.txtCourseInfoName);
        txtCourseInfo1 = (TextView) findViewById(R.id.txtCourseInfo1);
        txtCourseInfo2 = (TextView) findViewById(R.id.txtCourseInfo2);
        txtChoiceCourseKM = (TextView) findViewById(R.id.txtChoiceCourseKM);
        txtUserName = (TextView) findViewById(R.id.txtUserName);

        txtSpeechBox1 = (TextView) findViewById(R.id.txtSpeechBox1);
        txtSpeechBox2 = (TextView) findViewById(R.id.txtSpeechBox2);
        txtSpeechBox3 = (TextView) findViewById(R.id.txtSpeechBox3);
        txtSpeechBox4 = (TextView) findViewById(R.id.txtSpeechBox4);
        txtSpeechBox5 = (TextView) findViewById(R.id.txtSpeechBox5);
        txtSpeechBox6 = (TextView) findViewById(R.id.txtSpeechBox6);

        txtSpeechBox1.setVisibility(View.INVISIBLE);
        txtSpeechBox2.setVisibility(View.INVISIBLE);
        txtSpeechBox3.setVisibility(View.INVISIBLE);
        txtSpeechBox4.setVisibility(View.INVISIBLE);
        txtSpeechBox5.setVisibility(View.INVISIBLE);
        txtSpeechBox6.setVisibility(View.INVISIBLE);

        btnCourseName1 = (ImageButton) findViewById(R.id.btnCourseName1);
        btnCourseName2 = (ImageButton) findViewById(R.id.btnCourseName2);
        btnCourseName3 = (ImageButton) findViewById(R.id.btnCourseName3);
        btnCourseName4 = (ImageButton) findViewById(R.id.btnCourseName4);
        btnCourseName5 = (ImageButton) findViewById(R.id.btnCourseName5);
        btnCourseName6 = (ImageButton) findViewById(R.id.btnCourseName6);

        imageButtons = new ArrayList<ImageButton>();
        imageButtons.add(btnCourseName1);
        imageButtons.add(btnCourseName2);
        imageButtons.add(btnCourseName3);
        imageButtons.add(btnCourseName4);
        imageButtons.add(btnCourseName5);
        imageButtons.add(btnCourseName6);

        textViews = new ArrayList<TextView>();
        textViews.add(txtSpeechBox1);
        textViews.add(txtSpeechBox2);
        textViews.add(txtSpeechBox3);
        textViews.add(txtSpeechBox4);
        textViews.add(txtSpeechBox5);
        textViews.add(txtSpeechBox6);

        btnSelectFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedPosition!=-1)
                    updateCourse();
                else
                    toastErrorMsg("코스를 선택해주세요.");
            }
        });
    }

    private void updateCourse(){
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] params) {
                String dml = "update member set course='"+courses[clickedPosition].id+"' where id='"+Session.ID+"'";
                Properties p = new Properties();
                p.setProperty("dml", dml);
                p.setProperty("sender", Session.ID);
                p.setProperty("sendername", Session.NICKNAME);
                p.setProperty("type", "7");
                return NetworkAction.sendDataToServer("gcmToAll.php",p);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                new MyPHP().execute();
            }
        }.execute();

    }

    public void SelectCourseButtonClick(View v) {

        switch (v.getId()) {
            case R.id.btnCourseName1 :
                clickedPosition = 0;
                break;
            case R.id.btnCourseName2 :
                clickedPosition = 1;
                break;
            case R.id.btnCourseName3 :
                clickedPosition = 2;
                break;
            case R.id.btnCourseName4 :
                clickedPosition = 3;
                break;
            case R.id.btnCourseName5 :
                clickedPosition = 4;
                break;
            case R.id.btnCourseName6 :
                clickedPosition = 5;
                break;
        }

        clickedCourse(clickedPosition);
    }

    public void clickedCourse(int position) {

        for (int i=0; i<imageButtons.size(); i++) {
            if(i==position) {
                imageButtons.get(i).setImageResource(R.drawable.course_button_mint);
                textViews.get(i).setVisibility(View.VISIBLE);
                textViews.get(i).setText(Session.NICKNAME+"님");

                txtCourseInfoName.setText(courses[i].name);
                txtChoiceCourseKM.setText(courses[i].km+"KM");

                String info1 =  courses[i].info.substring(0, 19);
                String info2 = courses[i].info.substring(19);

                txtCourseInfo1.setText(info1);
                txtCourseInfo2.setText(info2);

                txtCourseArray.clickedCourse(i);
                txtCourseArray.invalidate();
            }
            else {
                imageButtons.get(i).setImageResource(R.drawable.course_button_grey);
                textViews.get(i).setVisibility(View.INVISIBLE);
                int j=0;
                for(Properties p: members) {
                    j = Integer.parseInt(p.getProperty("course"));
                    if(i==j) {
                        imageButtons.get(i).setImageResource(R.drawable.course_button_red);
                        textViews.get(i).setVisibility(View.VISIBLE);
                        textViews.get(i).setBackgroundResource(R.drawable.speechbubble_pink);
                        textViews.get(i).setText(p.getProperty("nickname")+"님");
                    }
                }
            }
        }
    }

    private void setCourses() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String dml = "select * from course where local=0";
                return NetworkAction.sendDataToServer("course.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                /*
                * 모든 코스 로드
                * */
                if(!o.equals("error")) {

                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            try {
                                list = NetworkAction.parse("course.xml","course");
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
                                    String dml = "select * from member where groups="+ Session.GROUPS;
                                    return NetworkAction.sendDataToServer("member.php",dml);
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

                                            int i=0;
                                            for(Properties p : list) {
                                                courses[i] = new Course(p);
                                                names[i] = courses[i].name;
                                                i++;
                                            }


                                            txtCourseArray.courseName(names);
                                            txtCourseArray.invalidate();
                                        }

                                        @Override
                                        protected Object doInBackground(Object[] params) {
                                            try {
                                                members=NetworkAction.parse("member.xml","member");
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
                                            * 선택된 코스 얻어오기 성공
                                            * 그 코스를 선택한 사람 얻어오기 성공
                                            * */
                                            for(Properties p : members) {
                                                for(Properties ps : list) {
                                                    if(p.getProperty("course").equals(ps.getProperty("id"))) {
                                                        selected.add(p.getProperty("course"));
                                                        selectedMembers.add(p.getProperty("nickname"));
                                                    }
                                                }
                                            }

                                            /*
                                            * 말풍선이랑 뷰에 반영
                                            * */
                                            txtCourseArray.selectedCourse(selected);
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

    private void redirectSelectFinalActivity() {
        Intent intent = new Intent(getApplicationContext(), SelectFinalActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(selectReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.course"));
    }

    private BroadcastReceiver selectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setCourses();
        }
    };


    /*
    * 사용자 정보 로드
    * */
    private class MyPHP extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Properties prop = new Properties();
            prop.setProperty("id", Session.ID);
            return NetworkAction.sendDataToServer("my.php", prop);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new getMyInfo().execute();
        }
    }

    private class getMyInfo extends AsyncTask<Void, Void, Void> {

        List<Properties> props;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                props = NetworkAction.parse("my.xml", "member");
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
            Session.removeAllPreferences(getApplicationContext());
            Session.savePreferences(getApplicationContext(), props.get(0));
            redirectSelectFinalActivity();
        }
    }

}
