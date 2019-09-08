package com.lordsofts.sensortest;


import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lordsofts.sensortest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener,GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{

    private static final String TAG = "MainActivity";
    SensorManager sensorManager;
    Sensor accelerometer,gyro;
    TextView xvalue,yvalue,zvalue,xgyro,ygyro,zgyro,gestureText,globaltext;
    GestureDetectorCompat gestureDetectorCompat;
    Button startbutton,endbutton;
    boolean tmp = false;
    Timer t;
    TimerTask task;
    public String startTime,endTime;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_xml_ui_design);

        DatabaseHelper db1=new DatabaseHelper(this);
        db1.CopyDB(this);

        xvalue=findViewById(R.id.xvalue);
        yvalue=findViewById(R.id.yvalue);
        zvalue=findViewById(R.id.zvalue);

        xgyro=findViewById(R.id.xGyrovalue);
        ygyro=findViewById(R.id.yGyrovalue);
        zgyro=findViewById(R.id.zGyrovalue);

        startbutton=findViewById(R.id.startButtonid);
        endbutton=findViewById(R.id.endButtonid);


        gestureText=findViewById(R.id.gestureText);

        startbutton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
                Date date = new Date();
                startTime=dateFormat.format(date);

                tmp = true;

                gestureDetectorCompat = new GestureDetectorCompat(MainActivity.this, MainActivity.this);
                gestureDetectorCompat.setOnDoubleTapListener(MainActivity.this);



                Log.d(TAG, "onCreate: initializing sensor services");
                sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);

                accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if(accelerometer!=null){
                    sensorManager.registerListener(MainActivity.this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    Log.d(TAG, "onCreate: initializing accelerometer listener");
                }else{
                    xvalue.setText("Accelerometer is not available");
                    yvalue.setText("Accelerometer is not available");
                    zvalue.setText("Accelerometer is not available");
                }
                gyro=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                if(gyro!=null){
                    sensorManager.registerListener(MainActivity.this,gyro, SensorManager.SENSOR_DELAY_NORMAL);
                    Log.d(TAG, "onCreate: initializing gyro listener");
                }else{
                    xgyro.setText("Gyrometer is not available");
                    ygyro.setText("Gyrometer is not available");
                    zgyro.setText("Gyrometer is not available");
                }
                startTimer();
            }
        });

        endbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
                    Date date = new Date();
                    endTime = dateFormat.format(date);
                    DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                    db.insertevents(startTime, endTime);
                    Log.d(TAG, "time inserted to database");
                    tmp = false;
                    t.cancel();
                    Log.d(TAG, "End Button Clicked");
                }catch (Exception e){

                }

            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (this.tmp == true) {
            Sensor sensor = event.sensor;
            if (sensor.getType() == sensor.TYPE_ACCELEROMETER) {
                Log.d(TAG, "onSensorChanged: X:" + event.values[0] + "Y:" + event.values[1] + "Z:" + event.values[2]);
                xvalue.setText("" + event.values[0]);
                yvalue.setText("" + event.values[1]);
                zvalue.setText("" + event.values[2]);
            } else if (sensor.getType() == sensor.TYPE_GYROSCOPE) {
                Log.d(TAG, "onSensorChanged: X:" + event.values[0] + "Y:" + event.values[1] + "Z:" + event.values[2]);
                xgyro.setText("" + event.values[0]);
                ygyro.setText("" + event.values[1]);
                zgyro.setText("" + event.values[2]);
            }

        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (this.tmp ==true) {
            gestureText.setText("onSingleTapConfirmed is clicked\nvalue of X=" + Float.toString(e.getRawX()) + "\nvalue of Y=" + Float.toString(e.getRawY()));
            Log.d(TAG, "onSingleTapConfirmed: "+ Float.toString(e.getRawX())+ Float.toString(e.getRawY()));

        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (this.tmp == true) {
            gestureText.setText("onDoubleTap is clicked\nvalue of X=" + Float.toString(e.getRawX()) + "\nvalue of Y=" + Float.toString(e.getRawY()));
            Log.d(TAG, "onDoubleTap: "+ Float.toString(e.getRawX())+ Float.toString(e.getRawY()));
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        if (this.tmp == true) {
            gestureText.setText("onDoubleTapEvent is clicked\nvalue of X=" + Float.toString(e.getRawX()) + "\nvalue of Y=" + Float.toString(e.getRawY()));
            Log.d(TAG, "onDoubleTapEvent: "+ Float.toString(e.getRawX()) + Float.toString(e.getRawY()));
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (this.tmp == true) {
            gestureText.setText("onDown is clicked\nvalue of X=" + Float.toString(e.getRawX()) + "\nvalue of Y=" + Float.toString(e.getRawY()));
            Log.d(TAG, "onDown: "+ Float.toString(e.getRawX())+ Float.toString(e.getRawY()));
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        if (this.tmp == true) {
            gestureText.setText("onShowPress is clicked\nvalue of X=" + Float.toString(e.getRawX()) + "\nvalue of Y=" + Float.toString(e.getRawY()));
            Log.d(TAG, "onShowPress: "+ Float.toString(e.getRawX())+ Float.toString(e.getRawY()) );
        }

        }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (this.tmp == true) {
            gestureText.setText("onSingleTapUp is clicked\nvalue of X=" + Float.toString(e.getRawX()) + "\nvalue of Y=" + Float.toString(e.getRawY()));
            Log.d(TAG, "onSingleTapUp: "+ Float.toString(e.getRawX())+ Float.toString(e.getRawX()));
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (this.tmp == true) {
            gestureText.setText("onScroll is clicked\ndistance in X=" + Float.toString(distanceX) + "\ndistance in Y=" + Float.toString(distanceY));
            Log.d(TAG, "onScroll: "+ Float.toString(distanceX)+ Float.toString(distanceY));
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (this.tmp == true) {
            gestureText.setText("onLongPress is clicked\nvalue of X=" + Float.toString(e.getRawX()) + "\nvalue of Y=" + Float.toString(e.getRawY()));

            Log.d(TAG, "onSingleTapUp: "+ Float.toString(e.getRawX())+ Float.toString(e.getRawX()));
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.tmp == true) {
            gestureText.setText("onFling is clicked\n" + "velocity in X=" + Float.toString(velocityX) + "\nvelocity in Y=" + Float.toString(velocityY));
            Log.d(TAG, "onFling: "+ Float.toString(velocityX)+ Float.toString(velocityY));

        }
        return true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            this.gestureDetectorCompat.onTouchEvent(event);

        }catch (Exception e){

        }
        return false;
    }

    public void startTimer(){
        t = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
                        Date date = new Date();
                        String time=dateFormat.format(date);
                        Log.d("CurTime","Time is : " + time);


                        JSONObject accelerometerobj=new JSONObject();
                        try{

                            accelerometerobj.put("X",xvalue.getText());
                            accelerometerobj.put("Y",yvalue.getText());
                            accelerometerobj.put("Z",zvalue.getText());
                        }catch(JSONException e){
                            Log.d(TAG, "JSONexception"+e);
                        }

                        JSONObject gyrometerobj=new JSONObject();
                        try{

                            gyrometerobj.put("X",xgyro.getText());
                            gyrometerobj.put("Y",ygyro.getText());
                            gyrometerobj.put("Z",zgyro.getText());
                        }catch(JSONException e){
                            Log.d(TAG, "JSONexception"+e);
                        }

                        JSONObject gestureobj=new JSONObject();
                        try{

                            gestureobj.put("Gesture Value",gestureText.getText());
                        }catch(JSONException e){
                            Log.d(TAG, "JSONexception"+e);
                        }
                        Log.d(TAG, "All three json object has created");


                        String accelerometerInsert=accelerometerobj.toString();
                        String gyrometerInsert=gyrometerobj.toString();
                        String gestureInsert=gestureobj.toString();

                        Log.d(TAG, accelerometerInsert);


                        DatabaseHelper db=new DatabaseHelper(MainActivity.this);
                        int id=db.fatcheventsid();
                        Log.d(TAG, "" + "Events last id is : "+id);
                        db.inserevents_metaaccelerometer(id,"accelerometer",accelerometerInsert,time);
                        db.inserevents_metagyrometer(id,"Gyrometer",gyrometerInsert,time);
                        db.inserevents_metagysture(id,"Gesture",gestureInsert,time);

                        Log.d(TAG, "Events_meta inserted successfull");


                    }
                });
            }
        };

        t.scheduleAtFixedRate(task, 0, 1000);
    }


}
