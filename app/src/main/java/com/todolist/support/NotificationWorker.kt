package com.todolist.support

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.todolist.database.DBManager

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val dbManager = DBManager(applicationContext)

        val note = dbManager.notesModel.getNote(inputData.getInt("note_id", 0))
        return if (note != null) {
            val serviceIntent =
                Intent(applicationContext, NotificationService::class.java)
            serviceIntent.putExtra("noteId", note.id)
            serviceIntent.putExtra("noteName", note.name)
            applicationContext.startService(serviceIntent)

            dbManager.notesModel.removeWorkersGuid(note) // удаление guid по завершению воркера

            Result.success()
        } else
            Result.failure()
    }
}