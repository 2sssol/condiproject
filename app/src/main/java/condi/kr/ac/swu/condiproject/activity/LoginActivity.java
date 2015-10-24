package condi.kr.ac.swu.condiproject.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;


public class LoginActivity extends RootActivity {

    private EditText editLoginPhone;
    private EditText editLoginPassword;
    private Button btnLogin, btnPassword, goToJoin;

    private String phone;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initActionBar("어울림 로그인");
        initView();
    }


    private void initView() {
        editLoginPhone = (EditText) findViewById(R.id.editLoginPhone);
        editLoginPassword = (EditText) findViewById(R.id.editLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLoign);
        btnPassword = (Button) findViewById(R.id.btnPassword);
        goToJoin = (Button) findViewById(R.id.goToJoin);

        btnLogin.setOnClickListener(new LoginEventListener());
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectPasswordActivity();
            }
        });
        goToJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectJoinActivity();
            }
        });
    }

    private void redirectJoinActivity() {
        startActivity(new Intent(getApplicationContext(), JoinActivity.class));
        finish();
    }

    private void redirectPasswordActivity() {
        Intent intent = new Intent(LoginActivity.this, PasswordActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectCheckInviteActivity() {
        Intent intent = new Intent(LoginActivity.this, CheckInviteActivity.class);
        startActivity(intent);
        finish();
    }

    private class LoginEventListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            phone = editLoginPhone.getText().toString();
            password = editLoginPassword.getText().toString();

            if (checkValue(phone, password)) {
                new Login().execute();
            }
        }
    }

    private boolean checkValue(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            editLoginPhone.setError("전화번호를 입력해주세요.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editLoginPassword.setError("비밀번호를 입력해주세요.");
            return false;
        }

        return true;
    }

    private class Login extends AsyncTask<Void, Void, String> {

        Properties prop;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            prop = new Properties();
            prop.setProperty("phone", phone);
            prop.setProperty("password", password);
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkAction.sendDataToServer("login.php", prop);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("success")) {
                new getMyInfo().execute();
            } else
                Toast.makeText(getApplicationContext(), "로그인을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private class getMyInfo extends AsyncTask<Void, Void, Void> {

        List<Properties> props;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                props = NetworkAction.parse("my.xml", "member");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String key = "";
            String value = "";

            System.out.println("============고객 정보============");
            for (Properties p : props) {
                Enumeration keys = p.propertyNames();
                while (keys.hasMoreElements()) {
                    key = keys.nextElement().toString();
                    value = p.getProperty(key);

                    System.out.println(key + " : " + value);
                }
            }
            System.out.println("================================");
            Session.savePreferences(getApplicationContext(), props.get(0));
            redirectCheckInviteActivity();
        }
    }
}
