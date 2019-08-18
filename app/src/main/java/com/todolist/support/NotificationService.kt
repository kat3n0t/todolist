package com.todolist.support

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.IBinder
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import com.todolist.R
import com.todolist.mvp.views.MainActivity


private const val CHANNEL_ID = "1"

class NotificationService : Service() {

    private lateinit var preferences: SharedPreferences
    private lateinit var notificationManager: NotificationManager

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (preferences.getBoolean("haveNotificationChannel", false))
            createNotificationChannel()

        val noteId = intent.getIntExtra("noteId", 0)
        val noteName = intent.getStringExtra("noteName")
        Handler().postDelayed({
            startNotification(noteId, noteName)
        }, 5 * 1000)

        return START_STICKY
    }

    private fun startNotification(noteId: Int, noteName: String) {
        val resultIntent = Intent(this, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT)

        val deleteIntent = Intent(this, ActionReceiver::class.java)
        deleteIntent.putExtra("noteId", noteId)
        val deletePendingIntent =
            PendingIntent.getBroadcast(
                this,
                0,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.note))
            .setContentText(noteName.getSlicedText(20))
            .setAutoCancel(true) // автозакрытие при тапе по уведомлению
            .setContentIntent(resultPendingIntent)
            .addAction(
                R.drawable.ic_action_add_note,
                getString(R.string.complete_execute),
                deletePendingIntent
            )

        val notification = builder.build()
        notificationManager.notify(noteId, notification)
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Notification's Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        val editor = preferences.edit()
        editor.putBoolean("haveNotificationChannel", true) // указать, что канал создан или не нужен
        editor.apply()
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