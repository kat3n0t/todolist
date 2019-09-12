package com.devcom.todolist.interfaces

import android.view.MenuItem
import com.devcom.todolist.support.Note

interface IMainView {
    fun startNewNoteActivity()
    fun startEditNoteActivity(note: Note)
    fun setCompletedMenuItem(item : MenuItem)
    fun setOptionsMenuVisible(haveCompleted: Boolean)
    fun fillRecyclerView(notesSet: LinkedHashSet<Note>)
}