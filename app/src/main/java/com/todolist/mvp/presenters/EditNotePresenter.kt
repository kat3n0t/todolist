package com.todolist.mvp.presenters

import com.todolist.R
import com.todolist.database.DBManager
import com.todolist.interfaces.IEditNotePresenter
import com.todolist.interfaces.INoteView
import com.todolist.mvp.views.NoteActivity

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

    override fun onNotificationClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

        activity.showDeleteButton(true)

        val noteId = activity.getEditingNoteId()
        if (noteId != null)
            setTextNote(noteId)
    }

    override fun onDestroy() {

    }
}