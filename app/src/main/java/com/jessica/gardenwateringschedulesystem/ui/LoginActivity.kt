package com.jessica.gardenwateringschedulesystem.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jessica.gardenwateringschedulesystem.databinding.ActivityLoginBinding
import java.lang.StringBuilder

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener(this::login)
    }

    private fun login(v: View) {
        auth = Firebase.auth
        val username = StringBuilder()
            .append(binding.etUsername.text)
            .append("@jasica.com")
            .toString()
        val password = binding.etPassword.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(baseContext,
                            "Username atau password Anda salah", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(baseContext,
                "Username dan Password tidak boleh kosong", Toast.LENGTH_LONG).show()
        }
    }
}