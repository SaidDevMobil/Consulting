package courses.pluralsight.com.tabianconsulting.utility;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import courses.pluralsight.com.tabianconsulting.BuildConfig;
import courses.pluralsight.com.tabianconsulting.LoginActivity;
import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.PushNotification;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final  String TAG = "MyFirebaseMessagingServ";
    private static final String CHANNEL_ID = "channel_test";


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String message = remoteMessage.getData().get("message");
        Log.e(TAG, "onMessageReceived: remoteMessage : "+ remoteMessage.getData().get("key"));


        String notificationBody = "";
        String notificationTitle = "";
        String notificationData = "";
        String notificationForm = "";

        try {
            notificationData = remoteMessage.getData().get("message");
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
            String notificationBadge = remoteMessage.getNotification().getIcon();

            notificationForm = remoteMessage.getFrom();

        }catch (NullPointerException e){
            Log.e(TAG, "onMessageReceived: NullPointerException : "+ e.getMessage());
        }

        Log.e(TAG, "onMessageReceived: Data : "+ notificationData);
        Log.e(TAG, "onMessageReceived: data2 : "+ remoteMessage.getData().toString());

        Log.e(TAG, "onMessageReceived: notificationBody : "+ notificationBody);
        Log.e(TAG, "onMessageReceived: notificationTitle : "+ notificationTitle);
        Log.e(TAG, "onMessageReceived: notificationForm : "+ notificationForm);


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PushNotification obj = objectMapper.readValue(remoteMessage.getNotification().toString(), PushNotification.class);
            if (isApplicationBroughtToBackground()) {
                sendNotification(obj);
            } else {
                //display notification if screen is locked
                KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                if (km != null && km.inKeyguardRestrictedInputMode())
                    sendNotification(obj);
            }
            Log.e(TAG, "onMessageReceived: obj.toString() : "+ obj.toString());
            Log.e(TAG, "onMessageReceived: obj.toString() : "+ obj.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isApplicationBroughtToBackground() {

        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo app : appList) {
            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && app.importanceReasonCode == 0 && Arrays.asList(app.pkgList).contains(this.getPackageName()))
                return false;
        }

        return true;
    }

    private void sendNotification(PushNotification message) {
        //Intent intent = new Intent(this, MainActivity.class);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Intent.EXTRA_COMPONENT_NAME, "EXTRA_COMPONENT_NAME_NOTIFICATION");
        intent.putExtra("PUSH_NOTIFICATION", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int resIcon = R.drawable.ic_schedule;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);




        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "test", importance);
            // Configure the notification channel.
            mChannel.setDescription("Notification");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(resIcon)
                            .setContentTitle(message.getTitle())
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message.getBody()))
                            .setContentText(message.getBody())
                            .setAutoCancel(true);

            mBuilder.setContentIntent(pendingIntent);
            notificationManager.notify(1, mBuilder.build());

        } else {

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(resIcon)
                            .setContentTitle(message.getTitle())
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message.getBody()))
                            .setContentText(message.getBody())
                            .setAutoCancel(true);

            /*

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    mBuilder
                            //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic))
                            .setColor(getResources().getColor(R.color.orange))
                            .setSmallIcon(R.drawable.ic_notif)
                    ;
                }else
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
             */

            mBuilder.setContentIntent(pendingIntent);
            notificationManager.notify(1, mBuilder.build());


        }
    }


}
