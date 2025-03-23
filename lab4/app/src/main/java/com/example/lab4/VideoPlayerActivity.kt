package com.example.lab4

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        videoView = findViewById(R.id.videoView)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)

        val uriString = intent.getStringExtra("uri")
        if (uriString == null) {
            Toast.makeText(this, "URI не передано", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val uri = Uri.parse(uriString)

        videoView.setVideoURI(uri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        playButton.setOnClickListener {
            if (!videoView.isPlaying) {
                videoView.start()
            }
        }

        pauseButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }

        stopButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.stopPlayback()
                videoView.setVideoURI(uri) // Перезавантаження відео
            }
        }
    }
}