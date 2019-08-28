package com.juanmacuevas.shoottheflakup

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool

class SoundManager(context: Context) {

    private val sounds = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
    private val shoot = sounds.load(context, R.raw.shoot, 1)
    private val explode = sounds.load(context, R.raw.explode, 1)
    private val movegun = sounds.load(context, R.raw.movegun, 1)

    private var movegunPlaying = false
    private var movegunTimer = 0L
    private val musicTheme = MediaPlayer.create(context, R.raw.theme)

    fun update(time: Long) {
        playMusicTheme()
        if (movegunPlaying) {
            movegunTimer += time
            if (movegunTimer > 302) {
                movegunPlaying = false
                movegunTimer = 0
            }

        }
    }

    fun playMusicTheme() {
        if (!musicTheme.isPlaying) {
            musicTheme.seekTo(0)
            musicTheme.start()
        }
    }

    fun pauseMusic() {
        if (musicTheme.isPlaying) {
            musicTheme.stop()
        }
    }

    fun playShoot() {
        play(shoot, 0.3f)
    }
    fun playExplode() {
        play(explode, 0.6f)

    }

    fun playMovegun() {
        if (!movegunPlaying) {
            play(movegun, 0.6f)
            movegunPlaying = true
            movegunTimer = 0
        }
    }

    private fun play(res:Int, vol:Float){
        sounds.play(res,vol,vol,1,0,1f)

    }

}
