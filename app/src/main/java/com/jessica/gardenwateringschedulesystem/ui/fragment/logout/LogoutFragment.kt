package com.jessica.gardenwateringschedulesystem.ui.fragment.logout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jessica.gardenwateringschedulesystem.ui.MainActivity

class LogoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Firebase.auth.signOut()
        startActivity(Intent(activity, MainActivity::class.java))
        return null
    }
}