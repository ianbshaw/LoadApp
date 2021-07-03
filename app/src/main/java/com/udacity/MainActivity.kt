package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.sendNotification

class MainActivity : AppCompatActivity() {

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide.git"
        private const val LOADAPP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter.git"
        private const val RETROFIT_URL = "https://github.com/square/retrofit.git"
    }


    private lateinit var binding: ActivityMainBinding

    private lateinit var notificationManager: NotificationManager
    private var urlSelected = ""
    private var repoSelected = ""

    private var downloadID: Long = 0

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val action = intent?.action

            if (id == downloadID) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(id)
                    val manager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor = manager.query(query)

                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                notificationManager.sendNotification(
                                    repoSelected,
                                    context,
                                    "Download successful")
                            } else {
                                notificationManager.sendNotification(
                                    repoSelected,
                                    context,
                                    "Download failed")
                            }
                            binding.download.setState(ButtonState.Completed)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        notificationManager = ContextCompat.getSystemService(
            this, NotificationManager::class.java
        ) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.download.setOnClickListener {
            if (urlSelected != "") {
                download(urlSelected)
            } else {
                Toast.makeText(this, "Please select the file to download", Toast.LENGTH_LONG)
                    .show()
            }
        }

        createChannel(
            getString(R.string.download_channel_id),
            getString(R.string.download_notification_channel_name))
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {

            val checked = view.isChecked

            when (view.getId()) {
                R.id.glide->
                    if (checked) {
                        urlSelected = GLIDE_URL
                        repoSelected = "Glide - Image Loading Library by BumpTech"
                    }
                R.id.loadapp ->
                    if (checked) {
                        urlSelected = LOADAPP_URL
                        repoSelected = "LoadApp - Current repository by Udacity"
                    }
                R.id.retrofit ->
                    if (checked) {
                        urlSelected = RETROFIT_URL
                        repoSelected = "Retrofit - Type-safe HTTP client for Android and Java by Square, Inc"
                    }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "You download was successful!"

            notificationManager.createNotificationChannel(notificationChannel)
        } else {
            Toast.makeText(this, "VERSION.SDK_INT < O", Toast.LENGTH_SHORT).show()
        }
    }

    private fun download(selectedURL: String) {
        binding.download.setState(ButtonState.Loading)

        val request =
            DownloadManager.Request(Uri.parse(selectedURL))
                .setDescription("Downloading from internet")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

}