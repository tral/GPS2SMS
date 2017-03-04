package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

public class ThreadSendSMS extends Thread {

    Handler mHandler;

    private Context context;

    final static int STATE_DONE = 0;
    final static int STATE_RUNNING = 1;
    final static int STATE_WAITING_RESULT = 2;

    BroadcastReceiver sendBroadcastReceiver;
    //BroadcastReceiver deliveryBroadcastReciever;

    int mState;

    String smsMessage;
    String smsPhone;

    String res_send;
    // String res_deliver;

    int send_receiver_to_fire;
    int send_receiver_fired;

    // int deliver_receiver_to_fire;
    // int deliver_receiver_fired;

    // Constructor
    // We need a context from MainActivity to register receivers
    ThreadSendSMS(Handler h, Context paramContext) {
        mHandler = h;
        context = paramContext;
    }

    // Deliver SMS Receiver
    /*
     * class deliverReceiver extends BroadcastReceiver {
	 * 
	 * @Override public void onReceive(Context context, Intent arg1) {
	 * 
	 * setDeliverRecFired(deliver_receiver_fired+1);
	 * 
	 * switch (getResultCode()) { case Activity.RESULT_OK:
	 * setResDeliver(context.getString(R.string.info_sms_delivered)); break;
	 * case Activity.RESULT_CANCELED:
	 * setResDeliver(context.getString(R.string.info_sms_not_delivered)); break;
	 * } } }
	 */


    // Send SMS Receiver
    class sentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {

            setSendRecFired(send_receiver_fired + 1);

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    setResSend(context.getString(R.string.info_sms_sent));
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    setResSend(context.getString(R.string.info_sms_generic));
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    setResSend(context.getString(R.string.info_sms_noservice));
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    setResSend(context.getString(R.string.info_sms_nullpdu));
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    setResSend(context.getString(R.string.info_sms_radioof));
                    break;
            }
        }
    }

    // Avoid memory leaks, unregister receivers
    public void unregisterReceivers() {
        context.unregisterReceiver(sendBroadcastReceiver);
        // context.unregisterReceiver(deliveryBroadcastReciever);
    }

    // Main part of the thread
    public void run() {

        // In this state we send the SMS message
        if (mState == STATE_RUNNING) {

            res_send = "";
            // res_deliver = "n/a";
            setSendRecFired(0);
            setSendRecToFire(0);
            // setDeliverRecFired(0);
            // setDeliverRecToFire(0);

            sendBroadcastReceiver = new sentReceiver();
            // deliveryBroadcastReciever = new deliverReceiver();

            try {

                String SENT = "SMS_SENT";
                // String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(this.context,
                        0, new Intent(SENT), 0);
                // PendingIntent deliveredPI =
                // PendingIntent.getBroadcast(this.context, 0, new
                // Intent(DELIVERED), 0);

                context.registerReceiver(sendBroadcastReceiver,
                        new IntentFilter(SENT));
                // context.registerReceiver(deliveryBroadcastReciever, new
                // IntentFilter(DELIVERED));

                SmsManager sms = SmsManager.getDefault();

                ArrayList<String> mArray = sms.divideMessage(this.smsMessage);
                ArrayList<PendingIntent> sentArrayIntents = new ArrayList<PendingIntent>();
                // ArrayList<PendingIntent> deliveredArrayIntents = new
                // ArrayList<PendingIntent>();

                for (int i = 0; i < mArray.size(); i++) {
                    sentArrayIntents.add(sentPI);
                    // deliveredArrayIntents.add(deliveredPI);
                    setSendRecToFire(send_receiver_to_fire + 1);
                    // setDeliverRecToFire(deliver_receiver_to_fire+1);
                }

                // sms.sendMultipartTextMessage("+7" + this.smsPhone, null,
                // mArray, sentArrayIntents, deliveredArrayIntents);
                sms.sendMultipartTextMessage(this.smsPhone, null, mArray,
                        sentArrayIntents, null);

            } catch (Exception e) {
                // String res_error = "EX6! " + e.toString() +" Message:"
                // +e.getMessage();
            } finally {
                mState = STATE_WAITING_RESULT;
            }

        }

        // In this state we wait for the result of sending and delivery
        while (mState == STATE_WAITING_RESULT) {

            // to avoid hi-load cycle
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }

            // if ( deliver_receiver_fired==deliver_receiver_to_fire &&
            // send_receiver_fired==send_receiver_to_fire ) {
            if (send_receiver_fired == send_receiver_to_fire) {
                unregisterReceivers(); // We need to unregister receivers to
                // avoid potential memory leaks
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("res_send", res_send);
                b.putString("res_sms_text", this.smsMessage);
                b.putString("phone", this.smsPhone);
                msg.setData(b);
                mState = STATE_DONE;
                mHandler.sendMessage(msg);
            }

        } // EOF while

    }

    public void setState(int state) {
        mState = state;
    }

    public void setMsg(String m) {
        smsMessage = m;
    }

    public void setPhone(String p) {
        smsPhone = p;
    }

    public void setResSend(String r) {
        // res_send = res_send + r;
        res_send = r;
    }

    public void setSendRecToFire(int f) {
        send_receiver_to_fire = f;
    }

    public void setSendRecFired(int f) {
        send_receiver_fired = f;
    }
    /*
	 * public void setResDeliver(String r) { res_deliver = res_deliver+r; }
	 * 
	 * public void setDeliverRecToFire(int f) { deliver_receiver_to_fire = f; }
	 * 
	 * public void setDeliverRecFired(int f) { deliver_receiver_fired = f; }
	 */

}
