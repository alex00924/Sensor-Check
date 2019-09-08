package com.lordsofts.sensortest;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.lordsofts.sensortest.R;
/**
*   This file continues the graph representation according to a time interval.
*/


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AccelerometerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccelerometerFragment extends Fragment{
    SensorManager sensorManager;
    Sensor accelerometer;
    public Handler mHandler = null;
    public Runnable mTimer;
    public float x = 0,y=0,z=0;
    public boolean boolSwitch = false;
    GraphView graph = null;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    LineGraphSeries<DataPoint> xseries;
    LineGraphSeries<DataPoint> yseries;
    LineGraphSeries<DataPoint> zseries;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int counter = 1;


    public AccelerometerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccelerometerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccelerometerFragment newInstance(String param1, String param2) {
        AccelerometerFragment fragment = new AccelerometerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_accelerometer, container, false);

        // Inflate the layout for this fragment
        graph = (GraphView) v.findViewById(R.id.graph);

        //This check is done because there could be no instance before the fragment is called. If there is already an instance running then we do not need to create a new series.
        if (xseries == null) xseries = new LineGraphSeries<>(new DataPoint[]{});
        if (yseries == null) yseries = new LineGraphSeries<>(new DataPoint[]{});
        if (zseries == null) zseries = new LineGraphSeries<>(new DataPoint[]{});



        //Color setup according to the axis.
        xseries.setColor(Color.RED);
        yseries.setColor(Color.GREEN);
        zseries.setColor(Color.BLUE);

        //Bound setup for the axis
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-10);
        graph.getViewport().setMaxX(10);


        //We save the currently running state in the boolSwitch. So if it is running then it will continue through the toggleSwitch(true)
        toggleSwitcher(boolSwitch);

        return v;


    }

    //Toggle switcher is the function what switches the graph on or off, Or even it continues the previous recorded graph.
    //This is where we are setting up the Timer to check for the sensor values after a time interval again and again until boolSwitch = False.
    // To turn this graph off we are sending  toggleSwitch(false)
    public void toggleSwitcher(boolean bs )
    {
        counter = 1;
        boolSwitch = bs;
        if(graph !=null) {
            if (xseries != null) xseries.resetData(new DataPoint[]{new DataPoint(0, 0)});
            if (yseries != null) yseries.resetData(new DataPoint[]{new DataPoint(0, 0)});
            if (zseries != null) zseries.resetData(new DataPoint[]{new DataPoint(0, 0)});
            graph.removeAllSeries();
            graph.addSeries(xseries);
            graph.addSeries(yseries);
            graph.addSeries(zseries);

        }
        else
        {
            return;
        }


        if (boolSwitch) {
            if (mHandler == null) {

                if (graph != null) {

                    try
                    {
                        //Timer codes
                        mHandler = new Handler();
                        mTimer = new Runnable() {
                            @Override
                            public void run() {
                                try
                                {
                                    mHandler = new Handler();
                                    xseries.appendData(new DataPoint(counter, x), true, counter);
                                    yseries.appendData(new DataPoint(counter, y), true, counter);
                                    zseries.appendData(new DataPoint(counter, z), true, counter);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                catch (Error e2)
                                {
                                    e2.printStackTrace();
                                }


                                counter++;
                                //Re run if it is true.
                                if (boolSwitch) {
                                    mHandler.postDelayed(this, 300);
                                } else {
                                    mHandler = null;
                                    mTimer = null;
                                }
                            }
                        };

                        mHandler.postDelayed(mTimer, 300);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    catch (Error e2)
                    {
                        e2.printStackTrace();
                    }
                }
            }

        }
        else
        {
            mHandler = null;
            mTimer = null;
            if(graph != null) graph.removeAllSeries();
            counter = 0;
        }




    }

}
