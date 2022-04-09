package com.ntt.orderfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntt.orderfood.R
import com.ntt.orderfood.databinding.ItemFoodListInOrderBinding
import com.ntt.orderfood.model.Cart

class FoodListInOrderAdapter(private val carts: ArrayList<Cart>) :
    RecyclerView.Adapter<FoodListInOrderAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFoodListInOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            binding.tvNameFood.text = cart.foodName
            binding.tvNameFood.isSelected = true
            Glide.with(binding.root)
                .load(cart.foodImage)
                .centerCrop()
                .placeholder(R.drawable.load_food)
                .into(binding.imgFood)
            binding.tvPriceFood.text = "${cart.price} VNĐ"
            binding.tvQuantity.text = cart.quantity
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFoodListInOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(carts[position])
    }

    override fun getItemCount(): Int = carts.size
}