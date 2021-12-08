package ru.ladgertha.loadingapplication

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.FileObserver
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import ru.ladgertha.loadingapplication.databinding.ActivityMainBinding
import java.io.File
import java.net.URL
import java.net.URLConnection


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var binding: ActivityMainBinding

    private lateinit var fileObserver: FileObserver

    private fun initFileObserver() {
        if (::fileObserver.isInitialized.not()) {
            val myUrl = URL(getUrl())
            val urlConnection: URLConnection = myUrl.openConnection()
            urlConnection.connect()
            val fileLength = urlConnection.contentLength
            fileObserver = DownloadsObserver(
                path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/downloadFile",
                loadingButton = binding.downloadButton,
                totalFileLength = fileLength
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.downloadButton.setOnClickListener {
            binding.downloadButton.setState(ButtonState.Clicked)
            getUrl()?.let { url ->
                Thread {
                    initFileObserver()
                    binding.downloadButton.setState(ButtonState.Loading)
                    fileObserver.startWatching()
                    download(url)
                }.start()
            } ?: kotlin.run {
                binding.downloadButton.setState(ButtonState.Completed)
            }
        }
    }

    private fun getUrl(): String? = when {
        binding.glideRadioButton.isChecked -> GLIDE_URL
        binding.retrofitRadioButton.isChecked -> RETROFIT_URL
        binding.udacityRadioButton.isChecked -> UDACITY_URL
        else -> null
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            fileObserver.stopWatching()
            if (id == downloadID) {
                binding.downloadButton.setState(ButtonState.Completed)

                //query status of download
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = if (columnIndex >= 0) {
                        cursor.getInt(columnIndex)
                    } else -1
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Log.d("ANYYYA", downloadManager.getUriForDownloadedFile(id).toString())
                            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT)
                                .show()
                        }
                        DownloadManager.STATUS_FAILED -> {
                            Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT)
                                .show()
                        }
                        DownloadManager.STATUS_PAUSED -> {
                            Toast.makeText(context, "PAUSED", Toast.LENGTH_SHORT)
                                .show()
                        }
                        DownloadManager.STATUS_PENDING -> {
                            Toast.makeText(context, "PENDING", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun download(url: String) {
        val file = File(Environment.getExternalStorageDirectory().path + "/downloadFile")
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationUri(Uri.fromFile(file))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        Log.d(
            "ANYYYA",
            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/downloadFile"
        )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}