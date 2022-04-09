package com.ntt.orderfood.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ntt.orderfood.model.Category
import com.ntt.orderfood.model.Food

class CategoryViewModel : ViewModel() {
    private var listCategory = arrayListOf<Category>()
    private val _categories = MutableLiveData<ArrayList<Category>>()
    val categories: LiveData<ArrayList<Category>>
        get() = _categories

    private var listFood = arrayListOf<Food>()
    private val _foods = MutableLiveData<ArrayList<Food>>()
    val foods: LiveData<ArrayList<Food>>
        get() = _foods

    init {
        _categories.postValue(listCategory)
        _foods.postValue(listFood)
    }

    fun getCategories(categories: ArrayList<Category>) {
        listCategory = categories
        _categories.postValue(listCategory)
    }

    fun getFoods(foods: ArrayList<Food>) {
        listFood = foods
        _foods.postValue(listFood)
    }

}