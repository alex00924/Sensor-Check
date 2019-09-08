package com.lordsofts.sensortest;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Fragment which shows the keyboard and sends the pressed key, the position and the duration of the press
 * and the duration between two presses to the database. The transfer to the database is done after each press.
 */
public class KeyboardFragment extends Fragment implements KeyboardView.OnKeyboardActionListener, View.OnTouchListener {

    //The keyboard view
    Keyboard keyboard;
    //The whole view
    KeyboardView keyboardView;
    //The textview
    TextView textView;
    //The logging view
    TextView textViewLog;
    Context context;

    //variables, which hold the data for using in keypress or keytouch (which one is later)
    private long lastPress = 0;
    private long pressDiff = 0;
    private long presstime = 0;
    float posX;
    float posY;
    int keyPressed = ' ';

    //the state, which controlls, if keypress or keytouch manages the event
    int eventState = 0;

    //capslock?
    private boolean caps = false;

    /**
     * The onCreate of the view
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * OnCreateView of the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflating the view
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);

        //setting the elements on the view
        textView = (TextView) view.findViewById(R.id.keyboard_fragment_textview);
        textViewLog = (TextView) view.findViewById(R.id.keyboard_fragment_textview_log);
        keyboardView = (KeyboardView) view.findViewById(R.id.keyboard_view);

        //creating the keyboard and setting the listeners
        keyboard = new Keyboard(view.getContext(),R.xml.keys_layout);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setOnTouchListener(this);
        keyboardView.setPreviewEnabled(false);
        lastPress = Calendar.getInstance().getTimeInMillis();

        return view;
    }

    /**
     * onAttach of the fragment
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * onDetach of the fragment
     */
    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    /**
     * onPress of the keyboard
     * @param i
     */
    @Override
    public void onPress(int i) {

    }

    /**
     * onRelease of the keyboard
     * @param i
     */
    @Override
    public void onRelease(int i) {

    }

    /**
     * onKey of the keyboard. Used for detecting the presses key
     * @param primaryCode
     * @param keyCodes
     */
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        keyPressed = primaryCode;

        eventState++;
        //both finished the event
        if(eventState > 1)
        {
            eventState = 0;
            handleDataAfterBothEvents();
        }
    }

    /**
     * handling the collected data after both events
     */
    private void handleDataAfterBothEvents()
    {
        String key;
        switch (keyPressed) {
            case Keyboard.KEYCODE_DELETE:
                String text = String.valueOf(textView.getText());

                if (text.length() > 0) {
                    textView.setText(text.substring(0, text.length() - 1));
                }
                key = "Delete";
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                keyboardView.invalidateAllKeys();
                key = "Caps";
                break;
            case Keyboard.KEYCODE_DONE:
                textView.setText(textView.getText() + String.valueOf("\n"));
                key = "Enter";
                break;
            default:
                char code = (char) keyPressed;
                if (code == 32)
                {
                    key = "Space";
                }
                else
                {
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code);
                    }

                    key = String.valueOf(code);
                }
                textView.setText(textView.getText() + String.valueOf(code));
                break;
        }

        //send data to db
        saveDataset(key, presstime, pressDiff, posX, posY);
        //write in logwindow
        String logText = "Key: " + key + "; Pos: " + Math.round(posX) + ", " + Math.round(posY) + "\nDuration: " + presstime + "; Diff: " + pressDiff + "\n\n";
        logLine(logText);
    }


    /**
     * writing in the log window and delete lines, if more than 40
     * @param data The data, which will be printed
     */
    private void logLine(String data) {
        textViewLog.append(data);
        // Erase excessive lines
        int excessLineNumber = textViewLog.getLineCount() - 40;
        if (excessLineNumber > 0) {
            int eolIndex = -1;
            CharSequence charSequence = textViewLog.getText();
            for (int i = 0; i < excessLineNumber; i++) {
                do {
                    eolIndex++;
                }
                while (eolIndex < charSequence.length() && charSequence.charAt(eolIndex) != '\n');
            }
            if (eolIndex < charSequence.length()) {
                textViewLog.getEditableText().delete(0, eolIndex + 1);
            } else {
                textViewLog.setText("");
            }
        }
    }

    /**
     * Creating the a JSONObject with the data and putting it in the database. According to
     * the other Fragments in this app
     * @param key the pressed key as string
     * @param pressTime the duration of the touchevent
     * @param pressDiff the duration between two keys
     * @param posX the x-position
     * @param posY the y-position
     */
    private void saveDataset(String key, float pressTime, float pressDiff, float posX, float posY)
    {
        if(context != null) {
            SharedPreferences preferences = context.getSharedPreferences("prefName", Context.MODE_PRIVATE);
            int id1 = preferences.getInt("fetchID", -1);
            if(id1 != -1) {

                //create the Object to send
                JSONObject keyboardObject = new JSONObject();
                try {
                    keyboardObject.put("key", key);
                    keyboardObject.put("pressTime", Float.toString(pressTime));
                    keyboardObject.put("pressDiff", Float.toString(pressDiff));
                    keyboardObject.put("posX", Float.toString(posX));
                    keyboardObject.put("posY", Float.toString(posY));
                } catch (JSONException e) {
                    Log.d(TAG, "JSONexception" + e);
                }
                insert_keyBoard(id1, "keyboard", keyboardObject.toString());
            }
        }
    }

    /**
     * Inserting into database and starting the background task for sending. According to other Fragments.
     * @param id The id
     * @param type The type ("keyboard")
     * @param value The value - the json-string
     */
    void insert_keyBoard(int id,String type,String value){

        String method="keyboard";
        String sid=Integer.toString(id);
        BackgroundTask backgroundTask =new BackgroundTask(context);
        backgroundTask.setMainActivityDash();
        backgroundTask.execute(method,sid,type,value);
    }

    /**
     *
     * @param charSequence
     */
    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    /**
     * The onTouch-event. It collects the times and position. As ACTION_DOWN will be always the first (I hope)
     * it resets the eventState. If eventState is 2, the touch and the keypress are collected.
     * @param view
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            //reset the eventState
            eventState = 0;

            //calculating the time between up and down
            pressDiff = Calendar.getInstance().getTimeInMillis() - lastPress;
        }
        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //collecting the values
            lastPress = Calendar.getInstance().getTimeInMillis();

            presstime = motionEvent.getEventTime() - motionEvent.getDownTime();

            posX = motionEvent.getX();
            posY = view.getHeight() - motionEvent.getY();

            //find out, if both events are finished
            eventState++;
            //both finished the event
            if(eventState > 1)
            {
                eventState = 0;
                handleDataAfterBothEvents();
            }
        }

        return false;
    }
}
