@file:Suppress("DEPRECATION")

package com.example.lab4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE = 101

    private val pickAudioLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("uri", it.toString())
            startActivity(intent)
        }
    }

    private val pickVideoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("uri", it.toString())
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        val playLocalAudioButton: Button = findViewById(R.id.playLocalAudioButton)
        playLocalAudioButton.setOnClickListener {
            pickAudioLauncher.launch("audio/*")
        }

        val playLocalVideoButton: Button = findViewById(R.id.playLocalVideoButton)
        playLocalVideoButton.setOnClickListener {
            pickVideoLauncher.launch("video/*")
        }

        val downloadMediaButton: Button = findViewById(R.id.downloadMediaButton)
        downloadMediaButton.setOnClickListener {
            val intent = Intent(this, DownloadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Дозвіл надано", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Дозвіл відхилено", Toast.LENGTH_SHORT).show()
            }
        }
    }
}