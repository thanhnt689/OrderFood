package com.ntt.orderfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntt.orderfood.R
import com.ntt.orderfood.databinding.ItemRatingBinding
import com.ntt.orderfood.model.Rating

class RatingAdapter(private val ratings: ArrayList<Rating>) :
    RecyclerView.Adapter<RatingAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemRatingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rating: Rating) {
            binding.tvTime.text = rating.time
            binding.tvComment.text = rating.comment
            binding.rbFood.rating = rating.rateValue.toFloat()
            binding.tvName.text = rating.user.name
            Glide.with(binding.imgAvatar).load(rating.user.image).error(R.drawable.ic_user)
                .into(binding.imgAvatar)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRatingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ratings[position])
    }

    override fun getItemCount(): Int {
        return ratings.size
    }
}