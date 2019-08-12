package com.todolist.mvp.presenters

import com.todolist.R
import com.todolist.database.DBManager
import com.todolist.interfaces.INotePresenter
import com.todolist.mvp.views.NoteActivity

class NewNotePresenter(private var activity: NoteActivity) : INotePresenter {

    private val dbManager = DBManager(activity)

    override fun onSaveClick() {
        val noteName = activity.getNoteText()
        if (noteName != "") {
            dbManager.notesModel.addNote(noteName)
            activity.finish()
        }
    }

    override fun onStart() {
        activity.setTitle(R.string.new_note)
        activity.showDeleteButton(false)
    }

    override fun onDestroy() {

    }
}