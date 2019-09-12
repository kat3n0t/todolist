package com.devcom.todolist.mvp.presenters

import androidx.lifecycle.Observer
import androidx.work.*
import com.devcom.todolist.R
import com.devcom.todolist.database.DBManager
import com.devcom.todolist.interfaces.IEditNotePresenter
import com.devcom.todolist.interfaces.INoteView
import com.devcom.todolist.mvp.views.NoteActivity
import com.devcom.todolist.support.Note
import com.devcom.todolist.support.NotificationWorker
import java.util.*
import java.util.concurrent.TimeUnit

class EditNotePresenter(private var activity: NoteActivity) : IEditNotePresenter {

    private val dbManager = DBManager(activity)

    override fun onStart() {
        activity.setTitle(R.string.note)
        setView(activity)
    }

    private fun setView(activity: INoteView) {

        fun setTextNote(note: Note) {
            activity.setText(note.name)
        }

        fun setNotification(note: Note) {
            val guid = dbManager.notesModel.getWorkersGuid(note.id)
            if (guid != null) {
                activity.setImageButtonType(R.drawable.ic_notifications_on)
                setWorkManagerLiveData(WorkManager.getInstance(this.activity), guid)
            } else
                activity.setImageButtonType(R.drawable.ic_notifications_off)
        }

        activity.showDeleteButton(true)

        val note = activity.getEditingNoteId()?.let {
            dbManager.notesModel.getNote(it)
        }
        if (note != null) {
            setTextNote(note)
            setNotification(note)
        }
    }

    private fun setWorkManagerLiveData(workManager: WorkManager, guidWorker: UUID) {
        workManager.getWorkInfoByIdLiveData(guidWorker)
            .observe(activity, Observer { workInfo ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> activity.setImageButtonType(R.drawable.ic_notifications_on)
                        else -> activity.setImageButtonType(R.drawable.ic_notifications_off)
                    }
                }
            })
    }

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
        val note = activity.getEditingNoteId()?.let {
            dbManager.notesModel.getNote(it)
        }
        if (note != null) {
            dbManager.notesModel.removeNote(note)
            activity.finish()
        }
    }

    override fun onNotificationClick(isNotificationOn: Boolean) {
        val note = activity.getEditingNoteId()?.let {
            dbManager.notesModel.getNote(it)
        }
        if (note != null) {
            if (isNotificationOn) {
                // создание воркера, добавление в бд его guid
                val data = Data.Builder()
                data.putInt("note_id", note.id)

                val oneTimeWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(10, TimeUnit.MINUTES)
                    .setInputData(data.build())
                    .build()
                dbManager.notesModel.addWorkersGuid(note.id, oneTimeWorkRequest.id)
                setWorkManager(oneTimeWorkRequest, note)
            } else {
                // остановка запущенного воркера и удаление guid из бд
                dbManager.notesModel.getWorkersGuid(note.id)?.let {
                    WorkManager.getInstance(activity)
                        .cancelWorkById(it)
                    dbManager.notesModel.removeWorkersGuid(note)
                }
                activity.setImageButtonType(R.drawable.ic_notifications_off)
            }
        }
    }

    private fun setWorkManager(request: WorkRequest, note: Note) {
        val workManager = WorkManager.getInstance(activity)
        workManager.enqueue(request)
        dbManager.notesModel.getWorkersGuid(note.id)?.let {
            setWorkManagerLiveData(workManager, it)
        }
    }

    override fun onDestroy() {

    }
}