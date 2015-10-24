package condi.kr.ac.swu.condiproject.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condiproject.R;

/**
 * Created by 8304 on 2015-10-23.
 */
public class PushListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> data;


    public PushListAdapter(Context context, List<Properties> data) {
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
            convertView = inflater.inflate(R.layout.push_list, null);

        // 리소스와 연결
        TextView txtPushUserName = (TextView) convertView.findViewById(R.id.txtPushUserName);     // 이름
        TextView txtPushCourseName = (TextView) convertView.findViewById(R.id.txtPushCourseName);    // 선택코스
        TextView txtPushCourseKm = (TextView) convertView.findViewById(R.id.txtPushCourseKm);   // km
        Button btnPush = (Button) convertView.findViewById(R.id.btnPush);

        //텍스트 세팅
        txtPushUserName.setText(data.get(position).get("mname"));
        txtPushCourseKm.setText(data.get(position).get("ckm"));

        setCourseName(txtPushCourseName, data.get(position).get("cname"));
        //setButton(btnPush, data.get(position).get("cname"), data.get(position).get("mid"));


        return convertView;
    }

    public void setCourseName(final TextView courseName, final String course) {
        if(course.equals(""))
            courseName.setText("선택 중입니다.");
        else
            courseName.setText(course);
    }


}
