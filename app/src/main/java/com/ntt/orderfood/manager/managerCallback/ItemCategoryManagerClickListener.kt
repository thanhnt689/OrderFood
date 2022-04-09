package com.ntt.orderfood.manager.managerCallback

import com.ntt.orderfood.model.Category

interface ItemCategoryManagerClickListener {
    fun onClickItemCategory(category: Category)
    fun onClickEditCategory(category: Category)
    fun onClickDeleteCategory(category: Category)
}