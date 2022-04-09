package com.ntt.orderfood.manager.managerAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntt.orderfood.R
import com.ntt.orderfood.databinding.HomeCategoryItemBinding
import com.ntt.orderfood.manager.managerCallback.ItemCategoryManagerClickListener
import com.ntt.orderfood.model.Category
import java.util.*


class CategoryManagerAdapter(
    private val categories: ArrayList<Category>,
    private val mItemCategoryManagerClickListener: ItemCategoryManagerClickListener
) :
    RecyclerView.Adapter<CategoryManagerAdapter.ViewHolder>(), Filterable {

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
            mItemCategoryManagerClickListener.onClickItemCategory(categoryFiltered[position])
        }
        holder.binding.root.setOnLongClickListener {
            showPopupMenu(it, position)
            false
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showPopupMenu(it: View?, position: Int) {
        val popupMenu: PopupMenu = PopupMenu(it?.context, it).also {
            it.inflate(R.menu.popup_menu)
            it.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(p0: MenuItem?): Boolean {
                    when (p0?.itemId) {
                        R.id.actionPopupEdit -> mItemCategoryManagerClickListener.onClickEditCategory(
                            categoryFiltered[position]
                        )
                        R.id.actionPopupDelete -> mItemCategoryManagerClickListener.onClickDeleteCategory(
                            categoryFiltered[position]
                        )
                    }
                    return true
                }
            })
        }
        popupMenu.show()
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