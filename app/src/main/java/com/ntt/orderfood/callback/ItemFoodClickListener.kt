package com.ntt.orderfood.callback

import com.ntt.orderfood.model.Food

interface ItemFoodClickListener {
    fun onClickItemFood(food: Food)
}