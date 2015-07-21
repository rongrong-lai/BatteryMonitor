package com.rongrong.batterymonitor;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private int batterylevel = -1;
    private BroadcastReceiver batteryLevelReceiver;
    private AlarmManager am;

    private void getBatteryPercentage() {
        batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                TextView batteryPercent = (TextView)findViewById(R.id.batteryPercent);
                TextView batteryVoltage = (TextView)findViewById(R.id.batteryVoltage);
                TextView batteryTemperature = (TextView)findViewById(R.id.batteryTemperature);

                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

                if (currentLevel >= 0 && scale > 0) {
                    Log.i("Test", "battery change! current level:" + currentLevel);
                    batterylevel = (currentLevel * 100) / scale;
                }
                batteryPercent.setText("Battery Level Remaining: " + batterylevel + "%");
                batteryVoltage.setText("Battery Voltage: " + voltage);
                batteryTemperature.setText("Battery Temperature: " + temperature);

                /*To get the update battery level, we need to cancel the pending alarm and start a new one
                * Create a PendingIntent (or update the existing PendingIntent with new values
                */
                Intent intentAlarm = new Intent(MainActivity.this, AlarmReceiver.class);
                Bundle b = new Bundle();
                b.putInt("batteryLevel", batterylevel);
                intentAlarm.putExtras(b);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intentAlarm,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                // cancel any pending alarms
                am.cancel(pendingIntent);
                //start new
                am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 1000 * 10, pendingIntent);
                Toast.makeText(MainActivity.this, "Alarm Scheduled for every 30 seconds", Toast.LENGTH_LONG).show();
            }
        };

        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        getBatteryPercentage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        unregisterReceiver(batteryLevelReceiver);

        //cancel the alarm
        Intent intentAlarm = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intentAlarm,
                PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pendingIntent);
    }

    public void stopRecording(View view)
    {
        this.getBaseContext().deleteFile("batteryData.txt");
        Toast.makeText(MainActivity.this, "Battery data file is deleted ...", Toast.LENGTH_LONG).show();
    }
}
