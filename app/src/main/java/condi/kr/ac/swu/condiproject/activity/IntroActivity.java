package condi.kr.ac.swu.condiproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import condi.kr.ac.swu.condiproject.R;


public class IntroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectLoginActivity();
            }
        }, 2000);
    }

    private void checkSession() {}

    private void redirectLoginActivity() {
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        finish();
    }

}
