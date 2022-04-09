package com.ntt.orderfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntt.orderfood.databinding.SlideItemContainerBinding
import com.ntt.orderfood.model.IntroSlide

class IntroSlideAdapter(private val introSlides: List<IntroSlide>) :
    RecyclerView.Adapter<IntroSlideAdapter.ViewHolder>() {

    class ViewHolder(val binding: SlideItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(introSlide: IntroSlide) {
            binding.tvTitle.text = introSlide.title
            binding.tvDescription.text = introSlide.description
            Glide.with(binding.root)
                .load(introSlide.icon)
                .centerCrop()
                .into(binding.imgSlideIcon)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SlideItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(introSlides[position])
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }

}