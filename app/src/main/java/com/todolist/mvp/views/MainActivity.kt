package com.todolist.mvp.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.todolist.R
import com.todolist.interfaces.IMainView
import com.todolist.mvp.presenters.NotesPresenter
import com.todolist.support.Note
import com.todolist.support.NotesAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val NOTE_ID = "NOTE_ID"

const val NOTE_MODE = "NOTE_MODE"
const val EDIT_NOTE = 0
const val CREATE_NEW_NOTE = 1

class MainActivity : AppCompatActivity(), IMainView {

    private lateinit var presenter: NotesPresenter
    private var completedMenuItem : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = NotesPresenter(this)

        fab.setOnClickListener {
            presenter.onFABClick()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun fillRecyclerView(notesSet: LinkedHashSet<Note>) {
        rv_notes.adapter = NotesAdapter(this, notesSet)
    }

    override fun startNewNoteActivity() {
        val intent = Intent(this, NoteActivity::class.java)
        intent.apply { putExtra(NOTE_MODE, CREATE_NEW_NOTE) }
        startActivity(intent)
    }

    override fun startEditNoteActivity(note: Note) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.apply {
            putExtra(NOTE_MODE, EDIT_NOTE)
            putExtra(NOTE_ID, note.id)
        }
        startActivity(intent)
    }

    override fun setCompletedMenuItem(item : MenuItem) {
        completedMenuItem = item
    }

    override fun setOptionsMenuVisible(haveCompleted: Boolean) {
        completedMenuItem?.isVisible = haveCompleted
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return presenter.onCreateMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_action_completed -> {
                presenter.onActionCompletedClick(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
