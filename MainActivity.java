package com.example.lzhan274.getsensor2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.util.Locale;


public class MainActivity extends WearableActivity {

    private EditText resultView;
    private SensorManager sensorManager;
    private MySensorEventListener sensorEventListener;
    private List<Double> accList = new ArrayList<Double>();
    private long time_start;
    private long time_end;
    private int indicator = 0;
    private FileOutputStream f;
    private File myFile;
    String fileDir, myPath, filename;
    FileWriter myFW;
    BufferedWriter myBW;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button buttonStart = (Button) findViewById(R.id.buttonStart);
        Button buttonEnd = (Button) findViewById(R.id.buttonEnd);

        resultView = (EditText) this.findViewById(R.id.resultView);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //        resultView.setText("Start!");
                time_start = System.currentTimeMillis();
                filename = resultView.getText().toString();
        //        resultView.setText("Collecting... ");

                String externaldir = "";
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    externaldir = Environment.getExternalStorageDirectory().getAbsolutePath();
                }

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                }
                /*
                if (-1 == ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
                */
                if (!"".equals(externaldir)){
                    myPath = externaldir + File.separator + "llz" + File.separator + filename + ".txt";
                    myFile = new File(myPath);

                if (myFile.exists()){
                    myFile.delete();
                }

                    if (!myFile.exists()){
                        try {
                            String filedir = myPath.substring(0, myPath.lastIndexOf(File.separator));

                            File my_file_dir = new File(filedir);
                            if (!my_file_dir.exists()) {
                                boolean ll = my_file_dir.mkdirs();
                                System.out.print(ll);
                            }
                            boolean tt = myFile.createNewFile();
                            System.out.print(tt);

                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "file exists", Toast.LENGTH_LONG).show();
                    }
                }

                try {
                    myFW = new FileWriter(myFile, true);
                    myBW = new BufferedWriter(myFW);
                } catch (IOException e){
                    e.printStackTrace();
                }

                sensorEventListener = new MySensorEventListener();

                Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(sensorEventListener,accelerometerSensor,SensorManager.SENSOR_DELAY_FASTEST);

                Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                sensorManager.registerListener(sensorEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);

                Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
                sensorManager.registerListener(sensorEventListener,gravitySensor,SensorManager.SENSOR_DELAY_FASTEST);

                //        resultView.setText("now time: " + time_start);
        //        resultView.setText("the last value: " + accList.get(accList.size()-1));
            }
        });

        buttonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //        time_end = System.currentTimeMillis();

        //        long time = time_end - time_start;

                sensorManager.unregisterListener(sensorEventListener);

         //       resultView.setText("the time is:" + time);

        //        openfilename = new File(Environment.getExternalStorageDirectory(),"MyApp.txt");

                try{
        //            finish();
                    myBW.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        //        resultView.setText(accList.size()/2 + " Data Saved!");
            }
        });

     //   resultView = (TextView) findViewById(R.id.text);

        // Enables Always-on
    //    setAmbientEnabled();
    }

    private class MySensorProcess
    {
        public String MySensorProcessing()
        {
            String value = "000";
            int size = accList.size()/2;
            double[] data = new double[size];
            double[] time = new double[size];
            for (int i = 0; i < size; i++){
                data[i] = accList.get(2*i+1);
                time[i] = accList.get(2*i);
                time[i] = time[i] - time[0] + 1;
            }


            return value;
        }
    }

    private final class MySensorEventListener implements SensorEventListener
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            indicator = 1;
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                double mag = Math.sqrt(x*x+y*y+z*z);
        //        resultView.setText("Acc: " + mag);
                accList.add((double)System.currentTimeMillis()-time_start);
                accList.add((double)Math.sqrt(x*x+y*y+z*z));

                try{
                    myBW.write(System.currentTimeMillis()-time_start + " " + x + " " + y + " " + z + " 1" + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
            //    double mag = Math.sqrt(x*x+y*y+z*z);
            //    resultView.setText("Acc: " + mag);
            //    accList.add((double)System.currentTimeMillis()-time_start);
            //    accList.add((double)Math.sqrt(x*x+y*y+z*z));

                try{
                    myBW.write(System.currentTimeMillis()-time_start + " " + x + " " + y + " " + z + " 2" + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
            {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
            //    double mag = Math.sqrt(x*x+y*y+z*z);
            //    resultView.setText("Acc: " + mag);
            //    accList.add((double)System.currentTimeMillis()-time_start);
            //    accList.add((double)Math.sqrt(x*x+y*y+z*z));

                try{
                    myBW.write(System.currentTimeMillis()-time_start + " " + x + " " + y + " " + z + " 4" + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
        }

    }
}
