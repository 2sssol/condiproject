package condi.kr.ac.swu.condiproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

/**
 * Created by 8304 on 2015-10-23.
 */
public class SensorReceiver extends BroadcastReceiver {

    public String serviceData ;

    @Override
    public void onReceive(Context context, Intent intent) {
        serviceData = intent.getStringExtra("walk");
    }

    public void setWalk(TextView textView, String msg) {
        textView.setText(String.format("%s %s",serviceData,msg));
    }


}
