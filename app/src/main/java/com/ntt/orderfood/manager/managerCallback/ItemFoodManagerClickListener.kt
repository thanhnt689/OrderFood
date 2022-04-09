package com.ntt.orderfood.manager.managerCallback

import com.ntt.orderfood.model.Food

interface ItemFoodManagerClickListener {
    fun onClickItemFood(food: Food)
    fun onClickEditFood(food: Food)
    fun onClickDeleteFood(food: Food)
}