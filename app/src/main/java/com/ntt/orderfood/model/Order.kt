package com.ntt.orderfood.model

data class Order(
    var id: String = "0",
    var phone: String = "",
    var name: String = "",
    var address: String = "",
    var total: String = "",
    var orderDate: String = "",
    var status: String = "Placed",
    var carts: List<Cart> = arrayListOf()
)