package com.juanmacuevas;
import java.util.concurrent.ArrayBlockingQueue;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class InputObject {
	public static final byte EVENT_TYPE_KEY = 1;
	public static final byte EVENT_TYPE_TOUCH = 2;
	public static final int ACTION_KEY_DOWN = 1;
	public static final int ACTION_KEY_UP = 2;
	public static final int ACTION_TOUCH_DOWN = 3;
	public static final int ACTION_TOUCH_MOVE = 4;
	public static final int ACTION_TOUCH_UP = 5;
	public ArrayBlockingQueue<InputObject> pool;
	public byte eventType;
	public long time;
	public int action;
	public int keyCode;
	public int x;
	public int y;

	public InputObject(ArrayBlockingQueue<InputObject> pool) {
		this.pool = pool;
	}

	public void useEvent(KeyEvent event) {
		eventType = EVENT_TYPE_KEY;
		int a = event.getAction();
		switch (a) {
		case KeyEvent.ACTION_DOWN:
			action = ACTION_KEY_DOWN;
			break;
		case KeyEvent.ACTION_UP:
			action = ACTION_KEY_UP;
			break;
		default:
			action = 0;
		}
		time = event.getEventTime();
		keyCode = event.getKeyCode();
	}

	public void useEvent(MotionEvent event) {
		eventType = EVENT_TYPE_TOUCH;
		int a = event.getAction();
		switch (a) {
		case MotionEvent.ACTION_DOWN:
			action = ACTION_TOUCH_DOWN;
			break;
		case MotionEvent.ACTION_MOVE:
			action = ACTION_TOUCH_MOVE;
			break;
		case MotionEvent.ACTION_UP:
			action = ACTION_TOUCH_UP;
			break;
		default:
			action = 0;
		}
		time = event.getEventTime();
		x = (int) event.getX() ;
		//Log.i("motion", "x: "+Float.toString(event.getRawX())+" y: "+ Float.toString(event.getRawY()) );
		y = (int) event.getY();
	}

	public void useEventHistory(MotionEvent event, int historyItem) {
		eventType = EVENT_TYPE_TOUCH;
		action = ACTION_TOUCH_MOVE;
		time = event.getHistoricalEventTime(historyItem);
		x = (int) event.getHistoricalX(historyItem);
		y = (int) event.getHistoricalY(historyItem);
	}

	public void returnToPool() {
		pool.add(this);
	}
}