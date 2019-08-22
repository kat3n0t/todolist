package com.todolist.interfaces

import android.view.MenuItem
import com.todolist.support.Note

interface IMainView {
    fun startNewNoteActivity()
    fun startEditNoteActivity(note: Note)
    fun setCompletedMenuItem(item : MenuItem)
    fun setOptionsMenuVisible(haveCompleted: Boolean)
    fun showProgressBar(isShow: Boolean)
    fun fillRecyclerView(notesSet: LinkedHashSet<Note>)
}