package com.todolist.support

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.todolist.database.DBManager

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val noteId =  inputData.getInt("note_id", 0)
        val note = DBManager(applicationContext).notesModel.getNote(noteId)
        return if (note != null) {
            val serviceIntent =
                Intent(applicationContext, NotificationService::class.java)
            serviceIntent.putExtra("noteId", note.id)
            serviceIntent.putExtra("noteName", note.name)
            applicationContext.startService(serviceIntent)

            Result.success()
        } else
            Result.failure()
    }
}