package com.example.ballance

import android.content.Context
import android.media.MediaPlayer
import com.example.ballance.R

object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
        private set

    fun toggle(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.music)
            mediaPlayer?.isLooping = true
        }

        if (isPlaying) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }

        isPlaying = !isPlaying
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }
}
