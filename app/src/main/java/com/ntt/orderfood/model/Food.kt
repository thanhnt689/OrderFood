package com.ntt.orderfood.model

import java.io.Serializable

data class Food(
    var id: String = "",
    var name: String = "",
    var category: String = "",
    var image: String = "",
    var price: String = "",
    var description: String = "",
    var menuId: String = ""
) : Serializable