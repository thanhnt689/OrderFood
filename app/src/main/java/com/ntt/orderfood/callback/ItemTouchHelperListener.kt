package com.ntt.orderfood.callback

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperListener {
    fun onSwiped(viewHolder: RecyclerView.ViewHolder)
}