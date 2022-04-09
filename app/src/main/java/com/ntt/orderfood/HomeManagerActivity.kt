package com.ntt.orderfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.databinding.ActivityHomeManagerBinding
import com.ntt.orderfood.model.Category
import com.ntt.orderfood.service.ManagerListenOrderService
import com.ntt.orderfood.vm.CategoryViewModel
import io.paperdb.Paper

class HomeManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeManagerBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration

    private var categories = arrayListOf<Category>()

    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        Paper.init(this)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_manager) as NavHostFragment
        navController = navHost.navController

        setSupportActionBar(binding.toolbar)

        appBarConfig = AppBarConfiguration(
            setOf(R.id.homeManagerFragment)
        )

        setupActionBarWithNavController(navController)
        binding.bnvHome.setupWithNavController(navController)


        val intent = Intent(this, ManagerListenOrderService::class.java)
        startService(intent)
    }

    private fun init() {

        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        database = Firebase.database
        val myRef = database.getReference("Category")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val category = dataSnapshot.getValue(Category::class.java)
                    if (category != null) {
                        categories.add(category)
                    }
                }
                viewModel.getCategories(categories)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}