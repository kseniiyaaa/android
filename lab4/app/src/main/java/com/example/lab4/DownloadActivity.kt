package com.example.lab4

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File

class DownloadActivity : AppCompatActivity() {
    private lateinit var urlEditText: EditText
    private lateinit var downloadButton: Button
    private lateinit var audioRadioButton: RadioButton
    private lateinit var videoRadioButton: RadioButton
    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        urlEditText = findViewById(R.id.urlEditText)
        downloadButton = findViewById(R.id.downloadButton)
        audioRadioButton = findViewById(R.id.audioRadioButton)
        videoRadioButton = findViewById(R.id.videoRadioButton)

        audioRadioButton.isChecked = true

        downloadButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (url.isNotEmpty()) {
                Log.d("DownloadActivity", "Starting download for URL: $url")
                downloadFile(url)
            } else {
                Toast.makeText(this, "Будь ласка, введіть URL", Toast.LENGTH_SHORT).show()
            }
        }

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        ContextCompat.registerReceiver(
            this,
            onDownloadComplete,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    private fun downloadFile(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Завантаження медіа")
            .setDescription("Завантаження файлу...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "downloaded_media.mp4")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        request.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
        Log.d("DownloadActivity", "Download started with ID: $downloadID")
    }

    private val onDownloadComplete = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                Log.d("DownloadActivity", "Download completed for ID: $id")
                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query().setFilterById(downloadID)
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(columnIndex)

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        val uri = Uri.parse(uriString)
                        Log.d("DownloadActivity", "Downloaded URI: $uriString")

                        val file = File(uri.path ?: "")
                        if (file.exists()) {
                            val intent = if (audioRadioButton.isChecked) {
                                Intent(this@DownloadActivity, AudioPlayerActivity::class.java)
                            } else {
                                Intent(this@DownloadActivity, VideoPlayerActivity::class.java)
                            }
                            intent.putExtra("uri", uri.toString())
                            startActivity(intent)
                        } else {
                            Toast.makeText(context, "Файл не знайдено: $uriString", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                        val reason = cursor.getInt(reasonIndex)
                        Log.e("DownloadActivity", "Download failed with status: $status, reason: $reason")
                        Toast.makeText(context, "Завантаження не вдалося. Причина: $reason", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("DownloadActivity", "Cursor is empty, no download info found")
                    Toast.makeText(context, "Завантаження не вдалося: інформація відсутня", Toast.LENGTH_LONG).show()
                }
                cursor.close()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }
}