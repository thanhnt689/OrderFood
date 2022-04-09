package com.ntt.orderfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntt.orderfood.R
import com.ntt.orderfood.callback.ItemClickListener
import com.ntt.orderfood.databinding.HomeCategoryItemBinding
import com.ntt.orderfood.model.Category
import java.util.*
import kotlin.collections.ArrayList

class CategoryAdapter(
    private val categories: ArrayList<Category>,
    private val mItemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>(), Filterable {

    var categoryFiltered = ArrayList<Category>()

    init {
        categoryFiltered.addAll(categories)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: HomeCategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.tvCategory.text = category.name
            binding.tvCategory.isSelected = true
            Glide.with(binding.root)
                .load(category.image)
                .centerCrop()
                .placeholder(R.drawable.load_food)
                .into(binding.imgCategory)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HomeCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryFiltered[position])
        holder.binding.root.setOnClickListener {
            mItemClickListener.onClickItemCategory(categoryFiltered[position])
        }
    }

    override fun getItemCount(): Int {
        return categoryFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val jobSearch = p0.toString()
                if (jobSearch.isEmpty()) {
                    categoryFiltered.clear()
                    categoryFiltered.addAll(categories)
                } else {
                    categoryFiltered.clear()
                    val resultList = ArrayList<Category>()
                    for (category in categories) {
                        if (category.name.lowercase(Locale.getDefault()).contains(
                                p0.toString().lowercase(Locale.getDefault()))
                        ) {
                            resultList.add(category)
                        }
                    }
                    categoryFiltered.addAll(resultList)
                }
                val filterResults = FilterResults()
                filterResults.values = categoryFiltered
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                categoryFiltered = p1?.values as ArrayList<Category>
                notifyDataSetChanged()
            }

        }
    }
}