package com.juanmacuevas.shoottheflakup;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundManager {

	private SoundPool sounds;
	private int shoot;
	private int explode;

	private int movegun;
	private boolean movegunPlaying ;
	private long movegunTimer;
	private MediaPlayer musicTheme;

	public SoundManager(Context context) {

		//sound = context.gets // should there be sound?
		sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		// three ref. to the sounds I need in the application
		shoot = sounds.load(context, R.raw.shoot, 1);
		explode = sounds.load(context, R.raw.explode, 1);
		movegun = sounds.load(context, R.raw.movegun, 1);
		movegunPlaying = false;
		// the music that is played at the beginning and when there is only 10 seconds left in a game
		musicTheme = MediaPlayer.create(context, R.raw.theme);


	}


	public final void playMusicTheme() {

		if (!musicTheme.isPlaying()) {
			musicTheme.seekTo(0);
			musicTheme.start();
		}
	}

	public final void pauseMusic() {

		if (musicTheme.isPlaying()) {
			musicTheme.stop();
		}
	}

	public void playShoot() {

		sounds.play(shoot, 0.3f, 0.3f, 1, 0, 1);
	}

	public void playExplode() {

		sounds.play(explode, 0.6f, 0.6f, 1, 0, 1);

	}

	public void playMovegun() {
		if (!movegunPlaying){
			sounds.play(movegun, 1, 1, 1, 0, 1);
			movegunPlaying = true;
			movegunTimer=0;
		}
	}

	public void update(long time){
		playMusicTheme();
		if (movegunPlaying){
			movegunTimer+=time;
			if (movegunTimer>302){
				movegunPlaying=false;
				movegunTimer=0;
			}

		}
	}

}
