package com.jessica.gardenwateringschedulesystem.fragment.tentang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jessica.gardenwateringschedulesystem.R
import com.jessica.gardenwateringschedulesystem.databinding.FragmentTentangBinding

class TentangFragment : Fragment() {

    private lateinit var tentangViewModel: TentangViewModel
    private lateinit var binding: FragmentTentangBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTentangBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}