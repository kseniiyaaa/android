package com.example.lab4

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var timerText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        seekBar = findViewById(R.id.seekBar)
        timerText = findViewById(R.id.timerText)

        val uriString = intent.getStringExtra("uri")
        if (uriString == null) {
            Toast.makeText(this, "URI не передано", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val uri = Uri.parse(uriString)
        Log.d("AudioPlayerActivity", "Received URI: $uriString")

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AudioPlayerActivity, uri)
                prepare()
            }
            setupSeekBar()
        } catch (e: Exception) {
            Log.e("AudioPlayerActivity", "Error preparing MediaPlayer: ${e.message}")
            Toast.makeText(this, "Помилка відтворення: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        playButton.setOnClickListener {
            if (!isPlaying) {
                mediaPlayer.start()
                isPlaying = true
                updateSeekBar()
                Log.d("AudioPlayerActivity", "Playback started")
            }
        }

        pauseButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                isPlaying = false
                handler.removeCallbacksAndMessages(null)
                Log.d("AudioPlayerActivity", "Playback paused")
            }
        }

        stopButton.setOnClickListener {
            if (isPlaying || mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                isPlaying = false
                handler.removeCallbacksAndMessages(null)
                mediaPlayer.reset()
                try {
                    mediaPlayer.setDataSource(this@AudioPlayerActivity, uri)
                    mediaPlayer.prepare()
                } catch (e: Exception) {
                    Log.e("AudioPlayerActivity", "Error resetting MediaPlayer: ${e.message}")
                }
                seekBar.progress = 0
                timerText.text = "00:00"
                Log.d("AudioPlayerActivity", "Playback stopped")
            }
        }
    }

    private fun setupSeekBar() {
        seekBar.max = mediaPlayer.duration
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
                updateTimer(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateSeekBar() {
        seekBar.progress = mediaPlayer.currentPosition
        if (isPlaying) {
            handler.postDelayed({
                updateSeekBar()
            }, 1000)
        }
    }

    private fun updateTimer(progress: Int) {
        val minutes = (progress / 1000) / 60
        val seconds = (progress / 1000) % 60
        timerText.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        handler.removeCallbacksAndMessages(null)
    }
}