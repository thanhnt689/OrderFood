package com.ntt.orderfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntt.orderfood.R
import com.ntt.orderfood.databinding.ItemCartBinding
import com.ntt.orderfood.model.Cart

class CartAdapter(private val carts: ArrayList<Cart>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

        val tvNameFood: TextView = binding.tvNameFood
        var tvPriceFood: TextView = binding.tvPriceFood
        var imgFood: ImageView = binding.imgFood
        var tvQuantity: TextView = binding.tvQuantity

        var layoutForeground: LinearLayout = binding.layoutForeground

        fun bind(cart: Cart) {
            tvNameFood.text = cart.foodName
            tvPriceFood.text = "${cart.price} VNĐ"
            tvQuantity.text = cart.quantity
            tvNameFood.isSelected = true
            Glide.with(binding.imgFood)
                .load(cart.foodImage)
                .centerCrop()
                .placeholder(R.drawable.load_food)
                .into(binding.imgFood)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(carts[position])
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    fun removeCart(index: Int) {
        carts.removeAt(index)
        notifyDataSetChanged()
        notifyItemRemoved(index)
    }

    fun undoCart(cart: Cart, index: Int) {
        carts.add(index, cart)
        notifyDataSetChanged()
        notifyItemInserted(index)
    }
}