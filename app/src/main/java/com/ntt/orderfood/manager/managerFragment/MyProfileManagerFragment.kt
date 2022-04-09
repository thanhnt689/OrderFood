package com.ntt.orderfood.manager.managerFragment

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ntt.orderfood.R
import com.ntt.orderfood.databinding.FragmentMyProfileManagerBinding
import com.ntt.orderfood.model.Common
import com.ntt.orderfood.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MyProfileManagerFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileManagerBinding
    private lateinit var dataBase: FirebaseDatabase
    private lateinit var imgAvatar: ImageView
    private lateinit var imageUri: Uri
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProfileManagerBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "My Profile"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvName.text = Common.currentUser?.name
        binding.tvPhoneNumber.text = Common.currentUser?.phone
        Glide.with(binding.imgAvatar).load(Common.currentUser?.image).error(R.drawable.ic_user)
            .into(binding.imgAvatar)

        dataBase = Firebase.database

        binding.btnEditProfile.setOnClickListener {
            editProfile()
        }
        binding.btnEditPassword.setOnClickListener {
            editPassword()
        }
        binding.btnLogOut.setOnClickListener {
            activity?.finish()
        }
    }

    private fun editPassword() {
        val dialog = context?.let { it1 -> Dialog(it1) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_edit_password)
        val window: Window? = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        val tvPhone: TextView? = dialog?.findViewById(R.id.tv_phone)
        val edtPassword: EditText? = dialog?.findViewById(R.id.edt_password)
        val edtNewPassword: EditText? = dialog?.findViewById(R.id.edt_new_password)
        val edtConfirmPassword: TextView? = dialog?.findViewById(R.id.edt_confirm_password)
        val btnEdit: Button? = dialog?.findViewById(R.id.btn_edit)
        val btnCancel: Button? = dialog?.findViewById(R.id.btn_cancel)

        tvPhone?.text = Common.currentUser?.phone
        btnCancel?.setOnClickListener {
            dialog.dismiss()
        }
        btnEdit?.setOnClickListener {
            if (edtPassword?.text.toString().isEmpty() ||
                edtNewPassword?.text.toString().isEmpty() ||
                edtConfirmPassword?.text.toString().isEmpty() ||
                edtPassword?.text.toString() != Common.currentUser?.password ||
                edtNewPassword?.text.toString() != edtNewPassword?.text.toString()
            ) {
                Toast.makeText(context, "Please check the information again", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val pref = dataBase.getReference("User")

                val newPassword = edtNewPassword?.text.toString().trim()
                val user = User(
                    Common.currentUser?.name.toString(),
                    Common.currentUser?.password.toString(),
                    Common.currentUser?.phone.toString()
                )

                user.password = newPassword

                pref.child("${user.phone}")
                    .updateChildren(user.toMapChangePassword(),
                        object : DatabaseReference.CompletionListener {
                            override fun onComplete(
                                error: DatabaseError?,
                                ref: DatabaseReference
                            ) {
                                Toast.makeText(
                                    context,
                                    "Update Data Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                            }
                        }
                    )
                activity?.finish()
            }
        }
        dialog?.show()
    }

    private fun editProfile() {
        val dialog = context?.let { it1 -> Dialog(it1) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_edit_profile)
        val window: Window? = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        val tvPhone: TextView? = dialog?.findViewById(R.id.tv_phone)
        val edtPassword: EditText? = dialog?.findViewById(R.id.edt_password)
        val edtNewName: EditText? = dialog?.findViewById(R.id.edt_new_name)
        imgAvatar = dialog?.findViewById(R.id.img_avatar)!!

        val btnEdit: Button? = dialog.findViewById(R.id.btn_edit)
        val btnCancel: Button? = dialog.findViewById(R.id.btn_cancel)

        tvPhone?.text = Common.currentUser?.phone
        Glide.with(imgAvatar).load(Common.currentUser?.image).error(R.drawable.ic_user)
            .into(imgAvatar)

        imgAvatar.setOnClickListener {
            val intent = Intent()
            intent.apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(intent, 100)
        }

        btnCancel?.setOnClickListener {
            dialog.dismiss()
        }
        btnEdit?.setOnClickListener {
            if (edtPassword?.text.toString().isEmpty() ||
                edtNewName?.text.toString().isEmpty() ||
                edtPassword?.text.toString() != Common.currentUser?.password
            ) {
                Toast.makeText(context, "Please check the information again", Toast.LENGTH_SHORT)
                    .show()
            } else {
                storage = FirebaseStorage.getInstance()
                storageReference = storage.reference
                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage("Uploading...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                val now = Date()
                val fileName = formatter.format(now)
                val imageFolder: StorageReference = storageReference.child("images/$fileName")
                imageFolder.putFile(imageUri)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Uploaded!!", Toast.LENGTH_SHORT).show()
                        imageFolder.downloadUrl.addOnSuccessListener {
                            val pref = dataBase.getReference("User")
                            val newName = edtNewName?.text.toString().trim()

                            val user = User(
                                Common.currentUser?.name.toString(),
                                Common.currentUser?.password.toString(),
                                Common.currentUser?.phone.toString(),
                                Common.currentUser?.position.toString(),
                                it.toString()
                            )

                            user.name = newName

                            pref.child(user.phone).setValue(user)

                        }
                        activity?.finish()
                    }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!
            imgAvatar.setImageURI(imageUri)
        }
    }
}