package com.lordsofts.sensortest;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class MeasureFingerPressure extends Fragment {
    public boolean boolSwitch = false;
    TextView measurefinger;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_measure_fingerprint_pressure, container, false);

        measurefinger= v.findViewById(R.id.measurefingerpressuretextid);

        measurefinger.setVisibility(View.GONE);
        Log.d("MeasureFingerPressure","Test test");




            v.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_MOVE) {

                            measurefinger.setText("The finger pressure is : " + event.getPressure());

                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {

                            measurefinger.setText("The finger pressure is : " + event.getPressure());

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            measurefinger.setText("The finger pressure is : 0");

                    } else if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {

                            measurefinger.setText("The finger pressure is : " + event.getPressure());
                    }


                    return true;
            }
            });








        //SharedPreferences preferences = this.getActivity().getSharedPreferences("pressuresend", Context.MODE_PRIVATE);
        //measurefinger.setText("The Finger Pressure is :"+preferences.getString("pressure",null));

        toggleSwitcher(boolSwitch);
        return v;

    }

    public void toggleSwitcher(boolean bs) {
        boolSwitch = bs;

        if (measurefinger != null) {
            if (boolSwitch) {


                measurefinger.setVisibility(View.VISIBLE);

            } else {
                measurefinger.setVisibility(View.GONE);
            }
        }
    }


}
