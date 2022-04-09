package com.ntt.orderfood

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ntt.orderfood.adapter.CartAdapter
import com.ntt.orderfood.callback.ItemTouchHelperListener

class RecyclerViewItemTouchHelper(
    private val dragDirs: Int,
    private val swipeDirs: Int,
    private val mItemTouchHelperListener: ItemTouchHelperListener
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (mItemTouchHelperListener != null) {
            mItemTouchHelperListener.onSwiped(viewHolder)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView: View = (viewHolder as CartAdapter.ViewHolder).layoutForeground
            getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val foregroundView: View = (viewHolder as CartAdapter.ViewHolder).layoutForeground
        getDefaultUIUtil().onDrawOver(
            c,
            recyclerView,
            foregroundView,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
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
        val foregroundView: View = (viewHolder as CartAdapter.ViewHolder).layoutForeground
        getDefaultUIUtil().onDraw(
            c,
            recyclerView,
            foregroundView,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView: View = (viewHolder as CartAdapter.ViewHolder).layoutForeground
        getDefaultUIUtil().clearView(recyclerView)
    }
}