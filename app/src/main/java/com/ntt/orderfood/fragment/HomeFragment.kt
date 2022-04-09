package com.ntt.orderfood.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.adapter.CategoryAdapter
import com.ntt.orderfood.callback.ItemClickListener
import com.ntt.orderfood.databinding.FragmentHomeBinding
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ntt.orderfood.R
import com.ntt.orderfood.model.Category
import com.ntt.orderfood.model.Common
import com.ntt.orderfood.model.Food
import com.ntt.orderfood.vm.CategoryViewModel

class HomeFragment : Fragment(), ItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var dataBase: FirebaseDatabase
    private var categories = arrayListOf<Category>()
    private var foods = arrayListOf<Food>()
    private lateinit var viewModel:CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        Glide.with(binding.imgAvatar).load(Common.currentUser?.image).into(binding.imgAvatar)

        dataBase = Firebase.database

        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        viewModel.categories.observe(viewLifecycleOwner) {
            categories = it

            categoryAdapter = CategoryAdapter(categories, this)
            binding.rvCategory.layoutManager =
                GridLayoutManager(context, 2)
            binding.rvCategory.adapter = categoryAdapter
            categoryAdapter.notifyDataSetChanged()
        }

        binding.svSearchCategory.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                categoryAdapter.filter.filter(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                categoryAdapter.filter.filter(p0)
                return false
            }
        })

        return binding.root
    }

    override fun onClickItemCategory(category: Category) {
        val action = HomeFragmentDirections.actionHomeFragmentToFoodListFragment(category)

        val myRef =
            dataBase.getReference("Food")

        myRef.orderByChild("menuId").equalTo(category.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (foods != null) {
                        foods.clear()
                    }
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val food = dataSnapshot.getValue(Food::class.java)
                        if (food != null) {
                            foods.add(food)
                        }
                    }
                    viewModel.getFoods(foods)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        findNavController().navigate(action)
    }
}