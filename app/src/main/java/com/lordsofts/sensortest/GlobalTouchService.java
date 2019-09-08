package com.lordsofts.sensortest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalTouchService extends Service implements OnTouchListener {

	private String TAG = this.getClass().getSimpleName();
	// window manager 
	private WindowManager mWindowManager;
	// linear layout will use to detect touch event

	JSONObject jsonObject = new JSONObject();
	MainActivityDash mainActivityDash;

	private LinearLayout touchLayout1, touchLayout2, touchLayout3;
	int action_outside_counter = 0;
    JSONArray action_move = null;
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d("Intent:", arg0.toString());
		return null;
	}

	@Override
	public void onCreate() {

		try {
			super.onCreate();
			// create linear layout
			touchLayout1 = new LinearLayout(this);
			touchLayout2 = new LinearLayout(this);
			touchLayout3 = new LinearLayout(this);

			// set layout width 30 px and height is equal to full screen
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			touchLayout1.setLayoutParams(lp);
			touchLayout2.setLayoutParams(lp);
			touchLayout3.setLayoutParams(lp);

			//touchLayout1.setBackgroundColor(Color.RED);
			//touchLayout2.setBackgroundColor(Color.RED);
			//touchLayout3.setBackgroundColor(Color.RED);
			// set color if you want layout visible on screen

			// fetch window manager object
			mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
			touchLayout1.setOnTouchListener(this);
			touchLayout2.setOnTouchListener(this);
			touchLayout3.setOnTouchListener(this);

			// set layout parameter of window manager
			WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(30,
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
					PixelFormat.TRANSPARENT);
			mParams.gravity = Gravity.START | Gravity.TOP;
			mWindowManager.addView(touchLayout1, mParams);
			mParams.gravity = Gravity.END | Gravity.TOP;
			mWindowManager.addView(touchLayout2, mParams);
			mParams.height = 30;
			mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
			mParams.gravity = Gravity.END | Gravity.TOP;
			mWindowManager.addView(touchLayout3, mParams);
		}catch(Exception e){

		}
	}

	@Override
	public void onDestroy() {
		try {
			if (mWindowManager != null) {
				if (touchLayout1 != null) mWindowManager.removeView(touchLayout1);
				if (touchLayout2 != null) mWindowManager.removeView(touchLayout2);
				if (touchLayout3 != null) mWindowManager.removeView(touchLayout3);
			}
		}catch(Exception e){

		}
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {


		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			jsonObject = new JSONObject();

			Log.i(TAG, "Action :" + event.getAction() + "\t X :" + event.getRawX() + "\t Y :" + event.getRawY());

			JSONObject s = new JSONObject();

			try {
                s.put("X",event.getRawX());
                s.put("Y",event.getRawX());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            try {
                jsonObject.put("ACTION_DOWN", s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

		}

		float pressure=event.getPressure();
		SharedPreferences.Editor editor = getSharedPreferences("pressuresend", MODE_PRIVATE).edit();
		editor.putString("pressure", String.valueOf(pressure));
		editor.apply();
		Log.d("TOuch presseure from gl",String.valueOf(pressure));


		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			Log.i(TAG, "Action :" + event.getAction() + "\t X :" + event.getRawX() + "\t Y :" + event.getRawY());
			JSONObject s = new JSONObject();
			editor.putString("pressure", String.valueOf(event.getPressure()));
			editor.apply();

			try {
				s.put("X",event.getRawX());
				s.put("Y",event.getRawX());
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}


            if(action_move == null)
            {
                action_move = new JSONArray();
                action_move.put(s);
            }
            else
            {
				action_move.put(s);
            }
		}

		if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			//Log.i(TAG, "Action :" + event.getAction() + "\t X :" + event.getRawX() + "\t Y :" + event.getRawY());
			if(action_outside_counter==0) {
				JSONObject s = new JSONObject();
				editor.putString("pressure", String.valueOf(event.getPressure()));

				try {
					s.put("X",event.getRawX());
					s.put("Y",event.getRawX());
					SharedPreferences preferences = this.getSharedPreferences("pressuresend", Context.MODE_PRIVATE);
					s.put("Finger Pressure",preferences.getString("pressure",null));
					jsonObject.put("ACTION_OUTSIDE",s);
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}

				DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
				Date date = new Date();
				String time = dateFormat.format(date);

				//DatabaseHelper db = new DatabaseHelper(GlobalTouchService.this);
				//int id = db.fatcheventsid();
				//db.inserevents_metagysture(id, "Gesture", jsonObject.toString(), time);

				SharedPreferences preferences = this.getSharedPreferences("prefName", Context.MODE_PRIVATE);
				int id1=preferences.getInt("fetchID",-1);
				Log.d("FetchIDFromGTS",Integer.toString(id1));

				if(id1 !=-1) {
					insert_ges(id1, "Gesture", jsonObject.toString());
				}

				Log.i(TAG, "outside value has been added to database");

			}
			action_outside_counter++;
			//Log.i("ActionCounter",Integer.toString(action_outside_counter));
			if(action_outside_counter>2)action_outside_counter=0;
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.i(TAG, "Action :" + event.getAction() + "\t X :" + event.getRawX() + "\t Y :" + event.getRawY());

			editor.putString("pressure", "0.0");

			try {

                jsonObject.put("ACTION_MOVE",action_move);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }


            JSONObject s = new JSONObject();

            try {
                s.put("X",event.getRawX());
                s.put("Y",event.getRawX());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }


				try {
					jsonObject.put("ACTION_UP", s);

				} catch (JSONException e1) {
                    e1.printStackTrace();
				}

			DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
			Date date = new Date();
			String time=dateFormat.format(date);

			DatabaseHelper db=new DatabaseHelper(GlobalTouchService.this);
			SharedPreferences preferences = this.getSharedPreferences("prefName", Context.MODE_PRIVATE);
			int id1=preferences.getInt("fetchID",-1);
			Log.d("FetchIDFromGTS",Integer.toString(id1));
			//int id=db.fatcheventsid();
			//db.inserevents_metagysture(id,"Gesture",jsonObject.toString(),time);
			if(id1 !=-1) {
                insert_ges(id1, "Gesture", jsonObject.toString());
            }

			Log.i("GlobalTouchToDB",jsonObject.toString());
			Log.i(TAG,"All Gesture has been added to database");

			action_move = null;
		}


		return true;
	}

	void insert_ges(int id,String type,String value){

		String method="gesture";
		String sid=Integer.toString(id);
		BackgroundTask backgroundTask=new BackgroundTask(this);
		backgroundTask.execute(method,sid,type,value);
	}


}

