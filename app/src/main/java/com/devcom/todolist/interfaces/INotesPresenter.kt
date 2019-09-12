package com.devcom.todolist.interfaces

import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import com.devcom.todolist.support.Note
import com.devcom.todolist.support.NotesAdapter

interface INotesPresenter : IPresenter {
    fun onFABClick()
    fun onActionCompletedClick(item: MenuItem)
    fun onCreateMenu(menu: Menu): Boolean
    fun onItemClicked(holder: NotesAdapter.NotesViewHolder, note: Note, isChecked: Boolean)
    fun onItemLongClicked(checkBox: CheckBox, note: Note)
}