package com.annevonwolffen.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.annevonwolffen.todoapp.model.TaskPresentationModel

class TasksAdapter(
    private val itemActionListener: TaskItemActionListener,
    private val itemClickListener: OnTaskClickListener
) :
    ListAdapter<TaskPresentationModel, TasksAdapter.ViewHolder>(DiffUtilCallback()),
    ItemTouchHelperCallback.ItemTouchHelperAdapter {

    class DiffUtilCallback : DiffUtil.ItemCallback<TaskPresentationModel>() {

        override fun areItemsTheSame(
            oldItem: TaskPresentationModel,
            newItem: TaskPresentationModel
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: TaskPresentationModel,
            newItem: TaskPresentationModel
        ): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when (viewType) {
            TOP -> R.layout.task_top_list_item
            BOTTOM -> R.layout.task_bottom_list_item
            else -> R.layout.task_list_item
        }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return ViewHolder(binding.root, itemClickListener, itemActionListener)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TOP
            currentList.size - 1 -> BOTTOM
            else -> MIDDLE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val view: View,
        private val clickHandler: OnTaskClickListener,
        private val itemActionListener: TaskItemActionListener
    ) :
        RecyclerView.ViewHolder(view) {
        fun bind(task: TaskPresentationModel) {
            val binding = DataBindingUtil.getBinding<ViewDataBinding>(view)
            binding?.let {
                it.setVariable(BR.task, task)
                it.setVariable(BR.clickHandler, clickHandler)
                it.setVariable(BR.doneHandler, itemActionListener)
                it.executePendingBindings()
            }
        }
    }

    private companion object {
        const val TOP = 0
        const val MIDDLE = 1
        const val BOTTOM = 2
    }

    override fun onItemSwipedToStart(position: Int) {
        itemActionListener.onDeleteTask(getItem(position))
    }

    override fun onItemSwipedToEnd(position: Int) {
        itemActionListener.onDoneTask(getItem(position))
    }
}