package com.juanmacuevas.shoottheflakup;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundManager {

	private static SoundPool sounds;
	private static int shoot;
	private static int explode;

	private static int movegun;
	private static boolean movegunPlaying ;
	private static long movegunTimer;
	private static MediaPlayer musicTheme;
	private static int theme;

	public static void loadSound(Context context) {

		//sound = context.gets // should there be sound?
		sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		// three ref. to the sounds I need in the application
		shoot = sounds.load(context, R.raw.shoot, 1);
		explode = sounds.load(context, R.raw.explode, 1);
		//theme = sounds.load(context, R.raw.theme, 1);
		movegun = sounds.load(context, R.raw.movegun, 1);
		movegunPlaying = false;
		// the music that is played at the beginning and when there is only 10 seconds left in a game
		musicTheme = MediaPlayer.create(context, R.raw.theme);


	}


	public static final void playMusicTheme() {

		if (!musicTheme.isPlaying()) {
			musicTheme.seekTo(0);
			musicTheme.start();
		}
	}

	public static final void pauseMusic() {

		if (musicTheme.isPlaying()) musicTheme.pause();
	}

	public static void playShoot() {

		sounds.play(shoot, 1, 1, 1, 0, 1);
	}

	public static void playExplode() {

		sounds.play(explode, 1, 1, 1, 0, 1);

	}

	public static void playMovegun() {
		if (!movegunPlaying){
			sounds.play(movegun, 1, 1, 1, 0, 1);
			movegunPlaying = true;
			movegunTimer=0;
		}





	}

	public static void update(long time){
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
