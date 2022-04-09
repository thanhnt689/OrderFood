package com.ntt.orderfood.callback

import com.ntt.orderfood.model.Order

interface ItemOrderClickListener {
    fun onClickItemOrder(order: Order)
}