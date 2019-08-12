package com.todolist.mvp.presenters

import android.os.Handler
import android.preference.PreferenceManager
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import com.todolist.R
import com.todolist.database.DBManager
import com.todolist.interfaces.INotesPresenter
import com.todolist.mvp.views.MainActivity
import com.todolist.support.Note


class NotesPresenter(private var activity: MainActivity) : INotesPresenter {

    private val dbManager = DBManager(activity)

    private val preferenceManager = PreferenceManager.getDefaultSharedPreferences(activity)
    private var isShow = preferenceManager.getBoolean("isShowCompletedNotes", true)

    override fun onStart() {
        activity.showProgressBar(true)
        Handler().postDelayed(
            {
                loadNotes()
                activity.showProgressBar(false)
            },
            1000
        )
    }

    fun onCreateMenu(menu: Menu) : Boolean {
        return if (dbManager.notesModel.getCompletedNotes().size > 0) {
            activity.menuInflater.inflate(R.menu.menu_main, menu)
            setSwitcherTitle(menu.findItem(R.id.item_action_completed))
            true
        } else
            false
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

    override fun onItemClicked(checkBox: CheckBox, note: Note, isChecked: Boolean) {
        dbManager.notesModel.changeCompleted(note, isChecked)
        setCheckBoxCompleted(checkBox, note, isChecked)
    }

    private fun setCheckBoxCompleted(checkBox: CheckBox, note: Note, isCompleted: Boolean) {
        if (isCompleted) {
            checkBox.alpha = 0.5f // делает элемент полупрозрачным

            val spannableString = SpannableString(note.name)
            spannableString.setSpan(StrikethroughSpan(), 0, spannableString.length, 0)
            checkBox.text = spannableString // перечеркивает текст заметки
        } else {
            checkBox.text = note.name
            checkBox.alpha = 1.0f
        }
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