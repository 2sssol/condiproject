package condi.kr.ac.swu.condiproject.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import condi.kr.ac.swu.condiproject.R;

public class PromiseActivity extends BaseActivity {

    private ListView promise_list;
    private ImageView btn_add_promise;
    private TextView promise_tutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise);
        initActionBar("약속 잡기");
        initView();
    }

    private void initView() {
        btn_add_promise = (ImageView) findViewById(R.id.btn_add_promise);
        promise_tutorial = (TextView) findViewById(R.id.promise_tutorial);
    }

    private void setPromiseList() {

    }
}
