package com.ntt.orderfood.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.HomeActivity
import com.ntt.orderfood.HomeManagerActivity
import com.ntt.orderfood.databinding.FragmentLoginBinding
import com.ntt.orderfood.model.Common
import com.ntt.orderfood.model.User
import io.paperdb.Paper

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        progressDialog = ProgressDialog(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Paper.init(context)

        database = FirebaseDatabase.getInstance()
        val tbUser: DatabaseReference = database.getReference("User")

        binding.btnLogin.setOnClickListener {

            if (Common.isConnectedToInternet(context)) {
                if (binding.cbRemember.isChecked) {
                    Paper.book().write(Common.PHONE_KEY, binding.edtPhoneNumber.text.toString())
                    Paper.book().write(Common.PWD_KEY, binding.edtPassword.text.toString())
                    Paper.book()
                        .write(Common.POSITION_KEY, binding.spinnerPosition.selectedItem.toString())
                }

                val password = binding.edtPassword.text.toString()
                val phoneNumber = binding.edtPhoneNumber.text.toString()
                val strPosition = binding.spinnerPosition.selectedItem.toString()

                if (phoneNumber.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Please fill out the form", Toast.LENGTH_SHORT).show()
                } else {
                    progressDialog.show()
                    tbUser.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            progressDialog.dismiss()
                            if (snapshot.child(phoneNumber).exists()) {
                                val user: User? = snapshot.child(phoneNumber)
                                    .getValue<User>()
                                if (user != null) {
                                    if (user.password == password && user.position == strPosition && strPosition == "User") {
                                        Toast.makeText(
                                            context,
                                            "Login Successfully",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        val intent = Intent(context, HomeActivity::class.java)
                                        Common.currentUser = user

                                        binding.edtPassword.setText("")

                                        startActivity(intent)
                                    } else if (user.password == password && user.position == strPosition && strPosition == "Admin") {
                                        Toast.makeText(
                                            context,
                                            "Login Successfully",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        val intent =
                                            Intent(context, HomeManagerActivity::class.java)
                                        Common.currentUser = user

                                        binding.edtPassword.setText("")

                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT)
                                            .show()

                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "User not exist in DataBase",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }

            } else {
                Toast.makeText(context, "Please check your connection!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}