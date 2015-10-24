package condi.kr.ac.swu.condiproject.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import condi.kr.ac.swu.condiproject.R;

public class AddPromiseActivity extends AppCompatActivity {

    private ImageButton addPromise;
    private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promise);
    }

    private void initView() {
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
    }

    private void setCalendarView() {
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView materialCalendarView, CalendarDay calendarDay, boolean b) {

            }
        });
    }

    protected void initActionBar(String title) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        ((TextView)findViewById(R.id.titleText)).setText(title);
        addPromise = (ImageButton) findViewById(R.id.sidemenu);
        addPromise.setImageResource(R.drawable.icon_promise_ok);
        addPromise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
