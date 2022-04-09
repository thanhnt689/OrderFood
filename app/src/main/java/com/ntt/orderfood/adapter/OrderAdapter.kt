package com.ntt.orderfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ntt.orderfood.callback.ItemOrderClickListener
import com.ntt.orderfood.databinding.ItemOrderBinding
import com.ntt.orderfood.model.Order

class OrderAdapter(
    private val orders: ArrayList<Order>,
    private val mItemOrderClickListener: ItemOrderClickListener
) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvName.text = order.name
            binding.tvAddress.text = order.address
            binding.tvPhone.text = order.phone
            binding.tvAddress.isSelected = true
            binding.tvStatus.text = order.status
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orders[position])
        holder.binding.root.setOnClickListener {
            mItemOrderClickListener.onClickItemOrder(orders[position])
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}