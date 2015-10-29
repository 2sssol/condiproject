package condi.kr.ac.swu.condiproject.gcm;

/**
 * Created by 8304 on 2015-10-26.
 */
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.gcm.GcmListenerService;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.activity.IntroActivity;
import condi.kr.ac.swu.condiproject.activity.PreGroupActivity;
import condi.kr.ac.swu.condiproject.activity.SelectRegionActivity;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private boolean isDialogShow = false;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String type = data.getString("type");
        String sender = data.getString("sender");
        String sendername = data.getString("sendername");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Type: " + type);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

    /*
    type
        3 : 초대에 수락
        4 : 초대에 거절
        5 : 초대함
        6 : 그룹 시작
        7 : 코스 선택
        8 : 걸음 시작
        9 : 목표 달성
    */

        int t =  Integer.parseInt(type);
        Intent intentResponse;

        switch (t) {
            case 0 :
                break;
            case 1 :  // push
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.start.walk");
                sendBroadcast(intentResponse);
                sendNotification(message);
                break;
            case 2 :    // 콕 찌르기
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.cock");
                sendBroadcast(intentResponse);
                sendNotification(message);
                showRoomDialog(message, sendername);
                break;
            case 3 :    // 초대 수락
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.invite");
                sendBroadcast(intentResponse);
                break;
            case 4 :    // 초대 거절
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.invite");
                sendBroadcast(intentResponse);
                break;
            case 5 :    // 초대 함
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.invite");
                sendBroadcast(intentResponse);
                break;
            case 6 :    // 그룹 시작
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.start.groups");
                sendBroadcast(intentResponse);
                sendNotification(message);
                break;
            case 7 :    // 코스 선택함
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.course");
                sendBroadcast(intentResponse);
                break;
            case 8 :    // 걸음 시작
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.count.walk");
                sendBroadcast(intentResponse);
                sendNotification(message);
                break;
            case 9 :    // 목표 달성
                break;
            case 10 :    // 초대 취소
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.invite");
                sendBroadcast(intentResponse);
                break;
        }


        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */

        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.push)
                .setContentTitle("어울림 알림이 왔습니다!")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public void showRoomDialog(String message, String sendername) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(850,450);

        TextView dlgDefaultText_big = (TextView) dialog.findViewById(R.id.dlgDefaultText_big);
        TextView dlgDefaultText_small = (TextView) dialog.findViewById(R.id.dlgDefaultText_small);
        Button dlgOk = (Button) dialog.findViewById(R.id.dlgOk);

        dlgDefaultText_big.setText(sendername+"님으로 부터");
        dlgDefaultText_small.setText(message);
        dlgOk.setText("확   인");

        dlgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isDialogShow = false;
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        dialog.show();
        isDialogShow = true;
    }

}