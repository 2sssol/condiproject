package condi.kr.ac.swu.condiproject.gcm;

/**
 * Created by 8304 on 2015-10-26.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.activity.IntroActivity;
import condi.kr.ac.swu.condiproject.activity.PreGroupActivity;
import condi.kr.ac.swu.condiproject.activity.SelectRegionActivity;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

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
        int t =  Integer.parseInt(type);
        Intent intentResponse;

        switch (t) {
            case 0 :
                break;
            case 1 :
                break;
            case 2 :    // push
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.start.walk");
                sendBroadcast(intentResponse);
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
                break;
            case 7 :    // 코스 선택함
                intentResponse = new Intent("condi.kr.ac.swu.condiproject.course");
                sendBroadcast(intentResponse);
                break;
            case 8 :    // 걸음 시작
                break;
            case 9 :    // 목표 달성
                break;
        }


        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
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
                .setSmallIcon(R.drawable.icon_step)
                .setContentTitle("어울림 알림이 왔습니다!")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}