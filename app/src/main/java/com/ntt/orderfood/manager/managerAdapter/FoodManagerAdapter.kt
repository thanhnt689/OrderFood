package com.ntt.orderfood.manager.managerAdapter

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
import com.ntt.orderfood.databinding.HomeFoodItemBinding
import com.ntt.orderfood.manager.managerCallback.ItemFoodManagerClickListener
import com.ntt.orderfood.model.Food
import java.util.*
import kotlin.collections.ArrayList

class FoodManagerAdapter(
    private val foods: List<Food>,
    private val mItemFoodManagerClickListener: ItemFoodManagerClickListener
) :
    RecyclerView.Adapter<FoodManagerAdapter.ViewHolder>(), Filterable {
    var foodFiltered = ArrayList<Food>()

    init {
        foodFiltered.addAll(foods)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: HomeFoodItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            binding.tvNameFood.text = food.name
            binding.tvNameFood.isSelected = true
            binding.tvPriceFood.setText("${food.price} VNĐ")
            Glide.with(binding.root)
                .load(food.image)
                .centerCrop()
                .placeholder(R.drawable.load_food)
                .into(binding.imgFood)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HomeFoodItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(foodFiltered[position])
        holder.binding.root.setOnClickListener {
            mItemFoodManagerClickListener.onClickItemFood(foodFiltered[position])
        }
        holder.binding.root.setOnLongClickListener {
            showPopupMenu(it, position)
            false
        }
    }

    private fun showPopupMenu(it: View?, position: Int) {
        val popupMenu: PopupMenu = PopupMenu(it?.context, it).also {
            it.inflate(R.menu.popup_menu)
            it.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(p0: MenuItem?): Boolean {
                    when (p0?.itemId) {
                        R.id.actionPopupEdit -> mItemFoodManagerClickListener.onClickEditFood(
                            foodFiltered[position]
                        )
                        R.id.actionPopupDelete -> mItemFoodManagerClickListener.onClickDeleteFood(
                            foodFiltered[position]
                        )
                    }
                    return true
                }
            })
        }
        popupMenu.show()
    }

    override fun getItemCount(): Int {
        return foodFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val jobSearch = p0.toString()
                if (jobSearch.isEmpty()) {
                    foodFiltered.clear()
                    foodFiltered.addAll(foods)
                } else {
                    foodFiltered.clear()
                    val resultList = ArrayList<Food>()
                    for (food in foods) {
                        if (food.name.lowercase(Locale.getDefault()).contains(
                                p0.toString().lowercase(Locale.getDefault())
                            )
                        ) {
                            resultList.add(food)
                        }
                    }
                    foodFiltered.addAll(resultList)
                }
                val filterResults = FilterResults()
                filterResults.values = foodFiltered
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                foodFiltered = p1?.values as ArrayList<Food>
                notifyDataSetChanged()
            }

        }
    }
}