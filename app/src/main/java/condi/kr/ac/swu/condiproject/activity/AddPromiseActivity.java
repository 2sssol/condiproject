package condi.kr.ac.swu.condiproject.activity;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import condi.kr.ac.swu.condiproject.R;

public class AddPromiseActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private final DateFormat FORMATTER = new SimpleDateFormat("yyyy.MM.dd (E)");

    private ImageButton addPromise;
    private MaterialCalendarView calendarView;
    private TextView newSchDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promise);
        initView();
    }

    private void initView() {
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        newSchDate = (TextView) findViewById(R.id.new_sch_date);

        calendarView.setLeftArrowMask(getResources().getDrawable(R.drawable.icon_date_left));
        calendarView.setRightArrowMask(getResources().getDrawable(R.drawable.icon_date_right));
        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);
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

    @Override
    public void onDateSelected(MaterialCalendarView materialCalendarView, CalendarDay calendarDay, boolean b) {
        newSchDate.setText(getSelectedDatesString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        Toast.makeText(getApplicationContext(), FORMATTER.format(calendarDay.getDate()), Toast.LENGTH_SHORT).show();
    }

    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        if (date == null) {
            return FORMATTER.format(new Date());
        }
        return FORMATTER.format(date.getDate());
    }
}
