package ru.perm.trubnikov.gps2sms;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Incoming SMS Handler here
 * *
 */

public class IncomingSms extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;

    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (sharedPrefs.getBoolean("prefRegexpSMS", true)) {

            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage
                                .createFromPdu((byte[]) pdusObj[i]);
                        // String phoneNumber =
                        // currentMessage.getDisplayOriginatingAddress();
                        // String senderNum = phoneNumber;
                        // Log.d("gps", "senderNum: "+ senderNum + "; message: "
                        // + message);

                        String Coordinates = GpsHelper.extractCoordinates(currentMessage.getDisplayMessageBody());

                        if (!Coordinates.equalsIgnoreCase("0,0")) {

                            sendNotification(context, Coordinates);
                            //DBHelper.openOnMap(context, Coordinates);

                        }

                        // Show Alert
                        // Toast toast = Toast.makeText(context, clip,
                        // Toast.LENGTH_LONG);
                        // toast.show();

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.d("gps", "Exception smsReceiver" + e);

            }

        }

    }

    public void sendNotification(Context context, String Coordinates) {

        Intent intent = GpsHelper.getIntentForMap(Coordinates);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.ic_stat_notify_location);

        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT > 13) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        }

        builder.setContentTitle(context.getResources().getString(R.string.sms_notify_title));
        builder.setContentText(context.getResources().getString(R.string.sms_notify_desc1));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }


}