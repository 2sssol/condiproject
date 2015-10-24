package condi.kr.ac.swu.condiproject.view.adapter;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.GlobalApplication;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.view.CircularNetworkImageView;
import condi.kr.ac.swu.condiproject.view.CustomCircularRingView;


/**
 * Created by 8304 on 2015-10-24.
 */
public class GroupListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> data;
    private Handler mHandler = new Handler();

    int step = 0;
    float km = 0;

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

        setCurrent(group_current_step, group_current_km, data.get(position).get("mid"));

        //이미지세팅
        setProfileURL(group_picture, data.get(position).get("mprofile"));

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

    public void setCock(String receiver) {

    }

    private void setCurrent(final TextView step_tv, final TextView km_tv, final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String dml = "select sum(currentwalk) as step from walk where user='"+id+"'";
                String result = NetworkAction.sendDataToServer("step.php", dml);
                if(result.equals(""))
                    step = 0;
                else
                    step = Integer.parseInt(result);

                km = Math.round(step * 0.011559 * 100)/100;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        step_tv.setText(Integer.toString(step));
                        km_tv.setText(Float.toString(km));
                    }
                });

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
