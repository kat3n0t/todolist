package com.todolist.support

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.todolist.R
import com.todolist.interfaces.INotesPresenter
import com.todolist.mvp.presenters.NotesPresenter
import com.todolist.mvp.views.MainActivity

class NotesAdapter(private val context: Context, private val notesSet: LinkedHashSet<Note>) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return NotesViewHolder(view, NotesPresenter(context as MainActivity))
    }

    override fun getItemCount(): Int {
        return notesSet.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notesSet.elementAt(position))
    }

    inner class NotesViewHolder(itemView: View, private val presenter: INotesPresenter) :
        RecyclerView.ViewHolder(itemView) {

        private val listItemNoteView: CheckBox = itemView.findViewById(R.id.checkBox_note)

        fun bind(note: Note) {
            setItemStyle(listItemNoteView, note)

            listItemNoteView.setOnLongClickListener {
                presenter.onItemLongClicked(listItemNoteView, note)
                true
            }
        }

        private fun setItemStyle(itemView: CheckBox, note: Note) {
            itemView.text = note.name

            itemView.setOnCheckedChangeListener { _, isChecked ->
                presenter.onItemClicked(itemView, note, isChecked)
            }

            itemView.isChecked = note.isCompleted
        }
    }
}