package com.ntt.orderfood.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.R
import com.ntt.orderfood.RecyclerViewItemTouchHelper
import com.ntt.orderfood.adapter.CartAdapter
import com.ntt.orderfood.callback.ItemTouchHelperListener
import com.ntt.orderfood.databinding.FragmentMyCartBinding
import com.ntt.orderfood.model.Cart
import com.ntt.orderfood.model.Common
import com.ntt.orderfood.model.Order
import com.ntt.orderfood.vm.CategoryViewModel
import java.util.*

class MyCartFragment : Fragment(), ItemTouchHelperListener {
    private lateinit var binding: FragmentMyCartBinding
    private lateinit var cartAdapter: CartAdapter
    private var carts = arrayListOf<Cart>()
    private lateinit var database: FirebaseDatabase
    private var orders = arrayListOf<Order>()
    private var totalPrice: Int = 0
    private lateinit var order: Order
    private lateinit var viewModel: CategoryViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        database = Firebase.database
        val pref = database.getReference("Cart")
        pref.orderByChild("phone").equalTo(Common.currentUser?.phone.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (carts != null) {
                        carts.clear()
                    }
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val cart = dataSnapshot.getValue(Cart::class.java)
                        if (cart != null) {
                            carts.add(cart)
                        }
                    }
                    setTotalPrice()
//                    viewModel.getCountCart(carts.size)
                    cartAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        val myRef = database.getReference("Order")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val order = dataSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val itemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        cartAdapter = CartAdapter(carts)
        binding.rvCart.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvCart.addItemDecoration(itemDecoration)
        binding.rvCart.adapter = cartAdapter

        setTotalPrice()

        binding.tvTotalPrice.text = totalPrice.toString()
        val simpleCallback: ItemTouchHelper.SimpleCallback =
            RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rvCart)

        binding.btnOrder.setOnClickListener {
            openDialogConfirmOrder()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openDialogConfirmOrder() {
        val dialog = context?.let { it1 -> Dialog(it1) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_order_dialog)
        val window: Window? = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        val tvName: TextView? = dialog?.findViewById(R.id.tv_name)
        val tvPhone: TextView? = dialog?.findViewById(R.id.tv_phone)
        val tvListFood: TextView? = dialog?.findViewById(R.id.tv_list_food)
        val tvTotalPrice: TextView? = dialog?.findViewById(R.id.tv_total_price)
        val edtAddress: EditText? = dialog?.findViewById(R.id.edt_address)
        val btnOrder: Button? = dialog?.findViewById(R.id.btn_order)
        val btnCancel: Button? = dialog?.findViewById(R.id.btn_cancel)

        tvName?.text = "Name: ${Common.currentUser?.name}"
        tvPhone?.text = "Phone: ${Common.currentUser?.phone}"
        tvTotalPrice?.text = "Total: ${binding.tvTotalPrice.text} VNĐ"

        var listFood: String = ""
        for (cart in carts) {
            val str: String = " ${cart.foodName}: ${cart.quantity};"
            listFood += str
        }
        tvListFood?.text = "Food: ${listFood.trim().substring(0, listFood.length - 2)}."

//        edtAddress?.setOnClickListener {
//
//        }

        btnOrder?.setOnClickListener {
            if (edtAddress?.text.toString().isEmpty()) {
                Toast.makeText(activity, "Please enter your address", Toast.LENGTH_SHORT).show()
            } else {
                if (orders.size == 0) {
                    order = Order(
                        id = "0",
                        phone = Common.currentUser?.phone.toString(),
                        name = Common.currentUser?.name.toString(),
                        address = edtAddress?.text.toString(),
                        total = binding.tvTotalPrice.text.toString(),
                        orderDate = Calendar.getInstance().time.toString(),
                        carts = carts
                    )
                } else if (orders.size != 0) {
                    val id = orders[orders.size - 1].id.toInt()
                    order = Order(
                        id = (id + 1).toString(),
                        phone = Common.currentUser?.phone.toString(),
                        name = Common.currentUser?.name.toString(),
                        address = edtAddress?.text.toString(),
                        total = binding.tvTotalPrice.text.toString(),
                        orderDate = Calendar.getInstance().time.toString(),
                        carts = carts
                    )
                }
                val myRef = database.getReference("Order")
                myRef.child("${order.id}")
                    .setValue(order, object : DatabaseReference.CompletionListener {
                        override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                            Toast.makeText(activity, "Order Success", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })

                dialog.dismiss()
            }
        }


        btnCancel?.setOnClickListener {
            dialog.dismiss()
        }

        dialog?.show()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is CartAdapter.ViewHolder) {
            val nameCart = carts[viewHolder.adapterPosition].foodName
            val cart: Cart = carts[viewHolder.adapterPosition]
            val indexDelete = viewHolder.adapterPosition

            cartAdapter.removeCart(indexDelete)

            val myPref = database.getReference("Cart")

            myPref.child("${cart.id}")
                .removeValue(object : DatabaseReference.CompletionListener {
                    override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                        Toast.makeText(
                            context,
                            "Delete Data Success",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

            setTotalPrice()

            binding.tvTotalPrice.text = totalPrice.toString()

            Log.d("thanhnt", "$cart , $indexDelete")

            val snackBar: Snackbar =
                Snackbar.make(binding.rootView, "$nameCart remove!", Snackbar.LENGTH_LONG)
            snackBar.setAction(
                "Undo"
            ) {
                cartAdapter.undoCart(cart, indexDelete)

                myPref.child("${cart.id}").setValue(cart)

                setTotalPrice()

                binding.tvTotalPrice.text = totalPrice.toString()
                Log.d("thanhnt", "$cart , $indexDelete")
                if (indexDelete == 0 || indexDelete == carts.size - 1) {
                    binding.rvCart.scrollToPosition(indexDelete)
                }
            }

            snackBar.setActionTextColor(Color.YELLOW)
            snackBar.show()
        }
    }

    private fun setTotalPrice() {
        totalPrice = 0
        for (cart in carts) {
            val price: Int = cart.price.toInt() * cart.quantity.toInt()
            totalPrice += price
        }

        binding.tvTotalPrice.text = totalPrice.toString()
    }
}