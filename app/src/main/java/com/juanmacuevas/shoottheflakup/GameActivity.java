package com.juanmacuevas.shoottheflakup;

import java.util.concurrent.ArrayBlockingQueue;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;

public class GameActivity extends Activity {

	public static final int INPUT_QUEUE_SIZE = 30;
	/** A handle for the thread running the game main loop */
	private GameThread mGameThread;
	/**A handle to the View containing the game*/
	private GameView mGameView;

	public final DisplayMetrics metrics = new DisplayMetrics();
	public final ArrayBlockingQueue<InputObject> inputObjectPool = new ArrayBlockingQueue<>(INPUT_QUEUE_SIZE);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// turn off the window's title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// tell system to use the layout defined in our XML file
		setContentView(R.layout.main);

		// get handles to the GameView from XML, and the GameThread
		mGameView = (GameView) findViewById(R.id.game);

		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		
		mGameThread = mGameView.getThread();
		mGameThread.setDisplayMetrics(metrics);

		createInputObjectPool();
		Log.i("metrics","density:"+ metrics.density+" DensityDPI:"+metrics.densityDpi+
				" heightPix:"+metrics.heightPixels+" WidthPix:"+metrics.widthPixels+
				" xReal/inch"+metrics.xdpi+" yReal/inch"+metrics.ydpi);
	}

	private void createInputObjectPool() {
		for (int i = 0; i < INPUT_QUEUE_SIZE; i++) {
			inputObjectPool.add(new InputObject(inputObjectPool));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// we only care about down actions in this game.
		try {
			// history first
			int hist = event.getHistorySize();
			if (hist > 0) {
				// add from oldest to newest
				for (int i = 0; i < hist; i++) {
					InputObject input = inputObjectPool.take();
					input.useEventHistory(event, i);
					mGameThread.feedInput(input);
				}
			}
			// current last
			InputObject input = inputObjectPool.take();
			input.useEvent(event);
			mGameThread.feedInput(input);
		} catch (InterruptedException e) {
		}
		// don't allow more than 60 motion events per second
		try {
			Thread.sleep(16);
		} catch (InterruptedException e){
		}
		return true;
	}


	/**
	 * Invoked when the Activity loses user focus.
	 */
	@Override
	protected void onPause() {    	
		super.onPause();
		SoundManager.pauseMusic();
		mGameView.getThread().pause(); // pause game when Activity pauses

	}





}

