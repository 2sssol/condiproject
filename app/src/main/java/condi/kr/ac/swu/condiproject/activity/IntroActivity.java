package condi.kr.ac.swu.condiproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.Session;


public class IntroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLogined())
                    redirectLoadingActivity();
                else
                    redirectLoginActivity();
            }
        }, 2000);
    }

    private boolean isLogined() {
        if(Session.getPreferences(getApplicationContext(), "id").equals(""))
            return true;
        else
            return false;

    }

    private void redirectLoginActivity() {
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        finish();
    }

    private void redirectLoadingActivity() {
        startActivity(new Intent(IntroActivity.this, LoadingActivity.class));
        finish();
    }

}
