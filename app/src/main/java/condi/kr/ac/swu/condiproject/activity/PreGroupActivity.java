package condi.kr.ac.swu.condiproject.activity;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.GlobalApplication;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;
import condi.kr.ac.swu.condiproject.view.CircularNetworkImageView;
import condi.kr.ac.swu.condiproject.view.adapter.InviteListAdapter;

public class PreGroupActivity extends RootActivity {

    private String senderId = "";
    private InviteListAdapter adapter;
    private ListView inviteList;

    // 프로필 관련 뷰
    private CircularNetworkImageView myProfile;
    private TextView myName, myPhone;

    private Button btnRoomExit, btnRoomStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_group);
        initActionBar("어울림방 만들기");
        initView();
        start();
    }

    private void initView() {
        // 뷰
        myProfile = (CircularNetworkImageView) findViewById(R.id.myProfile);
        myName = (TextView) findViewById(R.id.myName);
        myPhone = (TextView) findViewById(R.id.myPhone);

        btnRoomExit = (Button) findViewById(R.id.btnRoomExit);
        btnRoomStart = (Button) findViewById(R.id.btnRoomStart);

        initProfile();
        initEvent();
    }

    private void initEvent() {
        btnRoomStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertGroups();
            }
        });
    }

    private void initProfile() {
        setProfileURL(Session.PROFILE);
        myName.setText(Session.NICKNAME);
        myPhone.setText(Session.PHONE);
    }

    public void setProfileURL(final String profileImageURL) {
        Application app = GlobalApplication.getGlobalApplicationContext();
        if (app == null)
            throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");

        if (myProfile != null && profileImageURL != null) {
            if(profileImageURL.equals(""))
                myProfile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
            else
                myProfile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/"+profileImageURL, ((GlobalApplication) app).getImageLoader());
        } else
            myProfile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
    }

    private void start() {
        if(isSender())
            loadInviteList();
        else
            new Receiver().execute();
    }

    /*
    * true : sender
    * false : receiver
    * */
    private boolean isSender() {
        return getIntent().getBooleanExtra("mode", true);
    }

    private void loadInviteList() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String sender = "";
                if(isSender()) {
                    sender = Session.ID;
                    senderId = sender;
                }
                else
                    sender = senderId;

                String dml = "select i.id as id, i.sender as sender, i.receiver as receiver, m.nickname as receivername, m.profile as profile, m.phone as phone " +
                        "from invite i, member m " +
                        "where i.sender='"+sender+"' and m.id=i.receiver and m.id!='"+Session.ID+"'";
                return NetworkAction.sendDataToServer("inviteList.php",dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("success")) {
                    new AsyncTask() {

                        List<Properties> list;
                        @Override
                        protected Object doInBackground(Object[] params) {
                            try {
                                list = NetworkAction.parse("inviteList.xml", "invite");
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
                            final int count = list.size();
                            inviteList = (ListView) findViewById(R.id.inviteList);
                            adapter = new InviteListAdapter(PreGroupActivity.this.getApplicationContext(), list);
                            View footer = getLayoutInflater().inflate(R.layout.invited_list_footer, null, false);

                            if(count>=3) {
                                footer.setVisibility(View.INVISIBLE);
                            } else {
                                footer.setVisibility(View.VISIBLE);
                                footer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                            Intent i = new Intent(PreGroupActivity.this.getApplicationContext(), AddFriendActivity.class);
                                            i.putExtra("sender", senderId);
                                            i.putExtra("count", count);
                                            i.putExtra("isSender", isSender());
                                            startActivity(i);
                                            finish();

                                    }
                                });
                            }

                            inviteList.addFooterView(footer);
                            inviteList.setAdapter(adapter);
                        }
                    }.execute();
                }
            }
        }.execute();
    }

    private class Receiver extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String dml = "select sender from invite where receiver='"+Session.ID+"'";

            return NetworkAction.sendDataToServer("getSender.php", dml);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("error"))
                Toast.makeText(getApplicationContext(), "sender정보 받아오기 실패", Toast.LENGTH_LONG).show();
            else {
                senderId = s;
                loadInviteList();
            }
        }
    }

    private void insertGroups() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String dml = "insert into groups(host, region) values('"+senderId+"', '중구')";
                return NetworkAction.sendDataToServer(dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                if(o.equals("success")) {
                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            String dml = "update member set groups=(select id from groups where host='"+senderId+"') where id in (select receiver from invite where sender='"+senderId+"')";
                            System.out.println(dml);
                            return NetworkAction.sendDataToServer(dml);
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            if(o.equals("success")) {
                                new AsyncTask() {
                                    @Override
                                    protected String doInBackground(Object[] params) {
                                        Properties p = new Properties();
                                        p.setProperty("id", Session.ID);
                                        return NetworkAction.sendDataToServer("my.php", p);
                                    }

                                    @Override
                                    protected void onPostExecute(Object o) {
                                        super.onPostExecute(o);
                                        new AsyncTask() {
                                            List<Properties> my;
                                            @Override
                                            protected Object doInBackground(Object[] params) {
                                                try {
                                                    my = NetworkAction.parse("my.xml", "member");
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
                                                Session.removeAllPreferences(getApplicationContext());
                                                Session.savePreferences(getApplicationContext(), my.get(0));
                                                redirectSelectRegionActivity();
                                            }
                                        }.execute();
                                    }
                                }.execute();

                            }
                            else
                                Toast.makeText(getApplicationContext(), "업데이트 실패", Toast.LENGTH_SHORT).show();
                        }
                    }.execute();

                } else {
                    Toast.makeText(getApplicationContext(), "그룹 삽입 실패", Toast.LENGTH_SHORT).show();
                }

            }
        }.execute();
    }

    private void redirectSelectRegionActivity() {
        startActivity(new Intent(getApplicationContext(), SelectRegionActivity.class));
        finish();
    }
}
