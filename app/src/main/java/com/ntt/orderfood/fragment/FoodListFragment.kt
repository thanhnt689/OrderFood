package com.ntt.orderfood.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.R
import com.ntt.orderfood.adapter.FoodAdapter
import com.ntt.orderfood.callback.ItemFoodClickListener
import com.ntt.orderfood.databinding.FragmentFoodListBinding
import com.ntt.orderfood.model.Category
import com.ntt.orderfood.model.Food
import com.ntt.orderfood.vm.CategoryViewModel

class FoodListFragment : Fragment(), ItemFoodClickListener {
    private lateinit var binding: FragmentFoodListBinding
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var category: Category
    private val args: FoodListFragmentArgs by navArgs()
    private var foods = arrayListOf<Food>()
    private lateinit var viewModel: CategoryViewModel

    private lateinit var dataBase: FirebaseDatabase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodListBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        dataBase = Firebase.database

        category = args.category

        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        viewModel.foods.observe(viewLifecycleOwner){
            foods = it
            foodAdapter = FoodAdapter(foods, this)
            binding.rvFoodList.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.rvFoodList.adapter = foodAdapter
            foodAdapter.notifyDataSetChanged()
        }

        (activity as AppCompatActivity).supportActionBar?.title = category.name

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val menuItem : MenuItem = menu.findItem(R.id.actionSearch)
        val searchView:SearchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                foodAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                foodAdapter.filter.filter(newText)
                return false
            }
        })
    }

    override fun onClickItemFood(food: Food) {
        val action = FoodListFragmentDirections.actionFoodListFragmentToDetailFoodFragment(food)
        findNavController().navigate(action)
    }
}