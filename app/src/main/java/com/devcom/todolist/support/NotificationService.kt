package com.devcom.todolist.support

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.devcom.todolist.R
import com.devcom.todolist.mvp.views.MainActivity

private const val CHANNEL_ID = "1"
private const val CHANNEL_NAME = "Notification's Channel"

class NotificationService : Service() {

    private lateinit var preferences: SharedPreferences
    private lateinit var notificationManager: NotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (!preferences.getBoolean("haveNotificationChannel", false))
            createNotificationChannel(notificationManager)

        return if (intent != null) {
            val noteId = intent.getIntExtra("noteId", 0)
            val noteName = intent.getStringExtra("noteName")
            val notification = getNotification(noteId, noteName)
            notificationManager.notify(noteId, notification)

            START_STICKY
        } else
            START_NOT_STICKY
    }

    private fun getNotification(noteId: Int, noteName: String): Notification {
        val resultIntent = Intent(this, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this).apply {
            addNextIntent(resultIntent)
        }
        val resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val deleteIntent = Intent(this, ActionReceiver::class.java)
        deleteIntent.putExtra("noteId", noteId)
        val deletePendingIntent =
            PendingIntent.getBroadcast(
                this,
                0,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(getString(R.string.note))
            setContentText(noteName.getSlicedText(20))
            setAutoCancel(true) // автозакрытие при тапе по уведомлению
            setContentIntent(resultPendingIntent)
            setCategory(NotificationCompat.CATEGORY_REMINDER)
            addAction(
                R.drawable.ic_note_add_primary_24dp,
                getString(R.string.complete_execute),
                deletePendingIntent
            )
        }.build()
    }

    private fun createNotificationChannel(manager: NotificationManager) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                manager.createNotificationChannel(channel)
            }
            val editor = preferences.edit()
            editor.putBoolean(
                "haveNotificationChannel",
                true
            ) // указать, что канал создан или не нужен
            editor.apply()
        } catch (e: Exception) {
            Log.e("Notification's Error", "Channel not created")
        }
    }

    private fun String.getSlicedText(size: Int): String {
        var noteText = this
        if (noteText.count() > size) noteText = "${noteText.take(size)}..."
        return noteText // возвращает первые 20 символов заметки
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}