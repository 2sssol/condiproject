package condi.kr.ac.swu.condiproject.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.receiver.SensorReceiver;
import condi.kr.ac.swu.condiproject.service.AccSensor;

public class MainActivity extends BaseActivity {

    private ImageView imgCourseBackground;
    private TextView txtCourseWalkKM, txtWalkCount, txtCourseName1, txtCourseName2, txtCourseKM, btnCoursePicture;
    private ImageButton btnBeforeCourse, btnAfterCourse;
    private Button btnMyWalk, btnGroup;

    // 비디오 재생
    private VideoView videoCourseBackground;
    private ImageButton btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar("메인");

        initView();
        initVideo();

        setSensor();
    }

    private void initView() {
        videoCourseBackground = (VideoView) findViewById(R.id.videoCourseBackground);
        imgCourseBackground = (ImageView) findViewById(R.id.imgCourseBackground);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);

        videoCourseBackground.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.INVISIBLE);
        imgCourseBackground.setVisibility(View.VISIBLE);

        txtCourseWalkKM = (TextView) findViewById(R.id.txtCourseWalkKM);
        txtWalkCount = (TextView) findViewById(R.id.txtWalkCount);
        txtCourseName1 = (TextView) findViewById(R.id.txtCourseName1);
        txtCourseName2 = (TextView) findViewById(R.id.txtCourseName2);
        txtCourseKM = (TextView) findViewById(R.id.txtCourseKM);
        btnBeforeCourse = (ImageButton) findViewById(R.id.btnBeforeCourse);
        btnAfterCourse = (ImageButton) findViewById(R.id.btnAfterCourse);
        btnMyWalk = (Button) findViewById(R.id.btnMyWalk);
        btnGroup = (Button) findViewById(R.id.btnGroup);
        btnCoursePicture = (TextView) findViewById(R.id.btnCoursePicture);
    }

    private void initVideo() {
        videoCourseBackground.setVisibility(View.VISIBLE);
        //.gew
        videoCourseBackground.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.walkinforest_0606));
        //videoCourseBackground.setVideoURI(Uri.parse("http://sssol2.esy.es/condi/walkinforest0609.mp4"));
        videoCourseBackground.requestFocus();

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imgCourseBackground.setVisibility(View.INVISIBLE);
                if(!videoCourseBackground.isPlaying()) {
                    videoCourseBackground.start();
                    btnPlay.setImageResource(R.drawable.pause_empty);
                } else {
                    videoCourseBackground.pause();
                    btnPlay.setImageResource(R.drawable.play_empty);
                }
            }
        });
        videoCourseBackground.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoCourseBackground.pause();
                btnPlay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setSensor() {
        SensorReceiver receiver = new SensorReceiver();
        IntentFilter mainFilter = new IntentFilter("condi.kr.ac.swu.walkprojectdemo.step");
        registerReceiver(receiver, mainFilter);

        receiver.setWalk(txtWalkCount, "걸음");
    }
}
