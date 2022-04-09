package com.ntt.orderfood.model

data class Cart(
    var id: String = "0",
    var phone: String = "",
    var foodId: String = "",
    var foodName: String = "",
    var foodImage: String = "",
    var quantity: String = "",
    var price: String = "",
)
