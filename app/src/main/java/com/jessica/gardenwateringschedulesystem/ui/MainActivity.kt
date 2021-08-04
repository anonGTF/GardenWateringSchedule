package com.jessica.gardenwateringschedulesystem.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jessica.gardenwateringschedulesystem.R
import com.jessica.gardenwateringschedulesystem.background.DailyReminder
import com.jessica.gardenwateringschedulesystem.background.DailyReminder.Companion.TIME_EXTRA
import com.jessica.gardenwateringschedulesystem.databinding.ActivityMainBinding
import com.jessica.gardenwateringschedulesystem.utils.SCHEDULES
import com.jessica.gardenwateringschedulesystem.utils.USERS
import com.jessica.gardenwateringschedulesystem.utils.monthNumberToString
import java.lang.StringBuilder
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_ASK_PERMISSIONS = 1
        val RUNTIME_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        handlePermissions()
    }

    private fun handlePermissions() {
        if (hasPermissions(this, *RUNTIME_PERMISSIONS)) {
            setupNavController()
            setupHeader()
            setupNotification()
            showNotif()
        } else {
            ActivityCompat
                .requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS)
        }
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun setupNavController() {
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_jadwal, R.id.nav_tentang, R.id.nav_logout
            ), binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun setupNotification() {
        val reminder = DailyReminder()
        val userId = auth.currentUser?.uid
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val ref = StringBuilder()
            .append(monthNumberToString(month))
            .append(" ")
            .append(year)
            .toString()

        if (userId != null) {
            val dbRef = db.collection(SCHEDULES).document(userId).collection(ref)
            dbRef.whereEqualTo("is_notification_set", false).get()
                .addOnSuccessListener { collection ->
                    collection.documents.forEach {
                        val tanggal = it.data?.get("tanggal").toString()
                        val jam = it.data?.get("jam").toString()
                        reminder.setDailyReminder(applicationContext, jam, tanggal)
                        dbRef.document(it.reference.id)
                            .set(hashMapOf("is_notification_set" to true), SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d("coba", "setupNotification: $tanggal notif set true")
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Set notification error", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun showNotif() {
        val timeExtra = intent.getStringExtra(TIME_EXTRA)
        if (timeExtra != null) {
            val bundle = bundleOf("time" to timeExtra)
            navController.navigate(R.id.nav_home, bundle)
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                var index = 0
                while (index < permissions.size) {
                    if (grantResults[index] !== PackageManager.PERMISSION_GRANTED) {
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index])
                        ) {
                            Toast.makeText(
                                this,
                                "Required permission " + permissions[index] + " not granted. " + "Please go to settings and turn on for sample app",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Required permission " + permissions[index] + " not granted",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    index++
                }
                setupNavController()
                setupHeader()
                setupNotification()
                showNotif()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}