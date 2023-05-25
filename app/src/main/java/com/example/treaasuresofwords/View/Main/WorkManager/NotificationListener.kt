package com.example.treaasuresofwords.View.Main.WorkManager

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.treaasuresofwords.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class NotificationListener(val context : Context, workerParams : WorkerParameters)
        : Worker(context,workerParams)
{
    private var PERMISSION_NOTIFICATİON = Manifest.permission.POST_NOTIFICATIONS
    var preferences = context.getSharedPreferences("notification",Context.MODE_PRIVATE)

    var CHANNEL_ID = "1"
    var CHANNEL_NAME = "Notification"
    var NOTIFICATON_ID = 1
    var a = 1

    init {
        createNotificationChannel()
        //sendNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        checkNotificationTime()
        sendNotification()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkNotificationTime(){

        val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")

        val localDate = LocalDateTime.now()
        val instant = localDate.atZone(ZoneId.systemDefault()).toInstant()
        val currentDate = Date.from(instant)
        val notificationTimeString = preferences.getString("usedDate",null)

        if(notificationTimeString != null){
            val noticationDate = formatter.parse(notificationTimeString)
            val diff  = (( currentDate.time - noticationDate.time ) / 1000 / 60 ).toInt() // convert to hour
            Log.w("ARABAM2","SECOND = $diff")
            if(diff >= 15){
            }
        }
    }

    fun createNotificationChannel(){

        val title = "Notification Title"
        var descriptionText = "Notification Description"
        var importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var channel = NotificationChannel(CHANNEL_ID,title,importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }


    }

    fun sendNotification(){

        val channelId = "my_channel_id"
        val title = context.getString(R.string.notification_title)
        val description = context.getString(R.string.notification_content)

        val notificationManager = getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        notificationManager.notify(0, notification)

    }

    fun sendNotification2(){

        val title = context.getString(R.string.notification_title)
        val description = context.getString(R.string.notification_content)

        val builder = NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if(ContextCompat.checkSelfPermission(context,PERMISSION_NOTIFICATİON) == PackageManager.PERMISSION_GRANTED) {
            // permission granted
            with(NotificationManagerCompat.from(context)){
                notify(NOTIFICATON_ID,builder.build())
            }
        }


    }


}