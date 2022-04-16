package com.annevonwolffen.todoapp

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class ItemTouchHelperCallback(private val adapter: ItemTouchHelperAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> adapter.onItemSwipedToStart(viewHolder.adapterPosition)
            ItemTouchHelper.END -> adapter.onItemSwipedToEnd(viewHolder.adapterPosition)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val iconHorizontalMargin =
            recyclerView.context.resources.getDimensionPixelSize(R.dimen.margin_medium_big)
        if (dX > 0) {
            // swipe to right (end)
            c.clipRect(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
            val background =
                ColorDrawable(ContextCompat.getColor(recyclerView.context, R.color.colorGreen))
            background.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
            )
            background.draw(c)
            ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_check_24dp)?.let {
                val iconSize = it.intrinsicHeight
                val halfIcon: Int = iconSize / 2
                val top = itemView.top + ((itemView.bottom - itemView.top) / 2 - halfIcon)
                it.setBounds(
                    itemView.left + iconHorizontalMargin,
                    top,
                    itemView.left + iconHorizontalMargin + it.intrinsicWidth,
                    top + it.intrinsicHeight
                )
                it.setTint(ContextCompat.getColor(recyclerView.context, R.color.colorWhite))
                it.draw(c)
            }
        } else if (dX < 0) {
            // swipe to left (start)
            c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            val background =
                ColorDrawable(ContextCompat.getColor(recyclerView.context, R.color.colorRed))
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)
            ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete_24dp)?.let {
                val iconSize = it.intrinsicHeight
                val halfIcon: Int = iconSize / 2
                val top = itemView.top + ((itemView.bottom - itemView.top) / 2 - halfIcon)
                val left = itemView.right - iconHorizontalMargin - halfIcon * 2
                it.setBounds(
                    left,
                    top,
                    viewHolder.itemView.right - iconHorizontalMargin,
                    top + it.intrinsicHeight
                )
                it.setTint(ContextCompat.getColor(recyclerView.context, R.color.colorWhite))
                it.draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    interface ItemTouchHelperAdapter {
        fun onItemSwipedToStart(position: Int)
        fun onItemSwipedToEnd(position: Int)
    }
}