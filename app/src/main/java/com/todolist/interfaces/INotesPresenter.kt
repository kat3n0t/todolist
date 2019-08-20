package com.todolist.interfaces

import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import com.todolist.support.Note

interface INotesPresenter : IPresenter {
    fun onFABClick()
    fun onActionCompletedClick(item: MenuItem)
    fun onCreateMenu(menu: Menu): Boolean
    fun onItemClicked(checkBox: CheckBox, note: Note, isChecked: Boolean)
    fun onItemLongClicked(checkBox: CheckBox, note: Note)
}