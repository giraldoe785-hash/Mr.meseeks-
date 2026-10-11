package com.example.meeseekstaskmaster.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.meeseekstaskmaster.R

object AudioService {
    private const val TAG = "AudioService"
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false

    @Synchronized
    fun playAlarm(context: Context) {
        if (isPlaying) {
            return
        }

        try {
            stopAudio()

            mediaPlayer = MediaPlayer().apply {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setAudioAttributes(audioAttributes)

                val afd = context.resources.openRawResourceFd(R.raw.meeseeks_alarm)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()

                isLooping = true
                prepare()
                start()
                this@AudioService.isPlaying = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al reproducir audio de alarma Meeseeks: ${e.message}", e)
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        }
    }

    @Synchronized
    fun stopAudio() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                player.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al liberar MediaPlayer: ${e.message}", e)
        } finally {
            mediaPlayer = null
            isPlaying = false
        }
    }

    fun isAlarmPlaying(): Boolean = isPlaying
}
