package com.jessica.gardenwateringschedulesystem

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jessica.gardenwateringschedulesystem.databinding.ActivityMainBinding
import com.jessica.gardenwateringschedulesystem.utils.USERS

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_jadwal, R.id.nav_tentang
            ), binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        setupHeader()
    }

    private fun setupHeader() {
        val header = binding.navView.getHeaderView(0)
        val imgProfile = header.findViewById<ImageView>(R.id.img_profile)
        val tvName = header.findViewById<TextView>(R.id.tv_name)
        val tvUsername = header.findViewById<TextView>(R.id.tv_username)

        val username = "@" + auth.currentUser?.email?.split("@")?.get(0)
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection(USERS).document(userId).get()
                .addOnSuccessListener { doc ->
                    tvName.text = doc.data?.get("name").toString()
                    tvUsername.text = username
                    Glide.with(binding.root.context)
                        .load(doc.data?.get("photo_profile").toString())
                        .override(76)
                        .into(imgProfile)
                }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}