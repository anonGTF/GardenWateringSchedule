package com.jessica.gardenwateringschedulesystem.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jessica.gardenwateringschedulesystem.utils.*
import java.util.*

class DailyCheck : BroadcastReceiver() {

    companion object {
        const val ID_REPEATING = 101
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onReceive(context: Context, intent: Intent) {
        executeThread {
            getDailySchedule(context)
        }
    }

    private fun getDailySchedule(context: Context) {
        val userId = auth.currentUser?.uid
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val ref = StringBuilder()
            .append(monthNumberToString(month))
            .append(" ")
            .append(year)
            .toString()
        val todayRef = getCurrentDateTime().toString("dd/MM/yyyy")

        if (userId != null) {
            Log.d("coba", "getDailySchedule: $todayRef")
            db.collection(SCHEDULES).document(userId).collection(ref)
                .whereEqualTo("tanggal", todayRef).get()
                .addOnSuccessListener { doc ->
                    doc.documents.forEach {
                        val jam = it.data?.get("jam").toString()
                        DailyReminder().setDailyReminder(context, jam)
                        Log.d("coba", "getDailySchedule: $jam")
                    }
                } .addOnFailureListener {
                    Log.d("coba", "getDailyScheduleOrNull: ${it.localizedMessage}")
                }
        }
    }

    fun checkDailySchedule(context: Context) {
        val time = "01:00".split(":").toTypedArray()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time[0].toInt())
            set(Calendar.MINUTE, time[1].toInt())
        }
        val intent = Intent(context, DailyReminder::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}