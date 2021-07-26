package com.jessica.gardenwateringschedulesystem.fragment.tentang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jessica.gardenwateringschedulesystem.R
import com.jessica.gardenwateringschedulesystem.databinding.FragmentTentangBinding
import com.jessica.gardenwateringschedulesystem.utils.ABOUT

class TentangFragment : Fragment() {

    private lateinit var binding: FragmentTentangBinding
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTentangBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db.collection(ABOUT).document("1").get()
            .addOnSuccessListener { doc ->
                val version = doc.data?.get("version").toString()
                val lastUpdate = doc.data?.get("last_update").toString()
                val desc = doc.data?.get("desc").toString()

                binding.tvVersion.text = version
                binding.tvLastUpdate.text = lastUpdate
                binding.tvDescription.text = desc
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}