package com.todolist.mvp.presenters

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.todolist.R
import com.todolist.database.DBManager
import com.todolist.interfaces.IEditNotePresenter
import com.todolist.interfaces.INoteView
import com.todolist.mvp.views.NoteActivity
import com.todolist.support.NotificationWorker
import java.util.concurrent.TimeUnit

class EditNotePresenter(private var activity: NoteActivity) : IEditNotePresenter {

    private val dbManager = DBManager(activity)

    override fun onSaveClick() {
        val noteName = activity.getNoteText()
        val noteId = activity.getEditingNoteId()
        if ((noteId != null) and (noteName != "")) {
            val note = dbManager.notesModel.getNote(noteId!!)
            if (note != null) {
                dbManager.notesModel.renameNote(note, noteName)
                activity.finish()
            }
        }
    }

    override fun onDeleteClick() {
        val noteId = activity.getEditingNoteId()
        if (noteId != null) {
            val note = dbManager.notesModel.getNote(noteId)
            if (note != null) {
                dbManager.notesModel.removeNote(note)
                activity.finish()
            }
        }
    }

    override fun onNotificationClick(isNotificationOn: Boolean) {
        val noteId = activity.getEditingNoteId()
        if (noteId != null) {
            val note = dbManager.notesModel.getNote(noteId)
            if (note != null) {
                val data = Data.Builder()
                data.putInt("note_id", note.id)

                val uploadWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(10, TimeUnit.SECONDS)
                    .setInputData(data.build())
                    .build()
                if (isNotificationOn) {
                    WorkManager.getInstance(activity ).enqueue(uploadWorkRequest)
                    activity.setImageButtonType(R.drawable.ic_notifications_on)
                } else {
                    WorkManager.getInstance(activity).cancelWorkById(uploadWorkRequest.id)
                    activity.setImageButtonType(R.drawable.ic_notifications_off)
                }
            }
        }
    }

    override fun onStart() {
        activity.setTitle(R.string.note)
        setView(activity)
    }

    private fun setView(activity: INoteView) {

        fun setTextNote(noteId: Int) {
            val note = dbManager.notesModel.getNote(noteId)
            if (note != null)
                activity.setText(note.name)
        }

        fun setNotificationButtonImage(noteId: Int) {
            val note = dbManager.notesModel.getNote(noteId)
            if (note != null) {
                if (note.haveNotification)
                    activity.setImageButtonType(R.drawable.ic_notifications_on)
                else
                    activity.setImageButtonType(R.drawable.ic_notifications_off)
            } else activity.setImageButtonType(R.drawable.ic_notifications_off)
        }

        activity.showDeleteButton(true)

        val noteId = activity.getEditingNoteId()
        if (noteId != null) {
            setTextNote(noteId)
            setNotificationButtonImage(noteId)
        }
    }

    override fun onDestroy() {

    }
}