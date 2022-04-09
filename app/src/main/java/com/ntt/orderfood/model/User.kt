package com.ntt.orderfood.model

data class User(
    var name: String = "",
    var password: String = "",
    var phone: String = "",
    var position: String = "",
    var image: String = "",
) {
    fun toMapChangePassword(): Map<String, Any?> {
        return mapOf(
            "password" to password,
        )
    }

    fun toMapChangeName(): Map<String, Any?> {
        return mapOf(
            "name" to name,
        )
    }
}
