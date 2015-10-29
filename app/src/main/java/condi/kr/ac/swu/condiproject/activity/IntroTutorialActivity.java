package condi.kr.ac.swu.condiproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import condi.kr.ac.swu.condiproject.R;

public class IntroTutorialActivity extends Activity {

    private Button btnGoLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_tutorial);

        btnGoLoading = (Button) findViewById(R.id.btnGoLoading);
        btnGoLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
                finish();
            }
        });
    }

}
