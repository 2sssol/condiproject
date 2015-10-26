package condi.kr.ac.swu.condiproject.view.adapter;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.activity.GroupActivity;
import condi.kr.ac.swu.condiproject.data.GlobalApplication;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;
import condi.kr.ac.swu.condiproject.view.CircularNetworkImageView;
import condi.kr.ac.swu.condiproject.view.CustomCircularRingView;


/**
 * Created by 8304 on 2015-10-24.
 */
public class GroupListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> data;
    private boolean isDialogShow = false;
    private Handler mHandler;
    private String result;

    public GroupListAdapter(Context context, List<Properties> data) {
        this.context = context;

        ArrayList<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        String key = "";

        for(Properties p : data) {
            map = new HashMap<String, String>();
            Enumeration keys = p.propertyNames();
            while(keys.hasMoreElements()) {
                key = keys.nextElement().toString();
                map.put(key, p.getProperty(key));
            }
            maps.add(map);
        }

        this.data = maps;
        mHandler = new Handler();
    }

    /*
    * 오버라이드
    * */
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
            convertView = inflater.inflate(R.layout.group_list, null);

        // 리소스와 연결
        CircularNetworkImageView group_picture = (CircularNetworkImageView) convertView.findViewById(R.id.group_picture);
        TextView group_name = (TextView) convertView.findViewById(R.id.group_name);
        TextView group_current_km = (TextView) convertView.findViewById(R.id.group_current_km);
        TextView group_current_step = (TextView) convertView.findViewById(R.id.group_current_step);
        TextView group_course = (TextView) convertView.findViewById(R.id.group_course);
        TextView group_km = (TextView) convertView.findViewById(R.id.group_km);
        ImageView groups_cock = (ImageView) convertView.findViewById(R.id.groups_cock);

        //텍스트 세팅
        group_name.setText(data.get(position).get("mname"));
        group_course.setText(data.get(position).get("cname"));
        group_km.setText(data.get(position).get("ckm"));

        //이미지세팅
        setProfileURL(group_picture, data.get(position).get("mprofile"));

        // button
        setButton(groups_cock, data.get(position).get("mid"), data.get(position).get("mname"));

        setCurrent(group_current_step, group_current_km, data.get(position).get("mid"), position);

        return convertView;
    }

    public void setProfileURL(final CircularNetworkImageView profile, final String profileImageURL) {

        Application app = GlobalApplication.getGlobalApplicationContext();
        if (app == null)
            throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");

        if (profile != null && profileImageURL != null) {
            if(profileImageURL.equals(""))
                profile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
            else
                profile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/"+profileImageURL, ((GlobalApplication) app).getImageLoader());
        } else  {
            profile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
        }
    }

    private void setCurrent(final TextView step, final TextView km, final String id, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dml = "select count(currentwalk) as count from walk where user='"+id+"'";
                String php = "";
                while (true) {


                    switch (position) {
                        case 0:
                            php = "memberwalk0.php";
                            break;
                        case 1:
                            php = "memberwalk1.php";
                            break;
                        case 2 :
                            php = "memberwalk2.php";
                            break;
                    }

                    result = NetworkAction.sendDataToServer(php, dml);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            step.setText(result);
                            km.setText(Float.toString((float)Math.round(Integer.parseInt(result) * 0.011559 * 100)/100));
                        }
                    });

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    private void setButton(ImageView button, final String receiver, final String name) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        Properties p = new Properties();
                        p.setProperty("sender", Session.ID);
                        p.setProperty("receiver", receiver);
                        p.setProperty("sendername", Session.NICKNAME);
                        p.setProperty("type", "콕");
                        p.setProperty("content", name+"님으로 부터 '콕' 찌르기가 왔습니다!");
                        return NetworkAction.sendDataToServer("gcm.php", p);
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        Toast.makeText(context, name + "님을 '콕' 찔렀습니다.", Toast.LENGTH_SHORT).show();
                    }
                }.execute();
            }
        });
    }
}
