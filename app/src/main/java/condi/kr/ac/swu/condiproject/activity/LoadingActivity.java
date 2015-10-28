package condi.kr.ac.swu.condiproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;


/*
* 분기  :
*   그룹 ?
*       o :
*       코스 ?
*           o :
*               걸음?
*                   o : main
*                   x : 선택중
*          x : 지역구 선택
*       x :
*          초대 체크
*
* */
public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        branchPage();
    }

    private void branchPage() {
        if(hasGroup()) {
            if(hasCourse()) {
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        String dml = "select * from walk where user='"+Session.ID+"' and groups="+Session.GROUPS;
                        return NetworkAction.sendDataToServer("checkWalk.php", dml);
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        if(o.equals("ok"))
                            redirectMainActivity();
                        else
                            redirectSelectCourseActivity();
                    }
                }.execute();
            } else {
                redirectSelectRegionActivity();
            }
        } else {
            redirectCheckInviteActivity();
        }
    }


    private boolean hasGroup() {
        return (Session.getPreferences(getApplicationContext(), "groups").equals("") ? false : true);
    }

    private boolean hasCourse() {
        return (Session.getPreferences(getApplicationContext(), "course").equals("") ? false : true);
    }

    /*
    * redirect
    * */
    private void redirectCheckInviteActivity() {
        Intent intent = new Intent(getApplicationContext(), CheckInviteActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectSelectRegionActivity() {
        startActivity(new Intent(getApplicationContext(), SelectRegionActivity.class));
        finish();
    }

    private void redirectSelectCourseActivity() {
        startActivity(new Intent(getApplicationContext(), SelectCourseActivity.class));
        finish();
    }

    private void redirectMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
