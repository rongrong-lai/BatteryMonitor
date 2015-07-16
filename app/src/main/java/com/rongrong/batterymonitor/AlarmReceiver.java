package com.rongrong.batterymonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by rongrong_lai on 7/15/15.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub

        int result = intent.getIntExtra("batteryLevel", 0);

        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone

        Toast.makeText(context, "Alarm Triggered and battery leve is " + result, Toast.LENGTH_LONG).show();

        try {
            FileOutputStream fileout = context.openFileOutput("batteryData.txt", Context.MODE_APPEND);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write("Data: " + result + "  " + "Time: " + System.currentTimeMillis());
            outputWriter.write("\n");
            outputWriter.close();

            //display file saved message
            //Toast.makeText(context, "File saved successfully!",
            //        Toast.LENGTH_SHORT).show();

            /*  code to read file*/
            File file = context.getFileStreamPath("batteryData.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                Log.i("Test", line);
            }

            br.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
