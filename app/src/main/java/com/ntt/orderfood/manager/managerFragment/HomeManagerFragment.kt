package com.ntt.orderfood.manager.managerFragment

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ntt.orderfood.R
import com.ntt.orderfood.manager.managerAdapter.CategoryManagerAdapter
import com.ntt.orderfood.manager.managerCallback.ItemCategoryManagerClickListener
import com.ntt.orderfood.databinding.FragmentHomeManagerBinding
import com.ntt.orderfood.model.Category
import com.ntt.orderfood.model.Food
import com.ntt.orderfood.vm.CategoryViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomeManagerFragment : Fragment(), ItemCategoryManagerClickListener {
    private lateinit var binding: FragmentHomeManagerBinding
    private lateinit var categoryAdapter: CategoryManagerAdapter
    private lateinit var dataBase: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var categories = arrayListOf<Category>()
    private lateinit var imageUri: Uri
    private lateinit var imgCategory: ImageView
    private lateinit var newCategory: Category
    private var foods = arrayListOf<Food>()
    private lateinit var viewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeManagerBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        dataBase = Firebase.database

        viewModel.categories.observe(viewLifecycleOwner) {
            categories = it

            categoryAdapter = CategoryManagerAdapter(categories, this)
            binding.rvCategory.layoutManager =
                GridLayoutManager(context, 2)
            binding.rvCategory.adapter = categoryAdapter
            categoryAdapter.notifyDataSetChanged()
        }

        binding.svSearchCategory.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                categoryAdapter.filter.filter(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                categoryAdapter.filter.filter(p0)
                return false
            }
        })

        binding.btnAddCategory.setOnClickListener {
            addCategory()
        }

        return binding.root
    }

    private fun addCategory() {
        val dialog = context?.let { it1 -> Dialog(it1) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_add_category_dialog)
        val window: Window? = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        val edtNameCategory: EditText? = dialog?.findViewById(R.id.edt_name_category)
        imgCategory = dialog?.findViewById(R.id.img_category)!!
        val btnAdd: Button? = dialog.findViewById(R.id.btn_add)
        val btnCancel: Button? = dialog.findViewById(R.id.btn_cancel)

        imgCategory.setOnClickListener {
            val intent = Intent()
            intent.apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(intent, 100)
        }
        btnAdd?.setOnClickListener {
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
                        if (categories.size != 0) {
                            val maxId = categories[categories.size - 1].id.toInt()
                            newCategory =
                                Category(
                                    (maxId + 1).toString(),
                                    it.toString(),
                                    edtNameCategory?.text.toString(),
                                )
                        } else if (categories.size == 0) {
                            newCategory =
                                Category(
                                    "0",
                                    it.toString(),
                                    edtNameCategory?.text.toString(),
                                )
                        }

                        val myPref = dataBase.getReference("Category")
                        myPref.child(newCategory.id).setValue(newCategory)
                        Snackbar.make(
                            requireView(),
                            "New Category ${newCategory.name} was added",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                    val progress: Double = (100.0 * it.bytesTransferred / it.totalByteCount)
                    progressDialog.setMessage("Uploaded $progress%")
                }
            dialog.dismiss()
        }

        btnCancel?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            imgCategory.setImageURI(imageUri)
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            imgCategory.setImageURI(imageUri)
        }
    }

    override fun onClickItemCategory(category: Category) {
        val action = HomeManagerFragmentDirections.actionHomeManagerFragmentToFoodListManagerFragment(category)

        val myRef =
            dataBase.getReference("Food")

        myRef.orderByChild("menuId").equalTo(category.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (foods != null) {
                        foods.clear()
                    }
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val food = dataSnapshot.getValue(Food::class.java)
                        if (food != null) {
                            foods.add(food)
                        }
                    }
                    viewModel.getFoods(foods)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        findNavController().navigate(action)
    }

    override fun onClickEditCategory(category: Category) {
        val dialog = context?.let { it1 -> Dialog(it1) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_edit_category_dialog)
        val window: Window? = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        val edtNameCategory: EditText? = dialog?.findViewById(R.id.edt_name_category)
        imgCategory = dialog?.findViewById(R.id.img_category)!!
        val btnEdit: Button? = dialog.findViewById(R.id.btn_edit)
        val btnCancel: Button? = dialog.findViewById(R.id.btn_cancel)

        edtNameCategory?.setText(category.name)
        Glide.with(imgCategory).load(category.image).centerCrop().into(imgCategory)

        imgCategory.setOnClickListener {
            val intent = Intent()
            intent.apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(intent, 1000)
        }
        btnEdit?.setOnClickListener {
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
                        val newCategory =
                            Category(
                                category.id,
                                it.toString(),
                                edtNameCategory?.text.toString(),
                            )

                        val myPref = dataBase.getReference("Category")
                        myPref.child(newCategory.id).setValue(newCategory)
                        Snackbar.make(
                            requireView(),
                            "Update Category ${newCategory.name} Successful",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                    val progress: Double = (100.0 * it.bytesTransferred / it.totalByteCount)
                    progressDialog.setMessage("Uploaded $progress%")
                }
            dialog.dismiss()
        }

        btnCancel?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onClickDeleteCategory(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Warning")
            .setMessage("Are you sure you want to delete it?")
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                    val database = Firebase.database
                    val myRef = database.getReference("Category")
                    myRef.child("${category.id}")
                        .removeValue(object : DatabaseReference.CompletionListener {
                            override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                                Snackbar.make(
                                    requireView(),
                                    "Delete Category ${category.name} Successful",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        })

                    val ref = database.getReference("Food")
                    ref.orderByChild("menuId").equalTo(category.id)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(dataSnapshot:DataSnapshot in snapshot.children){
                                    dataSnapshot.ref.removeValue()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }
            })
            .setNegativeButton("No", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                    dialogInterface?.dismiss()
                }
            })
            .show()
    }

}