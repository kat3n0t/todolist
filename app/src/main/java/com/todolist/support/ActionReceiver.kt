package com.todolist.support

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.todolist.database.DBManager

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra("noteId", 0)
        if (noteId != 0) {
            val dbManager = DBManager(context)
            val note = dbManager.notesModel.getNote(noteId)
            if (note != null) {
                dbManager.notesModel.changeCompleted(note, true)
            }
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(noteId) // id уведомления = id заметки
    }
}