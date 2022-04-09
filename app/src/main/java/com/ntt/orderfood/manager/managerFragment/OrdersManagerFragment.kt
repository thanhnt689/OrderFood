package com.ntt.orderfood.manager.managerFragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.R
import com.ntt.orderfood.adapter.FoodListInOrderAdapter
import com.ntt.orderfood.adapter.OrderAdapter
import com.ntt.orderfood.callback.ItemOrderClickListener
import com.ntt.orderfood.databinding.FragmentOrdersManagerBinding
import com.ntt.orderfood.model.Cart
import com.ntt.orderfood.model.Order

class OrdersManagerFragment : Fragment(), ItemOrderClickListener {
    private lateinit var binding: FragmentOrdersManagerBinding
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private var orders = arrayListOf<Order>()
    private lateinit var bubbleNavigation: BubbleNavigationConstraintView
    private var carts = arrayListOf<Cart>()
    private lateinit var foodListInOrderAdapter: FoodListInOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersManagerBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Order"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database

        bubbleNavigation = view.findViewById(R.id.top_navigation_constraint_order)

        myRef = database.getReference("Order")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (orders != null) {
                    orders.clear()
                }
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val order = dataSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }
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

        bubbleNavigation.setNavigationChangeListener { view, position ->
            when (view.id) {
                R.id.btv_all -> {
                    val listOrderAll = arrayListOf<Order>()
                    listOrderAll.addAll(orders)

                    if (orders.size == 0) {
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
                    orderAdapter.notifyDataSetChanged()

                }
                R.id.btv_placed -> {
                    val listOrderPlaced = arrayListOf<Order>()
                    for (order: Order in orders) {
                        if (order.status == "Placed") {
                            listOrderPlaced.add(order)
                        }
                    }

                    if (listOrderPlaced.size == 0) {
                        binding.rvOrders.visibility = View.GONE
                        binding.layoutNoOrder.visibility = View.VISIBLE
                    } else {
                        binding.rvOrders.visibility = View.VISIBLE
                        binding.layoutNoOrder.visibility = View.GONE
                    }

                    orderAdapter = OrderAdapter(listOrderPlaced, this)
                    binding.rvOrders.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    binding.rvOrders.adapter = orderAdapter
                    orderAdapter.notifyDataSetChanged()
                }
                R.id.btv_shipped -> {
                    val listOrderShipped = arrayListOf<Order>()
                    for (order: Order in orders) {
                        if (order.status == "Shipped") {
                            listOrderShipped.add(order)
                        }
                    }

                    if (listOrderShipped.size == 0) {
                        binding.rvOrders.visibility = View.GONE
                        binding.layoutNoOrder.visibility = View.VISIBLE
                    } else {
                        binding.rvOrders.visibility = View.VISIBLE
                        binding.layoutNoOrder.visibility = View.GONE
                    }

                    orderAdapter = OrderAdapter(listOrderShipped, this)
                    binding.rvOrders.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    binding.rvOrders.adapter = orderAdapter
                    orderAdapter.notifyDataSetChanged()
                }
                R.id.btv_on_my_way -> {
                    val listOrderOnMyWay = arrayListOf<Order>()
                    for (order: Order in orders) {
                        if (order.status == "On my way") {
                            listOrderOnMyWay.add(order)
                        }
                    }

                    if (listOrderOnMyWay.size == 0) {
                        binding.rvOrders.visibility = View.GONE
                        binding.layoutNoOrder.visibility = View.VISIBLE
                    } else {
                        binding.rvOrders.visibility = View.VISIBLE
                        binding.layoutNoOrder.visibility = View.GONE
                    }

                    orderAdapter = OrderAdapter(listOrderOnMyWay, this)
                    binding.rvOrders.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    binding.rvOrders.adapter = orderAdapter
                    orderAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onClickItemOrder(order: Order) {
        val viewDialog: View =
            layoutInflater.inflate(R.layout.layout_bottom_sheet_order_manager, null)
        val btnCancel: Button = viewDialog.findViewById(R.id.btn_cancel)
        val btnUpdate: Button = viewDialog.findViewById(R.id.btn_update)
        val btnDelete: Button = viewDialog.findViewById(R.id.btn_delete)
        val tvAddress: TextView = viewDialog.findViewById(R.id.tv_address)
        val tvTotalPrice: TextView = viewDialog.findViewById(R.id.tv_total_price)
        val rvListFood: RecyclerView = viewDialog.findViewById(R.id.rv_list_food)
        val tvOrderDate: TextView = viewDialog.findViewById(R.id.tv_order_date)
        val spinnerStatus: Spinner = viewDialog.findViewById(R.id.spinner_status)

        spinnerStatus.setSelection(getIndex(spinnerStatus, order.status))

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

        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(viewDialog)
        bottomSheetDialog.setCancelable(false)

        btnUpdate.setOnClickListener {
            val newOrder = Order(
                order.id,
                order.phone,
                order.name,
                order.address,
                order.total,
                order.orderDate,
                spinnerStatus.selectedItem.toString(),
                order.carts
            )
            myRef.child(order.id).setValue(newOrder)
            bottomSheetDialog.dismiss()
            Snackbar.make(
                requireView(),
                "Update Order ${order.name} Successful",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Warning")
                .setMessage("Are you sure you want to delete it?")
                .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                        myRef.child(order.id)
                            .removeValue(object : DatabaseReference.CompletionListener {
                                override fun onComplete(
                                    error: DatabaseError?,
                                    ref: DatabaseReference
                                ) {
                                    Snackbar.make(
                                        requireView(),
                                        "Delete Order ${order.name} Successful",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            })

                        bottomSheetDialog.dismiss()
                    }
                })
                .setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                        dialogInterface?.dismiss()
                    }
                })
                .show()
        }

        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return -1
    }
}