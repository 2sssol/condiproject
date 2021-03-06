package condi.kr.ac.swu.condiproject.view.adapter;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.GlobalApplication;
import condi.kr.ac.swu.condiproject.view.CircularNetworkImageView;


/**
 * Created by Administrator on 2015-10-01.
 */
public class MyMsgListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> data;

    public MyMsgListAdapter(Context context, List<Properties> data) {
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
            convertView = inflater.inflate(R.layout.msg_list, null);

        // 리소스와 연결
        TextView my_msgText = (TextView) convertView.findViewById(R.id.my_msgText);
        TextView my_msgDate = (TextView) convertView.findViewById(R.id.my_msgDate);
        TextView my_msg_userName = (TextView) convertView.findViewById(R.id.my_msg_userName);
        CircularNetworkImageView itemProfile = (CircularNetworkImageView) convertView.findViewById(R.id.my_msgProfile);

        //텍스트 세팅
        my_msg_userName.setText(data.get(position).get("sendername"));
        my_msgText.setText(data.get(position).get("sendername")+"님이 "+data.get(position).get("type")+"하셨어요.");
        my_msgDate.setText(data.get(position).get("msgdate"));

        //이미지세팅
        setProfileURL(itemProfile, data.get(position).get("profile"));

        return convertView;
    }

    public void setProfileURL(final CircularNetworkImageView profile, String profileImageURL) {
        if (profile != null && profileImageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            if(profileImageURL.equals(" "))
                profileImageURL = "http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png";
            else
                profileImageURL = "http://condi.swu.ac.kr:80/condi2/profile/"+profileImageURL;

            profile.setImageUrl(profileImageURL, ((GlobalApplication) app).getImageLoader());
            profile.invalidate();
        }
    }
}
