package com.jessica.gardenwateringschedulesystem.background

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.jessica.gardenwateringschedulesystem.R
import com.jessica.gardenwateringschedulesystem.ui.MainActivity
import com.jessica.gardenwateringschedulesystem.utils.executeThread
import java.util.*

class DailyReminder : BroadcastReceiver() {
    companion object {
        const val TIME_EXTRA = "time"
        const val ID_REMINDER = 102
        const val CHANNEL_ID = "notify-schedule"
        const val CHANNEL_NAME = "garden watering schedule"
        const val NOTIFICATION_ID = 103
    }

    override fun onReceive(context: Context, intent: Intent) {
        executeThread {
            val time = intent.getStringExtra(TIME_EXTRA)
            if (time != null) {
                showNotification(context, time)
                Log.d("coba", "onReceive: notif triggered")
            }
            Log.d("coba", "onReceive: triggered")
        }
    }

    fun setDailyReminder(context: Context, time: String) {
        val timeArr = time.split(":").toTypedArray()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timeArr[0].toInt())
            set(Calendar.MINUTE, timeArr[1].toInt())
        }
        val intent = Intent(context, DailyReminder::class.java)
        intent.putExtra(TIME_EXTRA, time)
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REMINDER, intent, 0)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun getPendingIntent(context: Context, time: String): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(TIME_EXTRA, time)
        }
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun showNotification(context: Context, time: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Saatnya menyiram taman")
            .setContentText("Pekerjaan mulai : $time")
            .setContentIntent(getPendingIntent(context, time))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}