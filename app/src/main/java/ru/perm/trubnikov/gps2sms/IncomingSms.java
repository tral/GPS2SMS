package ru.perm.trubnikov.gps2sms;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

                        String Coordinates = DBHelper.extractCoordinates(currentMessage.getDisplayMessageBody());

                        if (!Coordinates.equalsIgnoreCase("0,0")) {

                            sendNotification(context, Coordinates);
                            //DBHelper.openOnMap(context, Coordinates);

							/*
                             * Intent intent_openmap = new Intent(
							 * Intent.ACTION_VIEW, Uri.parse("geo:" +
							 * m.group(0))); intent_openmap
							 * .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							 * context.startActivity(intent_openmap);
							 */
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


    /**
     * Send a sample notification using the NotificationCompat API.
     */
    public void sendNotification(Context context, String Coordinates) {


        /** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
         * notification service can fire it on our behalf.
         */
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
        Intent intent = DBHelper.getIntentForMap(Coordinates);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        // BEGIN_INCLUDE (build_notification)
        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        /** Set the icon that will appear in the notification bar. This icon also appears
         * in the lower right hand corner of the notification itself.
         *
         * Important note: although you can use any drawable as the small icon, Android
         * design guidelines state that the icon should be simple and monochrome. Full-color
         * bitmaps or busy images don't render well on smaller screens and can end up
         * confusing the user.
         */
        builder.setSmallIcon(R.drawable.ic_stat_notify_location);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Set the notification to auto-cancel. This means that the notification will disappear
        // after the user taps it, rather than remaining until it's explicitly dismissed.
        builder.setAutoCancel(true);

        /**
         *Build the notification's appearance.
         * Set the large icon, which appears on the left of the notification. In this
         * sample we'll set the large icon to be the same as our app icon. The app icon is a
         * reasonable default if you don't have anything more compelling to use as an icon.
         */
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));

        /**
         * Set the text of the notification. This sample sets the three most commononly used
         * text areas:
         * 1. The content title, which appears in large type at the top of the notification
         * 2. The content text, which appears in smaller text below the title
         * 3. The subtext, which appears under the text on newer devices. Devices running
         *    versions of Android prior to 4.2 will ignore this field, so don't use it for
         *    anything vital!
         */
        builder.setContentTitle("BasicNotifications Sample");
        builder.setContentText("Time to learn about notifications!");
        builder.setSubText("Tap to view documentation about notifications.");

        // END_INCLUDE (build_notification)


        // Notification.Builder notificationBuilder = new Notification.Builder(getActivity())
        //       .setSmallIcon(R.drawable.ic_launcher_notification)
        //      .setPriority(Notification.PRIORITY_DEFAULT)
        //       .setCategory(Notification.CATEGORY_MESSAGE)
        //      .setContentTitle("Sample Notification")
        //      .setContentText("This is a normal notification.");
        //   if (makeHeadsUpNotification) {
         /*   Intent push = new Intent();
            push.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            push.setClass(MainActivity.this, MainActivity.class);

            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(MainActivity.this, 0,
                    push, PendingIntent.FLAG_CANCEL_CURRENT);
        builder
                    .setContentText("Heads-Up Notification on Android L or above.")
                    .setFullScreenIntent(fullScreenPendingIntent, true);*/
        //    }
        // return notificationBuilder.build();


        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }


}