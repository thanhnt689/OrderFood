package com.ntt.orderfood.model

import java.io.Serializable


data class Category(
    var id: String = "0",
    var image: String = "",
    var name: String = ""
) : Serializable