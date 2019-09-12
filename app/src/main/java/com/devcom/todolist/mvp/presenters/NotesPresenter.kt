package com.devcom.todolist.mvp.presenters

import android.os.Handler
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import com.devcom.todolist.database.DBManager
import com.devcom.todolist.interfaces.INotesPresenter
import com.devcom.todolist.mvp.views.MainActivity
import com.devcom.todolist.R
import com.devcom.todolist.support.Note
import com.devcom.todolist.support.NotesAdapter


class NotesPresenter(private var activity: MainActivity) : INotesPresenter {

    private val dbManager = DBManager(activity)

    private val preferenceManager = PreferenceManager.getDefaultSharedPreferences(activity)
    private var isShow = preferenceManager.getBoolean("isShowCompletedNotes", true)

    override fun onStart() {
        Handler().post {
            loadNotes()
        }
    }

    override fun onCreateMenu(menu: Menu): Boolean {
        activity.menuInflater.inflate(R.menu.menu_main, menu)
        setSwitcherTitle(menu.findItem(R.id.item_action_completed))
        activity.setCompletedMenuItem(menu.findItem(R.id.item_action_completed))
        activity.setOptionsMenuVisible(dbManager.notesModel.haveCompletedNotes())
        return true
    }

    private fun setSwitcherTitle(item: MenuItem) {
        if (isShow)
            item.setTitle(R.string.action_hide_completed)
        else
            item.setTitle(R.string.action_show_completed)
    }

    private fun loadNotes() {
        val notes = dbManager.notesModel.getNotes(isShow)
        activity.fillRecyclerView(notes)
    }

    override fun onItemClicked(holder: NotesAdapter.NotesViewHolder, note: Note, isChecked: Boolean) {
        dbManager.notesModel.changeCompleted(note, isChecked)
        holder.setCheckBoxCompleted(note, isChecked)
        activity.setOptionsMenuVisible(dbManager.notesModel.haveCompletedNotes())
    }

    override fun onItemLongClicked(checkBox: CheckBox, note: Note) {
        activity.startEditNoteActivity(note)
    }

    override fun onActionCompletedClick(item: MenuItem) {
        switchCompletedNote(item.title == activity.getString(R.string.action_show_completed))
        activity.recreate() // путем пересоздания активности меняется текст заголовка, обновляется список заметок
    }

    private fun switchCompletedNote(isShow: Boolean) {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = preferenceManager.edit()
        editor.putBoolean("isShowCompletedNotes", isShow)
        editor.apply()
    }

    override fun onFABClick() {
        activity.startNewNoteActivity()
    }

    override fun onDestroy() {

    }
}