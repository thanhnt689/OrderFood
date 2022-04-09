package com.ntt.orderfood.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.R
import com.ntt.orderfood.adapter.FoodListInOrderAdapter
import com.ntt.orderfood.adapter.OrderAdapter
import com.ntt.orderfood.callback.ItemOrderClickListener
import com.ntt.orderfood.databinding.FragmentOrderBinding
import com.ntt.orderfood.model.*

class OrdersFragment : Fragment(), ItemOrderClickListener {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var database: FirebaseDatabase
    private var orders = arrayListOf<Order>()
    private var carts = arrayListOf<Cart>()
    private lateinit var foodListInOrderAdapter: FoodListInOrderAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Order"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database

        val myRef =
            database.getReference("Order")

        myRef.orderByChild("phone").equalTo(Common.currentUser?.phone.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (orders != null) {
                        orders.clear()
                    }
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val order = dataSnapshot.getValue(Order::class.java)
                        Log.d("thanhnt", "$order")
                        if (order != null) {
                            orders.add(order)
                        }
                    }
                    Log.d("thanhnt", "$orders")
                    orderAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        if (orders == null) {
            binding.rvOrders.visibility = View.GONE
            binding.layoutNoOrder.visibility = View.VISIBLE
        } else {
            binding.rvOrders.visibility = View.VISIBLE
            binding.layoutNoOrder.visibility = View.GONE
        }

        orderAdapter = OrderAdapter(orders, this)
        binding.rvOrders.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvOrders.adapter = orderAdapter

    }

    override fun onClickItemOrder(order: Order) {
        val viewDialog: View = layoutInflater.inflate(R.layout.layout_bottom_sheet_order, null)
        val tvAddress: TextView = viewDialog.findViewById(R.id.tv_address)
        val tvTotalPrice: TextView = viewDialog.findViewById(R.id.tv_total_price)
        val rvListFood: RecyclerView = viewDialog.findViewById(R.id.rv_list_food)
        val tvOrderDate: TextView = viewDialog.findViewById(R.id.tv_order_date)

        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(viewDialog)

        carts.clear()

        for (cart in order.carts) {
            carts.add(cart)
        }
        foodListInOrderAdapter = FoodListInOrderAdapter(carts)
        rvListFood.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvListFood.adapter = foodListInOrderAdapter

        tvAddress.text = order.address
        tvTotalPrice.text = "${order.total} VNĐ"
        tvOrderDate.text = order.orderDate

        bottomSheetDialog.show()

    }
}