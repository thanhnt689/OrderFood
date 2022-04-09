package com.ntt.orderfood

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.ntt.orderfood.databinding.ActivityMainBinding
import com.ntt.orderfood.fragment.LoginFragment
import com.ntt.orderfood.fragment.SignUpFragment
import com.ntt.orderfood.model.Common
import com.ntt.orderfood.model.User
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_main, LoginFragment())
            commit()
        }

        binding.btnLogin.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_main, LoginFragment())
                commit()
            }
        }
        binding.btnSignUp.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_main, SignUpFragment())
                commit()
            }
        }

    }
}