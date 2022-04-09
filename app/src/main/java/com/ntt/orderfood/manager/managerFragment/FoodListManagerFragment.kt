package com.ntt.orderfood.manager.managerFragment

import android.app.Activity
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ntt.orderfood.R
import com.ntt.orderfood.adapter.RatingAdapter
import com.ntt.orderfood.databinding.FragmentFoodListManagerBinding
import com.ntt.orderfood.manager.managerAdapter.FoodManagerAdapter
import com.ntt.orderfood.manager.managerCallback.ItemFoodManagerClickListener
import com.ntt.orderfood.model.Category
import com.ntt.orderfood.model.Food
import com.ntt.orderfood.model.Rating
import com.ntt.orderfood.vm.CategoryViewModel
import java.text.SimpleDateFormat
import java.util.*

class FoodListManagerFragment : Fragment(), ItemFoodManagerClickListener {
    private lateinit var binding: FragmentFoodListManagerBinding
    private lateinit var foodAdapter: FoodManagerAdapter
    private var foods = arrayListOf<Food>()
    private var ratings = arrayListOf<Rating>()
    private lateinit var ratingAdapter: RatingAdapter
    private lateinit var dataBase: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var imgFood: ImageView
    private lateinit var category: Category
    private lateinit var newFood: Food
    private var listAllFood = arrayListOf<Food>()
    private val args: FoodListManagerFragmentArgs by navArgs()
    private lateinit var viewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodListManagerBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        category = args.category

        dataBase = Firebase.database

        (activity as AppCompatActivity).supportActionBar?.title = category.name

        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        viewModel.foods.observe(viewLifecycleOwner) {
            foods = it
            foodAdapter = FoodManagerAdapter(foods, this)
            binding.rvFoodList.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.rvFoodList.adapter = foodAdapter
            foodAdapter.notifyDataSetChanged()
        }

        binding.btnAddFood.setOnClickListener {
            addFood()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val menuItem: MenuItem = menu.findItem(R.id.actionSearch)
        val searchView: SearchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                foodAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                foodAdapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun addFood() {
        val dialog = context?.let { it1 -> Dialog(it1) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_add_food_dialog)
        val window: Window? = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        val edtNameFood: EditText? = dialog?.findViewById(R.id.edt_name_food)
        val edtPrice: EditText? = dialog?.findViewById(R.id.edt_price_food)
        val edtDescription: EditText? = dialog?.findViewById(R.id.edt_description_food)
        imgFood = dialog?.findViewById(R.id.img_food)!!
        val btnAdd: Button? = dialog.findViewById(R.id.btn_add)
        val btnCancel: Button? = dialog.findViewById(R.id.btn_cancel)

        imgFood.setOnClickListener {
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
                        if (listAllFood.size != 0) {
                            val maxId = listAllFood[listAllFood.size - 1].id.toInt()
                            newFood =
                                Food(
                                    (maxId + 1).toString(),
                                    edtNameFood?.text.toString(),
                                    category.name,
                                    it.toString(),
                                    edtPrice?.text.toString(),
                                    edtDescription?.text.toString(),
                                    category.id
                                )
                        } else if (listAllFood.size == 0) {
                            newFood =
                                Food(
                                    "0",
                                    edtNameFood?.text.toString(),
                                    category.name,
                                    it.toString(),
                                    edtPrice?.text.toString(),
                                    edtDescription?.text.toString(),
                                    category.id
                                )
                        }

                        val myPref = dataBase.getReference("Food")
                        myPref.child(newFood.id).setValue(newFood)
                        Snackbar.make(
                            requireView(),
                            "New Food ${newFood.name} was added",
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

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!
            imgFood.setImageURI(imageUri)
        }

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!
            imgFood.setImageURI(imageUri)
        }
    }

    override fun onClickItemFood(food: Food) {
        val viewDialog: View =
            layoutInflater.inflate(R.layout.layout_bottom_sheet_detail_food_manager, null)

        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(viewDialog)
        bottomSheetDialog.setCancelable(true)

        val bottomSheetBehavior: BottomSheetBehavior<View> =
            BottomSheetBehavior.from(viewDialog.parent as View)

        bottomSheetBehavior.state = STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false // disable dragging

        bottomSheetDialog.show()

        val btnCancel: Button = viewDialog.findViewById(R.id.btn_cancel)
        val tvNameFood: TextView = viewDialog.findViewById(R.id.tv_name_food)
        val tvPrice: TextView = viewDialog.findViewById(R.id.tv_price)
        val tvDescription: TextView = viewDialog.findViewById(R.id.tv_description)
        val imgFood: ImageView = viewDialog.findViewById(R.id.img_food)
        val rvRating: RecyclerView = viewDialog.findViewById(R.id.rv_rating)

        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        ratings.clear()

        val mRef = dataBase.getReference("Rating")

        mRef.orderByChild("foodId").equalTo(food.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        ratings.clear()
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            val rating = dataSnapshot.getValue(Rating::class.java)
                            if (rating != null) {
                                ratings.add(rating)
                            }
                        }
                        ratingAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        ratingAdapter = RatingAdapter(ratings)
        rvRating.adapter = ratingAdapter
        rvRating.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        tvNameFood.text = food.name
        tvPrice.text = "${food.price} VNĐ"
        tvDescription.text = food.description
        Glide.with(imgFood).load(food.image).centerCrop().into(imgFood)
    }

    override fun onClickEditFood(food: Food) {
        val dialog = context?.let { it1 -> Dialog(it1) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_edit_food_dialog)
        val window: Window? = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        val edtNameFood: EditText? = dialog?.findViewById(R.id.edt_name_food)
        val edtPrice: EditText? = dialog?.findViewById(R.id.edt_price_food)
        val edtDescription: EditText? = dialog?.findViewById(R.id.edt_description_food)
        imgFood = dialog?.findViewById(R.id.img_food)!!

        val btnEdit: Button? = dialog.findViewById(R.id.btn_edit)
        val btnCancel: Button? = dialog.findViewById(R.id.btn_cancel)

        edtNameFood?.setText(food.name)
        edtPrice?.setText(food.price)
        edtDescription?.setText(food.description)
        Glide.with(imgFood).load(food.image).centerCrop().into(imgFood)

        imgFood.setOnClickListener {
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
                        val newFood =
                            Food(
                                food.id,
                                edtNameFood?.text.toString(),
                                food.category,
                                it.toString(),
                                edtPrice?.text.toString(),
                                edtDescription?.text.toString(),
                                category.id
                            )

                        val myPref = dataBase.getReference("Food")
                        myPref.child(newFood.id).setValue(newFood)
                        Snackbar.make(
                            requireView(),
                            "Update Category ${newFood.name} Successful",
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

    override fun onClickDeleteFood(food: Food) {
        AlertDialog.Builder(requireContext())
            .setTitle("Warning")
            .setMessage("Are you sure you want to delete it?")
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                    val myPref = dataBase.getReference("Food")
                    myPref.child(food.id)
                        .removeValue(object : DatabaseReference.CompletionListener {
                            override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                                Snackbar.make(
                                    requireView(),
                                    "Delete Food ${food.name} Successful",
                                    Snackbar.LENGTH_SHORT
                                ).show()
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
