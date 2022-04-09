package com.ntt.orderfood.model

data class Rating(
    var id: String = "0",
    var user: User = User(),
    var foodId: String = "",
    var rateValue: String = "",
    var comment: String = "",
    var time: String = ""
)