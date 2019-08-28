package com.juanmacuevas.shoottheflakup;
import java.util.concurrent.ArrayBlockingQueue;

import android.view.KeyEvent;
import android.view.MotionEvent;

public class InputObject {
	static final byte EVENT_TYPE_KEY = 1;
	static final byte EVENT_TYPE_TOUCH = 2;
	static final int ACTION_KEY_DOWN = 1;
	static final int ACTION_KEY_UP = 2;
	static final int ACTION_TOUCH_DOWN = 3;
	static final int ACTION_TOUCH_MOVE = 4;
	static final int ACTION_TOUCH_UP = 5;
	ArrayBlockingQueue<InputObject> pool;
	byte eventType;
	long time;
	int action;
	int keyCode;
	int x;
	int y;

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