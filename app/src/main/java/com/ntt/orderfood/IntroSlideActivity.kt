package com.ntt.orderfood

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.ntt.orderfood.adapter.IntroSlideAdapter
import com.ntt.orderfood.databinding.ActivityIntroSlideBinding
import com.ntt.orderfood.model.Common
import com.ntt.orderfood.model.IntroSlide
import com.ntt.orderfood.model.User
import io.paperdb.Paper

class IntroSlideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroSlideBinding

    private lateinit var adapter: IntroSlideAdapter

    private var introSlides = listOf<IntroSlide>()

    private lateinit var database: FirebaseDatabase

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroSlideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)

        database = FirebaseDatabase.getInstance()

        Paper.init(this)

        introSlides =
            listOf(
                IntroSlide(
                    "Healthy and Hygienic - Home cooked food",
                    "For your everyday meal hot and fresh. Healthy and hygienic good food",
                    R.drawable.intro_slide_1
                ),
                IntroSlide(
                    "Order food online",
                    "Choose your favorite food from anywhere with just one touch",
                    R.drawable.intro_slide_3
                ),
                IntroSlide(
                    "Pre-schedule and customised delivery option",
                    "We deliver fresh and hot home cooked meal at your doorstep at your convenience time",
                    R.drawable.intro_slide_2
                )
            )

        adapter = IntroSlideAdapter(introSlides)
        binding.vpIntroSlider.adapter = adapter

        setupIndicators()
        setCurrentIndicator(0)

        binding.vpIntroSlider.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.vpIntroSlider.currentItem + 1 < adapter.itemCount) {
                binding.vpIntroSlider.currentItem += 1
            } else {
                val phone: String? = Paper.book().read(Common.PHONE_KEY)
                val pwd: String? = Paper.book().read(Common.PWD_KEY)
                val position: String? = Paper.book().read(Common.POSITION_KEY)

                if (phone != null && pwd != null && position != null) {
                    if (phone.isNotEmpty() && pwd.isNotEmpty() && position.isNotEmpty()) {
                        login(phone, pwd, position)
                    }
                } else {
                    Intent(applicationContext, MainActivity::class.java).also {
                        startActivity(it)
                    }
                }
            }
        }

        binding.tvSkipIntro.setOnClickListener {

            val phone: String? = Paper.book().read(Common.PHONE_KEY)
            val pwd: String? = Paper.book().read(Common.PWD_KEY)
            val position: String? = Paper.book().read(Common.POSITION_KEY)

            if (phone != null && pwd != null && position != null) {
                if (phone.isNotEmpty() && pwd.isNotEmpty() && position.isNotEmpty()) {
                    login(phone, pwd, position)
                }
            } else {
                Intent(applicationContext, MainActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.indicatorsContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.indicatorsContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

    private fun login(phone: String, pwd: String, position: String) {
        val tbUser: DatabaseReference = database.getReference("User")
        progressDialog.show()
        tbUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                if (snapshot.child(phone).exists()) {
                    val user: User? = snapshot.child(phone)
                        .getValue<User>()
                    if (user != null) {
                        if (user.password == pwd && user.position == position && position == "Staff") {
                            Toast.makeText(
                                this@IntroSlideActivity,
                                "Login Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val intent = Intent(this@IntroSlideActivity, HomeActivity::class.java)
                            Common.currentUser = user
                            startActivity(intent)
                        } else if (user.password == pwd && user.position == position && position == "Manager") {
                            Toast.makeText(
                                this@IntroSlideActivity,
                                "Login Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val intent =
                                Intent(this@IntroSlideActivity, HomeManagerActivity::class.java)
                            Common.currentUser = user
                            startActivity(intent)
                        } else {
                            Intent(applicationContext, MainActivity::class.java).also {
                                startActivity(it)
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@IntroSlideActivity,
                        "User not exist in DataBase",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}