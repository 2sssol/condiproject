package condi.kr.ac.swu.condiproject.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;
import condi.kr.ac.swu.condiproject.view.adapter.PromiseListAdapter;

public class PromiseActivity extends BaseActivity {

    private ListView promise_list;
    private ImageView btn_add_promise;
    private TextView promise_tutorial;

    private List<Properties> promiseList;
    private PromiseListAdapter adapter;

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

        btn_add_promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPromiseActivity.class));
                finish();
            }
        });
        setPromiseList();
    }

    private void setPromiseList() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from promise where groups="+ Session.GROUPS;
                return NetworkAction.sendDataToServer("promise.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("success")) {
                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            try {
                                promiseList = NetworkAction.parse("promise.xml", "promise");
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

                            if(promiseList.size() == 0) {
                                promise_tutorial.setVisibility(View.INVISIBLE);
                            } else {
                                /*
                                * set listView
                                * */
                                promise_list = (ListView) findViewById(R.id.promise_list);
                                adapter = new PromiseListAdapter(getApplicationContext(), promiseList);
                                promise_list.setAdapter(adapter);
                            }
                        }
                    }.execute();
                }
            }
        }.execute();
    }
}
