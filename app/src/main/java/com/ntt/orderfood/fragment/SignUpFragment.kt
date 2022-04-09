package com.ntt.orderfood.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.databinding.FragmentSignUpBinding
import com.ntt.orderfood.model.Common
import com.ntt.orderfood.model.User

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        progressDialog = ProgressDialog(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance()
        val tbUser: DatabaseReference = database.getReference("User")

        binding.btnSignUp.setOnClickListener {
            if (Common.isConnectedToInternet(context)) {
                val password = binding.edtPassword.text.toString()
                val phoneNumber = binding.edtPhoneNumber.text.toString()
                val name = binding.edtName.text.toString()
                val confirmPassword = binding.edtConfirmPassword.text.toString()

                if (phoneNumber.isEmpty() || password.isEmpty() || name.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(activity, "Please fill out the form", Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(
                        activity,
                        "Please check the confirmation password again",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    progressDialog.show()
                    tbUser.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            progressDialog.dismiss()
                            if (!snapshot.child(phoneNumber).exists()) {
                                val user = User(name, password, phoneNumber, "User","")
                                tbUser.child(phoneNumber).setValue(user)
                                Toast.makeText(
                                    context,
                                    "Sign-up Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

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