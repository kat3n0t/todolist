package com.todolist.mvp.presenters

import android.content.Intent
import com.todolist.R
import com.todolist.database.DBManager
import com.todolist.interfaces.IEditNotePresenter
import com.todolist.interfaces.INoteView
import com.todolist.mvp.views.NoteActivity
import com.todolist.support.NotificationService

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
                val serviceIntent =
                    Intent(activity.applicationContext, NotificationService::class.java)
                serviceIntent.putExtra("noteId", note.id)
                serviceIntent.putExtra("noteName", note.name)
                if (isNotificationOn) {
                    activity.startService(serviceIntent)
                    activity.setImageButtonType(R.drawable.ic_notifications_on)
                } else {
                    activity.stopService(serviceIntent)
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