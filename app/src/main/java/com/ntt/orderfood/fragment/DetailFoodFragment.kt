package com.ntt.orderfood.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.R
import com.ntt.orderfood.adapter.RatingAdapter
import com.ntt.orderfood.databinding.FragmentDetailFoodBinding
import com.ntt.orderfood.model.Cart
import com.ntt.orderfood.model.Common
import com.ntt.orderfood.model.Food
import com.ntt.orderfood.model.Rating
import java.util.*

class DetailFoodFragment : Fragment() {
    private lateinit var binding: FragmentDetailFoodBinding
    private lateinit var ratingAdapter: RatingAdapter
    private lateinit var dataBase: FirebaseDatabase
    private lateinit var pref: DatabaseReference
    private lateinit var myRef: DatabaseReference
    private lateinit var newCart: Cart
    private lateinit var newRating: Rating
    private var carts = arrayListOf<Cart>()
    private var ratings = arrayListOf<Rating>()
    private var listRatingByFood = arrayListOf<Rating>()
    private lateinit var food: Food
    private val args: DetailFoodFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailFoodBinding.inflate(inflater, container, false)

        food = args.food

        if (food != null) {
            getDetailFood(food)
        }

        dataBase = Firebase.database

        (activity as AppCompatActivity).supportActionBar?.title = food.name

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myRef = dataBase.getReference("Rating")

        myRef.orderByChild("foodId").equalTo(food.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (listRatingByFood != null) {
                            listRatingByFood.clear()
                        }
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            val rating = dataSnapshot.getValue(Rating::class.java)
                            if (rating != null) {
                                listRatingByFood.add(rating)
                            }
                        }
                        ratingAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        ratingAdapter = RatingAdapter(listRatingByFood)
        binding.rvRating.adapter = ratingAdapter
        binding.rvRating.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (ratings != null) {
                        ratings.clear()
                    }
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val rating = dataSnapshot.getValue(Rating::class.java)
                        if (rating != null) {
                            ratings.add(rating)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        pref = dataBase.getReference("Cart")

        pref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (carts != null) {
                        carts.clear()
                    }
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val cart = dataSnapshot.getValue(Cart::class.java)
                        if (cart != null) {
                            carts.add(cart)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        binding.btnNumber.setOnClickListener(ElegantNumberButton.OnClickListener {
            binding.tvFoodPrice.text =
                "${binding.btnNumber.number.toInt() * food.price.toInt()} VNĐ"
        })

        binding.btnCart.setOnClickListener {
            val dialog = context?.let { it1 -> Dialog(it1) }
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(R.layout.layout_add_cart_dialog)
            val window: Window? = dialog?.window
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setCancelable(false)

            val tvNameFood: TextView? = dialog?.findViewById(R.id.tv_name_food)
            val tvQuantity: TextView? = dialog?.findViewById(R.id.tv_quantity)
            val tvPrice: TextView? = dialog?.findViewById(R.id.tv_total_price)
            val btnAdd: Button? = dialog?.findViewById(R.id.btn_add)
            val btnCancel: Button? = dialog?.findViewById(R.id.btn_cancel)

            tvNameFood?.text = "Food: ${food.name}"
            tvQuantity?.text = "Quantity: ${binding.btnNumber.number}"
            tvPrice?.text =
                "Total Price: ${binding.btnNumber.number.toInt() * food.price.toInt()} VNĐ"

            btnAdd?.setOnClickListener {
                if (carts.size != 0) {
                    val maxId = carts[carts.size - 1].id.toInt()
                    newCart =
                        Cart(
                            id = (maxId + 1).toString(),
                            phone = Common.currentUser?.phone.toString(),
                            foodId = food.id,
                            foodName = food.name,
                            foodImage = food.image,
                            quantity = binding.btnNumber.number.toString(),
                            price = (binding.btnNumber.number.toInt() * food.price.toInt()).toString()
                        )
                } else if (carts.size == 0) {
                    newCart =
                        Cart(
                            id = "0",
                            phone = Common.currentUser?.phone.toString(),
                            foodId = food.id,
                            foodName = food.name,
                            foodImage = food.image,
                            quantity = binding.btnNumber.number.toString(),
                            price = (binding.btnNumber.number.toInt() * food.price.toInt()).toString()
                        )
                }
                pref.child(newCart.id).setValue(newCart)
                Toast.makeText(context, "Add to cart successfully", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }

            btnCancel?.setOnClickListener {
                dialog.dismiss()
            }

            dialog?.show()
        }

        binding.btnRating.setOnClickListener {
            showRatingDialog(food)
        }


    }

    private fun showRatingDialog(food: Food) {
        val dialog = context?.let { it1 -> Dialog(it1) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_rating_food)
        val window: Window? = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        val rbFood: RatingBar? = dialog?.findViewById(R.id.rb_food)
        val tvStartRating: TextView? = dialog?.findViewById(R.id.tv_star_rating)
        val edtComment: EditText? = dialog?.findViewById(R.id.edt_comment)
        val btnRating: Button? = dialog?.findViewById(R.id.btn_rating)
        val btnCancel: Button? = dialog?.findViewById(R.id.btn_cancel)

        rbFood?.setOnRatingBarChangeListener { ratingBar, fl, b ->
            tvStartRating?.text = fl.toString()
        }

        btnRating?.setOnClickListener {
            if (tvStartRating?.text.toString().isEmpty()) {
                Toast.makeText(context, "Please rate the Food", Toast.LENGTH_SHORT).show()
            } else if (edtComment?.text.toString().isEmpty() && ratings.size != 0) {
                val maxId = ratings[ratings.size - 1].id.toInt()
                newRating = Rating(
                    (maxId + 1).toString(),
                    Common.currentUser!!,
                    food.id,
                    tvStartRating?.text.toString(),
                    "No Comment",
                    Calendar.getInstance().time.toString()
                )
            } else if (edtComment?.text.toString().isEmpty() && ratings.size == 0) {
                newRating = Rating(
                    "0",
                    Common.currentUser!!,
                    food.id,
                    tvStartRating?.text.toString(),
                    "No Comment",
                    Calendar.getInstance().time.toString()
                )
            } else if (edtComment?.text.toString().isNotEmpty() && ratings.size != 0) {
                val maxId = ratings[ratings.size - 1].id.toInt()
                newRating = Rating(
                    (maxId + 1).toString(),
                    Common.currentUser!!,
                    food.id,
                    tvStartRating?.text.toString(),
                    edtComment?.text.toString(),
                    Calendar.getInstance().time.toString()
                )
            } else if (edtComment?.text.toString().isNotEmpty() && ratings.size == 0) {
                newRating = Rating(
                    "0",
                    Common.currentUser!!,
                    food.id,
                    tvStartRating?.text.toString(),
                    edtComment?.text.toString(),
                    Calendar.getInstance().time.toString()
                )
            }

            myRef.child(newRating.id).setValue(newRating)
            Toast.makeText(context, "Add Rating successfully", Toast.LENGTH_SHORT)
                .show()
            dialog.dismiss()
        }

        btnCancel?.setOnClickListener {
            dialog.dismiss()
        }

        dialog?.show()

    }

    private fun getDetailFood(food: Food) {
        binding.tvFoodName.text = food.name
        binding.tvFoodPrice.text = "${food.price} VNĐ"
        binding.toolbarLayout.title = food.name
        binding.tvDescription.text = food.description
        Glide.with(binding.root)
            .load(food.image)
            .error(R.drawable.load_food)
            .into(binding.imgFood)
    }
}