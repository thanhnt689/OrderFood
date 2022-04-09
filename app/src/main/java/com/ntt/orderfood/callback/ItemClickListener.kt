package com.ntt.orderfood.callback

import com.ntt.orderfood.model.Category

interface ItemClickListener {
    fun onClickItemCategory(category: Category)
}