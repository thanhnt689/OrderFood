package com.ntt.orderfood.model

import android.content.Context
import android.net.ConnectivityManager


object Common {
    var currentUser: User? = null

    fun isConnectedToInternet(context: Context?): Boolean {
        val connectivityManager: ConnectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }

    const val PHONE_KEY = "Phone"
    const val PWD_KEY = "Password"
    const val POSITION_KEY = "Position"
}