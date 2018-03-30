package com.example.hp2.sirus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.Calendar;

public class Ai extends BroadcastReceiver {

    Context cn;
    @Override
    public void onReceive(Context context, Intent intent) {
        cn=context;
        String ac=intent.getAction();
        Intent i= new Intent(context,Sirusservice.class);

        if(ac.equals("android.intent.action.ACTION_POWER_CONNECTED")) {
            i.putExtra("message","Charger Plugged in");
        }
        if(ac.equals("android.intent.action.ACTION_POWER_DISCONNECTED")){
            i.putExtra("message","Charger Plugged Out");
        }
        if(ac.equals("android.intent.action.AIRPLANE_MODE")){
            if(intent.getBooleanExtra("state",false)){
                i.putExtra("message","Airplane mode On");
            }
            else
            {
                i.putExtra("message","Airplane Mode Off");
            }

        }

        if(ac.equals("android.intent.action.SCREEN_OFF")){
            i.putExtra("message","Screen Off");

        }
        if(ac.equals("android.intent.action.SCREEN_ON")){
            i.putExtra("message","Screen On");
        }
        if(ac.equals("android.intent.action.USER_UNLOCKED")){
            i.putExtra("message","User Unlocked");
        }
        if(ac.equals("android.intent.action.BATTERY_LOW")){
            i.putExtra("message","Battery Low,Please Connect Charger");
        }

        if(ac.equals("android.provider.Telephony.SMS_RECEIVED")){
            StringBuffer string=new StringBuffer();
            Toast.makeText(context,string.toString(),Toast.LENGTH_LONG).show();
                    i.putExtra("message","You have a new Text Message ");
        }

        if(ac.equals("android.intent.action.PHONE_STATE")){
            String state=intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                String  n=intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                ContentResolver resolver = context.getContentResolver();
                Uri uri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor c=resolver.query(uri,null,null,null,null);
                while (c.moveToNext()) {
                    if ((c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).equals(n))) {
                        i.putExtra("message","You have an Incoming Call from: "+c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                        break;
                    }else{
                        i.putExtra("message","You have an Incoming Call from: "+n);}
            }}
        }
        if(ac.equals("android.intent.action.BOOT_COMPLETED")){
            i.putExtra("message","All systems ready to Gear Up");
        }

        if(ac.equals("android.intent.conn.CONNECTIVITY_CHANGE")){
            ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(wifi.isConnected()||mobile.isConnected()){
                i.putExtra("message","Internet Connected");
            }
        }

        if(ac.equals("remind")||ac.equals("android.intent.action.BOOT_COMPLETED")){
            Calendar c=Calendar.getInstance();
            int h=intent.getIntExtra("hour",c.get(Calendar.HOUR_OF_DAY));
            int m=intent.getIntExtra("minute",c.get(Calendar.MINUTE));
            int d=intent.getIntExtra("date",c.get(Calendar.DATE));
            int mm=intent.getIntExtra("month",c.get(Calendar.MONTH));
            int y=intent.getIntExtra("year",c.get(Calendar.YEAR));

            c.set(Calendar.HOUR_OF_DAY,h);
            c.set(Calendar.MINUTE,m);
            c.set(Calendar.SECOND,00);
            c.set(Calendar.DATE,d);
            c.set(Calendar.MONTH,mm);
            c.set(Calendar.YEAR,y);

            AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
           // Intent x=new Intent(context,AlramRing.class);
           // x.putExtra("message",intent.getStringExtra("message"));
           // PendingIntent pendingIntent=PendingIntent.getActivity(context,(int)Math.random()*1023,x,0);
            Toast.makeText(context,"Okay I will Remind you",Toast.LENGTH_LONG).show();
           // alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        }

        context.startService(i);
    }

}
